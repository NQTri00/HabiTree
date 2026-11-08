package com.mamxanh.app.feature.home.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.mamxanh.app.presentation.ui.*

/**
 * Draws the vintage teal water pump on brick column with metal bucket (Image 1).
 */
@Composable
fun WaterPumpIllustration(
    modifier: Modifier = Modifier,
    isDripping: Boolean = true
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w * 0.44f

        val platY = h * 0.88f
        drawOval(
            color = Color(0xFF6B4524),
            topLeft = Offset(w * 0.05f, platY),
            size = Size(w * 0.70f, h * 0.10f)
        )
        drawOval(
            color = Color(0xFF8B5A2B),
            topLeft = Offset(w * 0.07f, platY - h * 0.02f),
            size = Size(w * 0.66f, h * 0.09f)
        )

        val brickW = w * 0.38f
        val brickH = h * 0.28f
        val brickY = platY - brickH
        val brickLeft = cx - brickW * 0.5f

        drawRoundRect(
            color = BrickRed,
            topLeft = Offset(brickLeft, brickY),
            size = Size(brickW, brickH),
            cornerRadius = CornerRadius(w * 0.02f)
        )
        val rows = 4
        val rowH = brickH / rows
        for (r in 0 until rows) {
            val y = brickY + r * rowH
            drawLine(BrickRedDark, Offset(brickLeft, y), Offset(brickLeft + brickW, y), w * 0.012f)
            val numBricks = 2
            val bw = brickW / numBricks
            val offset = if (r % 2 == 0) 0f else bw * 0.5f
            for (c in 0..numBricks) {
                val bx = (brickLeft + offset + c * bw).coerceIn(brickLeft, brickLeft + brickW)
                drawLine(BrickRedDark, Offset(bx, y), Offset(bx, y + rowH), w * 0.012f)
            }
        }
        drawRoundRect(
            color = Color(0xFFE5C39E),
            topLeft = Offset(brickLeft - w * 0.02f, brickY - h * 0.025f),
            size = Size(brickW + w * 0.04f, h * 0.035f),
            cornerRadius = CornerRadius(w * 0.015f)
        )

        val pumpBaseY = brickY - h * 0.02f
        val pumpBodyH = h * 0.35f
        val pumpTopY = pumpBaseY - pumpBodyH

        drawOval(
            color = WaterTealDark,
            topLeft = Offset(cx - w * 0.16f, pumpBaseY - h * 0.03f),
            size = Size(w * 0.32f, h * 0.045f)
        )
        val pumpPath = Path().apply {
            moveTo(cx - w * 0.10f, pumpBaseY - h * 0.02f)
            cubicTo(cx - w * 0.18f, pumpBaseY - pumpBodyH * 0.35f, cx - w * 0.20f, pumpBaseY - pumpBodyH * 0.65f, cx - w * 0.11f, pumpTopY)
            lineTo(cx + w * 0.11f, pumpTopY)
            cubicTo(cx + w * 0.20f, pumpBaseY - pumpBodyH * 0.65f, cx + w * 0.18f, pumpBaseY - pumpBodyH * 0.35f, cx + w * 0.10f, pumpBaseY - pumpBodyH * 0.02f)
            close()
        }
        drawPath(pumpPath, WaterTeal)
        drawPath(pumpPath, WaterTealDark, style = Stroke(w * 0.015f))

        drawRoundRect(
            color = WaterTealDark,
            topLeft = Offset(cx - w * 0.08f, pumpTopY - h * 0.035f),
            size = Size(w * 0.16f, h * 0.04f),
            cornerRadius = CornerRadius(w * 0.015f)
        )
        drawCircle(WaterTeal, w * 0.03f, Offset(cx, pumpTopY - h * 0.04f))

        val spoutPath = Path().apply {
            moveTo(cx + w * 0.08f, pumpBaseY - pumpBodyH * 0.40f)
            cubicTo(cx + w * 0.28f, pumpBaseY - pumpBodyH * 0.40f, cx + w * 0.35f, pumpBaseY - pumpBodyH * 0.25f, cx + w * 0.32f, pumpBaseY - pumpBodyH * 0.10f)
            lineTo(cx + w * 0.24f, pumpBaseY - pumpBodyH * 0.10f)
            cubicTo(cx + w * 0.22f, pumpBaseY - pumpBodyH * 0.24f, cx + w * 0.16f, pumpBaseY - pumpBodyH * 0.28f, cx + w * 0.08f, pumpBaseY - pumpBodyH * 0.26f)
            close()
        }
        drawPath(spoutPath, WaterTeal)
        drawPath(spoutPath, WaterTealDark, style = Stroke(w * 0.012f))

        val handleStart = Offset(cx - w * 0.07f, pumpTopY + h * 0.02f)
        val handlePath = Path().apply {
            moveTo(handleStart.x, handleStart.y)
            cubicTo(cx - w * 0.30f, pumpTopY + h * 0.05f, cx - w * 0.38f, pumpTopY + h * 0.22f, cx - w * 0.35f, pumpTopY + h * 0.35f)
        }
        drawPath(handlePath, Color(0xFF1B554E), style = Stroke(w * 0.028f, cap = StrokeCap.Round))
        drawCircle(Color(0xFF1B554E), w * 0.035f, Offset(cx - w * 0.35f, pumpTopY + h * 0.35f))

        val bucketX = cx + w * 0.30f
        val bucketY = platY - h * 0.18f
        val bucketW = w * 0.32f
        val bucketH = h * 0.20f

        val bucketPath = Path().apply {
            moveTo(bucketX - bucketW * 0.40f, bucketY)
            lineTo(bucketX - bucketW * 0.30f, bucketY + bucketH)
            lineTo(bucketX + bucketW * 0.30f, bucketY + bucketH)
            lineTo(bucketX + bucketW * 0.40f, bucketY)
            close()
        }
        drawPath(bucketPath, Color(0xFFCCD4DD))
        drawPath(bucketPath, Color(0xFF8692A0), style = Stroke(w * 0.012f))

        drawOval(
            color = Color(0xFFE4E9F0),
            topLeft = Offset(bucketX - bucketW * 0.42f, bucketY - h * 0.015f),
            size = Size(bucketW * 0.84f, h * 0.03f)
        )
        drawOval(
            color = WaterBlue,
            topLeft = Offset(bucketX - bucketW * 0.36f, bucketY + h * 0.02f),
            size = Size(bucketW * 0.72f, h * 0.025f)
        )

        if (isDripping) {
            val dripX = cx + w * 0.28f
            val spoutTipY = pumpBaseY - pumpBodyH * 0.10f
            drawLine(
                color = WaterBlue.copy(alpha = 0.8f),
                start = Offset(dripX, spoutTipY),
                end = Offset(dripX, spoutTipY + h * 0.05f),
                strokeWidth = w * 0.014f,
                cap = StrokeCap.Round
            )
            drawOval(
                color = WaterBlue,
                topLeft = Offset(dripX - w * 0.012f, spoutTipY + h * 0.07f),
                size = Size(w * 0.024f, h * 0.030f)
            )
            drawOval(
                color = Color.White.copy(alpha = 0.7f),
                topLeft = Offset(bucketX - w * 0.08f, bucketY + h * 0.025f),
                size = Size(w * 0.16f, h * 0.012f),
                style = Stroke(w * 0.005f)
            )
        }
    }
}
