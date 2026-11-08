package com.mamxanh.app.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun GardenPastoralBackdrop(modifier: Modifier = Modifier) {
    GardenSceneBackdrop(GardenScene.BEDS, modifier)
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun GardenPastoralBackdropPreview() {
    GardenPastoralBackdrop(Modifier)
}
