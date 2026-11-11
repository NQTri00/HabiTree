package com.mamxanh.app.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamxanh.app.presentation.ui.Deep
import com.mamxanh.app.presentation.ui.Green

/** Shared category tabs for the shop catalog and owned inventory. */
@Composable
fun MintCatalogTabs(selectedIndex: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth().height(IntrinsicSize.Min).selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf("Vật phẩm" to GardenArt.FERTILIZER, "Hạt giống" to GardenArt.SEED)
            .forEachIndexed { index, (label, art) ->
                Surface(Modifier.weight(1f).fillMaxHeight()
                    .mintDepth(RoundedCornerShape(24.dp), if (selectedIndex == index) MintDepth.primary else MintDepth.card,
                        if (selectedIndex == index) 6.dp else 3.dp)
                    .selectable(selectedIndex == index, role = Role.Tab, onClick = { onSelect(index) }),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.Transparent) {
                    Row(Modifier.heightIn(min = 48.dp).padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically) {
                        GardenArtwork(art, Modifier.size(28.dp))
                        Text(label, color = if (selectedIndex == index) Color.White else Deep,
                            fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
    }
}
