package com.mamxanh.app.core.components.design

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
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

@Composable fun BioButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: String = "",
    secondary: Boolean = false,
    enabled: Boolean = true,
    danger: Boolean = false,
    labelSize: Int = LocalBioButtonSize.current,
    interactionSource: MutableInteractionSource? = null,
) {
    val shape = bioShape(AppRadii.VALUE_14)
    val fill =
        if (danger) {
            AppColors.SwatchFFF7DFDF
        } else if (secondary) {
            AppColors.White.copy(alpha = AppOpacity.VALUE_8)
        } else {
            bp.accent
        }
    Row(
        modifier
            .heightIn(
                min = AppDimensions.Dp48,
            ).alpha(
                if (enabled)AppOpacity.VALUE_1 else AppOpacity.VALUE_48,
            ).shadow(
                if (enabled)AppDimensions.Dp2 else AppDimensions.Dp0,
                shape,
                ambientColor = bp.deep.copy(alpha = AppOpacity.VALUE_08),
                spotColor = bp.deep.copy(alpha = AppOpacity.VALUE_08),
            ).background(fill, shape)
            .border(
                AppDimensions.Dp1,
                if (danger) {
                    AppColors.SwatchFFE2B8B8
                } else if (secondary) {
                    bp.line
                } else {
                    lerp(bp.accent, bp.deep, AppThemeEffects.BUTTON_OUTLINE_BLEND)
                },
                shape,
            ).clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            ).padding(
                horizontal =
                    if (LocalBioButtonSize.current ==
                        13
                    ) {
                        AppDimensions.Dp8
                    } else if (LocalConfiguration.current.screenWidthDp <
                        AppThemeEffects.COMPACT_WIDTH
                    ) {
                        AppDimensions.Dp10
                    } else {
                        AppDimensions.Dp18
                    },
                vertical = AppDimensions.Dp10,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (icon.isNotBlank()) {
            Bi(icon, tint = if (danger)AppColors.SwatchFF8C363B else bp.ink, size = AppIcons.SIZE_18)
            Spacer(Modifier.width(AppDimensions.Dp8))
        }
        Bt(label, size = labelSize, semi = true, tint = if (danger)AppColors.SwatchFF8C363B else bp.ink, align = TextAlign.Center)
    }
}

@Composable fun BioIconButton(
    icon: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    boxed: Boolean = false,
    enabled: Boolean = true,
    iconSize: Int = AppIcons.SIZE_20,
    tint: Color = bp.ink,
    label: String =
        when (icon) {
            "close" -> stringResource(R.string.copy_dong_5d54c2)
            "back" -> stringResource(R.string.copy_quay_lai_6d7935)
            "next" -> stringResource(R.string.copy_tiep_theo_1c33a9)
            "more" -> stringResource(R.string.copy_them_chuc_nang_545f7b)
            "plus" -> stringResource(R.string.copy_them_dd9262)
            "bag" -> stringResource(R.string.copy_mo_tui_do_f3f7f9)
            "settings" -> stringResource(R.string.copy_tuy_chon_598c1b)
            "refresh" -> stringResource(R.string.copy_lam_moi_b4c613)
            else -> icon
        },
) {
    Box(
        modifier
            .size(
                AppDimensions.Dp48,
            ).alpha(
                if (enabled)AppOpacity.VALUE_1 else AppOpacity.VALUE_45,
            ).then(
                if (boxed) {
                    Modifier
                        .background(
                            bp.surface,
                            bioShape(AppRadii.VALUE_12),
                        ).border(AppDimensions.Dp1, bp.line, bioShape(AppRadii.VALUE_12))
                } else {
                    Modifier
                },
            ).semantics {
                contentDescription =
                    label
            }.clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) { Bi(icon, size = iconSize, tint = tint) }
}

@Composable fun BioTextAction(
    label: String,
    onClick: () -> Unit,
    icon: String = "",
    tint: Color = bp.deep,
    modifier: Modifier = Modifier,
    iconFirst: Boolean = false,
) {
    Row(
        modifier
            .heightIn(
                min = AppDimensions.Dp48,
            ).widthIn(
                min = AppDimensions.Dp48,
            ).clickable(onClick = onClick)
            .padding(horizontal = AppDimensions.Dp2, vertical = AppDimensions.Dp8),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (iconFirst &&
            icon.isNotBlank()
        ) {
            Bi(icon, tint = tint, size = AppIcons.SIZE_16)
            Spacer(Modifier.width(AppDimensions.Dp5))
        }
        ;Bt(label, size = AppTypography.SIZE_12, tint = tint, semi = true)
        if (!iconFirst &&
            icon.isNotBlank()
        ) {
            Spacer(Modifier.width(AppDimensions.Dp5))
            Bi(icon, tint = tint, size = AppIcons.SIZE_16)
        }
    }
}

@Composable fun BioTag(
    text: String,
    icon: String = "",
    dot: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .background(
                AppColors.White.copy(alpha = AppOpacity.VALUE_65),
                RoundedCornerShape(AppDimensions.Dp20),
            ).border(
                AppDimensions.Dp1,
                AppColors.White.copy(alpha = AppOpacity.VALUE_55),
                RoundedCornerShape(AppDimensions.Dp20),
            ).padding(horizontal = AppDimensions.Dp9, vertical = AppDimensions.Dp6),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp4),
    ) {
        if (dot)Box(Modifier.size(AppDimensions.Dp5).background(bp.deep, RoundedCornerShape(50)))
        if (icon.isNotBlank())Bi(icon, size = AppIcons.SIZE_12)
        Bt(text, size = AppTypography.SIZE_11, tint = bp.deep, line = AppTypography.LINE_HEIGHT_15_4)
    }
}

@Composable fun BioProgress(
    value: Float,
    modifier: Modifier = Modifier,
    white: Boolean = false,
) {
    Box(
        modifier
            .fillMaxWidth()
            .height(
                AppDimensions.Dp4,
            ).clip(
                RoundedCornerShape(AppDimensions.Dp4),
            ).background(if (white)AppColors.White.copy(alpha = AppOpacity.VALUE_65)else AppColors.SwatchFFD8DFCE),
    ) {
        Box(Modifier.fillMaxHeight().fillMaxWidth(value.coerceIn(0f, 1f)).background(bp.deep))
    }
}

@Preview(name = "Care controls", widthDp = 335, showBackground = true)
@Composable
private fun BioControlsPreview() {
    val water = stringResource(R.string.copy_tuoi_cay_0990f6)
    val watered = stringResource(R.string.copy_da_tuoi_de2042)
    CompositionLocalProvider(LocalIndication provides BioTouchIndication(bp.deep)) {
        Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp8)) {
            BioButton(water, {}, Modifier.fillMaxWidth(), icon = "water")
            BioButton(watered, {}, Modifier.fillMaxWidth(), icon = "water", enabled = false)
            BioTag(watered, icon = "check")
            BioProgress(0.6f)
            BioTextAction(water, {})
            BioIconButton("close", {})
        }
    }
}
