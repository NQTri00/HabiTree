@file:JvmName("GardenPanelComponents")
package com.mamxanh.app.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class GardenPanelSkin { BADGE, PRICE, DIALOG, TASK_DIALOG, PLANTING_DIALOG, NAME, PROGRESS, HINT }

@Composable
fun GardenBotanicalFrame(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    cornerSize: Dp = 22.dp,
    skin: GardenPanelSkin = GardenPanelSkin.BADGE,
    content: @Composable BoxScope.() -> Unit
) {
    val modal = skin in setOf(GardenPanelSkin.DIALOG, GardenPanelSkin.TASK_DIALOG, GardenPanelSkin.PLANTING_DIALOG)
    val shape = RoundedCornerShape(if (modal) 28.dp else cornerSize)
    val fill = when (skin) {
        GardenPanelSkin.PRICE -> Color(0xFFFFF4CD)
        GardenPanelSkin.HINT -> Color(0xFFE8F4DE)
        else -> Color(0xFFF9FDFB)
    }
    Box(modifier.shadow(if (modal) 8.dp else 2.dp, shape)
        .background(fill, shape).border(1.dp, Color.White.copy(alpha = .95f), shape)) {
        Box(Modifier.padding(contentPadding), content = content)
    }
}

@Preview(widthDp = 320, heightDp = 180, device = "spec:width=320dp,height=180dp,dpi=160")
@Composable
fun GardenBotanicalFramePreview() {
    GardenBotanicalFrame(Modifier.fillMaxSize(), skin = GardenPanelSkin.DIALOG) {
        androidx.compose.material3.Text("Nhật ký khu vườn")
    }
}
