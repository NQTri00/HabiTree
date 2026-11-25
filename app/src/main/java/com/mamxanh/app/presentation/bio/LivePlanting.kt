package com.mamxanh.app.presentation.bio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.mamxanh.app.feature.garden.presentation.ui.garden.PlantingAction
import com.mamxanh.app.feature.garden.presentation.ui.garden.PlantingEvent
import com.mamxanh.app.feature.garden.presentation.ui.garden.PlantingState
import com.mamxanh.app.feature.garden.presentation.ui.garden.PlantingViewModel
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun bindLivePlanting(
    world: BioWorld,
    ui: BioController,
): LivePlanting {
    val model = koinViewModel<PlantingViewModel>()
    val state by model.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val modal = ui.modal
    val seed = ui.modalId
    DisposableEffect(model, ui) {
        ui.plantingBusy = { model.state.value.busy }
        onDispose { ui.plantingBusy = null }
    }
    LaunchedEffect(model, modal, seed, world) {
        if (modal == "plant" || modal == "seed-picker") {
            model.dispatch(PlantingAction.Open(seed.takeIf { modal == "plant" }.orEmpty(), ui.preferredPlantingPlot))
        } else {
            model.dispatch(PlantingAction.Close)
        }
    }
    LaunchedEffect(model, lifecycle, ui) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            model.events.collect { event ->
                if (ui.modal !in setOf("plant", "seed-picker")) return@collect
                when (event) {
                    is PlantingEvent.Feedback -> ui.modalMessage = event.message
                    is PlantingEvent.Planted -> {
                        ui.modal = ""
                        if (event.treeId != null) ui.go("tree", event.treeId.toString())
                        ui.message = AppText.text(TextKey.CopyMotMamXanhMoi63e3dc)
                    }
                }
            }
        }
    }
    return LivePlanting(state, model::dispatch)
}

internal class LivePlanting(
    val state: PlantingState,
    private val dispatch: (PlantingAction) -> Unit,
) {
    val full: Boolean get() = !state.loading && state.eligible.isEmpty()
    val canPlant: Boolean get() = !state.loading && !state.busy && state.plotId != null

    fun count(seed: String): Int = state.stock[seed] ?: 0

    fun setName(name: String) = dispatch(PlantingAction.SetName(name))

    fun selectPlot(plot: String) {
        plot.toIntOrNull()?.let { dispatch(PlantingAction.SelectPlot(it)) }
    }

    fun plant() = dispatch(PlantingAction.Plant)
}
