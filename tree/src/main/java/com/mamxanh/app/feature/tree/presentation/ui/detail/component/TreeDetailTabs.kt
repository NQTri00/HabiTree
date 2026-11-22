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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.*
import com.mamxanh.app.core.components.components.*
import com.mamxanh.app.core.components.design.AppColors
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppTypography
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.model.*

@Composable
internal fun TreeDetailTabs(
    selected: Int,
    onSelect: (Int) -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .selectableGroup()
            .background(Pale, RoundedCornerShape(AppDimensions.Dp15))
            .padding(AppDimensions.Dp4),
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp3),
    ) {
        listOf(
            AppText.text(TextKey.CopyChamSoc7b7df6),
            AppText.text(TextKey.CopyTrangTriD48231),
            AppText.text(TextKey.CopyThongTin4aa16c),
        ).forEachIndexed {
            index,
            label,
            ->
            Box(
                Modifier
                    .weight(1f)
                    .heightIn(min = AppDimensions.Dp48)
                    .selectable(selected == index, role = Role.Tab, onClick = { onSelect(index) }),
                contentAlignment = Alignment.Center,
            ) {
                Surface(
                    Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(AppDimensions.Dp9),
                    color = if (selected == index)AppColors.White else AppColors.Transparent,
                    shadowElevation = if (selected == index)AppDimensions.Dp2 else AppDimensions.Dp0,
                ) {
                    Column(
                        Modifier.padding(vertical = AppDimensions.Dp6),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp4),
                    ) {
                        Text(
                            label,
                            color = if (selected == index)Green else Muted,
                            fontSize = AppTypography.Sp13,
                            lineHeight = AppTypography.Sp19,
                            fontWeight = if (selected == index)FontWeight.Bold else FontWeight.Medium,
                            textAlign = TextAlign.Center,
                        )
                        Box(
                            Modifier
                                .width(AppDimensions.Dp16)
                                .height(AppDimensions.Dp2)
                                .background(if (selected == index)Green else AppColors.Transparent, RoundedCornerShape(50)),
                        )
                    }
                }
            }
        }
    }
}

/** Content has natural height: runtime scrolls it; previews show every row. */

@Preview(name = "Tree detail tabs", widthDp = 335, showBackground = true)
@Preview(name = "Tree tabs at large text", widthDp = 280, fontScale = 1.5f, showBackground = true)
@Composable
private fun TreeDetailTabsPreview() {
    MamXanhPreviewTheme { TreeDetailTabs(selected = 0, onSelect = {}) }
}
