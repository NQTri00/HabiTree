package com.mamxanh.app.feature.tree.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.mamxanh.app.presentation.ui.*

/**
 * Renders the adorable kawaii plant growing on the circular soil mound (Image 1):
 * - Circular/oval rich soil mound with grass tufts
 * - Golden glowing upward light beams and sparkling light particles
 * - Kawaii seed/potato character with cute bead eyes, blushing cheeks, and smiling face
 * - Vibrant green clover/foliage leaves with golden glowing tips
 * - Mystery mascot companion silhouette with '?' badge
 */
@Composable
fun KawaiiPlantIllustration(
    growth: Int,
    speciesId: String = "sprout",
    decoration: String = "NONE",
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.aspectRatio(1.15f)) {
        val w = size.width
        val h = size.height
        val cx = w * 0.50f
        val cy = h * 0.52f
        val progress = (growth.coerceIn(0, 120) / 120f)

        // 1. Soil Mound (Rich brown oval with lighter center)
        val soilW = w * 0.76f
        val soilH = h * 0.32f
        val soilY = cy + h * 0.05f

        drawOval(
            color = Color(0xFF6F9978).copy(alpha = 0.45f),
            topLeft = Offset(cx - soilW * 0.52f, soilY + soilH * 0.10f),
            size = Size(soilW * 1.04f, soilH * 0.95f)
        )
        drawOval(
            color = SoilMound,
            topLeft = Offset(cx - soilW * 0.5f, soilY),
            size = Size(soilW, soilH)
        )
        drawOval(
            color = SoilMoundLight,
            topLeft = Offset(cx - soilW * 0.38f, soilY + soilH * 0.14f),
            size = Size(soilW * 0.76f, soilH * 0.70f)
        )

        val grassPositions = listOf(
            Pair(-0.46f, 0.45f), Pair(-0.35f, 0.15f), Pair(-0.25f, 0.75f),
            Pair(0.28f, 0.80f), Pair(0.42f, 0.25f), Pair(0.46f, 0.65f)
        )
        grassPositions.forEach { (gx, gy) ->
            val px = cx + soilW * 0.5f * gx
            val py = soilY + soilH * gy
            drawGrassTuft(px, py, w * 0.035f)
        }

        // 2. Upward Glowing Light Beams
        val glowBrush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFFEA88).copy(alpha = 0.0f),
                Color(0xFFFFEA88).copy(alpha = 0.35f),
                Color(0xFFFFD54F).copy(alpha = 0.55f)
            ),
            startY = cy - h * 0.45f,
            endY = cy + h * 0.15f
        )
        val beamPath = Path().apply {
            moveTo(cx - w * 0.22f, cy + h * 0.12f)
            lineTo(cx - w * 0.30f, cy - h * 0.40f)
            lineTo(cx + w * 0.30f, cy - h * 0.40f)
            lineTo(cx + w * 0.22f, cy + h * 0.12f)
            close()
        }
        drawPath(beamPath, glowBrush)

        val sparkles = listOf(
            Triple(-0.28f, -0.15f, 0.022f),
            Triple(-0.16f, -0.32f, 0.016f),
            Triple(0.02f, -0.38f, 0.025f),
            Triple(0.22f, -0.26f, 0.020f),
            Triple(0.32f, -0.08f, 0.018f),
            Triple(-0.06f, 0.02f, 0.014f)
        )
        sparkles.forEach { (sx, sy, sr) ->
            val spCenter = Offset(cx + w * sx, cy + h * sy)
            drawCircle(Color(0xFFFFF7C2).copy(alpha = 0.45f), w * sr * 1.8f, spCenter)
            drawCircle(Color(0xFFFFD54F), w * sr, spCenter)
            drawCircle(Color.White, w * sr * 0.45f, spCenter)
        }

        // 3. Adorable Kawaii Plant Base
        val bulbW = w * (0.24f + progress * 0.06f)
        val bulbH = h * (0.19f + progress * 0.04f)
        val bulbY = cy - bulbH * 0.45f

        val bulbBody = Path().apply {
            moveTo(cx - bulbW * 0.48f, bulbY + bulbH * 0.5f)
            cubicTo(
                cx - bulbW * 0.52f, bulbY,
                cx - bulbW * 0.30f, bulbY - bulbH * 0.10f,
                cx, bulbY - bulbH * 0.08f
            )
            cubicTo(
                cx + bulbW * 0.30f, bulbY - bulbH * 0.10f,
                cx + bulbW * 0.52f, bulbY,
                cx + bulbW * 0.48f, bulbY + bulbH * 0.5f
            )
            cubicTo(
                cx + bulbW * 0.45f, bulbY + bulbH * 1.05f,
                cx - bulbW * 0.45f, bulbY + bulbH * 1.05f,
                cx - bulbW * 0.48f, bulbY + bulbH * 0.5f
            )
            close()
        }
        val bulbColor = Color(0xFFC78F5C)
        val bulbOutline = Color(0xFF754522)
        drawPath(bulbBody, bulbColor)
        drawPath(bulbBody, bulbOutline, style = Stroke(w * 0.008f))

        val eyeY = bulbY + bulbH * 0.56f
        val eyeSpacing = bulbW * 0.28f
        drawCircle(Color(0xFF332014), w * 0.014f, Offset(cx - eyeSpacing * 0.5f, eyeY))
        drawCircle(Color.White, w * 0.005f, Offset(cx - eyeSpacing * 0.5f - w * 0.003f, eyeY - w * 0.003f))
        drawCircle(Color(0xFF332014), w * 0.014f, Offset(cx + eyeSpacing * 0.5f, eyeY))
        drawCircle(Color.White, w * 0.005f, Offset(cx + eyeSpacing * 0.5f - w * 0.003f, eyeY - w * 0.003f))

        drawOval(
            color = Color(0xFFFF9BA8).copy(alpha = 0.65f),
            topLeft = Offset(cx - eyeSpacing * 0.85f - w * 0.02f, eyeY + h * 0.012f),
            size = Size(w * 0.040f, h * 0.022f)
        )
        drawOval(
            color = Color(0xFFFF9BA8).copy(alpha = 0.65f),
            topLeft = Offset(cx + eyeSpacing * 0.85f - w * 0.02f, eyeY + h * 0.012f),
            size = Size(w * 0.040f, h * 0.022f)
        )

        val mouthPath = Path().apply {
            moveTo(cx - w * 0.020f, eyeY + h * 0.028f)
            cubicTo(cx - w * 0.010f, eyeY + h * 0.042f, cx + w * 0.010f, eyeY + h * 0.042f, cx + w * 0.020f, eyeY + h * 0.028f)
        }
        drawPath(mouthPath, Color(0xFF6B3B1B), style = Stroke(w * 0.006f, cap = StrokeCap.Round))

        // 4. Vibrant Sprouting Leaves
        val stemStartY = bulbY - bulbH * 0.05f
        val stemEndY = stemStartY - h * (0.09f + progress * 0.08f)
        val stemPath = Path().apply {
            moveTo(cx, stemStartY)
            cubicTo(cx - w * 0.02f, stemStartY - h * 0.04f, cx + w * 0.02f, stemEndY + h * 0.03f, cx + w * 0.01f, stemEndY)
        }
        drawPath(stemPath, CuteSproutGreen, style = Stroke(w * 0.022f, cap = StrokeCap.Round))
        drawPath(stemPath, Color(0xFF427A23), style = Stroke(w * 0.006f, cap = StrokeCap.Round))

        val leafScale = 1.0f + progress * 0.6f
        val leafW = w * 0.15f * leafScale
        val leafH = h * 0.13f * leafScale

        drawCuteLeaf(
            cx = cx + w * 0.01f,
            cy = stemEndY - leafH * 0.40f,
            width = leafW * 1.05f,
            height = leafH * 1.05f
        )
        drawCuteLeaf(
            cx = cx - leafW * 0.60f,
            cy = stemEndY - leafH * 0.15f,
            width = leafW * 0.95f,
            height = leafH * 0.95f
        )
        drawCuteLeaf(
            cx = cx + leafW * 0.65f,
            cy = stemEndY - leafH * 0.12f,
            width = leafW * 0.92f,
            height = leafH * 0.92f
        )

        if (progress > 0.4f) {
            when (speciesId) {
                "cherry" -> {
                    drawCherryFlower(cx - leafW * 0.45f, stemEndY - leafH * 0.55f, w * 0.030f)
                    drawCherryFlower(cx + leafW * 0.50f, stemEndY - leafH * 0.45f, w * 0.028f)
                }
                "orange" -> {
                    drawCircle(Color(0xFFFFA01A), w * 0.035f, Offset(cx + leafW * 0.55f, stemEndY - leafH * 0.15f))
                    drawCircle(Color(0xFFFFD54F), w * 0.010f, Offset(cx + leafW * 0.52f, stemEndY - leafH * 0.18f))
                }
            }
        }

        // 5. Mystery Mascot Silhouette
        val mascotX = cx + soilW * 0.33f
        val mascotY = soilY + soilH * 0.48f
        val mascotSize = w * 0.10f

        drawMascotSilhouette(mascotX, mascotY, mascotSize)
    }
}

