package com.mamxanh.app.core.components.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.mamxanh.app.core.components.*
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppOpacity
import com.mamxanh.app.core.components.design.AppThemeEffects
import com.mamxanh.app.core.components.design.AppTypography
import com.mamxanh.core.ui.R
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

/** Half the usable app window, including the space occupied by app navigation. */
@Composable
fun mintTreeHeroHeight(
    speciesId: String = "",
    availableWidth: Dp? = null,
): Dp {
    val density = LocalDensity.current
    val windowHeight =
        with(density) {
            LocalWindowInfo.current.containerSize.height
                .toDp()
        }
    val bars = WindowInsets.systemBars.asPaddingValues()
    val halfWindow =
        ((windowHeight - bars.calculateTopPadding() - bars.calculateBottomPadding()) * .5f)
            .coerceAtLeast(AppDimensions.Dp240)
    // The temporary succulent sprite is wide. Trim empty scene space, retaining
    // its full visible width and aspect ratio without another bitmap decode.
    return if (speciesId == "succulent" && availableWidth != null) {
        minOf(halfWindow, (availableWidth - AppDimensions.Dp92).coerceAtLeast(AppDimensions.Dp48) * .62f)
    } else {
        halfWindow
    }
}

@Composable
fun MintCompactHeader(
    coins: Int,
    title: String = AppText.text(TextKey.CopyMamXanh7d3324),
    titleSize: androidx.compose.ui.unit.TextUnit = AppTypography.Sp17,
    actions: @Composable RowScope.() -> Unit = {},
) {
    val narrowLargeText =
        LocalDensity.current.fontScale > AppThemeEffects.LARGE_FONT_SCALE &&
            LocalConfiguration.current.screenWidthDp < AppThemeEffects.COMPACT_WIDTH
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = AppDimensions.Dp56)
            .padding(
                horizontal = if (narrowLargeText) AppDimensions.Dp12 else AppDimensions.Dp24,
                vertical = AppDimensions.Dp8,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp8),
    ) {
        if (!narrowLargeText) GardenArtwork(GardenArt.SPROUT, Modifier.size(AppDimensions.Dp16))
        Text(title, Modifier.weight(1f), fontSize = titleSize, lineHeight = titleSize * 1.2f, fontWeight = FontWeight.Bold, color = Deep)
        actions()
        MintCoinBadge(coins)
    }
}

@Composable
fun MintCoinBadge(coins: Int) {
    GardenBotanicalFrame(
        contentPadding = PaddingValues(horizontal = AppDimensions.Dp6, vertical = AppDimensions.Dp3),
        cornerSize = AppDimensions.Dp24,
    ) {
        GardenRichText(
            AppText.text(TextKey.Copy0Xu279a5c, coins),
            color = Deep,
            fontSize = AppTypography.Sp11,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun MintCareIcon(
    art: GardenArt,
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
    checked: Boolean = false,
    caption: String? = null,
) {
    Column(Modifier.width(AppDimensions.Dp48), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .size(AppDimensions.Dp48)
                .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
                .semantics(mergeDescendants = true) { contentDescription = label },
            contentAlignment = Alignment.Center,
        ) {
            androidx.compose.material3.Surface(
                Modifier.size(AppDimensions.Dp32).mintDepth(
                    androidx.compose.foundation.shape.CircleShape,
                    if (checked) MintDepth.hint else MintDepth.card,
                    if (enabled || checked) AppDimensions.Dp4 else AppDimensions.Dp1,
                ),
                shape = androidx.compose.foundation.shape.CircleShape,
                color = androidx.compose.ui.graphics.Color.Transparent,
                border = androidx.compose.foundation.BorderStroke(AppDimensions.Dp1, androidx.compose.ui.graphics.Color.White),
                shadowElevation = AppDimensions.Dp0,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    GardenArtwork(
                        art,
                        Modifier.size(AppDimensions.Dp20).alpha(
                            if (enabled ||
                                checked
                            ) {
                                AppOpacity.VALUE_1
                            } else {
                                AppOpacity.VALUE_4
                            },
                        ),
                    )
                    if (checked) {
                        Text(
                            "✓",
                            Modifier.align(Alignment.BottomEnd),
                            color = Green,
                            fontSize = AppTypography.Sp10,
                            lineHeight = AppTypography.Sp12,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
        if (caption != null) {
            Text(
                caption.replace('\n', ' '),
                color = Deep,
                fontSize = AppTypography.Sp9,
                lineHeight = AppTypography.Sp11,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview(name = "Tree controls", widthDp = 335, showBackground = true)
@Preview(name = "Tree controls at large text", widthDp = 280, fontScale = 1.5f, showBackground = true)
@Composable
private fun MintTreeControlsPreview() {
    val watering = stringResource(R.string.copy_tuoi_cay_0990f6)
    Column {
        MintCompactHeader(coins = 620)
        Row {
            MintCareIcon(GardenArt.DROP, watering, enabled = true, onClick = {})
            MintCareIcon(GardenArt.DROP, watering, enabled = false, onClick = {}, checked = true)
        }
    }
}
