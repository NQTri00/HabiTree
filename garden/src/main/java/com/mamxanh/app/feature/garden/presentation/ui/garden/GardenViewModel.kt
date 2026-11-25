package com.mamxanh.app.feature.garden.presentation.ui.garden

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mamxanh.app.core.presentation.BaseViewModel
import com.mamxanh.app.feature.garden.domain.usecase.GardenRepository
import com.mamxanh.app.feature.garden.domain.usecase.LoadGarden
import com.mamxanh.app.feature.garden.domain.usecase.PlantSeed
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GardenViewModel(
    private val repository: GardenRepository,
    private val savedState: SavedStateHandle,
) : BaseViewModel<GardenState, GardenEvent, GardenIntent>(
        initialState = GardenState(filter = savedState[GardenBrowseState.SAVED_FILTER_KEY] ?: "all"),
    ) {
    val uiState get() = state
    val effects get() = events
    private var mutating = false

    init {
        dispatch(GardenIntent.Refresh)
    }

    private fun send(event: GardenEvent) {
        viewModelScope.launch { emitEvent(event) }
    }

    override fun handleAction(action: GardenIntent) {
        val intent = action
        when (intent) {
            GardenIntent.OpenInventory -> send(GardenEvent.OpenInventory)
            GardenIntent.OpenCatalog -> send(GardenEvent.OpenCatalog)
            GardenIntent.PickSeed -> send(GardenEvent.PickSeed())
            GardenIntent.ConfirmWaterAll -> send(GardenEvent.ConfirmWaterAll)
            is GardenIntent.Filter -> {
                savedState[GardenBrowseState.SAVED_FILTER_KEY] = intent.value
                mutableState.value = state.value.copy(filter = intent.value)
            }
            is GardenIntent.SelectPlot -> {
                val plots = state.value.garden.plots
                val plot = plots.firstOrNull { it.id == intent.plotId } ?: return
                val treeId = plot.treeId
                when {
                    !plot.isUnlocked && plots.firstOrNull { !it.isUnlocked }?.id == plot.id -> send(GardenEvent.ConfirmUnlock(plot.id))
                    !plot.isUnlocked -> Unit
                    treeId != null -> send(GardenEvent.OpenTree(treeId))
                    else -> send(GardenEvent.PickSeed(plot.id))
                }
            }
            GardenIntent.Refresh ->
                viewModelScope.launch {
                    if (mutating) return@launch
                    try {
                        val garden = withContext(Dispatchers.IO) { LoadGarden(repository)() }
                        if (!mutating) mutableState.value = state.value.copy(garden = garden, isLoading = false)
                    } catch (cancelled: CancellationException) {
                        throw cancelled
                    } catch (_: Exception) {
                        if (!mutating) {
                            mutableState.value = state.value.copy(isLoading = false)
                            emitEvent(GardenEvent.MutationFinished(false, failureMessage()))
                        }
                    }
                }
            GardenIntent.OpenShop -> viewModelScope.launch { emitEvent(GardenEvent.OpenShop) }
            is GardenIntent.OpenTree -> viewModelScope.launch { emitEvent(GardenEvent.OpenTree(intent.id)) }
            GardenIntent.DismissMessage -> mutableState.value = mutableState.value.copy(message = null)
            is GardenIntent.Plant, is GardenIntent.UnlockPlot, is GardenIntent.WaterAll -> {
                if (mutableState.value.isLoading || mutating) return
                mutating = true
                mutableState.value = mutableState.value.copy(isLoading = true)
                viewModelScope.launch {
                    try {
                        var batchFeedback: String? = null
                        val changed =
                            withContext(Dispatchers.IO) {
                                when (intent) {
                                    is GardenIntent.Plant -> PlantSeed(repository)(intent.speciesId, intent.name, intent.plotId)
                                    is GardenIntent.UnlockPlot -> repository.unlockPlot(intent.plotId)
                                    is GardenIntent.WaterAll -> repository.waterAll(intent.key).also { batchFeedback = it.message }.success
                                    else -> false
                                }
                            }
                        val garden = withContext(Dispatchers.IO) { LoadGarden(repository)() }
                        mutableState.value =
                            state.value.copy(
                                garden = garden,
                                isLoading = false,
                                message =
                                    when {
                                        intent is GardenIntent.WaterAll -> batchFeedback
                                        changed && intent is GardenIntent.Plant ->
                                            AppText.text(TextKey.CopyDaGieoCayDaTrongE209c5) +
                                                (intent.name.ifBlank { AppText.text(TextKey.CopyMamMoiFa708b) }) +
                                                AppText.text(TextKey.CopyTaiOF143a2) +
                                                (
                                                    intent.plotId?.toString()?.padStart(2, '0')
                                                        ?: garden.plots
                                                            .firstOrNull {
                                                                it.treeId ==
                                                                    garden.trees.maxByOrNull { tree -> tree.id }?.id
                                                            }?.id
                                                        ?: ""
                                                )
                                        changed -> null
                                        intent is GardenIntent.UnlockPlot ->
                                            AppText.text(
                                                TextKey.CopyKhongTheMoOTrongHayKiemTraSoXuHienCo4a5480,
                                            )
                                        else -> AppText.text(TextKey.CopyChuaTheGieoCayHayKiemTraHatTrongTuiVaOTr249b81)
                                    },
                            )
                        if (intent is GardenIntent.UnlockPlot || intent is GardenIntent.WaterAll) {
                            val message =
                                if (changed && intent is GardenIntent.UnlockPlot) {
                                    AppText.text(TextKey.CopyDaMoOTrong37fc9b)
                                } else {
                                    state.value.message.orEmpty()
                                }
                            emitEvent(GardenEvent.MutationFinished(changed, message))
                        }
                        if (changed) emitEvent(GardenEvent.DataChanged)
                    } catch (cancelled: CancellationException) {
                        throw cancelled
                    } catch (_: Exception) {
                        emitEvent(GardenEvent.MutationFinished(false, failureMessage()))
                    } finally {
                        mutating = false
                        mutableState.value = mutableState.value.copy(isLoading = false)
                    }
                }
            }
        }
    }

    private fun failureMessage() = AppText.text(TextKey.CopyChuaTheCapNhatVuiLongThuLai42c8ac)
}
