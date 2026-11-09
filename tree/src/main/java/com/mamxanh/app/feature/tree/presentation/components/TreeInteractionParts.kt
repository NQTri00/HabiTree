package com.mamxanh.app.feature.tree.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import com.mamxanh.app.presentation.ui.components.GardenRichText as Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
    Box(modifier.fillMaxWidth().height(260.dp), contentAlignment = Alignment.Center) {
        GardenPlantPortrait(tree.speciesId, tree.decoration, sharedTreeModifier.width(290.dp).height(260.dp), growth = tree.growth)
    }
}

@Composable
fun TreeCareActions(wateredToday: Boolean, fertilizerCount: Int, wateringCanMl: Int,
    onWater: () -> Unit, onFertilize: () -> Unit,
    onGetWater: (() -> Unit)? = null, onBuyFertilizer: (() -> Unit)? = null) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        GardenWoodenTray {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                GardenCareAction(GardenArt.WATERING_CAN, when {
                    wateredToday -> "Đã tưới"
                    wateringCanMl < 50 -> "Cần nước"
                    else -> "Tưới · 50 ml"
                }, onWater, Modifier.weight(1f), enabled = !wateredToday && wateringCanMl >= 50)
                GardenCareAction(GardenArt.FERTILIZER, "Bón · 1 túi", onFertilize, Modifier.weight(1f), enabled = fertilizerCount > 0)
            }
        }
        GardenBotanicalFrame(contentPadding = PaddingValues(horizontal = 14.dp, vertical = 7.dp), cornerSize = 18.dp) {
            Text("💧 Bình nước $wateringCanMl ml  ·  Phân bón $fertilizerCount", color = Deep, fontSize = 12.sp, lineHeight = 15.sp, fontWeight = FontWeight.Bold)
        }
        if (wateringCanMl < 50 && onGetWater != null) {
            GardenPaintedButton("Thiếu nước · Về sân vườn bơm ›", onGetWater,
                Modifier.fillMaxWidth(), primary = false)
        }
        if (fertilizerCount <= 0 && onBuyFertilizer != null) {
            GardenPaintedButton("Hết phân · Ghé Tiệm hạt ›", onBuyFertilizer,
                Modifier.fillMaxWidth(), primary = false)
        }
    }
}

@Composable
fun DecorationPicker(selected: String, onSelect: (String) -> Unit) {
    GardenWoodenTray(decoration = true) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            listOf(
                Triple("NONE", "Tự nhiên", GardenArt.NATURAL),
                Triple("FLOWERS", "Hoa", GardenArt.FLOWERS),
                Triple("LANTERNS", "Đèn", GardenArt.LANTERN),
                Triple("RIBBON", "Nơ", GardenArt.RIBBON)
            ).forEach { (code, label, art) ->
                Surface(
                    modifier = Modifier.weight(1f).selectable(selected == code, role = Role.RadioButton, onClick = { onSelect(code) }),
                    color = if (selected == code) Color(0xFF805F22).copy(alpha = .6f) else Color.Transparent,
                    shape = RoundedCornerShape(16.dp),
                    border = if (selected == code) BorderStroke(1.5.dp, Color(0xFFFFD474)) else null
                ) {
                    Column(Modifier.padding(vertical = 5.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        GardenArtwork(art, Modifier.fillMaxWidth().height(38.dp))
                        GardenBotanicalFrame(Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 3.dp, vertical = 4.dp), cornerSize = 12.dp) {
                            Text(label, Modifier.fillMaxWidth(), color = Deep, fontSize = 12.sp,
                                lineHeight = 15.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Painted tree care", widthDp = 340)
@Composable
fun TreeCareActionsPreview() { MamXanhPreviewTheme { TreeCareActions(false, 2, 500, {}, {}) } }

@Preview(name = "Painted decoration tray", widthDp = 340)
@Composable
fun DecorationPickerPreview() { MamXanhPreviewTheme { DecorationPicker("FLOWERS", {}) } }
