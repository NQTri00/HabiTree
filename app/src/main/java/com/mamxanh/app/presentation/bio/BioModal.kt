package com.mamxanh.app.presentation.bio

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mamxanh.app.core.components.design.*
import com.mamxanh.app.feature.garden.presentation.ui.garden.GardenIntent
import com.mamxanh.app.feature.garden.presentation.ui.garden.GardenViewModel
import com.mamxanh.app.feature.home.presentation.ui.home.HomeViewModel
import com.mamxanh.app.feature.settings.presentation.ui.backup.BackupAction
import com.mamxanh.app.feature.settings.presentation.ui.backup.BackupOperation
import com.mamxanh.app.feature.settings.presentation.ui.settings.SettingsAction
import com.mamxanh.app.feature.shop.presentation.ui.shop.purchaseAvailability
import com.mamxanh.app.feature.tasks.presentation.ui.detail.TaskDetailAction
import com.mamxanh.app.feature.tree.domain.repository.AccessoryTree
import com.mamxanh.shared.model.gardenCosmetics
import org.koin.androidx.compose.koinViewModel

@Composable internal fun BioModal(
    world: BioWorld,
    ui: BioController,
    planting: LivePlanting? = null,
    shop: LiveShop? = null,
    treeHomeSelection: HomeViewModel? = null,
    activeFocus: LiveFocus? = null,
) {
    val type = ui.modal
    val backup = if (world.native && ui.route == "data") backupViewModel() else null
    val backupState = backup?.state?.collectAsStateWithLifecycle()?.value
    val backupSummary = backupState?.importSummary
    val settingsModel = if (world.native && type.startsWith("permission")) settingsViewModel() else null
    val id = ui.modalId
    val cosmetic = if (world.native && type == "accessory") gardenCosmetics.firstOrNull { it.id == id } else null
    val referenceItem = if (world.native) null else world.product(id)
    val livePurchase = world.native && (type == "purchase" || type == "plant")
    val product = if (livePurchase) livePurchaseProduct(id) else null
    val details = product?.details ?: referenceItem?.referencePurchaseDetails() ?: BioPurchaseDetails(name = "", price = 0)
    val price = cosmetic?.price ?: details.price
    val disposition = type == "remove" || type == "sell"
    val treeDisposition =
        if (world.native && disposition && ui.route == "tree" && ui.param == id) {
            bindBioTreeDisposition(id, type == "sell")
        } else {
            null
        }
    val tree = if (world.native) null else world.tree(id)
    val seed = product?.seed ?: (!world.native && world.species.any { it.s("id") == id })
    val permanent = cosmetic?.permanent ?: product?.permanent ?: (referenceItem?.b("permanent") == true)
    val wallet = shop?.state?.wallet
    val stock = if (world.native) wallet?.inventory?.get(id) ?: 0 else world.count(id)
    val equippedCan = if (world.native) wallet?.equippedCan.orEmpty() else world.state.s("equipped")
    val plantStock = if (world.native) planting?.count(id) ?: 0 else world.count(id)
    val accessory =
        if (world.native && type == "accessory") {
            bindLiveAccessory(ui, id, ui.accessoryTargetArgument, ui.homeTreeState.selectedId, world)
        } else {
            null
        }
    val accessorySnapshot = accessory?.state?.snapshot
    val owned =
        when {
            type != "accessory" -> permanent && stock > 0
            world.native -> accessorySnapshot?.owns(id) == true
            else -> world.owned(id) > 0
        }
    val coins =
        when {
            !world.native -> world.coins
            type == "accessory" -> accessorySnapshot?.coins ?: 0
            else -> wallet?.coins ?: 0
        }
    val accessoryTrees =
        when {
            type != "accessory" -> emptyList()
            world.native -> accessorySnapshot?.trees.orEmpty()
            else ->
                world.trees.map {
                    AccessoryTree(it.s("id"), it.s("name"), it.s("decor"), it.s("state") != "dead")
                }
        }
    val living = accessoryTrees.filter { it.living }
    val targetId =
        when {
            type != "accessory" -> null
            world.native -> accessory?.selected
            else -> ui.preview.shopSelection.accessoryTarget(living.map { it.id }, ui.homeTreeState.selectedId)
        }
    val target = living.firstOrNull { it.id == targetId }
    val quantity = if (world.native) shop?.quantity(id) ?: 1 else ui.preview.shopSelection.quantity
    val purchase =
        purchaseAvailability(
            coins,
            price,
            owned,
            permanent,
            ui.busy ||
                shop?.state?.isPurchasing == true ||
                shop?.state?.isLoading == true ||
                accessory?.busy == true ||
                accessory?.state?.loading == true,
            quantity,
        )
    val total = purchase.total
    val full =
        when {
            type == "plant" && planting != null -> planting.full
            type == "purchase" && shop != null -> !shop.state.isLoading && !shop.state.wallet.hasPlantingPlot
            !world.native && (type == "plant" || type == "purchase") -> world.emptyPlots().isEmpty()
            else -> false
        }
    val title = bioModalTitle(type, id, permanent, full) { cosmetic?.name ?: details.name }
    val garden =
        if (world.native && ui.route == "garden" && type in setOf("unlock", "water-all")) {
            koinViewModel<GardenViewModel>()
        } else {
            null
        }
    val gardenState = garden?.state?.collectAsStateWithLifecycle()?.value
    val dismiss = {
        if (!ui.busy &&
            planting?.state?.busy != true &&
            shop?.state?.isPurchasing != true &&
            accessory?.busy != true &&
            backupState?.pending == null &&
            gardenState?.isLoading != true &&
            treeDisposition?.loading != true
        ) {
            accessory?.close()
            treeDisposition?.dismiss()
            if (type == "confirm-import") backup?.dispatch(BackupAction.Cancelled)
            ui.modal = ""
            ui.modalMessage = ""
        }
    }
    val currentDismiss by rememberUpdatedState(dismiss)
    DisposableEffect(ui) {
        ui.dismissModal = { currentDismiss() }
        onDispose { ui.dismissModal = null }
    }
    val hasFooter =
        type !in listOf("tree-picker", "seed-picker", "accessory", "plant", "permission-choice", "permission-settings", "license") ||
            type == "plant" &&
            full
    BioModalSurface(
        title = title,
        busy = ui.busy,
        message = ui.modalMessage,
        scrollToBottom = !world.native && ui.preview.sheetBottom,
        dismiss = dismiss,
        content = {
            when (type) {
                "tree-picker" -> BioBoundTreePicker(world, ui, treeHomeSelection)
                "seed-picker" -> BioSeedPickerBody(world, ui, planting)
                "purchase" ->
                    BioPurchaseBody(
                        id = id,
                        item = details,
                        seed = seed,
                        permanent = permanent,
                        total = total,
                        full = full,
                        coins = coins,
                        stock = stock,
                        quantity = quantity,
                        quantityBusy = ui.busy || world.native && (shop?.state?.isLoading != false || shop.state.isPurchasing),
                        onQuantity = { value ->
                            if (world.native) shop?.setQuantity(value) else ui.preview.shopSelection.updateQuantity(value)
                        },
                    )
                "accessory" -> BioAccessoryBody(id, ui, price, owned, purchase, accessoryTrees, living, target, accessory, coins)
                "plant" -> BioBoundPlantDialog(world, ui, id, details.name, full, plantStock, planting)
                else ->
                    BioModalMessages(
                        type,
                        world,
                        ui,
                        tree,
                        dismiss,
                        importSummary = backupSummary,
                        dispositionName = treeDisposition?.name,
                        saleQuote = treeDisposition?.quote,
                        watering = gardenState?.garden?.waterAll,
                        unlockCost = gardenState?.nextUnlockCost,
                        unlockCoins = if (world.native) gardenState?.garden?.coins ?: 0 else world.coins,
                        requestPermission = settingsModel?.let { model -> { model.dispatch(SettingsAction.RequestPermission) } },
                        openPermissionSettings =
                            settingsModel?.let { model ->
                                { model.dispatch(SettingsAction.OpenPermissionSettings) }
                            },
                    )
            }
        },
        footer = {
            if (hasFooter) {
                val archive =
                    if (world.native &&
                        type == "archive" &&
                        ui.route == "task" &&
                        ui.param == id
                    ) {
                        taskDetailViewModel(id)
                    } else {
                        null
                    }
                val archiveBusy =
                    archive
                        ?.state
                        ?.collectAsStateWithLifecycle()
                        ?.value
                        ?.busy == true
                val focus = activeFocus.takeIf { type == "focus-end" }
                val rest = if (world.native && ui.route == "rest" && type == "ticket") restViewModel() else null
                val restState = rest?.state?.collectAsStateWithLifecycle()?.value
                val insights = if (world.native && ui.route == "insights" && type == "adjust") insightsViewModel() else null
                val insightsBusy =
                    insights
                        ?.state
                        ?.collectAsStateWithLifecycle()
                        ?.value
                        ?.busy == true
                BioModalFooter(
                    type,
                    id,
                    world,
                    ui,
                    purchase,
                    seed,
                    permanent,
                    owned,
                    full,
                    dismiss,
                    confirmArchive = archive?.let { model -> { model.dispatch(TaskDetailAction.Archive) } },
                    archiveBusy = archiveBusy,
                    confirmFocusEnd = focus?.let { it::end },
                    focusBusy = focus?.busy == true,
                    confirmGardenAction =
                        garden?.let { model ->
                            {
                                if (type == "water-all") {
                                    model.dispatch(
                                        GardenIntent.WaterAll(
                                            java.util.UUID
                                                .randomUUID()
                                                .toString(),
                                        ),
                                    )
                                } else {
                                    id.toIntOrNull()?.let { model.dispatch(GardenIntent.UnlockPlot(it)) }
                                }
                            }
                        },
                    gardenBusy = gardenState?.isLoading == true,
                    canUnlock = if (world.native) gardenState?.canUnlock(id.toIntOrNull()) == true else null,
                    confirmDisposition = treeDisposition?.confirm,
                    treeBusy = world.native && disposition && (treeDisposition?.quote == null || treeDisposition.loading),
                    confirmTicket =
                        rest?.let { model ->
                            { model.dispatch(com.mamxanh.app.feature.tasks.presentation.ui.rest.RestAction.BuyTicket) }
                        },
                    restBusy = restState?.busy == true,
                    canBuyTicket = if (world.native) restState?.schedule?.canBuyTicket == true else null,
                    requestPermission = settingsModel?.let { model -> { model.dispatch(SettingsAction.RequestPermission) } },
                    confirmAdjustment =
                        insights?.let { model ->
                            {
                                model.dispatch(
                                    com.mamxanh.app.feature.tasks.presentation.ui.insights.InsightsAction
                                        .ApplyAdjustment(id),
                                )
                            }
                        },
                    insightsBusy = insightsBusy,
                    buy = shop?.let { live -> { live.buy(id, quantity) } },
                    shopBuying = shop?.state?.isPurchasing == true,
                    pendingPurchase = if (world.native) shop?.state?.pendingAction else ui.preview.purchase,
                    quantity = quantity,
                    equippedCan = equippedCan,
                    backupAction =
                        backup?.let { model ->
                            {
                                when (type) {
                                    "clear" -> model.dispatch(BackupAction.ClearNext)
                                    "clear-final" -> model.dispatch(BackupAction.Confirm(BackupOperation.Clear))
                                    "recover" -> model.dispatch(BackupAction.Confirm(BackupOperation.Recover))
                                    "confirm-import" -> model.dispatch(BackupAction.Confirm(BackupOperation.Restore))
                                    else -> Unit
                                }
                            }
                        },
                    buyAndEquip = shop?.let { live -> { live.buyAndEquip(id, quantity) } },
                    equip = shop?.let { live -> { live.equip(id) } },
                    buyAndPlant =
                        shop?.let { live ->
                            { live.buyAndPlant(id, quantity, ui.preferredPlantingPlot) }
                        },
                )
            }
        },
    )
}
