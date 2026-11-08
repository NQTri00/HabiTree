package com.mamxanh.app.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mamxanh.app.presentation.ui.Deep
import com.mamxanh.app.presentation.ui.MamXanhPreviewTheme

@Composable
fun GardenBotanicalFrame(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(24.dp)
    Surface(
        modifier = modifier,
        color = Color(0xFFFFFCF1),
        shape = shape,
        border = BorderStroke(1.5.dp, Color(0xFFD6C49D)),
        shadowElevation = 3.dp
    ) {
        Box {
            Canvas(Modifier.matchParentSize().clip(shape)) {
                val accent = Color(0xFF83A96D).copy(alpha = .20f)
                val w = size.width
                val h = size.height
                drawCircle(accent, w * .15f, androidx.compose.ui.geometry.Offset(w * .98f, h * .02f))
                drawCircle(accent.copy(alpha = .7f), w * .09f, androidx.compose.ui.geometry.Offset(w * .02f, h * .98f))
            }
            Box(Modifier.padding(16.dp), content = content)
        }
    }
}

@Preview(name = "Botanical garden frame", showBackground = true, widthDp = 340, heightDp = 220)
@Composable
fun GardenBotanicalFramePreview() {
    MamXanhPreviewTheme {
        GardenBotanicalFrame(Modifier.fillMaxWidth()) {
            androidx.compose.material3.Text("Nhật ký khu vườn", color = Deep)
        }
    }
}
