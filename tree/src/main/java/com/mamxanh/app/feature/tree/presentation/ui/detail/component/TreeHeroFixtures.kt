package com.mamxanh.app.feature.tree.presentation.ui.detail.component

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.design.*

@Preview(name = "Home tree hero", widthDp = 335, showBackground = true)
@Preview(name = "Home tree hero at large text", widthDp = 280, fontScale = 1.5f, showBackground = true)
@Composable
private fun TreeHeroPreview() {
    TreeHero(
        TreeHeroState(
            species = "cherry",
            name = "Spring cherry",
            stageLabel = "Growing",
            growthCaption = "Growing with you",
            stage = 3,
            stages = 9,
            progress = 46,
            home = true,
            compact = false,
            dead = false,
            watered = true,
            busy = false,
            selectedOnHome = true,
            canBrowseTrees = false,
            accessoryIcon = null,
        ),
    ) {}
}
