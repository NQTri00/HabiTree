package com.mamxanh.app.feature.garden.presentation.ui.livinggarden

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.*
import com.mamxanh.app.core.components.components.*
import com.mamxanh.app.core.components.design.AppColors
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppTypography
import com.mamxanh.shared.model.*

@Composable internal fun PlotProgress(
    progress: Float,
    label: String? = null,
) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(
                if (label ==
                    null
                ) {
                    AppDimensions.Dp4
                } else {
                    with(LocalDensity.current) { AppTypography.Sp12.toDp() }
                },
            ).clip(RoundedCornerShape(AppDimensions.Dp6))
            .background(AppColors.SwatchFFDCEBE1),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(
                    progress.coerceIn(0f, 1f),
                ).align(Alignment.CenterStart)
                .background(MintDepth.diagonal(listOf(AppColors.SwatchFFA6D869, AppColors.SwatchFF74BA64))),
        )
        if (label != null) {
            Text(
                label,
                color = Deep,
                fontSize = AppTypography.Sp9,
                lineHeight = AppTypography.Sp11,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Preview(name = "Plot growth progress", widthDp = 180, showBackground = true)
@Composable
private fun PlotProgressPreview() {
    MamXanhPreviewTheme { PlotProgress(progress = 0.6f, label = "60%") }
}
