package com.mamxanh.app.core.components.components

import com.mamxanh.app.core.components.design.AppColors
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppOpacity

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
import androidx.compose.ui.unit.dp

/** Scenery only: these illustrations contain no text or interactive targets. */
@Composable
fun GardenCottage(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        drawOval(AppColors.SwatchFF617F39, Offset(w * .04f, h * .86f), Size(w * .92f, h * .12f))
        drawRect(AppColors.SwatchFF9D713C, Offset(w * .65f, h * .17f), Size(w * .12f, h * .25f))
        drawRoundRect(AppColors.SwatchFFC99750, Offset(w * .19f, h * .42f), Size(w * .62f, h * .48f), CornerRadius(w * .04f))
        val roof = Path().apply {
            moveTo(w * .06f, h * .48f)
            lineTo(w * .50f, h * .12f)
            lineTo(w * .94f, h * .48f)
            close()
        }
        drawPath(roof, AppColors.SwatchFF866039)
        drawPath(roof, AppColors.SwatchFFD7AB62, style = Stroke(w * .045f, cap = StrokeCap.Round))
        drawRoundRect(AppColors.SwatchFF75552F, Offset(w * .45f, h * .65f), Size(w * .17f, h * .25f), CornerRadius(w * .02f))
        listOf(.27f, .65f).forEach { x ->
            drawRoundRect(AppColors.SwatchFFFFDA69, Offset(w * x, h * .55f), Size(w * .13f, h * .16f), CornerRadius(w * .02f))
            drawLine(AppColors.SwatchFFBA863F, Offset(w * (x + .065f), h * .55f), Offset(w * (x + .065f), h * .71f), w * .02f)
        }
    }
}

@Composable
fun GardenHangingLantern(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val bronze = AppColors.IllustrationBronze
        drawLine(bronze, Offset(w * .5f, 0f), Offset(w * .5f, h * .28f), w * .045f, StrokeCap.Round)
        drawOval(AppColors.SwatchFFFFD15E.copy(alpha = AppOpacity.Value_14), Offset(w * .02f, h * .30f), Size(w * .96f, h * .68f))
        drawRoundRect(bronze, Offset(w * .20f, h * .40f), Size(w * .60f, h * .43f), CornerRadius(w * .18f))
        drawRoundRect(AppColors.SwatchFFFFD15B, Offset(w * .29f, h * .44f), Size(w * .42f, h * .33f), CornerRadius(w * .12f))
        drawRoundRect(AppColors.SwatchFFFFEEA2, Offset(w * .39f, h * .47f), Size(w * .16f, h * .26f), CornerRadius(w * .06f))
        val cap = Path().apply {
            moveTo(w * .15f, h * .43f)
            quadraticTo(w * .5f, h * .20f, w * .85f, h * .43f)
            close()
        }
        drawPath(cap, AppColors.SwatchFFCB953F)
        drawLine(AppColors.SwatchFFE7B653, Offset(w * .18f, h * .43f), Offset(w * .82f, h * .43f), w * .07f, StrokeCap.Round)
        drawLine(bronze, Offset(w * .27f, h * .83f), Offset(w * .73f, h * .83f), w * .09f, StrokeCap.Round)
    }
}

@Composable
fun GardenBookHandles(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val handleWidth = AppDimensions.Dp14.toPx()
        val top = size.height * .36f
        val height = minOf(size.height * .20f, AppDimensions.Dp78.toPx())
        listOf(false, true).forEach { right ->
            fun x(value: Float) = if (right) size.width - value else value
            val handle = Path().apply {
                moveTo(x(handleWidth), top)
                cubicTo(x(AppDimensions.DpMinus5.toPx()), top - AppDimensions.Dp5.toPx(),
                    x(AppDimensions.DpMinus5.toPx()), top + height + AppDimensions.Dp5.toPx(), x(handleWidth), top + height)
            }
            drawPath(handle, AppColors.SwatchFFB27B3D, style = Stroke(AppDimensions.Dp10.toPx(), cap = StrokeCap.Round))
            drawPath(handle, AppColors.SwatchFFF0C685, style = Stroke(AppDimensions.Dp6.toPx(), cap = StrokeCap.Round))
        }
    }
}
