@file:JvmName("GardenPanelComponents")

package com.mamxanh.app.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class GardenPanelSkin { BADGE, PRICE, DIALOG, TASK_DIALOG, PLANTING_DIALOG, NAME, PROGRESS, HINT }

/** Small live UI surfaces inspired by the smooth cream panels in mics. */
@Composable
fun GardenBotanicalFrame(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
    cornerSize: Dp = 24.dp,
    skin: GardenPanelSkin = GardenPanelSkin.BADGE,
    content: @Composable BoxScope.() -> Unit
) {
    val modal = skin in setOf(GardenPanelSkin.DIALOG, GardenPanelSkin.TASK_DIALOG, GardenPanelSkin.PLANTING_DIALOG)
    val shape = RoundedCornerShape(if (modal) 34.dp else cornerSize.coerceAtMost(28.dp))
    val border = if (modal) 7.dp else 1.5.dp
    val colors = when (skin) {
        GardenPanelSkin.PRICE -> listOf(Color(0xFFFFE3A0), Color(0xFFF4C96F))
        GardenPanelSkin.HINT -> listOf(Color(0xFFFFEBC0), Color(0xFFF8DDA7))
        else -> listOf(Color(0xFFFFF2D9), Color(0xFFFFE5B8))
    }
    Box(modifier.clip(shape)
        .background(Brush.verticalGradient(colors))
        .border(BorderStroke(border, if (modal) Color(0xFFC88A49) else Color(0xFFD7AD76)), shape)) {
        if (modal) Box(Modifier.matchParentSize().padding(9.dp)
            .border(1.dp, Color(0xFFEACB97), RoundedCornerShape(25.dp)))
        Box(Modifier.padding(contentPadding), content = content)
    }
}

@Preview(name = "Simple cream panel", widthDp = 320, heightDp = 180)
@Composable
fun GardenBotanicalFramePreview() {
    GardenBotanicalFrame(Modifier.fillMaxSize(), skin = GardenPanelSkin.DIALOG) {
        androidx.compose.material3.Text("Nhật ký khu vườn")
    }
}
