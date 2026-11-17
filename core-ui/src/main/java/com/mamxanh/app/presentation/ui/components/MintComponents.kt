package com.mamxanh.app.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamxanh.app.presentation.ui.*

/** Content-sized mint material, independent of document height and bitmap aspect ratio. */
@Composable
fun MintBackdrop(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(modifier.background(Cream), content = content)
}

@Composable
fun MintHeader(title: String, coins: Int, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth().mintDepth(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp), MintDepth.bar, 3.dp)
        .heightIn(min = 56.dp).padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        GardenArtwork(GardenArt.SPROUT, Modifier.size(28.dp))
        Text(title, Modifier.weight(1f), fontSize = 23.sp, lineHeight = 27.sp, fontWeight = FontWeight.Bold, color = Deep)
        GardenBotanicalFrame(contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp), cornerSize = 50.dp) {
            GardenRichText("🪙 ${coins} xu", color = Deep, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun MintProgress(value: Float, modifier: Modifier = Modifier) {
    LinearProgressIndicator(progress = { value.coerceIn(0f, 1f) },
        modifier = modifier.height(5.dp).clip(RoundedCornerShape(50)),
        color = Color(0xFF315C3C), trackColor = Color(0xFFD8DFCE), drawStopIndicator = {})
}

@Composable
fun MintSegments(labels: List<String>, selected: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth().height(IntrinsicSize.Min).mintDepth(RoundedCornerShape(50), MintDepth.secondary, 3.dp)) {
        labels.forEachIndexed { index, label ->
            GardenChoiceChip(label, selected == index, { onSelect(index) }, Modifier.weight(1f).fillMaxHeight())
        }
    }
}
