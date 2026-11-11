package com.mamxanh.app.feature.tree.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
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
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        listOf(Triple("NONE", "Tự nhiên", GardenArt.NATURAL), Triple("FLOWERS", "Hoa", GardenArt.FLOWERS),
            Triple("LANTERNS", "Đèn", GardenArt.LANTERN), Triple("RIBBON", "Nơ", GardenArt.RIBBON)).forEach { (code, label, art) ->
            GardenBotanicalFrame(Modifier.fillMaxWidth().heightIn(min = 48.dp).selectable(selected == code, role = Role.RadioButton, onClick = { onSelect(code) })) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    GardenArtwork(art, Modifier.size(20.dp))
                    Text(label, Modifier.weight(1f), color = Deep, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    if(selected == code) Text("✓", color = Green, fontSize = 16.sp)
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
@Composable
fun DecorationPickerPreview() { MamXanhPreviewTheme { DecorationPicker("FLOWERS", {}) } }
