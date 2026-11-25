package com.mamxanh.app.presentation.bio

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mamxanh.app.core.components.design.*
import com.mamxanh.app.core.components.design.AppColors
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppIcons
import com.mamxanh.app.core.components.design.AppOpacity
import com.mamxanh.app.core.components.design.AppRadii
import com.mamxanh.app.core.components.design.AppSpacing
import com.mamxanh.app.core.components.design.AppTypography
import com.mamxanh.app.feature.garden.presentation.ui.garden.GardenIntent
import com.mamxanh.app.feature.garden.presentation.ui.garden.GardenViewModel
import com.mamxanh.app.feature.home.presentation.ui.home.HomeAction
import com.mamxanh.app.feature.home.presentation.ui.home.HomeViewModel
import com.mamxanh.app.feature.settings.presentation.ui.backup.BackupAction
import com.mamxanh.app.feature.settings.presentation.ui.backup.BackupOperation
import com.mamxanh.app.feature.settings.presentation.ui.settings.SettingsAction
import com.mamxanh.app.feature.shop.presentation.ui.shop.purchaseAvailability
import com.mamxanh.app.feature.tasks.presentation.ui.detail.TaskDetailAction
import com.mamxanh.app.feature.tasks.presentation.ui.focus.FocusAction
import com.mamxanh.app.feature.tree.domain.repository.AccessoryTree
import com.mamxanh.app.feature.tree.presentation.ui.detail.TreeAction
import com.mamxanh.app.feature.tree.presentation.ui.detail.TreeViewModel
import com.mamxanh.core.ui.R
import com.mamxanh.shared.localization.AppText
import org.koin.androidx.compose.koinViewModel

