package com.mamxanh.app.feature.tree.presentation.components

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
    GardenPlantPortrait(tree.speciesId, tree.decoration, modifier.then(sharedTreeModifier).fillMaxWidth().height(260.dp), tree.growth)
}

@Composable
fun TreeCareActions(wateredToday: Boolean, fertilizerCount: Int, canWater: Boolean,
    onWater: () -> Unit, onFertilize: () -> Unit,
    onGetWater: (() -> Unit)? = null, onBuyFertilizer: (() -> Unit)? = null, showSupplyLinks: Boolean = true) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            CareTile(GardenArt.WATERING_CAN, if(wateredToday) "✓ Đã tưới" else "Tưới cây",
                canWater, onWater, Modifier.weight(1f).fillMaxHeight())
            CareTile(GardenArt.FERTILIZER, "Bón\n1 túi", fertilizerCount > 0, onFertilize, Modifier.weight(1f).fillMaxHeight())
        }
        if(showSupplyLinks && fertilizerCount <= 0 && onBuyFertilizer != null) GardenPaintedButton("Hết phân · Ghé Tiệm hạt ›", onBuyFertilizer, Modifier.fillMaxWidth(), primary = false)
    }
}

@Composable
private fun CareTile(art: GardenArt, label: String, enabled: Boolean, onClick: () -> Unit, modifier: Modifier) {
    GardenBotanicalFrame(modifier.clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentPadding = PaddingValues(10.dp), skin = if(label.startsWith("✓")) GardenPanelSkin.HINT else GardenPanelSkin.BADGE) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            GardenArtwork(art, Modifier.size(44.dp).alpha(if(enabled || label.startsWith("✓")) 1f else .35f))
            Text(label, color = if(label.startsWith("✓")) Green else if(enabled) Deep else Muted, fontWeight = FontWeight.Bold, fontSize = 15.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun DecorationPicker(selected: String, onSelect: (String) -> Unit) {
    val choices=listOf(Triple("NONE","Tự nhiên",GardenArt.NATURAL),Triple("FLOWERS","Hoa",GardenArt.FLOWERS),
        Triple("LANTERNS","Đèn",GardenArt.LANTERN),Triple("RIBBON","Nơ",GardenArt.RIBBON))
    Column(Modifier.selectableGroup(),verticalArrangement=Arrangement.spacedBy(6.dp)) {
        choices.chunked(2).forEach { row ->
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                row.forEach { (code,label,art) ->
                    val active=selected==code
                    androidx.compose.material3.Surface(Modifier.weight(1f).heightIn(min=48.dp)
                        .selectable(active,role=Role.RadioButton,onClick={onSelect(code)}),
                        shape=androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
                        color=if(active)Color(0xFFE3F2E8) else Color(0xFFF0F6F2),
                        border=androidx.compose.foundation.BorderStroke(1.dp,if(active)Green else Color(0xFFE0EAE4))) {
                        Row(Modifier.padding(horizontal=8.dp,vertical=6.dp),verticalAlignment=Alignment.CenterVertically,
                            horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                            GardenArtwork(art,Modifier.size(22.dp))
                            Text(label,Modifier.weight(1f),color=Deep,fontSize=11.sp,lineHeight=14.sp,
                                fontWeight=if(active)FontWeight.Bold else FontWeight.Medium)
                            Text(if(active)"●" else "○",color=if(active)Green else Muted,fontSize=12.sp)
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
