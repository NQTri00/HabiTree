package com.mamxanh.app.core.components.design

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview

private object ChipTokens {
    val TopInset = AppDimensions.Dp2
    val Gap = AppDimensions.Dp8
    val MinimumHeight = AppDimensions.Dp48
    val HorizontalPadding = AppDimensions.Dp12
    val VerticalPadding = AppDimensions.Dp9
    val BorderWidth = AppDimensions.Dp1
    val Shape = bioShape(AppRadii.Value13)
    val LabelSize = AppTypography.Size11
}

@Composable
fun BioChips(options: List<Pair<String, String>>, selected: String, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
            .padding(top = ChipTokens.TopInset),
        horizontalArrangement = Arrangement.spacedBy(ChipTokens.Gap),
    ) {
        options.forEach { (id, label) ->
            val isSelected = id == selected
            val fill = if (isSelected) bp.accent else AppColors.White.copy(alpha = AppOpacity.Value_55)
            val border = if (isSelected) bp.control else bp.line
            Box(
                modifier = Modifier.heightIn(min = ChipTokens.MinimumHeight)
                    .background(fill, ChipTokens.Shape)
                    .border(ChipTokens.BorderWidth, border, ChipTokens.Shape)
                    .semantics { this.selected = isSelected }
                    .clickable(role = Role.RadioButton) { onSelect(id) }
                    .padding(horizontal = ChipTokens.HorizontalPadding, vertical = ChipTokens.VerticalPadding),
                contentAlignment = Alignment.Center,
            ) {
                Bt(label, size = ChipTokens.LabelSize, maxLines = 1)
            }
        }
    }
}

@Preview(name = "Garden filters", widthDp = 335, showBackground = true)
@Preview(name = "Garden filters at large text", widthDp = 280, fontScale = 1.5f, showBackground = true)
@Composable
private fun BioChipsPreview() {
    BioChips(
        options = listOf("all" to "Tất cả", "water" to "Cần tưới", "growing" to "Đang lớn", "mature" to "Trưởng thành"),
        selected = "all",
        onSelect = {},
    )
}
