package com.mamxanh.app.presentation.ui.components

import com.mamxanh.app.presentation.ui.design.AppColors
import com.mamxanh.app.presentation.ui.design.AppDimensions
import com.mamxanh.app.presentation.ui.design.AppTypography

import com.mamxanh.shared.localization.TextKey

import com.mamxanh.shared.localization.AppText

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
    Row(modifier.fillMaxWidth().mintDepth(RoundedCornerShape(bottomStart = AppDimensions.Dp20, bottomEnd = AppDimensions.Dp20), MintDepth.bar, AppDimensions.Dp3)
        .heightIn(min = AppDimensions.Dp56).padding(horizontal = AppDimensions.Dp16, vertical = AppDimensions.Dp8),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp10)) {
        GardenArtwork(GardenArt.SPROUT, Modifier.size(AppDimensions.Dp28))
        Text(title, Modifier.weight(1f), fontSize = AppTypography.Sp23, lineHeight = AppTypography.Sp27, fontWeight = FontWeight.Bold, color = Deep)
        GardenBotanicalFrame(contentPadding = PaddingValues(horizontal = AppDimensions.Dp10, vertical = AppDimensions.Dp5), cornerSize = AppDimensions.Dp50) {
            GardenRichText(AppText.text(TextKey.Copy0Xu279a5c,coins), color = Deep, fontSize = AppTypography.Sp14, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun MintProgress(value: Float, modifier: Modifier = Modifier) {
    LinearProgressIndicator(progress = { value.coerceIn(0f, 1f) },
        modifier = modifier.height(AppDimensions.Dp5).clip(RoundedCornerShape(50)),
        color = AppColors.Green, trackColor = AppColors.SwatchFFD8DFCE, drawStopIndicator = {})
}

@Composable
fun MintSegments(labels: List<String>, selected: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth().height(IntrinsicSize.Min).mintDepth(RoundedCornerShape(50), MintDepth.secondary, AppDimensions.Dp3)) {
        labels.forEachIndexed { index, label ->
            GardenChoiceChip(label, selected == index, { onSelect(index) }, Modifier.weight(1f).fillMaxHeight())
        }
    }
}
