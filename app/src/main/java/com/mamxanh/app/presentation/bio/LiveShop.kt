package com.mamxanh.app.presentation.bio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.mamxanh.app.feature.shop.presentation.ui.shop.ShopAction
import com.mamxanh.app.feature.shop.presentation.ui.shop.ShopEvent
import com.mamxanh.app.feature.shop.presentation.ui.shop.ShopState
import com.mamxanh.app.feature.shop.presentation.ui.shop.ShopViewModel
import org.koin.androidx.compose.koinViewModel
import java.util.UUID

@Composable
internal fun bindLiveShop(
    world: BioWorld,
    ui: BioController,
): LiveShop {
    val model = koinViewModel<ShopViewModel>()
    val state by model.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(model, world) { if (!world.loading) model.dispatch(ShopAction.Refresh) }
    val tab = ui.pendingShopTab
    val purchaseId = ui.modalId.takeIf { ui.modal == "purchase" }.orEmpty()
    LaunchedEffect(model, purchaseId) { model.dispatch(ShopAction.OpenPurchase(purchaseId)) }
    DisposableEffect(ui, model) {
        ui.shopBuying = { model.state.value.isPurchasing }
        onDispose { ui.shopBuying = null }
    }
    LaunchedEffect(model, tab) {
        if (tab != null) {
            model.dispatch(ShopAction.SelectTab(tab))
            ui.pendingShopTab = null
        }
    }
    LaunchedEffect(model, lifecycle, ui) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            model.events.collect { event ->
                when (event) {
                    is ShopEvent.ShowProduct -> {
                        if (ui.route != "shop" || ui.modal.isNotBlank()) return@collect
                        val target = event.accessoryTarget
                        if (target == null) ui.show("purchase", event.id) else ui.openAccessory(event.id, target)
                    }
                    is ShopEvent.PurchaseFinished -> {
                        if (ui.modal == "purchase" && ui.modalId == event.id) {
                            if (event.success) {
                                ui.modal = ""
                                if (event.openTree && event.treeId != null) ui.go("tree", event.treeId.toString())
                                ui.message = event.message
                            } else {
                                ui.modalMessage = event.message
                            }
                        }
                    }
                    is ShopEvent.Feedback -> ui.message = event.message
                    ShopEvent.OpenGarden -> ui.go("garden")
                    ShopEvent.InventoryChanged -> Unit
                }
            }
        }
    }
    return LiveShop(state, model::dispatch)
}

internal class LiveShop(
    val state: ShopState,
    private val dispatch: (ShopAction) -> Unit,
) {
    fun selectTab(tab: String) = dispatch(ShopAction.SelectTab(tab))

    fun selectProduct(
        id: String,
        accessoryTarget: String?,
    ) = dispatch(ShopAction.SelectProduct(id, accessoryTarget))

    fun quantity(id: String): Int = if (state.purchaseId == id) state.quantity else 1

    fun setQuantity(quantity: Int) = dispatch(ShopAction.SetQuantity(quantity))

    fun buy(
        id: String,
        quantity: Int,
    ) = dispatch(ShopAction.Purchase(id, quantity, UUID.randomUUID().toString()))

    fun equip(id: String) = dispatch(ShopAction.Equip(id))

    fun buyAndEquip(
        id: String,
        quantity: Int,
    ) = dispatch(ShopAction.BuyAndEquip(id, quantity))

    fun buyAndPlant(
        id: String,
        quantity: Int,
        preferredPlot: Int?,
    ) = dispatch(ShopAction.BuyAndPlant(id, quantity, preferredPlot))
}
