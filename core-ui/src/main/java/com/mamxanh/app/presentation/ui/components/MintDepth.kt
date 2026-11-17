package com.mamxanh.app.presentation.ui.components

import com.mamxanh.app.presentation.ui.design.AppColors
import com.mamxanh.app.presentation.ui.design.AppDimensions
import com.mamxanh.app.presentation.ui.design.AppOpacity

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** One light direction and elevation palette for all live mint surfaces. */
object MintDepth {
    val card = listOf(AppColors.White, AppColors.White, AppColors.SwatchFFFEFFFC)
    val hint = listOf(AppColors.Pale, AppColors.Pale)
    val price = listOf(AppColors.SwatchFFFFF9E9, AppColors.RewardSurface)
    val primary = listOf(AppColors.Green, AppColors.Green)
    val secondary = listOf(AppColors.White, AppColors.White)
    val bar = listOf(AppColors.Cream, AppColors.Cream)
    val shadow = AppColors.Deep

    /** A true 45° light direction, independent of a card or button's aspect ratio. */
    fun diagonal(colors: List<Color>): Brush = object : ShaderBrush() {
        override fun createShader(size: Size): Shader {
            val center = Offset(size.width / 2f, size.height / 2f)
            val reach = (size.width + size.height) / 4f
            val axis = Offset(reach, reach)
            return LinearGradientShader(center - axis, center + axis, colors)
        }
    }
}

/** Opaque, content-sized gradient with a lit rim; elevation never changes layout size. */
fun Modifier.mintDepth(
    shape: Shape,
    colors: List<Color> = MintDepth.card,
    elevation: Dp = AppDimensions.Dp6
): Modifier = shadow(
    elevation = elevation, shape = shape, clip = false,
    ambientColor = MintDepth.shadow.copy(alpha = AppOpacity.Value_12),
    spotColor = MintDepth.shadow.copy(alpha = AppOpacity.Value_20)
).background(MintDepth.diagonal(colors), shape)
    .border(AppDimensions.Dp1, MintDepth.diagonal(listOf(AppColors.White.copy(alpha = AppOpacity.Value_96),
        AppColors.SwatchFFE4E7DC)), shape)
