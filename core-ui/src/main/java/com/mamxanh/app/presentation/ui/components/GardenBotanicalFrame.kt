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
        border = null,
        shadowElevation = 3.dp
    ) {
        Box {
            Canvas(Modifier.matchParentSize().clip(shape)) {
                val accent = Color(0xFF83A96D).copy(alpha = .20f)
                val w = size.width
                val h = size.height
                drawCircle(accent, w * .15f, androidx.compose.ui.geometry.Offset(w * .98f, h * .02f))
                drawCircle(accent.copy(alpha = .7f), w * .09f, androidx.compose.ui.geometry.Offset(w * .02f, h * .98f))
                val edge = 5.dp.toPx()
                val inset = edge / 2
                drawRoundRect(
                    Color(0xFF876342),
                    topLeft = androidx.compose.ui.geometry.Offset(inset, inset),
                    size = androidx.compose.ui.geometry.Size(w - edge, h - edge),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(22.dp.toPx()),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(edge)
                )
                // Grain follows the frame, leaving the paper and its text clear.
                for (i in 0..2) {
                    val y = 1.5.dp.toPx() + i * 1.25.dp.toPx()
                    drawLine(Color(0xFFD3AE79), androidx.compose.ui.geometry.Offset(24.dp.toPx(), y),
                        androidx.compose.ui.geometry.Offset(w - 24.dp.toPx(), y), .6.dp.toPx())
                    drawLine(Color(0xFFB18B59), androidx.compose.ui.geometry.Offset(24.dp.toPx(), h-y),
                        androidx.compose.ui.geometry.Offset(w - 24.dp.toPx(), h-y), .6.dp.toPx())
                }
                for (i in 0..5) {
                    val y = 26.dp.toPx() + (h - 52.dp.toPx()) * i / 5f
                    drawLine(Color(0xFFD3AE79).copy(alpha=.8f),
                        androidx.compose.ui.geometry.Offset(1.dp.toPx(), y),
                        androidx.compose.ui.geometry.Offset(4.dp.toPx(), y + 9.dp.toPx()), .8.dp.toPx())
                    drawLine(Color(0xFF553F2B).copy(alpha=.7f),
                        androidx.compose.ui.geometry.Offset(w - 4.dp.toPx(), y),
                        androidx.compose.ui.geometry.Offset(w - 1.dp.toPx(), y + 7.dp.toPx()), .8.dp.toPx())
                }
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