@Composable internal fun BioModal(
    world: BioWorld,
    ui: BioController,
    planting: LivePlanting? = null,
    shop: LiveShop? = null,
    treeHomeSelection: HomeViewModel? = null,
) {
    val type = ui.modal
    val backup = if (world.native && ui.route == "data") backupViewModel() else null
    val backupState = backup?.state?.collectAsStateWithLifecycle()?.value
    val backupSummary = backupState?.importSummary
    val settingsModel = if (world.native && type.startsWith("permission")) settingsViewModel() else null
    val id = ui.modalId
    val item = world.product(id)
    val disposition = type == "remove" || type == "sell"
    val treeId = id.toLongOrNull()
    val treeModel =
        if (world.native && disposition && ui.route == "tree" && ui.param == id) {
            koinViewModel<TreeViewModel>()
        } else {
            null
        }
    val treeState = treeModel?.state?.collectAsStateWithLifecycle()?.value
    val quote = treeState?.takeIf { it.dispositionId == treeId && it.selling == (type == "sell") }?.quote
    LaunchedEffect(treeModel, treeId, type) {
        if (treeModel != null && treeId != null && quote == null) {
            treeModel.dispatch(TreeAction.AskDisposition(treeId, type == "sell", openDialog = false))
        }
    }
    val tree = if (world.native) null else world.tree(id)
    val seed = world.species.any { it.s("id") == id }
    val permanent = item.b("permanent")
    val wallet = shop?.state?.wallet
    val coins = wallet?.coins ?: world.coins
    val stock = wallet?.inventory?.get(id) ?: if (wallet == null) world.count(id) else 0
    val equippedCan = wallet?.equippedCan ?: world.state.s("equipped")
    val accessory =
        if (world.native && type == "accessory") {
            bindLiveAccessory(ui, id, ui.accessoryTargetArgument, ui.homeTreeState.selectedId, world)
        } else {
            null
        }
    val owned = if (type == "accessory") accessory?.state?.snapshot?.owns(id) ?: (world.owned(id) > 0) else permanent && stock > 0
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
    val title =
        when (type) {
            "tree-picker" -> stringResource(R.string.copy_cay_tren_trang_chu_76c8b5)
            "seed-picker" -> stringResource(R.string.copy_gieo_mot_mam_moi_2a80ed)
            "purchase" ->
                if (permanent) {
                    stringResource(
                        R.string.copy_dung_cu_cham_soc_9f1ca9,
                    )
                } else {
                    stringResource(R.string.copy_them_vao_tui_a0e7dc)
                }
            "accessory" -> item.s("name")
            "plant" ->
                if (full) {
                    stringResource(
                        R.string.copy_chua_co_o_trong_6abd67,
                    )
                } else {
                    stringResource(R.string.copy_gieo_mot_mam_xanh_776075)
                }
            "water-all" -> stringResource(R.string.copy_tuoi_ca_vuon_20a14a)
            "unlock" -> stringResource(R.string.copy_mo_o_0_01668e, id)
            "fertilizer-help" -> stringResource(R.string.copy_dung_phan_bon_79fd5f)
            "remove" -> stringResource(R.string.copy_don_cay_e3d410)
            "sell" -> stringResource(R.string.copy_ban_cay_5210c7)
            "archive" -> stringResource(R.string.copy_luu_tru_viec_50123f)
            "discard" -> stringResource(R.string.copy_bo_thay_doi_b07820)
            "ticket" -> stringResource(R.string.copy_mua_ve_bao_ve_acdfc5)
            "adjust" -> stringResource(R.string.copy_dieu_chinh_lich_bdb141)
            "permission" -> stringResource(R.string.copy_nhac_viec_tuy_chon_907252)
            "permission-choice" -> stringResource(R.string.copy_cho_phep_thong_bao_6c0308)
            "permission-settings" -> stringResource(R.string.copy_cai_dat_thong_bao_765600)
            "clear" -> stringResource(R.string.copy_xoa_du_lieu_8ce180)
            "clear-final" -> stringResource(R.string.copy_xac_nhan_lan_cuoi_8421a1)
            "recover" -> stringResource(R.string.copy_khoi_phuc_ban_an_toan_9361dd)
            "confirm-import" -> stringResource(R.string.copy_khoi_phuc_du_lieu_feaa4a)
            "license" -> stringResource(R.string.copy_giay_phep_nguon_mo_2ecf92)
            "focus-end" -> stringResource(R.string.copy_ket_thuc_phien_7ea116)
            else -> stringResource(R.string.copy_xac_nhan_1503b0)
        }
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
            treeState?.isLoading != true
        ) {
            accessory?.close()
            treeModel?.dispatch(TreeAction.DismissDisposition)
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
    BoxWithConstraints(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize().background(AppColors.Swatch60252A2E).clickable(onClick = dismiss))
        val limit = maxHeight - AppDimensions.Dp32
        Column(
            Modifier
                .align(
                    Alignment.Center,
                ).widthIn(
                    max = AppDimensions.Dp432,
                ).fillMaxWidth()
                .padding(
                    horizontal = AppDimensions.Dp16,
                ).heightIn(
                    max = limit,
                ).shadow(
                    AppDimensions.Dp12,
                    bioShape(AppRadii.VALUE_28),
                ).background(
                    bp.bg,
                    bioShape(AppRadii.VALUE_28),
                ).border(
                    AppDimensions.Dp1,
                    AppColors.White.copy(alpha = AppOpacity.VALUE_6),
                    bioShape(AppRadii.VALUE_28),
                ).padding(AppDimensions.Dp1)
                .clip(bioShape(AppRadii.VALUE_28))
                .clickable(enabled = false) {
                },
        ) {
            Row(
                Modifier.fillMaxWidth().padding(
                    start = AppDimensions.Dp16,
                    end = AppDimensions.Dp8,
                    top = AppDimensions.Dp8,
                    bottom = AppDimensions.Dp4,
                ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp8),
            ) {
                Bt(
                    title,
                    Modifier.weight(1f),
                    size = AppTypography.SIZE_22,
                    line = AppTypography.LINE_HEIGHT_30_8,
                    bold = true,
                    spacing = AppTypography.TRACKING_MINUS_7,
                )
                BioIconButton("close", dismiss, enabled = !ui.busy, iconSize = AppIcons.SIZE_22, tint = bp.muted)
            }
            if (ui.busy)LinearProgressIndicator(Modifier.fillMaxWidth(), color = bp.deep, trackColor = bp.soft)
            Column(
                Modifier
                    .weight(1f, fill = false)
                    .fillMaxWidth()
                    .verticalScroll(
                        rememberScrollState(
                            if (ui.tabs["sheetBottom"] ==
                                "true"
                            ) {
                                Int.MAX_VALUE
                            } else {
                                0
                            },
                        ),
                    ).padding(
                        start = AppDimensions.Dp16,
                        end = AppDimensions.Dp16,
                        top = AppDimensions.Dp12,
                        bottom = AppDimensions.Dp18,
                    ),
                verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp18),
            ) {
                if (ui.modalMessage.isNotBlank()) {
                    BioCard(
                        Modifier.fillMaxWidth(),
                        bp.soft,
                        padding = AppSpacing.VALUE_12,
                        radius = AppRadii.VALUE_14,
                    ) {
                        Bt(ui.modalMessage, size = AppTypography.SIZE_12)
                    }
                }
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
                            dispositionName = treeState?.detail?.takeIf { it.id == treeId }?.name,
                            saleQuote = quote,
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
            }
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
                        ?.busy ?: false
                val focus = ui.liveFocus.takeIf { type == "focus-end" }
                val focusBusy =
                    focus
                        ?.state
                        ?.collectAsStateWithLifecycle()
                        ?.value
                        ?.busy ?: false
                val rest = if (world.native && ui.route == "rest" && type == "ticket") restViewModel() else null
                val restState = rest?.state?.collectAsStateWithLifecycle()?.value
                val insights = if (world.native && ui.route == "insights" && type == "adjust") insightsViewModel() else null
                val insightsBusy =
                    insights
                        ?.state
                        ?.collectAsStateWithLifecycle()
                        ?.value
                        ?.busy ?: false
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
                    confirmFocusEnd = focus?.let { model -> { model.dispatch(FocusAction.End) } },
                    focusBusy = focusBusy,
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
                    confirmDisposition =
                        treeModel?.let { model ->
                            {
                                id.toLongOrNull()?.let { model.dispatch(TreeAction.Dispose(it, type == "sell")) }
                                Unit
                            }
                        },
                    treeBusy = world.native && disposition && (quote == null || treeState?.isLoading == true),
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
        }
    }
}

