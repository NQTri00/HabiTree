package com.mamxanh.app.feature.garden.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.PathEffect
import com.mamxanh.app.presentation.ui.design.*
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

data class GardenDisplayTree(
    val id: String,
    val species: String,
    val name: String,
    val progress: Int,
    val stageLabel: String,
    val dead: Boolean,
    val watered: Boolean,
)

data class GardenDisplayPlot(val number: Int, val tree: GardenDisplayTree? = null, val price: Int? = null)

data class GardenDisplayState(
    val filter: String,
    val treeCount: Int,
    val emptyPlots: Int,
    val speciesCount: Int,
    val canWater: Boolean,
    val plots: List<GardenDisplayPlot>,
)

sealed interface GardenAction {
    data object Inventory : GardenAction
    data object WaterAll : GardenAction
    data object PickSeed : GardenAction
    data object Encyclopedia : GardenAction
    data class Filter(val value: String) : GardenAction
    data class Plot(val value: GardenDisplayPlot) : GardenAction
}

@Composable
fun BioGardenScreen(state: GardenDisplayState, onAction: (GardenAction) -> Unit) {
    BioIntro(AppText.text(TextKey.CopyVuonCuaBanAd1c1b),AppText.text(TextKey.CopyTungMamDangLon4a30e9),AppText.text(TextKey.Copy0Cay1OTrong964022,state.treeCount,state.emptyPlots),{BioIconButton("bag",{onAction(GardenAction.Inventory)})})
    Row(horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp10)){BioButton(AppText.text(TextKey.CopyTuoiTatCa3d814a),{onAction(GardenAction.WaterAll)},Modifier.weight(1f),"water",enabled=state.canWater);BioButton(AppText.text(TextKey.CopyGieoCay31adde),{onAction(GardenAction.PickSeed)},Modifier.weight(1f),"plus",true)}
    val filter = state.filter
    BioChips(listOf("all" to AppText.text(TextKey.CopyTatCa49c73a),"thirsty" to AppText.text(TextKey.CopyCanTuoiA0957f),"growing" to AppText.text(TextKey.CopyDangLon40daab),"mature" to AppText.text(TextKey.CopyTruongThanh8c14d7)),filter){onAction(GardenAction.Filter(it))}
    val plots = state.plots
    val plotLine=bp.line
    BioGrid(plots.size) { index, modifier ->
        val entry = plots[index]
        val plot = entry.number
        val t = entry.tree
        val locked = entry.price != null
        BioCard(modifier, padding = AppSpacing.Value12, onClick = { onAction(GardenAction.Plot(entry)) }) {
            Box(Modifier.fillMaxWidth().height(if(t==null&&!locked)AppDimensions.Dp110 else AppDimensions.Dp105).background(if(locked)AppColors.Transparent else bp.soft,bioShape(AppRadii.Value14)),contentAlignment=Alignment.Center){
                if(t!=null)BioPlant(t.species,Modifier.fillMaxSize(),t.dead)else Bi("plus",size=AppIcons.Size28)
                if(!locked)Bt(String.format("%02d",plot),Modifier.align(Alignment.TopStart).padding(AppDimensions.Dp6),size=AppTypography.Size10,tint=bp.muted,mono=true)
                if(t==null&&!locked)Canvas(Modifier.fillMaxSize()){drawRoundRect(plotLine,cornerRadius=androidx.compose.ui.geometry.CornerRadius(AppDimensions.Dp14.toPx()),style=androidx.compose.ui.graphics.drawscope.Stroke(AppDimensions.Dp1.toPx(),pathEffect=PathEffect.dashPathEffect(floatArrayOf(AppDimensions.Dp3.toPx(),AppDimensions.Dp3.toPx()))))}
            }
            Spacer(Modifier.height(AppDimensions.Dp25));Bt(t?.name?:if(locked)AppText.text(TextKey.CopyThemOTrong9979ac) else AppText.text(TextKey.CopyGieoCay31adde),size=AppTypography.Size13,semi=true)
            if(t!=null){Spacer(Modifier.height(AppDimensions.Dp6));BioProgress(t.progress/100f);Bt("${t.progress}% · ${t.stageLabel}",size=AppTypography.Size10,tint=bp.muted);Spacer(Modifier.height(AppDimensions.Dp16));Bt(if(t.dead)AppText.text(TextKey.CopyKhoHeoBf546a)else if(t.watered)AppText.text(TextKey.CopyDaTuoiDe2042)else AppText.text(TextKey.CopyCanTuoiA0957f),size=AppTypography.Size10,tint=bp.muted)}else Bt(if(locked)AppText.text(TextKey.Copy0Xu251e68,entry.price ?: 0)else AppText.text(TextKey.CopyMotKhoiDauMoi280995),size=AppTypography.Size10,tint=bp.muted)
        }
    }
    if(plots.isEmpty())BioEmpty(AppText.text(TextKey.CopyChuaCoCayODay1f9b1f),AppText.text(TextKey.CopyChonBoLocKhacDeXem6bf49e))
    BioLink(AppText.text(TextKey.CopyBachThaoF2ed64),"book",{onAction(GardenAction.Encyclopedia)},AppText.text(TextKey.Copy0LoaiCay921e87,state.speciesCount))
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun BioGardenScreenPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp18)) {
        BioGardenScreen(GardenDisplayState("all", 0, 2, 6, false, listOf(GardenDisplayPlot(1), GardenDisplayPlot(2))), {})
    }
}
