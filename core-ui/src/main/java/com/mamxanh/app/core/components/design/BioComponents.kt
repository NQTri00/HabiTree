package com.mamxanh.app.core.components.design

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.mamxanh.core.ui.R

@Composable fun BioHeader(
    title: String,
    coins: Int,
    primary: Boolean,
    onBack: () -> Unit,
    onMore: () -> Unit,
    showWallet: Boolean = true,
) {
    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().height(AppDimensions.Dp71).padding(
                start =
                    if (LocalConfiguration.current.screenWidthDp <
                        AppThemeEffects.COMPACT_WIDTH
                    ) {
                        AppDimensions.Dp14
                    } else {
                        AppDimensions.Dp20
                    },
                end =
                    if (LocalConfiguration.current.screenWidthDp <
                        AppThemeEffects.COMPACT_WIDTH
                    ) {
                        AppDimensions.Dp14
                    } else {
                        AppDimensions.Dp20
                    },
                top = AppDimensions.Dp10,
                bottom = AppDimensions.Dp8,
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp4),
        ) {
            if (primary) {
                Row(
                    Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp7),
                ) {
                    Bi("brand")
                    Bt(
                        stringResource(R.string.app_name),
                        size = AppTypography.SIZE_18,
                        bold = true,
                        spacing = AppTypography.TRACKING_MINUS_7,
                    )
                    Box(Modifier.padding(bottom = AppDimensions.Dp15).size(AppDimensions.Dp5).background(bp.deep, RoundedCornerShape(50)))
                }
            } else {
                BioIconButton("back", onBack)
                Bt(title, Modifier.weight(1f), size = AppTypography.SIZE_15, semi = true, maxLines = 1)
            }
            if (showWallet) {
                Row(
                    Modifier
                        .background(
                            AppColors.SwatchFFFFFDF5,
                            RoundedCornerShape(AppDimensions.Dp30),
                        ).border(
                            AppDimensions.Dp1,
                            AppColors.SwatchFFD8D0BD,
                            RoundedCornerShape(AppDimensions.Dp30),
                        ).padding(horizontal = AppDimensions.Dp10, vertical = AppDimensions.Dp7),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp5),
                ) {
                    Bi("coin", size = AppIcons.SIZE_15, tint = AppColors.SwatchFF765F27)
                    Bt(coins.toString(), size = AppTypography.SIZE_12, mono = true)
                }
            }
            if (primary)BioIconButton("more", onMore)
        }
        Box(
            Modifier
                .padding(
                    horizontal = AppDimensions.Dp24,
                ).fillMaxWidth()
                .height(AppDimensions.Dp1)
                .background(Brush.horizontalGradient(listOf(bp.line, AppColors.Transparent))),
        )
    }
}

@Composable fun BioMetrics(
    metrics: List<Pair<String, String>>,
    soft: Boolean = false,
) {
    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp8)) {
        metrics.forEach { (label, value) ->
            BioCard(
                Modifier.weight(1f).fillMaxHeight(),
                tint = if (soft)bp.soft else bp.surface,
                padding = AppSpacing.VALUE_7,
                radius = AppRadii.VALUE_18,
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(vertical = AppDimensions.Dp5),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp5),
                ) {
                    Bt(value, size = AppTypography.SIZE_19, line = AppTypography.LINE_HEIGHT_29_45, mono = true)
                    Bt(label, size = AppTypography.SIZE_12, tint = bp.muted, align = TextAlign.Center)
                }
            }
        }
    }
}

@Preview(name = "Home app header", widthDp = 375, showBackground = true)
@Composable
private fun BioHeaderPreview() {
    BioHeader(stringResource(R.string.app_name), coins = 620, primary = true, onBack = {}, onMore = {})
}

@Preview(name = "Home metrics", widthDp = 335, showBackground = true)
@Composable
private fun BioMetricsPreview() {
    BioMetrics(listOf("Today’s tasks" to "1/4", "Coins today" to "8/50", "Streak" to "3"))
}
