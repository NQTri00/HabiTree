package com.mamxanh.app.feature.tree.presentation.ui.detail.component
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.*
import com.mamxanh.app.core.components.components.*
import com.mamxanh.app.core.components.design.AppColors
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppThemeEffects
import com.mamxanh.app.core.components.design.AppTypography
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.model.*

@Composable
internal fun TreeDetailTabs(
    selected: Int,
    onSelect: (Int) -> Unit,
) {
    val labels =
        listOf(
            AppText.text(TextKey.CopyChamSoc7b7df6),
            AppText.text(TextKey.CopyTrangTriD48231),
            AppText.text(TextKey.CopyThongTin4aa16c),
        )
    val stacked =
        LocalDensity.current.fontScale > AppThemeEffects.LARGE_FONT_SCALE &&
            LocalConfiguration.current.screenWidthDp < AppThemeEffects.COMPACT_WIDTH
    val group =
        Modifier
            .fillMaxWidth()
            .selectableGroup()
            .background(Pale, RoundedCornerShape(AppDimensions.Dp15))
            .padding(AppDimensions.Dp4)
    if (stacked) {
        Column(group, verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp3)) {
            labels.forEachIndexed { index, label ->
                TreeDetailTab(label, selected == index, Modifier.fillMaxWidth()) { onSelect(index) }
            }
        }
    } else {
        Row(group, horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp3)) {
            labels.forEachIndexed { index, label ->
                TreeDetailTab(label, selected == index, Modifier.weight(1f)) { onSelect(index) }
            }
        }
    }
}

@Composable
private fun TreeDetailTab(
    label: String,
    selected: Boolean,
    modifier: Modifier,
    onSelect: () -> Unit,
) {
    Box(
        modifier
            .heightIn(min = AppDimensions.Dp48)
            .selectable(selected, role = Role.Tab, onClick = onSelect),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(AppDimensions.Dp9),
            color = if (selected)AppColors.White else AppColors.Transparent,
            shadowElevation = if (selected)AppDimensions.Dp2 else AppDimensions.Dp0,
        ) {
            Column(
                Modifier.padding(vertical = AppDimensions.Dp6),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp4),
            ) {
                Text(
                    label,
                    color = if (selected)Green else Muted,
                    fontSize = AppTypography.Sp13,
                    lineHeight = AppTypography.Sp19,
                    fontWeight = if (selected)FontWeight.Bold else FontWeight.Medium,
                    textAlign = TextAlign.Center,
                )
                Box(
                    Modifier
                        .width(AppDimensions.Dp16)
                        .height(AppDimensions.Dp2)
                        .background(if (selected)Green else AppColors.Transparent, RoundedCornerShape(50)),
                )
            }
        }
    }
}

@Preview(name = "Tree detail tabs", widthDp = 335, showBackground = true)
@Preview(name = "Tree tabs at large text", widthDp = 280, fontScale = 1.5f, showBackground = true)
@Composable
private fun TreeDetailTabsPreview() {
    MamXanhPreviewTheme { TreeDetailTabs(selected = 0, onSelect = {}) }
}
