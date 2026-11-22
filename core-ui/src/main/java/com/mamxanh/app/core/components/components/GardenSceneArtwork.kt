package com.mamxanh.app.core.components.components

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import com.mamxanh.app.core.components.design.AppColors

internal fun DrawScope.drawGardenShop() {
    drawRoundRect(AppColors.Honey, Offset(20f, 42f), Size(60f, 46f), CornerRadius(5f))
    drawRect(AppColors.Paper, Offset(28f, 47f), Size(22f, 22f))
    drawRect(AppColors.SwatchFF7A9B54, Offset(55f, 55f), Size(15f, 32f))
    val canopy =
        Path().apply {
            moveTo(17f, 20f)
            lineTo(83f, 20f)
            lineTo(96f, 47f)
            lineTo(4f, 47f)
            close()
        }
    drawPath(canopy, AppColors.SwatchFFEE8A73)
    drawPath(
        Path().apply {
            moveTo(32f, 20f)
            lineTo(45f, 20f)
            lineTo(43f, 47f)
            lineTo(26f, 47f)
            close()
        },
        AppColors.Paper,
    )
    drawPath(
        Path().apply {
            moveTo(59f, 20f)
            lineTo(72f, 20f)
            lineTo(78f, 47f)
            lineTo(60f, 47f)
            close()
        },
        AppColors.Paper,
    )
}

internal fun DrawScope.drawGardenFlowers() {
    repeat(3) { i ->
        val x = 30f + i * 22
        val y = 50f - (i % 2) * 20
        drawLine(AppColors.DarkLeaf, Offset(x, 88f), Offset(x, y), 3f)
        repeat(5) { p ->
            val a =
                p * 72.0 * Math.PI / 180
            drawCircle(
                AppColors.SwatchFFFFA7AD,
                12f,
                Offset(
                    x + kotlin.math.cos(a).toFloat() * 12,
                    y + kotlin.math.sin(a).toFloat() * 12,
                ),
            )
        }
        drawCircle(AppColors.SwatchFFFFD56D, 6f, Offset(x, y))
    }
}

internal fun DrawScope.drawGardenLantern() {
    drawLine(AppColors.InkBrown, Offset(50f, 0f), Offset(50f, 24f), 4f)
    drawRoundRect(AppColors.Honey, Offset(23f, 23f), Size(54f, 62f), CornerRadius(17f))
    drawRoundRect(
        Brush.verticalGradient(listOf(AppColors.SwatchFFFFEE8A, AppColors.SwatchFFFBC44D)),
        Offset(31f, 32f),
        Size(38f, 43f),
        CornerRadius(12f),
    )
    drawRoundRect(AppColors.InkBrown, Offset(20f, 23f), Size(60f, 7f), CornerRadius(4f))
    drawRoundRect(AppColors.InkBrown, Offset(27f, 80f), Size(46f, 7f), CornerRadius(4f))
}

internal fun DrawScope.drawGardenRibbon() {
    rotate(-24f, Offset(50f, 46f)) { drawOval(AppColors.SwatchFFF58E9D, Offset(7f, 23f), Size(43f, 34f)) }
    rotate(24f, Offset(50f, 46f)) { drawOval(AppColors.SwatchFFF58E9D, Offset(50f, 23f), Size(43f, 34f)) }
    drawPath(
        Path().apply {
            moveTo(42f, 50f)
            lineTo(27f, 89f)
            lineTo(45f, 82f)
            lineTo(50f, 57f)
            lineTo(55f, 82f)
            lineTo(73f, 89f)
            lineTo(58f, 50f)
            close()
        },
        AppColors.SwatchFFEB8293,
    )
    drawRoundRect(AppColors.SwatchFFFFB3BB, Offset(40f, 33f), Size(20f, 24f), CornerRadius(6f))
}

internal fun DrawScope.drawGardenBoots() {
    repeat(2) { i ->
        translate(i * 32f, -i * 8f) {
            val boot =
                Path().apply {
                    moveTo(25f, 24f)
                    lineTo(55f, 24f)
                    lineTo(54f, 59f)
                    quadraticTo(71f, 70f, 66f, 84f)
                    lineTo(17f, 84f)
                    quadraticTo(9f, 66f, 27f, 59f)
                    close()
                }
            drawPath(boot, Brush.verticalGradient(listOf(AppColors.SwatchFFDEAA65, AppColors.SwatchFFC4823E)))
            drawLine(AppColors.InkBrown, Offset(18f, 82f), Offset(65f, 82f), 6f, StrokeCap.Round)
            repeat(3) { r -> drawLine(AppColors.Paper, Offset(31f, 37f + r * 9), Offset(47f, 37f + r * 9), 3f, StrokeCap.Round) }
        }
    }
}

internal fun DrawScope.drawGardenBroom() {
    rotate(28f, Offset(50f, 50f)) {
        drawLine(AppColors.InkBrown, Offset(50f, 10f), Offset(50f, 62f), 9f, StrokeCap.Round)
        drawLine(AppColors.Honey, Offset(48f, 12f), Offset(48f, 58f), 3f, StrokeCap.Round)
        val bristles =
            Path().apply {
                moveTo(37f, 57f)
                lineTo(63f, 57f)
                lineTo(79f, 91f)
                quadraticTo(50f, 99f, 21f, 91f)
                close()
            }
        drawPath(bristles, Brush.verticalGradient(listOf(AppColors.SwatchFFE6BC72, AppColors.Honey), 57f, 96f))
        drawPath(bristles, AppColors.InkBrown, style = Stroke(2f))
        repeat(7) { index ->
            drawLine(
                AppColors.SwatchFFB17B3F,
                Offset(39f + index * 3.7f, 63f),
                Offset(28f + index * 7.3f, 90f),
                1.3f,
            )
        }
        drawLine(AppColors.DarkLeaf, Offset(35f, 61f), Offset(65f, 61f), 5f, StrokeCap.Round)
    }
}
