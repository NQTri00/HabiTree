package com.mamxanh.app.feature.garden.presentation.ui.garden

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mamxanh.app.core.presentation.BaseViewModel
import com.mamxanh.app.feature.garden.domain.usecase.GardenRepository
import com.mamxanh.app.feature.garden.domain.usecase.availablePlantingPlots
import com.mamxanh.app.feature.garden.domain.usecase.plantedTreeId
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlantingViewModel(
    private val repository: GardenRepository,
    private val savedState: SavedStateHandle,
) : BaseViewModel<PlantingState, PlantingEvent, PlantingAction>(
        PlantingState(
            seed = savedState["planting_seed"] ?: "",
            plotId = savedState["plot"],
            name = savedState["planting_name"] ?: "",
        ),
    ) {
    private var load: Job? = null

    override fun handleAction(action: PlantingAction) {
        val current = state.value
        if (current.busy) return
        when (action) {
            is PlantingAction.Open -> {
                val draft =
                    if (current.seed == action.seed) {
                        current.copy(plotId = current.plotId ?: action.preferredPlot, loading = true)
                    } else {
                        PlantingState(seed = action.seed, plotId = action.preferredPlot)
                    }
                update(draft)
                load?.cancel()
                load =
                    viewModelScope.launch {
                        try {
                            val garden = withContext(Dispatchers.IO) { repository.loadGarden() }
                            val plots = garden.availablePlantingPlots()
                            val selected = state.value.plotId?.takeIf { it in plots } ?: plots.firstOrNull()
                            update(state.value.copy(plotId = selected, eligible = plots, stock = garden.inventory, loading = false))
                        } catch (cancelled: CancellationException) {
                            throw cancelled
                        } catch (_: Exception) {
                            update(state.value.copy(loading = false))
                            emitEvent(PlantingEvent.Feedback(failure()))
                        }
                    }
            }
            is PlantingAction.SelectPlot -> {
                if (action.plotId in current.eligible) update(current.copy(plotId = action.plotId))
            }
            is PlantingAction.SetName -> update(current.copy(name = action.name.take(100)))
            PlantingAction.Plant -> plant(current)
            PlantingAction.Close -> {
                load?.cancel()
                update(PlantingState())
            }
        }
    }

    private fun plant(draft: PlantingState) {
        val plot = draft.plotId ?: return
        if (draft.loading || plot !in draft.eligible || (draft.stock[draft.seed] ?: 0) <= 0) return
        update(draft.copy(busy = true))
        viewModelScope.launch {
            try {
                val planted = withContext(Dispatchers.IO) { repository.plant(draft.seed, draft.name, plot) }
                if (planted) {
                    val treeId =
                        try {
                            withContext(Dispatchers.IO) { repository.loadGarden().plantedTreeId(plot) }
                        } catch (cancelled: CancellationException) {
                            throw cancelled
                        } catch (_: Exception) {
                            null
                        }
                    update(PlantingState())
                    emitEvent(PlantingEvent.Planted(treeId))
                } else {
                    val garden = withContext(Dispatchers.IO) { repository.loadGarden() }
                    val plots = garden.availablePlantingPlots()
                    val selected = plot.takeIf { it in plots } ?: plots.firstOrNull()
                    update(draft.copy(plotId = selected, eligible = plots, stock = garden.inventory, busy = false))
                    emitEvent(PlantingEvent.Feedback(AppText.text(TextKey.CopyKiemTraLaiHatVaOTrong36d949)))
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                emitEvent(PlantingEvent.Feedback(failure()))
            } finally {
                if (state.value.busy) update(state.value.copy(busy = false))
            }
        }
    }

    private fun update(next: PlantingState) {
        savedState["planting_seed"] = next.seed
        savedState["plot"] = next.plotId
        savedState["planting_name"] = next.name
        mutableState.value = next
    }

    private fun failure() = AppText.text(TextKey.CopyChuaTheCapNhatVuiLongThuLai42c8ac)
}
