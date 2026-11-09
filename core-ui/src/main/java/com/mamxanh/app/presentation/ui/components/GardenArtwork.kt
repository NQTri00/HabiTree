package com.mamxanh.app.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamxanh.app.presentation.ui.Deep
import com.mamxanh.core.ui.R

enum class GardenArt {
    WATERING_CAN, FERTILIZER, PUMP, BOOK, GLASS, BOOTS, SPROUT, CHECKLIST, JOURNAL,
    SHOP, TREE, NATURAL, FLOWERS, LANTERN, RIBBON, COIN, CALENDAR, CATEGORY, PENCIL, SEED, DROP, NUT, CHECKED_TASK, BROOM
}

@Composable
fun GardenArtwork(art: GardenArt, modifier: Modifier = Modifier) {
    when (art) {
        GardenArt.WATERING_CAN -> GardenSourceImage(R.drawable.simple_tools, modifier)
        GardenArt.SEED -> GardenSourceImage(R.drawable.simple_seed, modifier)
        else -> SimpleGardenArt(art, modifier)
    }
}

@Composable
fun GardenCareAction(art: GardenArt, label: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Column(modifier.alpha(if (enabled) 1f else .5f).clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally) {
        GardenArtwork(art, Modifier.fillMaxWidth().height(58.dp))
        GardenBotanicalFrame(Modifier.fillMaxWidth().heightIn(min = 30.dp),
            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 5.dp), cornerSize = 14.dp) {
            GardenRichText(label, color = Deep, fontSize = 15.sp, lineHeight = 17.sp, fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun GardenWoodenTray(modifier: Modifier = Modifier, decoration: Boolean = false, content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(if (decoration) 24.dp else 28.dp)
    Box(modifier.background(Brush.verticalGradient(listOf(Color(0xFFC78F50),Color(0xFFB87A3F),Color(0xFFD6A365))),shape)
        .border(3.dp,Color(0xFFDDB17A),shape)) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp), content = content)
    }
}

@Preview(name = "Simple care shelf", widthDp = 340)
@Composable
fun GardenCareActionPreview() { GardenWoodenTray { Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
    GardenCareAction(GardenArt.WATERING_CAN, "Tưới", {}, Modifier.weight(1f))
    GardenCareAction(GardenArt.FERTILIZER, "Bón", {}, Modifier.weight(1f))
    GardenCareAction(GardenArt.PUMP, "Bơm", {}, Modifier.weight(1f))
} } }

@Preview(name = "Simple navigation icons", widthDp = 360, heightDp = 80)
@Composable
fun GardenArtworkPreview() { Row { listOf(GardenArt.SPROUT,GardenArt.CHECKLIST,GardenArt.TREE,GardenArt.SHOP,GardenArt.JOURNAL).forEach {
    GardenArtwork(it,Modifier.weight(1f).height(70.dp))
} } }
