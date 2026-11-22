package com.mamxanh.app.core.components.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppTypography
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

@Composable
fun GardenPriceTag(
    price: Int,
    modifier: Modifier = Modifier,
) {
    GardenBotanicalFrame(
        modifier.heightIn(min = AppDimensions.Dp30).widthIn(min = AppDimensions.Dp88),
        contentPadding = PaddingValues(horizontal = AppDimensions.Dp10, vertical = AppDimensions.Dp3),
        cornerSize = AppDimensions.Dp12,
        skin = GardenPanelSkin.PRICE,
    ) {
        GardenRichText(
            AppText.text(TextKey.Copy0Xu251e68, price),
            Modifier.align(Alignment.Center),
            color = com.mamxanh.app.core.components.Deep,
            fontSize = AppTypography.Sp14,
            lineHeight = AppTypography.Sp17,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Preview(name = "Source price label")
@Composable
fun GardenPriceTagPreview() {
    GardenPriceTag(25)
}
