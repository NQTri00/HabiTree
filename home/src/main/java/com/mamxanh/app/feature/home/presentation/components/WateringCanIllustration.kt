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

/**
 * Draws the cute yellow-capped blue watering can matching Concept Image 1.
 */
@Composable
fun WateringCanIllustration(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val canBlue = Color(0xFF29B6F6)
        val canBlueDark = Color(0xFF0288D1)
        val canYellow = Color(0xFFFFD54F)
        val canYellowDark = Color(0xFFFFA000)
        val canOutline = Color(0xFF0A5882)

        val bodyW = w * 0.50f
        val bodyH = h * 0.55f
        val bodyX = w * 0.44f
        val bodyY = h * 0.35f

        val spoutPath = Path().apply {
            moveTo(bodyX, bodyY + bodyH * 0.70f)
            lineTo(w * 0.16f, bodyY + bodyH * 0.25f)
            lineTo(w * 0.20f, bodyY + bodyH * 0.16f)
            lineTo(bodyX + bodyW * 0.15f, bodyY + bodyH * 0.55f)
            close()
        }
        drawPath(spoutPath, canBlue)
        drawPath(spoutPath, canOutline, style = Stroke(w * 0.015f))

        drawCircle(canYellow, w * 0.085f, Offset(w * 0.16f, bodyY + bodyH * 0.20f))
        drawCircle(canOutline, w * 0.085f, Offset(w * 0.16f, bodyY + bodyH * 0.20f), style = Stroke(w * 0.015f))
        val roseCenter = Offset(w * 0.16f, bodyY + bodyH * 0.20f)
        drawCircle(Color(0xFF5A3D06), w * 0.012f, roseCenter)
        listOf(
            Offset(-w * 0.035f, 0f), Offset(w * 0.035f, 0f),
            Offset(0f, -h * 0.035f), Offset(0f, h * 0.035f)
        ).forEach { off ->
            drawCircle(Color(0xFF5A3D06), w * 0.010f, roseCenter + off)
        }

        val bodyPath = Path().apply {
            moveTo(bodyX - bodyW * 0.45f, bodyY + bodyH * 0.20f)
            cubicTo(
                bodyX - bodyW * 0.55f, bodyY + bodyH * 0.85f,
                bodyX + bodyW * 0.55f, bodyY + bodyH * 0.85f,
                bodyX + bodyW * 0.45f, bodyY + bodyH * 0.20f
            )
            close()
        }
        drawPath(bodyPath, canBlue)
        drawPath(bodyPath, canOutline, style = Stroke(w * 0.018f))

        drawArc(
            color = Color.White.copy(alpha = 0.45f),
            startAngle = 140f,
            sweepAngle = 70f,
            useCenter = false,
            topLeft = Offset(bodyX - bodyW * 0.40f, bodyY + bodyH * 0.25f),
            size = Size(bodyW * 0.80f, bodyH * 0.55f),
            style = Stroke(w * 0.025f, cap = StrokeCap.Round)
        )

        drawRoundRect(
            color = canYellow,
            topLeft = Offset(bodyX - bodyW * 0.42f, bodyY),
            size = Size(bodyW * 0.84f, bodyH * 0.28f),
            cornerRadius = CornerRadius(w * 0.04f)
        )
        drawRoundRect(
            color = canOutline,
            topLeft = Offset(bodyX - bodyW * 0.42f, bodyY),
            size = Size(bodyW * 0.84f, bodyH * 0.28f),
            cornerRadius = CornerRadius(w * 0.04f),
            style = Stroke(w * 0.015f)
        )
        drawCircle(canYellowDark, w * 0.035f, Offset(bodyX, bodyY - h * 0.015f))
        drawCircle(canOutline, w * 0.035f, Offset(bodyX, bodyY - h * 0.015f), style = Stroke(w * 0.012f))

        val handlePath = Path().apply {
            moveTo(bodyX + bodyW * 0.35f, bodyY + bodyH * 0.20f)
            cubicTo(
                bodyX + bodyW * 0.85f, bodyY + bodyH * 0.20f,
                bodyX + bodyW * 0.85f, bodyY + bodyH * 0.75f,
                bodyX + bodyW * 0.30f, bodyY + bodyH * 0.75f
            )
        }
        drawPath(handlePath, canBlueDark, style = Stroke(w * 0.045f, cap = StrokeCap.Round))
        drawPath(handlePath, canOutline, style = Stroke(w * 0.012f, cap = StrokeCap.Round))
    }
}
