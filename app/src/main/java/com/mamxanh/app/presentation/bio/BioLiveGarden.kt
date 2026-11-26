package com.mamxanh.app.presentation.bio

import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.mamxanh.app.feature.garden.presentation.navigation.GardenRouteContent
import com.mamxanh.app.feature.garden.presentation.ui.garden.GardenAction
import com.mamxanh.app.feature.garden.presentation.ui.garden.GardenEvent
import com.mamxanh.app.feature.garden.presentation.ui.garden.GardenIntent
import com.mamxanh.app.feature.garden.presentation.ui.garden.GardenViewModel
import com.mamxanh.app.feature.garden.presentation.ui.livinggarden.LivingGardenViewModel
import com.mamxanh.shared.model.catalog
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun liveGardenRouteContent(
    world: BioWorld,
    ui: BioController,
): GardenRouteContent {
    val model = koinViewModel<GardenViewModel>()
    val state by model.state.collectAsStateWithLifecycle()
    val living = koinViewModel<LivingGardenViewModel>()
    val collection by living.state.collectAsStateWithLifecycle()
    DisposableEffect(living) {
        living.observeCollection(true)
        onDispose { living.observeCollection(false) }
    }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(model, world) { if (!world.loading) model.dispatch(GardenIntent.Refresh) }
    LaunchedEffect(model, lifecycle, ui) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            model.events.collect { event ->
                if (ui.route != "garden") return@collect
                when (event) {
                    GardenEvent.OpenInventory -> ui.go("inventory")
                    GardenEvent.OpenCatalog -> ui.go("encyclopedia")
                    GardenEvent.OpenShop -> ui.go("shop")
                    GardenEvent.ConfirmWaterAll -> ui.show("water-all")
                    is GardenEvent.ConfirmUnlock -> ui.show("unlock", event.plotId.toString())
                    is GardenEvent.OpenTree -> ui.go("tree", event.id.toString())
                    is GardenEvent.PickSeed -> {
                        ui.openSeedPicker(event.plotId ?: ui.preferredPlantingPlot)
                    }
                    is GardenEvent.MutationFinished -> {
                        if (event.success && ui.modal in setOf("unlock", "water-all")) ui.modal = ""
                        if (ui.modal in setOf("unlock", "water-all")) ui.modalMessage = event.message else ui.message = event.message
                    }
                    GardenEvent.DataChanged -> Unit
                }
            }
        }
    }
    if (state.isLoading) LinearProgressIndicator()
    return GardenRouteContent(
        state.display(
            collection.snapshot.species.size.takeUnless {
                collection.loading
            } ?: catalog.size,
        ),
    ) { action ->
        model.dispatch(
            when (action) {
                GardenAction.Inventory -> GardenIntent.OpenInventory
                GardenAction.Encyclopedia -> GardenIntent.OpenCatalog
                GardenAction.WaterAll -> GardenIntent.ConfirmWaterAll
                GardenAction.PickSeed -> GardenIntent.PickSeed
                is GardenAction.Filter -> GardenIntent.Filter(action.value)
                is GardenAction.Plot -> GardenIntent.SelectPlot(action.value.number)
            },
        )
    }
}
