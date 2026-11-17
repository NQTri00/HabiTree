package com.mamxanh.app.feature.tree.presentation.components

import com.mamxanh.app.presentation.ui.design.AppColors
import com.mamxanh.app.presentation.ui.design.AppDimensions
import com.mamxanh.app.presentation.ui.design.AppOpacity
import com.mamxanh.app.presentation.ui.design.AppTypography

import com.mamxanh.shared.localization.TextKey

import com.mamxanh.shared.localization.AppText

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamxanh.app.feature.tree.domain.model.Tree
import com.mamxanh.app.presentation.ui.*
import com.mamxanh.app.presentation.ui.components.*

@Composable
fun TreePortrait(tree: Tree, modifier: Modifier = Modifier, sharedTreeModifier: Modifier = Modifier) {
    GardenPlantPortrait(tree.speciesId, tree.decoration, modifier.then(sharedTreeModifier).fillMaxWidth().height(AppDimensions.Dp260), tree.growth)
}

@Composable
fun TreeCareActions(wateredToday: Boolean, fertilizerCount: Int, canWater: Boolean,
    onWater: () -> Unit, onFertilize: () -> Unit,
    onGetWater: (() -> Unit)? = null, onBuyFertilizer: (() -> Unit)? = null, showSupplyLinks: Boolean = true) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp8)) {
        Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp10)) {
            CareTile(GardenArt.WATERING_CAN, if(wateredToday) AppText.text(TextKey.CopyDaTuoiD97f10) else AppText.text(TextKey.CopyTuoiCay0990f6),
                canWater, onWater, Modifier.weight(1f).fillMaxHeight())
            CareTile(GardenArt.FERTILIZER, AppText.text(TextKey.CopyBon1TuiB30d99), fertilizerCount > 0, onFertilize, Modifier.weight(1f).fillMaxHeight())
        }
        if(showSupplyLinks && fertilizerCount <= 0 && onBuyFertilizer != null) GardenPaintedButton(AppText.text(TextKey.CopyHetPhanGheTiemHat6c1c94), onBuyFertilizer, Modifier.fillMaxWidth(), primary = false)
    }
}

@Composable
private fun CareTile(art: GardenArt, label: String, enabled: Boolean, onClick: () -> Unit, modifier: Modifier) {
    GardenBotanicalFrame(modifier.clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentPadding = PaddingValues(AppDimensions.Dp10), skin = if(label.startsWith("✓")) GardenPanelSkin.HINT else GardenPanelSkin.BADGE) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            GardenArtwork(art, Modifier.size(AppDimensions.Dp44).alpha(if(enabled || label.startsWith("✓")) AppOpacity.Value1 else AppOpacity.Value_35))
            Text(label, color = if(label.startsWith("✓")) Green else if(enabled) Deep else Muted, fontWeight = FontWeight.Bold, fontSize = AppTypography.Sp15, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun DecorationPicker(selected: String, onSelect: (String) -> Unit) {
    val choices=listOf(Triple("NONE",AppText.text(TextKey.CopyTuNhien0bebae),GardenArt.NATURAL),Triple("FLOWERS","Hoa",GardenArt.FLOWERS),
        Triple("LANTERNS",AppText.text(TextKey.CopyDenBce8d2),GardenArt.LANTERN),Triple("RIBBON",AppText.text(TextKey.CopyNoA00232),GardenArt.RIBBON))
    Column(Modifier.selectableGroup(),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp6)) {
        choices.chunked(2).forEach { row ->
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp6)) {
                row.forEach { (code,label,art) ->
                    val active=selected==code
                    androidx.compose.material3.Surface(Modifier.weight(1f).heightIn(min=AppDimensions.Dp48)
                        .selectable(active,role=Role.RadioButton,onClick={onSelect(code)}),
                        shape=androidx.compose.foundation.shape.RoundedCornerShape(AppDimensions.Dp10),
                        color=if(active)AppColors.SwatchFFE3F2E8 else AppColors.SwatchFFF0F6F2,
                        border=androidx.compose.foundation.BorderStroke(AppDimensions.Dp1,if(active)Green else AppColors.SwatchFFE0EAE4)) {
                        Row(Modifier.padding(horizontal=AppDimensions.Dp8,vertical=AppDimensions.Dp6),verticalAlignment=Alignment.CenterVertically,
                            horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp6)) {
                            GardenArtwork(art,Modifier.size(AppDimensions.Dp22))
                            Text(label,Modifier.weight(1f),color=Deep,fontSize=AppTypography.Sp11,lineHeight=AppTypography.Sp14,
                                fontWeight=if(active)FontWeight.Bold else FontWeight.Medium)
                            Text(if(active)"●" else "○",color=if(active)Green else Muted,fontSize=AppTypography.Sp12)
                        }
                    }
                }
            }
        }
    }
}

@Preview(widthDp = 340, heightDp = 130, device = "spec:width=340dp,height=130dp,dpi=160")
@Composable
fun WateredCareActionsPreview() { MamXanhPreviewTheme { TreeCareActions(true, 2, false, {}, {}) } }
@Composable
fun TreeCareActionsPreview() { MamXanhPreviewTheme { TreeCareActions(false, 2, true, {}, {}) } }
@Preview(name = "Decoration choices · complete content", widthDp = 336)
@Composable
fun DecorationPickerPreview() { MamXanhPreviewTheme { DecorationPicker("FLOWERS", {}) } }