private fun DrawScope.drawGrassTuft(x: Float, y: Float, size: Float) {
    val grassColor = Color(0xFF67B547)
    val grassLight = Color(0xFF90D96A)
    drawLine(grassColor, Offset(x, y), Offset(x - size * 0.7f, y - size), size * 0.35f, cap = StrokeCap.Round)
    drawLine(grassLight, Offset(x, y), Offset(x, y - size * 1.2f), size * 0.38f, cap = StrokeCap.Round)
    drawLine(grassColor, Offset(x, y), Offset(x + size * 0.7f, y - size * 0.85f), size * 0.35f, cap = StrokeCap.Round)
}

private fun DrawScope.drawCuteLeaf(cx: Float, cy: Float, width: Float, height: Float) {
    val leafPath = Path().apply {
        moveTo(cx, cy + height * 0.5f)
        cubicTo(cx - width * 0.65f, cy + height * 0.2f, cx - width * 0.65f, cy - height * 0.4f, cx, cy - height * 0.5f)
        cubicTo(cx + width * 0.65f, cy - height * 0.4f, cx + width * 0.65f, cy + height * 0.2f, cx, cy + height * 0.5f)
        close()
    }
    drawPath(leafPath, CuteSproutGreen)
    val topHighlight = Path().apply {
        moveTo(cx, cy + height * 0.2f)
        cubicTo(cx - width * 0.45f, cy, cx - width * 0.45f, cy - height * 0.35f, cx, cy - height * 0.5f)
        cubicTo(cx + width * 0.45f, cy - height * 0.35f, cx + width * 0.45f, cy, cx, cy + height * 0.2f)
        close()
    }
    drawPath(topHighlight, CuteSproutLight.copy(alpha = 0.55f))
    drawPath(leafPath, Color(0xFF386C1D), style = Stroke(width * 0.05f))

    drawCircle(Color(0xFFFFEA88), width * 0.16f, Offset(cx, cy - height * 0.45f))
    drawCircle(Color.White, width * 0.08f, Offset(cx, cy - height * 0.45f))
}

