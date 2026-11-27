package com.mamxanh.app.presentation.bio

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mamxanh.app.core.components.design.*
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppTypography
import com.mamxanh.app.feature.home.presentation.ui.home.HomeAction
import com.mamxanh.app.feature.home.presentation.ui.home.HomeViewModel
import com.mamxanh.core.ui.R
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.model.catalog
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun BioTreePickerBody(
    choices: List<HomeTreeChoice>,
    selectedId: String,
    loading: Boolean,
    onPlant: () -> Unit,
    onSelect: (String) -> Unit,
) {
    Bt(
        stringResource(R.string.copy_chon_cay_sac_mau_se_di_cung_ban_58b284),
        Modifier.padding(top = AppDimensions.Dp8),
        size = AppTypography.SIZE_12,
        tint = bp.muted,
    )
    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp9)) {
        choices.forEach { tree ->
            BioChoice(
                tree.name,
                tree.palette,
                tree.species,
                selectedId == tree.id,
                { onSelect(tree.id) },
            )
        }
    }
    if (!loading && choices.isEmpty()) {
        BioButton(stringResource(R.string.copy_gieo_cay_dau_tien_afd3c1), onPlant, Modifier.fillMaxWidth(), "plus")
    }
}

internal data class HomeTreeChoice(
    val id: String,
    val name: String,
    val species: String,
    val palette: String,
)

internal fun BioWorld.homeTreeChoice(
    id: String,
    name: String,
    species: String,
) = HomeTreeChoice(id, name, species, data.o("palettes").o(species).s("name"))

internal fun BioWorld.previewHomeTreeChoices() = trees.map { homeTreeChoice(it.s("id"), it.s("name"), it.s("species")) }

@Composable
internal fun BioSeedPickerBody(
    world: BioWorld,
    ui: BioController,
    planting: LivePlanting? = null,
) {
    fun count(seed: String) = if (world.native) planting?.count(seed) ?: 0 else world.count(seed)
    val seeds =
        if (!world.native) {
            world.species.map { it.s("id") to it.s("name") }
        } else {
            catalog.map { it.id to it.name }
        }
    val available = seeds.filter { (id, _) -> count(id) > 0 }
    Bt(stringResource(R.string.copy_hat_dang_co_chon_de_gieo_237689), size = AppTypography.SIZE_12, tint = bp.muted)
    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp9)) {
        available.forEach { (id, name) ->
            BioChoice(
                name,
                stringResource(R.string.copy_con_0_hat_149c5e, count(id)),
                id,
                false,
                { ui.show("plant", id) },
                "plus",
            )
        }
    }
    if (available.isEmpty()) {
        Bt(
            stringResource(R.string.copy_chua_co_hat_trong_tui_095661),
            size = AppTypography.SIZE_12,
            tint = bp.muted,
        )
    }
    BioButton(stringResource(R.string.copy_chon_hat_o_tiem_a4c334), {
        ui.requestShopTab("seeds")
        ;ui.go("shop")
    }, Modifier.fillMaxWidth(), "shop", true)
}

@Composable
internal fun BioPlantBody(
    id: String,
    seedName: String,
    full: Boolean,
    stock: Int,
    plantName: String,
    selectedPlot: String,
    eligiblePlots: List<Int>,
    onName: (String) -> Unit,
    onPlot: (String) -> Unit,
    onPlant: () -> Unit,
    canPlant: Boolean,
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
                Bt(seedName, size = AppTypography.SIZE_17, bold = true)
                Bt(
                    stringResource(R.string.copy_trong_tui_0_hat_d56e0e, stock),
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
            eligiblePlots.map {
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
        BioButton(stringResource(R.string.copy_gieo_hat_6d82a9), onPlant, Modifier.fillMaxWidth(), enabled = canPlant)
    }
}

@Composable
internal fun BioBoundTreePicker(
    world: BioWorld,
    ui: BioController,
    treeHomeSelection: HomeViewModel?,
) {
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
        if (home == null) ui.selectPreviewTree(id) else home.dispatch(HomeAction.SelectTree(id))
    }
}

@Composable
internal fun BioBoundPlantDialog(
    world: BioWorld,
    ui: BioController,
    id: String,
    seedName: String,
    full: Boolean,
    plantStock: Int,
    planting: LivePlanting?,
) {
    var plantName by rememberSaveable(id) { mutableStateOf("") }
    var selectedPlot by rememberSaveable(id) {
        mutableStateOf(if (world.native) "" else ui.preview.plantingSelection.initialDialogPlot(world.emptyPlots()))
    }
    BioPlantBody(
        id,
        seedName,
        full,
        plantStock,
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
        canPlant = plantStock > 0 && !ui.busy && (planting?.canPlant ?: true),
    )
}

@Preview(name = "Tree picker content", widthDp = 335, showBackground = true)
@Composable
private fun BioTreePickerBodyPreview() {
    val context = LocalContext.current
    val world = remember(context) { BioWorld.reference(context) }
    val choices = world.previewHomeTreeChoices()
    BioTheme(world, "1") {
        Column {
            BioTreePickerBody(choices, choices.firstOrNull()?.id.orEmpty(), loading = false, onPlant = {}) {}
        }
    }
}
