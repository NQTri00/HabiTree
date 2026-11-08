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
 * Draws foliage for various kawaii potted plants in Shop.
 */
fun DrawScope.drawKawaiiFoliage(
    variety: Int,
    cx: Float,
    baseY: Float,
    w: Float,
    h: Float
) {
    val outlineColor = Color(0xFF385E3B)
    val plantGreen = Color(0xFF72B872)
    val plantLight = Color(0xFF98D992)

    when (variety % 10) {
        0 -> { // Jade Succulent
            val leaves = listOf(
                Offset(0f, -h * 0.16f),
                Offset(-w * 0.14f, -h * 0.10f),
                Offset(w * 0.14f, -h * 0.10f),
                Offset(-w * 0.22f, -h * 0.02f),
                Offset(w * 0.22f, -h * 0.02f),
                Offset(0f, -h * 0.05f)
            )
            leaves.forEach { off ->
                val lx = cx + off.x
                val ly = baseY + off.y
                drawOval(plantGreen, Offset(lx - w * 0.09f, ly - h * 0.07f), Size(w * 0.18f, h * 0.14f))
                drawOval(plantLight, Offset(lx - w * 0.06f, ly - h * 0.05f), Size(w * 0.12f, h * 0.09f))
                drawOval(outlineColor, Offset(lx - w * 0.09f, ly - h * 0.07f), Size(w * 0.18f, h * 0.14f), style = Stroke(w * 0.015f))
            }
        }
        1 -> { // Monstera
            drawLine(outlineColor, Offset(cx, baseY), Offset(cx - w * 0.16f, baseY - h * 0.14f), w * 0.022f, cap = StrokeCap.Round)
            drawHeartLeaf(cx - w * 0.16f, baseY - h * 0.18f, w * 0.28f, h * 0.22f, plantGreen, outlineColor)
            drawLine(outlineColor, Offset(cx, baseY), Offset(cx + w * 0.16f, baseY - h * 0.12f), w * 0.022f, cap = StrokeCap.Round)
            drawHeartLeaf(cx + w * 0.16f, baseY - h * 0.16f, w * 0.26f, h * 0.20f, plantGreen, outlineColor)
        }
        2 -> { // Ficus
            drawLine(outlineColor, Offset(cx, baseY), Offset(cx, baseY - h * 0.25f), w * 0.025f, cap = StrokeCap.Round)
            val sprigs = listOf(
                Offset(-w * 0.18f, -h * 0.08f), Offset(w * 0.18f, -h * 0.06f),
                Offset(-w * 0.15f, -h * 0.18f), Offset(w * 0.15f, -h * 0.17f),
                Offset(0f, -h * 0.26f)
            )
            sprigs.forEach { sp ->
                val lx = cx + sp.x
                val ly = baseY + sp.y
                drawLine(outlineColor, Offset(cx, ly + h * 0.04f), Offset(lx, ly), w * 0.018f, cap = StrokeCap.Round)
                drawCircle(plantGreen, w * 0.08f, Offset(lx, ly))
                drawCircle(plantLight, w * 0.05f, Offset(lx - w * 0.015f, ly - h * 0.015f))
                drawCircle(outlineColor, w * 0.08f, Offset(lx, ly), style = Stroke(w * 0.015f))
            }
        }
        3 -> { // Snake Plant
            val blades = listOf(
                Triple(-w * 0.12f, h * 0.22f, plantGreen),
                Triple(0f, h * 0.28f, Color(0xFF5CA35C)),
                Triple(w * 0.12f, h * 0.20f, plantGreen)
            )
            blades.forEach { (bx, bh, col) ->
                val bladePath = Path().apply {
                    moveTo(cx + bx - w * 0.06f, baseY)
                    lineTo(cx + bx, baseY - bh)
                    lineTo(cx + bx + w * 0.06f, baseY)
                    close()
                }
                drawPath(bladePath, col)
                drawPath(bladePath, outlineColor, style = Stroke(w * 0.015f))
                drawLine(Color(0xFFD6F5B8), Offset(cx + bx, baseY), Offset(cx + bx, baseY - bh + h * 0.03f), w * 0.012f)
            }
        }
        4 -> { // Flowering Cactus
            val cactusW = w * 0.36f
            val cactusH = h * 0.22f
            drawRoundRect(
                color = plantGreen,
                topLeft = Offset(cx - cactusW * 0.5f, baseY - cactusH),
                size = Size(cactusW, cactusH),
                cornerRadius = CornerRadius(cactusW * 0.45f)
            )
            drawRoundRect(
                color = outlineColor,
                topLeft = Offset(cx - cactusW * 0.5f, baseY - cactusH),
                size = Size(cactusW, cactusH),
                cornerRadius = CornerRadius(cactusW * 0.45f),
                style = Stroke(w * 0.018f)
            )
            drawLine(outlineColor, Offset(cx - cactusW * 0.25f, baseY - cactusH * 0.85f), Offset(cx - cactusW * 0.25f, baseY), w * 0.012f)
            drawLine(outlineColor, Offset(cx + cactusW * 0.25f, baseY - cactusH * 0.85f), Offset(cx + cactusW * 0.25f, baseY), w * 0.012f)
            drawCircle(Color(0xFFFF528A), w * 0.07f, Offset(cx, baseY - cactusH))
            drawCircle(Color(0xFFFFD54F), w * 0.03f, Offset(cx, baseY - cactusH))
        }
        5 -> { // Lavender
            val lavStalks = listOf(-w * 0.12f, 0f, w * 0.12f)
            lavStalks.forEach { lx ->
                drawLine(outlineColor, Offset(cx + lx, baseY), Offset(cx + lx, baseY - h * 0.24f), w * 0.018f, cap = StrokeCap.Round)
                for (step in 1..4) {
                    val fy = baseY - h * (0.08f + step * 0.04f)
                    drawCircle(Color(0xFFB39DDB), w * 0.045f, Offset(cx + lx - w * 0.025f, fy))
                    drawCircle(Color(0xFF9575CD), w * 0.045f, Offset(cx + lx + w * 0.025f, fy))
                }
            }
        }
        6 -> { // Pilea
            val coins = listOf(
                Pair(-w * 0.16f, -h * 0.14f),
                Pair(w * 0.16f, -h * 0.13f),
                Pair(0f, -h * 0.22f),
                Pair(-w * 0.10f, -h * 0.05f),
                Pair(w * 0.12f, -h * 0.06f)
            )
            coins.forEach { (cxOff, cyOff) ->
                val px = cx + cxOff
                val py = baseY + cyOff
                drawLine(outlineColor, Offset(cx, baseY), Offset(px, py), w * 0.014f, cap = StrokeCap.Round)
                drawCircle(plantGreen, w * 0.07f, Offset(px, py))
                drawCircle(plantLight, w * 0.04f, Offset(px - w * 0.012f, py - h * 0.012f))
                drawCircle(outlineColor, w * 0.07f, Offset(px, py), style = Stroke(w * 0.015f))
                drawCircle(Color(0xFFE4FCE2), w * 0.015f, Offset(px, py))
            }
        }
        else -> { // Clover / Cute Sprout
            val spr = listOf(-w * 0.12f to -h * 0.12f, 0f to -h * 0.18f, w * 0.12f to -h * 0.11f)
            spr.forEach { (ox, oy) ->
                val lx = cx + ox
                val ly = baseY + oy
                drawLine(outlineColor, Offset(cx, baseY), Offset(lx, ly), w * 0.018f, cap = StrokeCap.Round)
                drawOval(plantGreen, Offset(lx - w * 0.08f, ly - h * 0.06f), Size(w * 0.16f, h * 0.12f))
                drawOval(outlineColor, Offset(lx - w * 0.08f, ly - h * 0.06f), Size(w * 0.16f, h * 0.12f), style = Stroke(w * 0.015f))
            }
        }
    }
}

private fun DrawScope.drawHeartLeaf(
    cx: Float,
    cy: Float,
    w: Float,
    h: Float,
    fill: Color,
    outline: Color
) {
    val leaf = Path().apply {
        moveTo(cx, cy + h * 0.5f)
        cubicTo(cx - w * 0.55f, cy + h * 0.1f, cx - w * 0.50f, cy - h * 0.45f, cx, cy - h * 0.25f)
        cubicTo(cx + w * 0.50f, cy - h * 0.45f, cx + w * 0.55f, cy + h * 0.1f, cx, cy + h * 0.5f)
        close()
    }
    drawPath(leaf, fill)
    drawPath(leaf, outline, style = Stroke(w * 0.05f))
}
