package com.mamxanh.app.presentation.bio

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mamxanh.app.core.components.design.*
import com.mamxanh.app.feature.garden.presentation.ui.garden.GardenIntent
import com.mamxanh.app.feature.garden.presentation.ui.garden.GardenViewModel
import com.mamxanh.app.feature.home.presentation.ui.home.HomeAction
import com.mamxanh.app.feature.home.presentation.ui.home.HomeViewModel
import com.mamxanh.app.feature.settings.presentation.ui.backup.BackupAction
import com.mamxanh.app.feature.settings.presentation.ui.backup.BackupOperation
import com.mamxanh.app.feature.settings.presentation.ui.settings.SettingsAction
import com.mamxanh.app.feature.shop.presentation.ui.shop.purchaseAvailability
import com.mamxanh.app.feature.tasks.presentation.ui.detail.TaskDetailAction
import com.mamxanh.app.feature.tree.domain.repository.AccessoryTree
import com.mamxanh.shared.localization.AppText
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
    val item = world.product(id)
    val disposition = type == "remove" || type == "sell"
    val treeDisposition =
        if (world.native && disposition && ui.route == "tree" && ui.param == id) {
            bindBioTreeDisposition(id, type == "sell")
        } else {
            null
        }
    val tree = if (world.native) null else world.tree(id)
    val seed = world.species.any { it.s("id") == id }
    val permanent = item.b("permanent")
    val wallet = shop?.state?.wallet
    val stock = wallet?.inventory?.get(id) ?: if (wallet == null) world.count(id) else 0
    val equippedCan = wallet?.equippedCan ?: world.state.s("equipped")
    val accessory =
        if (world.native && type == "accessory") {
            bindLiveAccessory(ui, id, ui.accessoryTargetArgument, ui.homeTreeState.selectedId, world)
        } else {
            null
        }
    val owned = if (type == "accessory") accessory?.state?.snapshot?.owns(id) ?: (world.owned(id) > 0) else permanent && stock > 0
    val coins = accessory?.state?.snapshot?.coins ?: wallet?.coins ?: world.coins
    val accessoryTrees =
        if (type == "accessory") {
            accessory?.state?.snapshot?.trees ?: world.trees.map {
                AccessoryTree(it.s("id"), it.s("name"), it.s("decor"), it.s("state") != "dead")
            }
        } else {
            emptyList()
        }
    val living = accessoryTrees.filter { it.living }
    val targetId =
        if (accessory == null) {
            ui.shopSelection.accessoryTarget(living.map { it.id }, ui.homeTreeState.selectedId)
        } else {
            accessory.selected
        }
    val target = living.firstOrNull { it.id == targetId }
    val quantity = shop?.quantity(id) ?: ui.shopSelection.quantity
    val purchase =
        purchaseAvailability(
            coins,
            item.i("price"),
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
    val title = bioModalTitle(type, id, permanent, full) { item.s("name") }
    val garden =
        if (world.native && ui.route == "garden" && type in setOf("unlock", "water-all")) {
            koinViewModel<GardenViewModel>()
        } else {
            null
        }
    val gardenState = garden?.state?.collectAsStateWithLifecycle()?.value
    var plantName by rememberSaveable(type, id) { mutableStateOf("") }
    var selectedPlot by rememberSaveable(type, id) {
        mutableStateOf(if (world.native || type != "plant") "" else ui.plantingSelection.initialDialogPlot(world.emptyPlots()))
    }
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
        scrollToBottom = ui.tabs["sheetBottom"] == "true",
        dismiss = dismiss,
        content = {
            when (type) {
                "tree-picker" -> {
                    val home =
                        when {
                            !world.native -> null
                            ui.route == "home" -> koinViewModel<HomeViewModel>()
                            else -> treeHomeSelection
                        }
                    val homeState = home?.state?.collectAsStateWithLifecycle()?.value
                    val choices =
                        homeState?.dashboard?.trees?.map {
                            world.homeTreeChoice(it.id.toString(), AppText.plantName(it.speciesId, it.name), it.speciesId)
                        } ?: world.previewHomeTreeChoices()
                    BioTreePickerBody(
                        choices,
                        ui.homeTreeState.selectedId,
                        loading = homeState?.isLoading == true,
                        onPlant = { ui.show("seed-picker") },
                    ) { id ->
                        if (home == null) ui.act("bio-pin:" + id) else home.dispatch(HomeAction.SelectTree(id))
                    }
                }
                "seed-picker" -> BioSeedPickerBody(world, ui, planting)
                "purchase" -> BioPurchaseBody(id, world, ui, item, seed, permanent, total, full, shop, coins, stock)
                "accessory" -> BioAccessoryBody(id, ui, item, owned, purchase, accessoryTrees, living, target, accessory, coins)
                "plant" ->
                    BioPlantBody(
                        id,
                        item,
                        full,
                        planting?.count(id) ?: world.count(id),
                        planting?.state?.name ?: plantName,
                        if (planting == null) {
                            selectedPlot
                        } else {
                            planting.state.plotId
                                ?.toString()
                                .orEmpty()
                        },
                        if (planting == null) world.emptyPlots() else planting.state.eligible,
                        onName = { if (planting == null) plantName = it.take(100) else planting.setName(it) },
                        onPlot = { if (planting == null) selectedPlot = it else planting.selectPlot(it) },
                        onPlant = { planting?.plant() },
                        canPlant = (planting?.count(id) ?: world.count(id)) > 0 && !ui.busy && (planting?.canPlant ?: true),
                    )
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
                        unlockCoins = gardenState?.garden?.coins ?: world.coins,
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
                    canUnlock = gardenState?.canUnlock(id.toIntOrNull()),
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
                    pendingPurchase = shop?.state?.pendingAction,
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
