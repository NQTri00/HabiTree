package com.mamxanh.app.presentation.ui.components

import com.mamxanh.app.presentation.ui.design.AppColors
import com.mamxanh.app.presentation.ui.design.AppOpacity

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
            "cherry" -> AppColors.SwatchFF4E965A
            "orange" -> AppColors.SwatchFF43884A
            "pine" -> AppColors.SwatchFF286C50
            "maple" -> AppColors.SwatchFFB65A3C
            "bamboo" -> AppColors.SwatchFF5A9B51
            else -> AppColors.SwatchFF4E965A
        }
        val leafLight = when (speciesId) {
            "cherry" -> AppColors.SwatchFF8BCB85
            "orange" -> AppColors.SwatchFF9BCB63
            "pine" -> AppColors.SwatchFF75A98A
            "maple" -> AppColors.SwatchFFE39A57
            "bamboo" -> AppColors.SwatchFFA5D17D
            else -> AppColors.SwatchFF8BCB85
        }
        val bark = AppColors.IllustrationBark
        val outline = AppColors.IllustrationOutline

        drawRoundRect(
            color = AppColors.SwatchFFEAF3E7,
            size = size,
            cornerRadius = CornerRadius(w * 0.07f)
        )
        drawCircle(AppColors.SwatchFFFFF0C4, radius = min(w, h) * 0.075f, center = Offset(w * 0.79f, h * 0.17f))
        drawOval(
            color = AppColors.SwatchFFD7E6D0,
            topLeft = Offset(w * 0.08f, groundY - h * 0.025f),
            size = Size(w * 0.84f, h * 0.09f)
        )

        // Floating garden island: the tree grows directly from soil, not a flower pot.
        drawOval(
            color = AppColors.SwatchFFB7D5C1,
            topLeft = Offset(w * 0.055f, h * 0.665f),
            size = Size(w * 0.89f, h * 0.245f)
        )
        drawOval(
            color = AppColors.SwatchFFC98C4E,
            topLeft = Offset(w * 0.095f, h * 0.635f),
            size = Size(w * 0.81f, h * 0.235f)
        )
        drawOval(
            color = AppColors.SwatchFFE4B66F,
            topLeft = Offset(w * 0.17f, h * 0.66f),
            size = Size(w * 0.58f, h * 0.075f)
        )
        // Tiny grass tufts around the island.
        listOf(0.18f, 0.31f, 0.72f, 0.83f).forEachIndexed { index, x ->
            val y = h * (0.72f + (index % 2) * 0.06f)
            drawLine(AppColors.SwatchFF78AD59, Offset(w*x, y), Offset(w*(x-0.025f), y-h*.035f), w*.012f, cap = StrokeCap.Round)
            drawLine(AppColors.SwatchFF78AD59, Offset(w*x, y), Offset(w*(x+0.025f), y-h*.04f), w*.012f, cap = StrokeCap.Round)
        }

        // Seed stage: a seed resting just below the soil line.
        if (growth <= 0) {
            drawOval(
                color = AppColors.SwatchFF9A6943,
                topLeft = Offset(cx - w * 0.035f, groundY - h * 0.035f),
                size = Size(w * 0.07f, h * 0.045f)
            )
            drawLine(AppColors.SwatchFFD8B28A, Offset(cx - w * 0.01f, groundY - h * 0.028f), Offset(cx + w * 0.012f, groundY - h * 0.006f), w * 0.006f)
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
                drawLine(AppColors.SwatchFF4F914D, Offset(cx, topY + h * 0.018f), Offset(cx - w * 0.075f, topY - h * 0.015f), w * 0.018f, cap = StrokeCap.Round)
                drawLine(AppColors.SwatchFF4F914D, Offset(cx, topY + h * 0.018f), Offset(cx + w * 0.075f, topY - h * 0.015f), w * 0.018f, cap = StrokeCap.Round)
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
                    drawCircle(leaf.copy(alpha = AppOpacity.Value0_25), radius, center, style = Stroke(width = w * 0.004f))
                }

                // Species details appear only near maturity.
                if (progress >= 0.72f) {
                    val detailCount = if (mature) 7 else 3
                    for (i in 0 until detailCount) {
                        val x = cx + ((i % 4) - 1.5f) * crownRadius * 0.36f
                        val y = crownY + ((i / 4) - 0.25f) * crownRadius * 0.65f
                        when (speciesId) {
                            "orange" -> drawCircle(AppColors.SwatchFFFFA62B, crownRadius * 0.105f, Offset(x, y))
                            "cherry", "maple" -> drawCircle(AppColors.SwatchFFFFD3D9, crownRadius * 0.10f, Offset(x, y))
                            else -> drawCircle(AppColors.SwatchFFB9D7A1, crownRadius * 0.07f, Offset(x, y))
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
                            drawCircle(AppColors.SwatchFFFFD6E0, crownRadius * 0.12f, Offset(x, y))
                            drawCircle(AppColors.SwatchFFFFC857, crownRadius * 0.045f, Offset(x, y))
                        }
                    }
                    "LANTERNS" -> {
                        listOf(-0.45f, 0.45f).forEach { side ->
                            val x = cx + side * crownRadius
                            val y = crownY + crownRadius * 0.42f
                            drawLine(outline, Offset(x, y - h * 0.06f), Offset(x, y), w * 0.006f)
                            drawRoundRect(AppColors.SwatchFFFFC857, Offset(x - w * 0.025f, y), Size(w * 0.05f, h * 0.055f), CornerRadius(w * 0.012f))
                        }
                    }
                    "RIBBON" -> {
                        val y = groundY - stemHeight * 0.42f
                        drawLine(AppColors.SwatchFFDB6B72, Offset(cx - w * 0.035f, y), Offset(cx + w * 0.035f, y), w * 0.025f, cap = StrokeCap.Round)
                        drawCircle(AppColors.SwatchFFFFC1C5, w * 0.018f, Offset(cx, y))
                    }
                }
            }
        }
    }
}