@Composable internal fun BioChoice(
    title: String,
    subtitle: String,
    species: String,
    selected: Boolean,
    onClick: () -> Unit,
    icon: String = "next",
) {
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(
                min = AppDimensions.Dp76,
            ).background(
                if (selected)bp.soft else bp.surface,
                bioShape(AppRadii.VALUE_16),
            ).border(
                AppDimensions.Dp1,
                if (selected)bp.deep else bp.line,
                bioShape(AppRadii.VALUE_16),
            ).clickable(onClick = onClick)
            .padding(AppDimensions.Dp12),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp12),
    ) {
        BioPlant(species, Modifier.size(AppDimensions.Dp50))
        Column(Modifier.weight(1f)) {
            Bt(title, size = AppTypography.SIZE_13, semi = true)
            Spacer(Modifier.height(AppDimensions.Dp3))
            Bt(subtitle, size = AppTypography.SIZE_11, tint = bp.muted)
        }
        Bi(if (selected)"check"else icon, size = AppIcons.SIZE_18)
    }
}

@Composable
internal fun BioPurchaseQuantity(
    quantity: Int,
    busy: Boolean,
    onQuantity: (Int) -> Unit,
) {
    val canDecrease = quantity > 1 && !busy
    val decreaseLabel = stringResource(R.string.shop_decrease_quantity)
    Row(
        Modifier.fillMaxWidth().heightIn(min = AppDimensions.Dp52),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val shape = bioShape(AppRadii.VALUE_12)
        Box(
            Modifier
                .size(AppDimensions.Dp48)
                .alpha(if (canDecrease) AppOpacity.VALUE_1 else AppOpacity.VALUE_45)
                .background(bp.surface, shape)
                .border(AppDimensions.Dp1, bp.line, shape)
                .semantics { contentDescription = decreaseLabel }
                .clickable(enabled = canDecrease, role = Role.Button) { onQuantity(quantity - 1) },
            contentAlignment = Alignment.Center,
        ) {
            Bt("\u2212", size = AppTypography.SIZE_22, tint = bp.deep)
        }
        Spacer(Modifier.width(AppDimensions.Dp25))
        BioField(
            "",
            quantity.toString(),
            { onQuantity(it.toIntOrNull() ?: 1) },
            Modifier.width(AppDimensions.Dp65),
            number = true,
            centered = true,
            accessibilityLabel = stringResource(R.string.shop_purchase_quantity),
        )
        Spacer(Modifier.width(AppDimensions.Dp25))
        BioIconButton(
            "plus",
            { onQuantity(quantity + 1) },
            boxed = true,
            enabled = quantity < 99 && !busy,
            label = stringResource(R.string.shop_increase_quantity),
        )
    }
}

@Preview(name = "Purchase quantity", widthDp = 335, showBackground = true)
@Composable
private fun BioPurchaseQuantityPreview() {
    BioPurchaseQuantity(quantity = 1, busy = false) {}
}
