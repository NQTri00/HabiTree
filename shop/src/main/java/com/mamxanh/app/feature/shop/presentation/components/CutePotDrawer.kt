package com.mamxanh.app.feature.shop.presentation.components

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

/**
 * Draws a cute ceramic pot with rich, expressive kawaii faces.
 */
fun DrawScope.drawCuteCeramicPot(
    cx: Float,
    potY: Float,
    potW: Float,
    potH: Float,
    colorSeed: Int,
    expression: Int
) {
    val potColor = when (colorSeed % 4) {
        0 -> Color(0xFFFFD1D8) // Blush Pink
        1 -> Color(0xFFFFE6B5) // Warm Peach
        2 -> Color(0xFFD6F0E0) // Mint Green
        else -> Color(0xFFFFF1C7) // Cream Yellow
    }
    val outlineColor = Color(0xFF6B4A3A)

    val rimH = potH * 0.22f
    drawRoundRect(
        color = potColor,
        topLeft = Offset(cx - potW * 0.55f, potY),
        size = Size(potW * 1.10f, rimH),
        cornerRadius = CornerRadius(potW * 0.15f)
    )
    drawRoundRect(
        color = outlineColor,
        topLeft = Offset(cx - potW * 0.55f, potY),
        size = Size(potW * 1.10f, rimH),
        cornerRadius = CornerRadius(potW * 0.15f),
        style = Stroke(potW * 0.045f)
    )

    val bodyPath = Path().apply {
        moveTo(cx - potW * 0.48f, potY + rimH)
        lineTo(cx - potW * 0.40f, potY + potH)
        cubicTo(
            cx - potW * 0.25f, potY + potH * 1.05f,
            cx + potW * 0.25f, potY + potH * 1.05f,
            cx + potW * 0.40f, potY + potH
        )
        lineTo(cx + potW * 0.48f, potY + rimH)
        close()
    }
    drawPath(bodyPath, potColor)
    drawPath(bodyPath, outlineColor, style = Stroke(potW * 0.045f))

    val faceY = potY + rimH + potH * 0.35f
    val eyeDist = potW * 0.24f

    when (expression % 3) {
        0 -> { // Blush Smiling Face
            drawCircle(outlineColor, potW * 0.055f, Offset(cx - eyeDist * 0.5f, faceY))
            drawCircle(Color.White, potW * 0.020f, Offset(cx - eyeDist * 0.5f - potW * 0.012f, faceY - potW * 0.012f))
            drawCircle(outlineColor, potW * 0.055f, Offset(cx + eyeDist * 0.5f, faceY))
            drawCircle(Color.White, potW * 0.020f, Offset(cx + eyeDist * 0.5f - potW * 0.012f, faceY - potW * 0.012f))

            drawOval(
                Color(0xFFFF8B9C).copy(alpha = 0.6f),
                Offset(cx - eyeDist * 0.85f - potW * 0.05f, faceY + potH * 0.08f),
                Size(potW * 0.10f, potH * 0.09f)
            )
            drawOval(
                Color(0xFFFF8B9C).copy(alpha = 0.6f),
                Offset(cx + eyeDist * 0.85f - potW * 0.05f, faceY + potH * 0.08f),
                Size(potW * 0.10f, potH * 0.09f)
            )

            val smile = Path().apply {
                moveTo(cx - potW * 0.08f, faceY + potH * 0.08f)
                cubicTo(
                    cx - potW * 0.04f, faceY + potH * 0.18f,
                    cx + potW * 0.04f, faceY + potH * 0.18f,
                    cx + potW * 0.08f, faceY + potH * 0.08f
                )
            }
            drawPath(smile, outlineColor, style = Stroke(potW * 0.035f, cap = StrokeCap.Round))
        }
        1 -> { // Winking Cute Face
            val w1 = Path().apply {
                moveTo(cx - eyeDist * 0.5f - potW * 0.05f, faceY - potH * 0.04f)
                lineTo(cx - eyeDist * 0.5f + potW * 0.04f, faceY)
                lineTo(cx - eyeDist * 0.5f - potW * 0.05f, faceY + potH * 0.04f)
            }
            drawPath(w1, outlineColor, style = Stroke(potW * 0.038f, cap = StrokeCap.Round))

            val w2 = Path().apply {
                moveTo(cx + eyeDist * 0.5f + potW * 0.05f, faceY - potH * 0.04f)
                lineTo(cx + eyeDist * 0.5f - potW * 0.04f, faceY)
                lineTo(cx + eyeDist * 0.5f + potW * 0.05f, faceY + potH * 0.04f)
            }
            drawPath(w2, outlineColor, style = Stroke(potW * 0.038f, cap = StrokeCap.Round))

            val mouth = Path().apply {
                moveTo(cx - potW * 0.06f, faceY + potH * 0.06f)
                cubicTo(
                    cx - potW * 0.03f, faceY + potH * 0.16f,
                    cx + potW * 0.03f, faceY + potH * 0.16f,
                    cx + potW * 0.06f, faceY + potH * 0.06f
                )
            }
            drawPath(mouth, outlineColor, style = Stroke(potW * 0.035f, cap = StrokeCap.Round))

            drawOval(
                Color(0xFFFF8B9C).copy(alpha = 0.65f),
                Offset(cx - eyeDist * 0.85f - potW * 0.05f, faceY + potH * 0.07f),
                Size(potW * 0.11f, potH * 0.09f)
            )
            drawOval(
                Color(0xFFFF8B9C).copy(alpha = 0.65f),
                Offset(cx + eyeDist * 0.85f - potW * 0.05f, faceY + potH * 0.07f),
                Size(potW * 0.11f, potH * 0.09f)
            )
        }
        else -> { // Cheerful Kawaii Face
            drawCircle(outlineColor, potW * 0.055f, Offset(cx - eyeDist * 0.5f, faceY))
            drawCircle(Color.White, potW * 0.020f, Offset(cx - eyeDist * 0.5f - potW * 0.012f, faceY - potW * 0.012f))
            drawCircle(outlineColor, potW * 0.055f, Offset(cx + eyeDist * 0.5f, faceY))
            drawCircle(Color.White, potW * 0.020f, Offset(cx + eyeDist * 0.5f - potW * 0.012f, faceY - potW * 0.012f))
            drawCircle(Color(0xFFE84A5F), potW * 0.04f, Offset(cx, faceY + potH * 0.12f))
        }
    }
}