private fun DrawScope.drawCherryFlower(cx: Float, cy: Float, radius: Float) {
    val petalColor = Color(0xFFFFC0CB)
    for (i in 0 until 5) {
        val rad = Math.toRadians((i * 72).toDouble())
        val px = cx + (Math.cos(rad) * radius * 0.7f).toFloat()
        val py = cy + (Math.sin(rad) * radius * 0.7f).toFloat()
        drawCircle(petalColor, radius * 0.55f, Offset(px, py))
    }
    drawCircle(Color(0xFFFFD54F), radius * 0.35f, Offset(cx, cy))
}

private fun DrawScope.drawMascotSilhouette(cx: Float, cy: Float, size: Float) {
    val bodyColor = Color(0xFF2C1911)
    drawOval(
        color = bodyColor,
        topLeft = Offset(cx - size * 0.48f, cy - size * 0.40f),
        size = Size(size * 0.96f, size * 0.85f)
    )
    drawCircle(bodyColor, size * 0.22f, Offset(cx - size * 0.32f, cy - size * 0.38f))
    drawCircle(bodyColor, size * 0.22f, Offset(cx + size * 0.32f, cy - size * 0.38f))
    drawCircle(bodyColor, size * 0.16f, Offset(cx - size * 0.28f, cy + size * 0.38f))
    drawCircle(bodyColor, size * 0.16f, Offset(cx + size * 0.28f, cy + size * 0.38f))

    val qColor = Color.White
    drawArc(
        color = qColor,
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(cx - size * 0.15f, cy - size * 0.22f),
        size = Size(size * 0.30f, size * 0.24f),
        style = Stroke(size * 0.08f, cap = StrokeCap.Round)
    )
    drawLine(
        color = qColor,
        start = Offset(cx + size * 0.06f, cy - size * 0.08f),
        end = Offset(cx, cy + size * 0.04f),
        strokeWidth = size * 0.08f,
        cap = StrokeCap.Round
    )
    drawCircle(qColor, size * 0.045f, Offset(cx, cy + size * 0.16f))
}
