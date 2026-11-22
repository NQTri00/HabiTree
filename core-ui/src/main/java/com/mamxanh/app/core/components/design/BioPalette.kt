package com.mamxanh.app.core.components.design

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*

data class BioPalette(
    val accent: Color,
    val soft: Color,
    val bg: Color,
    val deep: Color,
    val muted: Color,
    val line: Color,
    val hue: Float = AppThemeEffects.DEFAULT_HUE,
) {
    val ink = AppColors.Ink
    val surface = AppColors.Surface
    val control get() =
        Color(
            androidx.core.graphics.ColorUtils
                .HSLToColor(floatArrayOf(hue, AppThemeEffects.CONTROL_SATURATION, AppThemeEffects.CONTROL_LIGHTNESS)),
        )
}

val LocalBioPalette =
    staticCompositionLocalOf {
        BioPalette(
            AppColors.SwatchFFF4C7D8,
            AppColors.SwatchFFF6E3EB,
            AppColors.SwatchFFEEE8EB,
            AppColors.SwatchFF71354F,
            AppColors.SwatchFF705F68,
            AppColors.SwatchFFD3C3CB,
        )
    }
val LocalBioExpanded = staticCompositionLocalOf { false }
val LocalBioButtonSize = staticCompositionLocalOf { AppTypography.SIZE_14 }
val bp: BioPalette @Composable get() = LocalBioPalette.current

fun color(hex: String) = Color(android.graphics.Color.parseColor(hex))
