package com.mamxanh.app.presentation.bio

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
) {
    val id = item.s("id")
    val seed = world.species.any { it.s("id") == id }
    val accessory =
        item.s("category") == "TREE"
    if (accessory && !encyclopedia) {
        val owned =
            if (inventory) {
                world.inventoryCount(id, accessory = true) > 0
            } else {
                world.owned(id) > 0
            }
        val equipped = world.trees.any { it.s("decor") == id }
        BioAccessoryCard(id, item.s("name"), item.i("price"), owned, equipped, modifier) {
            ui.openAccessory(id, target)
        }
        return
    }
    val quantity = world.inventoryCount(id, accessory)
    val known =
        !encyclopedia || world.record(id).b("discovered") || world.record(id).i("planted") > 0 || world.count(id) > 0
    val action = {
        if (encyclopedia) {
            ui.go("species", id)
        } else if (inventory) {
            if (seed) {
                ui.show("plant", id)
            } else if (item.b("permanent")) {
                Unit
            } else {
                ui.show("fertilizer-help")
            }
        } else {
            ui.show("purchase", id)
        }
    }
    val status =
        if (encyclopedia) {
            world.record(id).s("status", stringResource(R.string.copy_chua_kham_pha_c0b0cb))
        } else {
            null
        }
    BioProductCard(
        BioProductDisplay(id, item.s("name"), item.i("price"), seed, item.b("permanent"), item.s("tier")),
        modifier,
        quantity,
        world.state.s("equipped"),
        inventory,
        status,
        known,
        action,
    )
}

internal data class BioProductDisplay(
    val id: String,
    val name: String,
    val price: Int,
    val seed: Boolean = false,
    val permanent: Boolean = false,
    val tier: String = "",
)

@Composable
internal fun BioProductCard(
    item: BioProductDisplay,
    modifier: Modifier,
    quantity: Int,
    equippedCan: String,
    inventory: Boolean = false,
    catalogStatus: String? = null,
    known: Boolean = true,
    onClick: () -> Unit,
) {
    val encyclopedia = catalogStatus != null
    val id = item.id
    val seed = item.seed
    BioCard(modifier, padding = AppSpacing.VALUE_12, onClick = onClick) {
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
                    } else if (item.permanent) {
                        "water"
                    } else {
                        "fertilizer"
                    },
                    size = AppIcons.PRODUCT_ARTWORK_SIZE,
                )
            }
        }
        Spacer(Modifier.height(AppDimensions.Dp10))
        Bt(
            item.name,
            Modifier.heightIn(min = AppDimensions.Dp36),
            size = AppTypography.SIZE_13,
            line = AppTypography.LINE_HEIGHT_18_85,
            semi = true,
        )
        Spacer(Modifier.height(AppDimensions.Dp10))
        if (encyclopedia) {
            Bt(
                catalogStatus.orEmpty(),
                size = AppTypography.SIZE_11,
                tint = bp.muted,
                line = AppTypography.LINE_HEIGHT_15_4,
            )
        } else {
            Bt(
                if (inventory) {
                    if (item.permanent) {
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
                } else if (item.permanent &&
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
                    stringResource(R.string.copy_0_xu_251e68, item.price)
                },
                size = AppTypography.SIZE_13,
                tint = bp.deep,
            )
            if (seed &&
                !inventory
            ) {
                Bt(bioTier(item.tier), size = AppTypography.SIZE_12, line = AppTypography.LINE_HEIGHT_17, tint = bp.muted)
            }
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
