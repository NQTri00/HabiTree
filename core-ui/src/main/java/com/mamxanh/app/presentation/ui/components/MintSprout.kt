package com.mamxanh.app.presentation.ui.components

import com.mamxanh.app.presentation.ui.design.AppColors
import com.mamxanh.app.presentation.ui.design.AppDimensions
import com.mamxanh.app.presentation.ui.design.AppOpacity

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/** Native vector recreation of the two-leaf sprout in the current annotated concepts. */
@Composable
fun MintSprout(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        drawLine(AppColors.SwatchFF468F20, Offset(w * .5f, h * .9f), Offset(w * .5f, h * .42f),
            size.minDimension * .045f, StrokeCap.Round)
        val left = Path().apply {
            moveTo(w * .50f, h * .60f)
            cubicTo(w * .19f, h * .66f, w * .09f, h * .32f, w * .08f, h * .17f)
            cubicTo(w * .38f, h * .14f, w * .56f, h * .33f, w * .50f, h * .60f)
            close()
        }
        val right = Path().apply {
            moveTo(w * .51f, h * .54f)
            cubicTo(w * .48f, h * .21f, w * .70f, h * .10f, w * .95f, h * .08f)
            cubicTo(w * .95f, h * .36f, w * .80f, h * .58f, w * .51f, h * .54f)
            close()
        }
        drawPath(left, Brush.linearGradient(listOf(AppColors.SwatchFFBFEE57, AppColors.SwatchFF459D22), Offset(w * .1f, h * .2f), Offset(w * .6f, h * .6f)))
        drawPath(right, Brush.linearGradient(listOf(AppColors.SwatchFFD5F67B, AppColors.SwatchFF5AAA21), Offset(w * .6f, h * .1f), Offset(w * .7f, h * .6f)))
        drawLine(AppColors.SwatchFFE8FAB0.copy(alpha = AppOpacity.Value_7), Offset(w * .5f, h * .58f), Offset(w * .20f, h * .29f), size.minDimension * .018f)
        drawLine(AppColors.SwatchFFE8FAB0.copy(alpha = AppOpacity.Value_7), Offset(w * .52f, h * .53f), Offset(w * .83f, h * .20f), size.minDimension * .018f)
    }
}

@Composable
fun MintEmptySprout(modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height
            drawOval(AppColors.SwatchFFBBE394, Offset(w * .08f, h * .78f), Size(w * .84f, h * .16f))
            drawOval(Brush.verticalGradient(listOf(AppColors.SwatchFFC49664, AppColors.SwatchFF925B35), h * .75f, h * .94f),
                Offset(w * .23f, h * .75f), Size(w * .54f, h * .16f))
            repeat(5) { index ->
                val angle = Math.toRadians(-145.0 + index * 27.5)
                val center = Offset(w * .5f, h * .45f)
                val inner = size.minDimension * .34f
                val outer = size.minDimension * .42f
                drawLine(AppColors.SwatchFFFFC827,
                    center + Offset(kotlin.math.cos(angle).toFloat() * inner, kotlin.math.sin(angle).toFloat() * inner),
                    center + Offset(kotlin.math.cos(angle).toFloat() * outer, kotlin.math.sin(angle).toFloat() * outer),
                    AppDimensions.Dp4.toPx(), StrokeCap.Round)
            }
        }
        MintSprout(Modifier.fillMaxWidth(.46f).fillMaxHeight(.55f).align(Alignment.Center).offset(y = AppDimensions.Dp12))
    }
}

@Preview(widthDp = 160, heightDp = 160, device = "spec:width=160dp,height=160dp,dpi=160")
@Composable
fun MintEmptySproutPreview() { MintEmptySprout(Modifier.fillMaxSize()) }
