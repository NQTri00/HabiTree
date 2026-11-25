package com.mamxanh.app.presentation.bio

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.mamxanh.app.core.components.design.*
import com.mamxanh.app.core.components.design.AppColors
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppIcons
import com.mamxanh.app.core.components.design.AppRadii
import com.mamxanh.app.core.components.design.AppSpacing
import com.mamxanh.app.core.components.design.AppTypography
import com.mamxanh.app.feature.home.presentation.ui.home.HomeReward
import com.mamxanh.app.feature.inventory.presentation.navigation.InventoryRouteContent
import com.mamxanh.app.feature.shop.presentation.navigation.ShopRouteContent
import com.mamxanh.app.feature.shop.presentation.ui.shop.ShopTreeContext
import com.mamxanh.app.feature.tree.domain.repository.AccessorySnapshot
import com.mamxanh.core.ui.R
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import org.json.JSONObject

internal fun treeState(t: JSONObject) =
    when (t.s("state")) {
        "dead" -> AppText.text(TextKey.CopyDaKhoHeoA9c1b9)
        "mature" -> AppText.text(TextKey.CopyTruongThanh8c14d7)
        else -> AppText.text(TextKey.CopyDangLon40daab)
    }

internal fun taskReward(t: JSONObject) =
    if (t.s("verification") == "TIMER") {
        when {
            t.i("minutes") >= 45 -> 20
            t.i("minutes") >= 20 -> 15
            else -> 10
        }
    } else {
        8
    }

internal fun growthCaption(t: JSONObject) =
    when (t.s("state")) {
        "dead" -> AppText.text(TextKey.CopyMamXanhDaDungLai6dabc6)
        "mature" -> AppText.text(TextKey.CopyDaTruongThanh54f165)
        else -> AppText.text(TextKey.CopyDangLonCungBan6913c2)
    }

internal fun accessoryIcon(id: String) =
    when (id) {
        "tree_ribbon" -> "gift"
        "tree_lantern" -> "sun"
        else -> "spark"
    }

@Composable
internal fun BioReward(
    world: BioWorld,
    ui: BioController,
    claim: () -> Unit = { Unit },
) {
    HomeReward(world.state.b("claimed"), world.data.o("phase").b("canClaim"), claim)
}

