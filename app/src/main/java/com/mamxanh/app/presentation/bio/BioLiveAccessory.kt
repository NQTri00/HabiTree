package com.mamxanh.app.presentation.bio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.mamxanh.app.feature.tree.domain.repository.AccessorySnapshot
import com.mamxanh.app.feature.tree.presentation.ui.accessory.AccessoryAction
import com.mamxanh.app.feature.tree.presentation.ui.accessory.AccessoryEvent
import com.mamxanh.app.feature.tree.presentation.ui.accessory.AccessoryState
import com.mamxanh.app.feature.tree.presentation.ui.accessory.AccessoryViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun bindLiveAccessoryCatalog(
    world: BioWorld,
    modal: String,
): AccessorySnapshot {
    val model = koinViewModel<AccessoryViewModel>()
    val state by model.state.collectAsStateWithLifecycle()
    LaunchedEffect(model, world, modal) {
        if (!world.loading && modal != "accessory") model.dispatch(AccessoryAction.RefreshCatalog)
    }
    return state.snapshot
}

@Composable
internal fun bindLiveAccessory(
    ui: BioController,
    sku: String,
    preferred: String,
    home: String,
    world: BioWorld,
): LiveAccessory {
    val model = koinViewModel<AccessoryViewModel>()
    val state by model.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(model, sku, preferred, home, world) {
        model.dispatch(AccessoryAction.Open(sku, preferred, home))
    }
    LaunchedEffect(model, lifecycle, ui, sku) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            model.events.collect { event ->
                when (event) {
                    is AccessoryEvent.Completed -> {
                        if (ui.modal != "accessory" || ui.modalId != event.sku) return@collect
                        if (event.success) {
                            model.dispatch(AccessoryAction.Close)
                            ui.modal = ""
                            ui.message = event.message
                        } else {
                            ui.modalMessage = event.message
                        }
                    }
                }
            }
        }
    }
    return LiveAccessory(state.takeIf { it.sku == sku } ?: AccessoryState(sku = sku), model::dispatch)
}

internal class LiveAccessory(
    val state: AccessoryState,
    private val dispatch: (AccessoryAction) -> Unit,
) {
    val selected: String get() = state.selected
    val busy: Boolean get() = state.busy

    fun select(target: String) = dispatch(AccessoryAction.Select(target))

    fun equip() = dispatch(AccessoryAction.Equip)

    fun buy() = dispatch(AccessoryAction.Buy)

    fun close() = dispatch(AccessoryAction.Close)
}
