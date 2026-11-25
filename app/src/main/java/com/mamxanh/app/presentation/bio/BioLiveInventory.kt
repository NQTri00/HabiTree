package com.mamxanh.app.presentation.bio

import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.mamxanh.app.feature.inventory.presentation.navigation.InventoryRouteContent
import com.mamxanh.app.feature.inventory.presentation.ui.inventory.InventoryAction
import com.mamxanh.app.feature.inventory.presentation.ui.inventory.InventoryEvent
import com.mamxanh.app.feature.inventory.presentation.ui.inventory.InventoryState
import com.mamxanh.app.feature.inventory.presentation.ui.inventory.InventoryViewModel
import com.mamxanh.app.feature.tree.domain.repository.AccessorySnapshot
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun liveInventoryRouteContent(
    world: BioWorld,
    ui: BioController,
    accessoryCatalog: AccessorySnapshot?,
): InventoryRouteContent {
    val model = koinViewModel<InventoryViewModel>()
    val state by model.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(model, world) { model.dispatch(InventoryAction.Refresh) }
    LaunchedEffect(model, lifecycle, ui) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            model.events.collect { event ->
                if (ui.route != "inventory") return@collect
                when (event) {
                    is InventoryEvent.OpenShop -> {
                        ui.requestShopTab(event.tab)
                        ui.go("shop")
                    }
                    InventoryEvent.FertilizerHelp -> ui.show("fertilizer-help")
                    is InventoryEvent.Plant -> ui.show("plant", event.seed)
                    is InventoryEvent.Accessory -> ui.openAccessory(event.sku, event.target)
                    is InventoryEvent.Feedback -> {
                        ui.message =
                            event.message.ifBlank {
                                if (event.success) "" else AppText.text(TextKey.CopyChuaTheCapNhatVuiLongThuLai42c8ac)
                            }
                    }
                }
            }
        }
    }
    if (state.loading || state.busy) LinearProgressIndicator()
    return inventoryRouteContent(world, ui, LiveInventory(state, model::dispatch), accessoryCatalog)
}

internal class LiveInventory(
    val state: InventoryState,
    private val dispatch: (InventoryAction) -> Unit,
) {
    fun openShop() = dispatch(InventoryAction.OpenShop)

    fun selectTab(tab: String) = dispatch(InventoryAction.SelectTab(tab))

    fun use(
        id: String,
        target: String,
        permanent: Boolean,
    ) {
        if (state.busy) return
        dispatch(
            when (state.tab) {
                "seeds" -> InventoryAction.Plant(id)
                "accessories" -> InventoryAction.Accessory(id, target)
                else -> if (permanent) InventoryAction.EquipCan(id) else InventoryAction.FertilizerHelp
            },
        )
    }
}