@Composable internal fun BioProduct(
    world: BioWorld,
    ui: BioController,
    item: JSONObject,
    modifier: Modifier = Modifier,
    inventory: Boolean = false,
    target: String = "",
    encyclopedia: Boolean = false,
    onProductClick: (() -> Unit)? = null,
    ownedQuantity: Int? = null,
    equippedCan: String = world.state.s("equipped"),
    catalogStatus: String? = null,
    catalogKnown: Boolean? = null,
    accessoryCatalog: AccessorySnapshot? = null,
) {
    val id = item.s("id")
    val seed = world.species.any { it.s("id") == id }
    val accessory =
        item.s("category") == "TREE"
    val quantity = ownedQuantity ?: world.inventoryCount(id, accessory)
    val owned = if (inventory) quantity > 0 else accessoryCatalog?.owns(id) ?: (world.owned(id) > 0)
    val equipped = accessoryCatalog?.equipped(id) ?: world.trees.any { it.s("decor") == id }
    val known =
        !encyclopedia || (catalogKnown ?: (world.record(id).b("discovered") || world.record(id).i("planted") > 0 || world.count(id) > 0))
    val action = {
        if (encyclopedia) {
            ui.go("species", id)
        } else if (accessory) {
            ui.act("accessory:$id:$target")
        } else if (inventory) {
            if (seed) {
                ui.act("plant:$id:${ui.plantingSelection.plotKey}")
            } else if (item.b("permanent")) {
                Unit
            } else {
                ui.show("fertilizer-help")
            }
        } else {
            ui.show("purchase", id)
        }
    }
    BioCard(modifier, padding = AppSpacing.VALUE_12, onClick = onProductClick ?: action) {
        val backdrop =
            Brush.linearGradient(
                if (seed &&
                    (!encyclopedia || known)
                ) {
                    listOf(AppColors.SwatchFFFAFAF8, bp.soft)
                } else {
                    listOf(bp.soft, bp.soft)
                },
            )
        Box(
            Modifier
                .fillMaxWidth()
                .height(
                    if (LocalConfiguration.current.screenWidthDp <
                        AppThemeEffects.COMPACT_WIDTH
                    ) {
                        AppDimensions.Dp95
                    } else {
                        AppDimensions.Dp112
                    },
                ).background(backdrop, bioShape(AppRadii.VALUE_15)),
            contentAlignment = Alignment.Center,
        ) {
            if (seed &&
                (!encyclopedia || known)
            ) {
                BioPlant(id, Modifier.fillMaxSize())
            } else {
                Bi(
                    if (seed) {
                        "leaf"
                    } else if (accessory) {
                        accessoryIcon(id)
                    } else if (item.b("permanent")) {
                        "water"
                    } else {
                        "fertilizer"
                    },
                    size = if (accessory)AppIcons.SIZE_32 else AppIcons.PRODUCT_ARTWORK_SIZE,
                )
            }
        }
        Spacer(Modifier.height(AppDimensions.Dp10))
        Bt(
            item.s("name"),
            Modifier.heightIn(min = AppDimensions.Dp36),
            size = AppTypography.SIZE_13,
            line = AppTypography.LINE_HEIGHT_18_85,
            semi = true,
        )
        Spacer(Modifier.height(AppDimensions.Dp10))
        if (encyclopedia) {
            Bt(
                catalogStatus ?: world.record(id).s("status", stringResource(R.string.copy_chua_kham_pha_c0b0cb)),
                size = AppTypography.SIZE_11,
                tint = bp.muted,
                line = AppTypography.LINE_HEIGHT_15_4,
            )
        } else if (accessory) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Bt(
                    if (equipped) {
                        stringResource(R.string.copy_dang_dung_275ca2)
                    } else if (owned) {
                        stringResource(R.string.copy_da_so_huu_822625)
                    } else {
                        stringResource(R.string.copy_0_xu_251e68, item.i("price"))
                    },
                    Modifier.weight(1f),
                    size = AppTypography.SIZE_13,
                    tint = bp.deep,
                )
                ;Box(
                    Modifier.size(AppDimensions.Dp27).border(AppDimensions.Dp1, bp.line, RoundedCornerShape(AppDimensions.Dp8)),
                    contentAlignment = Alignment.Center,
                ) {
                    Bi(
                        if (equipped) {
                            "check"
                        } else if (owned) {
                            "next"
                        } else {
                            "plus"
                        },
                        size = AppIcons.SIZE_14,
                    )
                }
            }
        } else {
            Bt(
                if (inventory) {
                    if (item.b("permanent")) {
                        if (equippedCan ==
                            id
                        ) {
                            stringResource(R.string.copy_dang_dung_275ca2)
                        } else {
                            stringResource(R.string.copy_trang_bi_9e8e12)
                        }
                    } else {
                        "×$quantity"
                    }
                } else if (item.b("permanent") &&
                    quantity > 0
                ) {
                    if (equippedCan ==
                        id
                    ) {
                        stringResource(R.string.copy_dang_dung_275ca2)
                    } else {
                        stringResource(R.string.copy_da_so_huu_822625)
                    }
                } else {
                    stringResource(R.string.copy_0_xu_251e68, item.i("price"))
                },
                size = AppTypography.SIZE_13,
                tint = bp.deep,
            )
            if (seed &&
                !inventory
            ) {
                Bt(bioTier(item.s("tier")), size = AppTypography.SIZE_12, line = AppTypography.LINE_HEIGHT_17, tint = bp.muted)
            }
        }
    }
}

private fun catalogProducts(
    world: BioWorld,
    tab: String,
): List<JSONObject> =
    when (tab) {
        "care" -> world.products
        "accessories" -> world.accessories
        else -> world.species
    }

