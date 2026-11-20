package com.mamxanh.app.feature.garden.presentation.ui.garden
import com.mamxanh.app.feature.garden.presentation.ui.livinggarden.Encyclopedia

import androidx.compose.ui.res.stringResource
import com.mamxanh.core.ui.R
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.PathEffect
import com.mamxanh.app.core.components.design.*

@Composable
fun BioGardenScreen(state: GardenDisplayState, onAction: (GardenAction) -> Unit) {
    BioIntro(stringResource(R.string.copy_vuon_cua_ban_ad1c1b),stringResource(R.string.copy_tung_mam_dang_lon_4a30e9),stringResource(R.string.copy_0_cay_1_o_trong_964022, state.treeCount, state.emptyPlots),{BioIconButton("bag",{onAction(GardenAction.Inventory)})})
    Row(horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp10)){BioButton(stringResource(R.string.copy_tuoi_tat_ca_3d814a),{onAction(GardenAction.WaterAll)},Modifier.weight(1f),"water",enabled=state.canWater);BioButton(stringResource(R.string.copy_gieo_cay_31adde),{onAction(GardenAction.PickSeed)},Modifier.weight(1f),"plus",true)}
    val filter = state.filter
    BioChips(listOf("all" to stringResource(R.string.copy_tat_ca_49c73a),"thirsty" to stringResource(R.string.copy_can_tuoi_a0957f),"growing" to stringResource(R.string.copy_dang_lon_40daab),"mature" to stringResource(R.string.copy_truong_thanh_8c14d7)),filter){onAction(GardenAction.Filter(it))}
    val plots = state.plots
    val plotLine=bp.line
    BioGrid(plots.size) { index, modifier ->
        val entry = plots[index]
        val plot = entry.number
        val t = entry.tree
        val locked = entry.price != null
        val emptyPlot = t == null && !locked
        BioCard(
            modifier,
            tint = if (emptyPlot) AppColors.White.copy(alpha = AppOpacity.Value_5) else bp.surface,
            padding = AppSpacing.Value12,
            onClick = { onAction(GardenAction.Plot(entry)) },
        ) {
            if (emptyPlot) {
                Bt(String.format("%02d", plot), Modifier.padding(start = AppDimensions.Dp6), size = AppTypography.Size10, tint = bp.muted, mono = true)
                Spacer(Modifier.height(AppDimensions.Dp6))
            }
            Box(Modifier.fillMaxWidth().height(if(emptyPlot)AppDimensions.Dp110 else AppDimensions.Dp105).background(if(locked)AppColors.Transparent else bp.soft,bioShape(AppRadii.Value14)),contentAlignment=Alignment.Center){
                if(t!=null)BioPlant(t.species,Modifier.fillMaxSize(),t.dead)else Bi("plus",size=AppIcons.Size28)
                if(t!=null)Bt(String.format("%02d",plot),Modifier.align(Alignment.TopStart).padding(AppDimensions.Dp6),size=AppTypography.Size10,tint=bp.muted,mono=true)
                if(t==null&&!locked)Canvas(Modifier.fillMaxSize()){drawRoundRect(plotLine,cornerRadius=androidx.compose.ui.geometry.CornerRadius(AppDimensions.Dp14.toPx()),style=androidx.compose.ui.graphics.drawscope.Stroke(AppDimensions.Dp1.toPx(),pathEffect=PathEffect.dashPathEffect(floatArrayOf(AppDimensions.Dp3.toPx(),AppDimensions.Dp3.toPx()))))}
            }
            Spacer(Modifier.height(AppDimensions.Dp25));Bt(t?.name?:if(locked)stringResource(R.string.copy_them_o_trong_9979ac) else stringResource(R.string.copy_gieo_cay_31adde),size=AppTypography.Size13,semi=true)
            if(t!=null){Spacer(Modifier.height(AppDimensions.Dp6));BioProgress(t.progress/100f);Bt("${t.progress}% · ${t.stageLabel}",size=AppTypography.Size10,tint=bp.muted);Spacer(Modifier.height(AppDimensions.Dp16));Bt(if(t.dead)stringResource(R.string.copy_kho_heo_bf546a)else if(t.watered)stringResource(R.string.copy_da_tuoi_de2042)else stringResource(R.string.copy_can_tuoi_a0957f),size=AppTypography.Size10,tint=bp.muted)}else Bt(if(locked)stringResource(R.string.copy_0_xu_251e68, entry.price ?: 0)else stringResource(R.string.copy_mot_khoi_dau_moi_280995),size=AppTypography.Size10,tint=bp.muted)
        }
    }
    if(plots.isEmpty())BioEmpty(stringResource(R.string.copy_chua_co_cay_o_day_1f9b1f),stringResource(R.string.copy_chon_bo_loc_khac_de_xem_6bf49e))
    BioLink(stringResource(R.string.copy_bach_thao_f2ed64),"book",{onAction(GardenAction.Encyclopedia)},stringResource(R.string.copy_0_loai_cay_921e87, state.speciesCount))
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun BioGardenScreenPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp18)) {
        BioGardenScreen(GardenDisplayState("all", 0, 2, 6, false, listOf(GardenDisplayPlot(1), GardenDisplayPlot(2))), {})
    }
}
