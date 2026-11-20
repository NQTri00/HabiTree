package com.mamxanh.app.feature.catalog.presentation.ui.species

import com.mamxanh.app.feature.catalog.presentation.model.SpeciesDisplay

import androidx.compose.ui.res.stringResource
import com.mamxanh.core.ui.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.design.*

@Composable
fun SpeciesScreen(state: SpeciesDisplay, onTree: (String) -> Unit, onShop: () -> Unit) {
    BioCard(Modifier.fillMaxWidth(),bp.soft,padding=AppSpacing.Value18,radius=AppRadii.Value26){Column(Modifier.fillMaxWidth(),horizontalAlignment=Alignment.CenterHorizontally){BioTag(state.tier+" · "+state.status);if(state.discovered){Spacer(Modifier.height(AppDimensions.Dp2));BioPlant(state.id,Modifier.fillMaxWidth().height(AppDimensions.Dp220))}else Box(Modifier.fillMaxWidth().height(AppDimensions.Dp140),contentAlignment=Alignment.Center){Bi("leaf",size=AppIcons.Size36)};Bt(state.name,size=AppTypography.Size27,bold=true,spacing=AppTypography.TrackingMinus1,align=TextAlign.Center);Bt(state.description,size=AppTypography.Size12,tint=bp.muted,align=TextAlign.Center)}}
    BioSection(stringResource(R.string.copy_cac_giai_doan_0_1_bee217, state.highest,state.stages))
    BioGrid(state.stages, if (LocalDensity.current.fontScale > AppThemeEffects.LargeFontScale) 2 else 3) { index, modifier ->
        val reached = index < state.highest
        val status = stringResource(if (reached) R.string.copy_da_dat_1b9e88 else R.string.copy_chua_dat_f4aa8f)
        Column(
            modifier
                .background(if (reached) bp.soft else bp.surface.copy(alpha = AppOpacity.Value_3), RoundedCornerShape(AppDimensions.Dp12))
                .semantics(mergeDescendants = true) { stateDescription = status }
                .padding(vertical = AppDimensions.Dp8),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp5),
        ) {
            Box(Modifier.fillMaxWidth().height(AppDimensions.Dp40), contentAlignment = Alignment.Center) {
                if (reached) {
                    BioPlant(state.id, Modifier.fillMaxSize())
                } else {
                    Bi("lock", size = AppIcons.Size18, tint = bp.muted)
                }
            }
            Bt("${index + 1}/${state.stages}", size = AppTypography.Size11, mono = true, tint = if (reached) bp.deep else bp.muted)
        }
    }
    BioCard(Modifier.fillMaxWidth()){BioFacts(listOf(stringResource(R.string.copy_da_trong_42fa2b) to state.planted.toString(),stringResource(R.string.copy_trong_tui_905deb) to state.owned.toString(),stringResource(R.string.copy_dang_song_e3573d) to state.living.toString(),stringResource(R.string.copy_trong_dau_tien_2e1aed) to bioDisplayDate(state.firstPlanted),stringResource(R.string.copy_truong_thanh_dau_dd677a) to bioDisplayDate(state.firstMatured),stringResource(R.string.copy_cay_cao_nhat_dang_song_7d778f) to (state.highestTree?.let { "$it/${state.stages}" } ?: stringResource(R.string.copy_chua_co_bc2b97))))}
    BioDetails(stringResource(R.string.copy_cham_soc_loai_f520bc)){Bt(stringResource(R.string.copy_binh_tuoi_8_12_18_24_gio_bon_phan_khi_cay_dang_lon_v_cf24d9),size=AppTypography.Size12,tint=bp.muted)}
    state.trees.forEach { BioLink(it.name, "garden", { onTree(it.id) }) }
    BioButton(stringResource(R.string.copy_tim_hat_a56320),onShop,Modifier.fillMaxWidth(),"shop")
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun SpeciesScreenPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp18)) {
        SpeciesScreen(
            SpeciesDisplay("cherry", "Spring cherry", "", "Rare", "Undiscovered", false, 0, 9, 0, 0, 0, "", "", null, emptyList()),
            {},
            {},
        )
    }
}
