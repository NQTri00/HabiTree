package com.mamxanh.app.presentation.bio

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.design.*
import com.mamxanh.core.ui.R

@Composable
internal fun BioAccessoryCard(
    id: String,
    name: String,
    price: Int,
    owned: Boolean,
    equipped: Boolean,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    BioCard(modifier, padding = AppSpacing.VALUE_12, onClick = onClick) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(
                    if (LocalConfiguration.current.screenWidthDp < AppThemeEffects.COMPACT_WIDTH) {
                        AppDimensions.Dp95
                    } else {
                        AppDimensions.Dp112
                    },
                ).background(Brush.linearGradient(listOf(bp.soft, bp.soft)), bioShape(AppRadii.VALUE_15)),
            contentAlignment = Alignment.Center,
        ) {
            Bi(accessoryIcon(id), size = AppIcons.SIZE_32)
        }
        Spacer(Modifier.height(AppDimensions.Dp10))
        Bt(
            name,
            Modifier.heightIn(min = AppDimensions.Dp36),
            size = AppTypography.SIZE_13,
            line = AppTypography.LINE_HEIGHT_18_85,
            semi = true,
        )
        Spacer(Modifier.height(AppDimensions.Dp10))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Bt(
                if (equipped) {
                    stringResource(R.string.copy_dang_dung_275ca2)
                } else if (owned) {
                    stringResource(R.string.copy_da_so_huu_822625)
                } else {
                    stringResource(R.string.copy_0_xu_251e68, price)
                },
                Modifier.weight(1f),
                size = AppTypography.SIZE_13,
                tint = bp.deep,
            )
            Box(
                Modifier.size(AppDimensions.Dp27).border(AppDimensions.Dp1, bp.line, RoundedCornerShape(AppDimensions.Dp8)),
                contentAlignment = Alignment.Center,
            ) {
                Bi(
                    if (equipped) {
                        "check"
                    } else if (owned) {
                        "next"
                    } else {
                        "plus"
                    },
                    size = AppIcons.SIZE_14,
                )
            }
        }
    }
}

@Preview(name = "Tree accessory", widthDp = 335, showBackground = true)
@Composable
private fun BioAccessoryCardPreview() {
    val context = LocalContext.current
    val world = remember(context) { BioWorld.reference(context) }
    val item = world.accessories.first()
    BioTheme(world, "1") {
        BioAccessoryCard(item.s("id"), item.s("name"), item.i("price"), false, false, Modifier) {}
    }
}