internal fun inventoryRouteContent(
    world: BioWorld,
    ui: BioController,
    live: LiveInventory? = null,
    accessoryCatalog: AccessorySnapshot? = null,
): InventoryRouteContent {
    val tab = live?.state?.tab ?: ui.shopSelection.inventoryTab
    val items =
        catalogProducts(world, tab).filter {
            live?.state?.contains(it.s("id")) ?: (world.inventoryCount(it.s("id"), accessory = tab == "accessories") > 0)
        }
    val openShop = {
        if (live == null) {
            ui.shopSelection.shopTab = tab
            ui.go("shop")
        } else {
            live.openShop()
        }
    }
    return InventoryRouteContent(
        tab = tab,
        itemCount = items.size,
        onTab = { if (live == null) ui.shopSelection.inventoryTab = it else live.selectTab(it) },
        onShop = openShop,
        onBuy = openShop,
        item = { index, modifier ->
            val item = items[index]
            val target =
                (
                    if (live ==
                        null
                    ) {
                        ui.shopSelection.targetTree
                    } else {
                        ui.accessoryTargetArgument
                    }
                ).ifBlank { ui.homeTreeState.selectedId }
            BioProduct(
                world,
                ui,
                item,
                modifier,
                inventory = true,
                target = target,
                ownedQuantity = live?.state?.count(item.s("id")),
                equippedCan = live?.state?.equippedCan ?: world.state.s("equipped"),
                accessoryCatalog = accessoryCatalog,
                onProductClick =
                    live?.let { binding ->
                        { binding.use(item.s("id"), target, item.b("permanent")) }
                    },
            )
        },
    )
}

internal fun shopRouteContent(
    world: BioWorld,
    ui: BioController,
    argument: String,
    live: LiveShop? = null,
    accessoryCatalog: AccessorySnapshot? = null,
): ShopRouteContent {
    val tab = live?.state?.tab ?: ui.shopSelection.shopTab
    val items = catalogProducts(world, tab)
    val selected =
        if (live == null) {
            world.tree(argument)?.let { ShopTreeContext(it.s("id"), it.s("name"), it.s("species")) }
        } else {
            accessoryCatalog
                ?.trees
                ?.firstOrNull { it.id == argument }
                ?.let { ShopTreeContext(it.id, it.name, it.speciesId) }
        }
    return ShopRouteContent(
        tab = tab,
        shoppingTree = selected,
        itemCount = items.size,
        onTab = {
            if (live == null) ui.shopSelection.shopTab = it else live.selectTab(it)
        },
        onInventory = { ui.go("inventory") },
        onTree = { ui.go("tree", it) },
        onShop = { ui.go("shop") },
        item = { index, modifier ->
            val item = items[index]
            val target = if (live == null) ui.shopSelection.targetTree else argument.ifBlank { ui.accessoryTargetArgument }
            val wallet = live?.state?.wallet
            BioProduct(
                world,
                ui,
                item,
                modifier,
                target = target.ifBlank { ui.homeTreeState.selectedId },
                ownedQuantity = if (wallet == null || tab == "accessories") null else wallet.inventory[item.s("id")] ?: 0,
                equippedCan = wallet?.equippedCan ?: world.state.s("equipped"),
                accessoryCatalog = accessoryCatalog,
            )
        },
    )
}

@Composable internal fun ColumnScope.BioMore(ui: BioController) {
    BioIntro(stringResource(R.string.copy_them_mot_chut_006e8b), stringResource(R.string.copy_theo_nhip_cua_ban_5a2ada))
    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp12)) {
        listOf(
            Triple("focus", "clock", stringResource(R.string.copy_tap_trung_964002)),
            Triple("templates", "plus", stringResource(R.string.copy_viec_mau_535dc1)),
            Triple("review", "calendar", stringResource(R.string.copy_nhin_lai_tuan_ff3f0c)),
            Triple("achievements", "award", stringResource(R.string.copy_dau_moc_dffd66)),
            Triple("timeline", "book", stringResource(R.string.copy_ky_niem_e3e113)),
            Triple("insights", "chart", stringResource(R.string.copy_nhip_thoi_quen_1b8324)),
            Triple("encyclopedia", "book", stringResource(R.string.copy_bach_thao_f2ed64)),
            Triple("settings", "settings", stringResource(R.string.copy_tuy_chon_nhac_viec_a44d85)),
            Triple("data", "download", stringResource(R.string.copy_du_lieu_ung_dung_9843b7)),
        ).forEach { (r, i, t) ->
            BioLink(t, i, {
                ui.go(r)
            })
        }
    }
}

@Preview(name = "Shop products", widthDp = 335, showBackground = true)
@Composable
private fun BioProductPreview() {
    val context = LocalContext.current
    val world = remember(context) { BioWorld.reference(context) }
    val ui = remember { BioController() }
    BioTheme(world, "1") {
        Column {
            BioReward(world, ui)
            BioProduct(world, ui, world.products.first())
            BioTaskRow(world.tasks.first(), ui)
        }
    }
}

@Preview(name = "More actions", widthDp = 335, heightDp = 720)
@Composable
private fun BioMoreComponentPreview() {
    BioReferenceSurface("more")
}
