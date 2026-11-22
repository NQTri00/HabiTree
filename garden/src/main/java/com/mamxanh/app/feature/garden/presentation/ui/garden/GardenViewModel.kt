package com.mamxanh.app.feature.garden.presentation.ui.garden

import androidx.lifecycle.viewModelScope
import com.mamxanh.app.core.presentation.BaseViewModel
import com.mamxanh.app.feature.garden.domain.usecase.GardenRepository
import com.mamxanh.app.feature.garden.domain.usecase.LoadGarden
import com.mamxanh.app.feature.garden.domain.usecase.PlantSeed
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GardenViewModel(
    private val repository: GardenRepository,
) : BaseViewModel<GardenState, GardenEvent, GardenIntent>(
        initialState = GardenState(),
    ) {
    val uiState get() = state
    val effects get() = events

    init {
        dispatch(GardenIntent.Refresh)
    }

    override fun handleAction(action: GardenIntent) {
        val intent = action
        when (intent) {
            GardenIntent.Refresh ->
                viewModelScope.launch {
                    val garden = withContext(Dispatchers.IO) { LoadGarden(repository)() }
                    mutableState.value = mutableState.value.copy(garden = garden, isLoading = false)
                }
            GardenIntent.OpenShop -> viewModelScope.launch { emitEvent(GardenEvent.OpenShop) }
            is GardenIntent.OpenTree -> viewModelScope.launch { emitEvent(GardenEvent.OpenTree(intent.id)) }
            GardenIntent.DismissMessage -> mutableState.value = mutableState.value.copy(message = null)
            is GardenIntent.Plant, is GardenIntent.UnlockPlot, is GardenIntent.WaterAll -> {
                if (mutableState.value.isLoading) return
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
                            GardenState(
                                garden,
                                false,
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
                        if (changed) emitEvent(GardenEvent.DataChanged)
                    } finally {
                        mutableState.value = mutableState.value.copy(isLoading = false)
                    }
                }
            }
        }
    }
}
