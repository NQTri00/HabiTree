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
    contentPadding: PaddingValues = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
    cornerSize: Dp = 14.dp,
    skin: GardenPanelSkin = GardenPanelSkin.BADGE,
    content: @Composable BoxScope.() -> Unit
) {
    val modal = skin in setOf(GardenPanelSkin.DIALOG, GardenPanelSkin.TASK_DIALOG, GardenPanelSkin.PLANTING_DIALOG)
    val shape = RoundedCornerShape(if (modal) 28.dp else cornerSize)
    val colors = when (skin) {
        GardenPanelSkin.PRICE -> MintDepth.price
        GardenPanelSkin.HINT -> MintDepth.hint
        else -> MintDepth.card
    }
    Box(modifier.mintDepth(shape, colors, if (modal) 16.dp else 6.dp)) {
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
