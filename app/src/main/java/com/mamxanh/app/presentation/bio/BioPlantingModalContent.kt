package com.mamxanh.app.presentation.bio

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.mamxanh.app.core.components.design.*
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppTypography
import com.mamxanh.core.ui.R
import org.json.JSONObject

@Composable
internal fun BioTreePickerBody(
    world: BioWorld,
    ui: BioController,
) {
    Bt(
        stringResource(R.string.copy_chon_cay_sac_mau_se_di_cung_ban_58b284),
        size = AppTypography.SIZE_12,
        tint = bp.muted,
    )
    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp9)) {
        world.trees.forEach { t ->
            BioChoice(
                t.s("name"),
                world.data
                    .o("palettes")
                    .o(t.s("species"))
                    .s("name"),
                t.s("species"),
                ui.homeTreeState.selectedId == t.s("id"),
                { ui.act("bio-pin:" + t.s("id")) },
            )
        }
    }
    if (world.trees.isEmpty()) {
        BioButton(stringResource(R.string.copy_gieo_cay_dau_tien_afd3c1), {
            ui.show("seed-picker")
        }, Modifier.fillMaxWidth(), "plus")
    }
}

@Composable
internal fun BioSeedPickerBody(
    world: BioWorld,
    ui: BioController,
) {
    Bt(stringResource(R.string.copy_hat_dang_co_chon_de_gieo_237689), size = AppTypography.SIZE_12, tint = bp.muted)
    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp9)) {
        world.species
            .filter {
                world.count(it.s("id")) >
                    0
            }.forEach { s ->
                BioChoice(
                    s.s(
                        "name",
                    ),
                    stringResource(R.string.copy_con_0_hat_149c5e, world.count(s.s("id"))),
                    s.s("id"),
                    false,
                    {
                        ui.show("plant", s.s("id"))
                    },
                    "plus",
                )
            }
    }
    if (world.species.none {
            world.count(it.s("id")) >
                0
        }
    ) {
        Bt(
            stringResource(R.string.copy_chua_co_hat_trong_tui_095661),
            size = AppTypography.SIZE_12,
            tint = bp.muted,
        )
    }
    BioButton(stringResource(R.string.copy_chon_hat_o_tiem_a4c334), {
        ui.shopSelection.shopTab =
            "seeds"
        ;ui.go("shop")
    }, Modifier.fillMaxWidth(), "shop", true)
}

@Composable
internal fun BioPlantBody(
    id: String,
    world: BioWorld,
    ui: BioController,
    item: JSONObject,
    full: Boolean,
    plantName: String,
    selectedPlot: String,
    onName: (String) -> Unit,
    onPlot: (String) -> Unit,
) {
    if (full) {
        Bt(
            stringResource(R.string.copy_hat_van_o_trong_tui_mo_them_o_de_gieo_17fb08),
            size = AppTypography.SIZE_12,
            tint = bp.muted,
        )
    } else {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp16),
        ) {
            BioPlant(id, Modifier.size(AppDimensions.Dp85))
            Column {
                Bt(item.s("name"), size = AppTypography.SIZE_17, bold = true)
                Bt(
                    stringResource(R.string.copy_trong_tui_0_hat_d56e0e, world.count(id)),
                    size = AppTypography.SIZE_12,
                    tint = bp.muted,
                )
            }
        }
        BioField(stringResource(R.string.copy_ten_cay_tuy_chon_1af251), plantName, {
            onName(it)
        }, placeholder = stringResource(R.string.copy_mam_binh_yen_df7062))
        BioSelect(
            stringResource(R.string.copy_o_trong_5da6bd),
            world.emptyPlots().map {
                it.toString() to
                    stringResource(R.string.copy_o_5b7240, it.toString().padStart(2, '0'))
            },
            selectedPlot,
            onSelect = onPlot,
        )
        Bt(
            stringResource(R.string.copy_dung_1_hat_de_trong_ten_de_dung_ten_loai_61bbff),
            size = AppTypography.SIZE_12,
            tint = bp.muted,
        )
        BioButton(stringResource(R.string.copy_gieo_hat_6d82a9), {
            ui.send(
                "plant",
                mapOf(
                    "id" to id,
                    "plot" to selectedPlot,
                    "name" to plantName,
                ),
            )
        }, Modifier.fillMaxWidth(), enabled = world.count(id) > 0 && !ui.busy)
    }
}

@Preview(name = "Tree picker content", widthDp = 335, showBackground = true)
@Composable
private fun BioTreePickerBodyPreview() {
    val context = LocalContext.current
    val world = remember(context) { BioWorld.reference(context) }
    BioTheme(world, "1") {
        Column {
            BioTreePickerBody(world, remember { BioController("home") })
        }
    }
}
