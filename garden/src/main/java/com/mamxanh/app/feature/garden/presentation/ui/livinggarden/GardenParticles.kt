package com.mamxanh.app.feature.garden.presentation.ui.livinggarden

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import com.mamxanh.app.core.components.design.AppColors
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppOpacity
import kotlin.math.sin

@Composable
internal fun GardenParticles(low: Boolean) {
    val transition = rememberInfiniteTransition(label = "garden-ambience")
    val fraction by transition.animateFloat(0f, 1f, infiniteRepeatable(tween(10000, easing = LinearEasing)), label = "petals")
    Canvas(Modifier.fillMaxSize()) {
        repeat(if (low)4 else 8) { i ->
            drawCircle(
                AppColors.SwatchFFE6E6AC.copy(alpha = AppOpacity.VALUE_45),
                AppDimensions.Dp3.toPx(),
                Offset(
                    ((i * .173f + .12f) % 1f) * size.width + sin((fraction + i) * 6.28f) * AppDimensions.Dp12.toPx(),
                    ((fraction + i * .137f) % 1f) * size.height,
                ),
            )
        }
    }
}
