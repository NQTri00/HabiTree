package com.mamxanh.app.presentation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.runtime.Composable
import kotlin.math.min

/**
 * A deterministic, scalable tree illustration. The same species and growth value
 * always produce the same silhouette; no bitmap or network asset is required.
 */
@Composable
fun TreeIllustration(
    growth: Int,
    speciesId: String,
    decoration: String = "NONE",
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.aspectRatio(1.12f)) {
        val w = size.width
        val h = size.height
        val cx = w * 0.5f
        val groundY = h * 0.755f
        val progress = (growth.coerceIn(0, 120) / 120f)
        val mature = progress >= 1f
        val leaf = when (speciesId) {
            "cherry" -> Color(0xFF4E965A)
            "orange" -> Color(0xFF43884A)
            "pine" -> Color(0xFF286C50)
            "maple" -> Color(0xFFB65A3C)
            "bamboo" -> Color(0xFF5A9B51)
            else -> Color(0xFF4E965A)
        }
        val leafLight = when (speciesId) {
            "cherry" -> Color(0xFF8BCB85)
            "orange" -> Color(0xFF9BCB63)
            "pine" -> Color(0xFF75A98A)
            "maple" -> Color(0xFFE39A57)
            "bamboo" -> Color(0xFFA5D17D)
            else -> Color(0xFF8BCB85)
        }
        val bark = Color(0xFF80563A)
        val outline = Color(0xFF493C30)

        drawRoundRect(
            color = Color(0xFFEAF3E7),
            size = size,
            cornerRadius = CornerRadius(w * 0.07f)
        )
        drawCircle(Color(0xFFFFF0C4), radius = min(w, h) * 0.075f, center = Offset(w * 0.79f, h * 0.17f))
        drawOval(
            color = Color(0xFFD7E6D0),
            topLeft = Offset(w * 0.08f, groundY - h * 0.025f),
            size = Size(w * 0.84f, h * 0.09f)
        )

        // Pot body and rim.
        val pot = Path().apply {
            moveTo(w * 0.29f, groundY)
            lineTo(w * 0.71f, groundY)
            lineTo(w * 0.665f, h * 0.94f)
            quadraticTo(w * 0.5f, h * 0.975f, w * 0.335f, h * 0.94f)
            close()
        }
        drawPath(pot, Color(0xFFB9784D))
        drawPath(pot, outline, style = Stroke(width = w * 0.012f))
        drawRoundRect(
            color = Color(0xFFD89A68),
            topLeft = Offset(w * 0.275f, groundY - h * 0.018f),
            size = Size(w * 0.45f, h * 0.045f),
            cornerRadius = CornerRadius(w * 0.018f)
        )
        drawOval(
            color = Color(0xFF6C4A35),
            topLeft = Offset(w * 0.305f, groundY - h * 0.018f),
            size = Size(w * 0.39f, h * 0.035f)
        )

        // Seed stage: a seed resting just below the soil line.
        if (growth <= 0) {
            drawOval(
                color = Color(0xFF9A6943),
                topLeft = Offset(cx - w * 0.035f, groundY - h * 0.035f),
                size = Size(w * 0.07f, h * 0.045f)
            )
            drawLine(Color(0xFFD8B28A), Offset(cx - w * 0.01f, groundY - h * 0.028f), Offset(cx + w * 0.012f, groundY - h * 0.006f), w * 0.006f)
        } else {
            val stemHeight = h * (0.055f + 0.40f * progress)
            val topY = groundY - stemHeight
            val trunkWidth = w * (0.012f + 0.035f * progress)
            val trunk = Path().apply {
                moveTo(cx - trunkWidth, groundY)
                cubicTo(cx - trunkWidth * 0.8f, groundY - stemHeight * 0.38f, cx - trunkWidth * 0.45f, topY + stemHeight * 0.12f, cx, topY)
                cubicTo(cx + trunkWidth * 0.5f, topY + stemHeight * 0.12f, cx + trunkWidth * 0.8f, groundY - stemHeight * 0.35f, cx + trunkWidth, groundY)
                close()
            }
            drawPath(trunk, bark)
            drawPath(trunk, outline, style = Stroke(width = w * 0.006f))

            if (progress < 0.10f) {
                // Two cotyledons on the new sprout.
                drawLine(Color(0xFF4F914D), Offset(cx, topY + h * 0.018f), Offset(cx - w * 0.075f, topY - h * 0.015f), w * 0.018f, cap = StrokeCap.Round)
                drawLine(Color(0xFF4F914D), Offset(cx, topY + h * 0.018f), Offset(cx + w * 0.075f, topY - h * 0.015f), w * 0.018f, cap = StrokeCap.Round)
                drawOval(leafLight, Offset(cx - w * 0.12f, topY - h * 0.055f), Size(w * 0.12f, h * 0.065f))
                drawOval(leaf, Offset(cx + w * 0.015f, topY - h * 0.055f), Size(w * 0.12f, h * 0.065f))
            } else {
                val crownRadius = h * (0.055f + 0.19f * progress)
                val crownY = topY + crownRadius * 0.42f
                val branchReach = w * (0.07f + 0.16f * progress)
                // Main branches become visible as the trunk grows.
                val branchCount = (2 + progress * 4f).toInt()
                for (i in 0 until branchCount) {
                    val side = if (i % 2 == 0) -1f else 1f
                    val level = 0.28f + (i / 2) * 0.22f
                    val start = Offset(cx, groundY - stemHeight * level)
                    val end = Offset(cx + side * branchReach, start.y - stemHeight * 0.12f)
                    drawLine(bark, start, end, w * (0.008f + progress * 0.012f), cap = StrokeCap.Round)
                }

                // A cluster of overlapping leaf masses creates a soft, illustrated canopy.
                val clusters = listOf(
                    Offset(-0.56f, 0.08f), Offset(-0.28f, -0.30f), Offset(0f, -0.43f),
                    Offset(0.30f, -0.28f), Offset(0.57f, 0.06f), Offset(-0.20f, 0.22f),
                    Offset(0.22f, 0.20f), Offset(0f, 0.05f), Offset(-0.48f, -0.26f),
                    Offset(0.48f, -0.24f)
                )
                val visible = (3 + progress * 7f).toInt().coerceIn(3, clusters.size)
                for (i in 0 until visible) {
                    val p = clusters[i]
                    val radius = crownRadius * (if (i % 3 == 0) 0.62f else 0.53f)
                    val center = Offset(cx + p.x * crownRadius, crownY + p.y * crownRadius)
                    drawCircle(if (i % 2 == 0) leaf else leafLight, radius, center)
                    drawCircle(leaf.copy(alpha = 0.25f), radius, center, style = Stroke(width = w * 0.004f))
                }

                // Species details appear only near maturity.
                if (progress >= 0.72f) {
                    val detailCount = if (mature) 7 else 3
                    for (i in 0 until detailCount) {
                        val x = cx + ((i % 4) - 1.5f) * crownRadius * 0.36f
                        val y = crownY + ((i / 4) - 0.25f) * crownRadius * 0.65f
                        when (speciesId) {
                            "orange" -> drawCircle(Color(0xFFFFA62B), crownRadius * 0.105f, Offset(x, y))
                            "cherry", "maple" -> drawCircle(Color(0xFFFFD3D9), crownRadius * 0.10f, Offset(x, y))
                            else -> drawCircle(Color(0xFFB9D7A1), crownRadius * 0.07f, Offset(x, y))
                        }
                    }
                }

                // Decoration is a persisted property of this tree.
                when (decoration) {
                    "FLOWERS" -> {
                        val points = listOf(Offset(-0.48f, -0.16f), Offset(0.34f, -0.34f), Offset(0.48f, 0.15f))
                        points.forEach { p ->
                            val x = cx + p.x * crownRadius
                            val y = crownY + p.y * crownRadius
                            drawCircle(Color(0xFFFFD6E0), crownRadius * 0.12f, Offset(x, y))
                            drawCircle(Color(0xFFFFC857), crownRadius * 0.045f, Offset(x, y))
                        }
                    }
                    "LANTERNS" -> {
                        listOf(-0.45f, 0.45f).forEach { side ->
                            val x = cx + side * crownRadius
                            val y = crownY + crownRadius * 0.42f
                            drawLine(outline, Offset(x, y - h * 0.06f), Offset(x, y), w * 0.006f)
                            drawRoundRect(Color(0xFFFFC857), Offset(x - w * 0.025f, y), Size(w * 0.05f, h * 0.055f), CornerRadius(w * 0.012f))
                        }
                    }
                    "RIBBON" -> {
                        val y = groundY - stemHeight * 0.42f
                        drawLine(Color(0xFFDB6B72), Offset(cx - w * 0.035f, y), Offset(cx + w * 0.035f, y), w * 0.025f, cap = StrokeCap.Round)
                        drawCircle(Color(0xFFFFC1C5), w * 0.018f, Offset(cx, y))
                    }
                }
            }
        }
    }
}
