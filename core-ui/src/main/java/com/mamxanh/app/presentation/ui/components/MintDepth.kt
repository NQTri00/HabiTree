package com.mamxanh.app.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** One light direction and elevation palette for all live mint surfaces. */
object MintDepth {
    val card = listOf(Color.White, Color(0xFFF4FCF7), Color(0xFFE0F1E7))
    val hint = listOf(Color(0xFFF5FCEB), Color(0xFFE5F3D9), Color(0xFFD3E9C3))
    val price = listOf(Color(0xFFFFFCEB), Color(0xFFFFF0BE), Color(0xFFEFD791))
    val primary = listOf(Color(0xFF237F4F), Color(0xFF146C40), Color(0xFF075432))
    val secondary = listOf(Color.White, Color(0xFFEDF8F1), Color(0xFFD8EDE1))
    val bar = listOf(Color(0xFFFEFFFE), Color(0xFFF1FAF5), Color(0xFFDCEEE3))
    val shadow = Color(0xFF1E543D)
}

/** Opaque, content-sized gradient with a lit rim; elevation never changes layout size. */
fun Modifier.mintDepth(
    shape: Shape,
    colors: List<Color> = MintDepth.card,
    elevation: Dp = 6.dp
): Modifier = shadow(
    elevation = elevation, shape = shape, clip = false,
    ambientColor = MintDepth.shadow.copy(alpha = .45f),
    spotColor = MintDepth.shadow.copy(alpha = .70f)
).background(Brush.verticalGradient(colors), shape)
    .border(1.dp, Brush.verticalGradient(listOf(Color.White.copy(alpha = .96f),
        Color(0xFFAACDBA).copy(alpha = .65f))), shape)
