package com.mamxanh.app.presentation.ui.components

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
    val card = listOf(Color.White, Color.White, Color(0xFFFEFFFC))
    val hint = listOf(Color(0xFFEBF0DF), Color(0xFFEBF0DF))
    val price = listOf(Color(0xFFFFF9E9), Color(0xFFF3E8C9))
    val primary = listOf(Color(0xFF315C3C), Color(0xFF315C3C))
    val secondary = listOf(Color.White, Color.White)
    val bar = listOf(Color(0xFFF7F8F2), Color(0xFFF7F8F2))
    val shadow = Color(0xFF23382B)

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
    elevation: Dp = 6.dp
): Modifier = shadow(
    elevation = elevation, shape = shape, clip = false,
    ambientColor = MintDepth.shadow.copy(alpha = .12f),
    spotColor = MintDepth.shadow.copy(alpha = .20f)
).background(MintDepth.diagonal(colors), shape)
    .border(1.dp, MintDepth.diagonal(listOf(Color.White.copy(alpha = .96f),
        Color(0xFFE4E7DC))), shape)
