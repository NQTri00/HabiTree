package com.mamxanh.app.feature.home.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mamxanh.app.presentation.ui.components.GardenForestScene

/** The garden uses separate painted forest and stream layers rather than a screen image. */
@Composable
fun FarmPastoralBackdrop(modifier: Modifier = Modifier) {
    GardenForestScene(modifier)
}
