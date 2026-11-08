package com.mamxanh.app.feature.shop.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Wooden display shelf with cute smiling potted plants on the left of ShopScreen (Concept Image 2).
 */
@Composable
fun ShopShelfDisplay(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(108.dp)
            .fillMaxHeight()
            .padding(start = 10.dp, end = 4.dp)
    ) {
        WoodenPlantShelf(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 20.dp)
        )

        // Top shelf plant with '!' speech bubble (Image 2)
        KawaiiPottedPlant(
            varietyIndex = 0,
            hasAlertBubble = true,
            potExpression = 0,
            modifier = Modifier
                .size(78.dp, 92.dp)
                .align(Alignment.TopCenter)
                .offset(y = 48.dp)
        )

        // Middle shelf plant (winking cute face)
        KawaiiPottedPlant(
            varietyIndex = 1,
            hasAlertBubble = false,
            potExpression = 1,
            modifier = Modifier
                .size(68.dp, 82.dp)
                .align(Alignment.Center)
                .offset(y = 12.dp)
        )

        // Bottom shelf plant
        KawaiiPottedPlant(
            varietyIndex = 4,
            hasAlertBubble = false,
            potExpression = 2,
            modifier = Modifier
                .size(72.dp, 86.dp)
                .align(Alignment.BottomCenter)
                .offset(y = (-40).dp)
        )
    }
}

@Preview(name = "Shop shelf display", showBackground = true, widthDp = 120, heightDp = 600)
@Composable
fun ShopShelfDisplayPreview() {
    com.mamxanh.app.presentation.ui.MamXanhPreviewTheme {
        ShopShelfDisplay()
    }
}
