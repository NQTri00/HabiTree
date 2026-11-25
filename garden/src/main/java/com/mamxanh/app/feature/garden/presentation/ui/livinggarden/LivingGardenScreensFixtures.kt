package com.mamxanh.app.feature.garden.presentation.ui.livinggarden

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.*
import com.mamxanh.app.core.components.components.*
import com.mamxanh.shared.model.*

@Preview(name = "Garden appearance", widthDp = 375, heightDp = 812)
@Composable
private fun LivingGardenScreenPreview() {
    MamXanhPreviewTheme {
        LivingGardenScreen(
            state = LivingGardenUiState(route = "APPEARANCE", loading = false),
            onOpen = { _, _ -> },
            onAct = {},
            onSelect = {},
            onPlace = { _, _ -> },
            onRemove = {},
            onUndo = {},
            onSave = {},
            onCancel = {},
            onTree = {},
            onSeedShop = {},
            onMemories = {},
            reducedMotion = true,
            onReducedMotion = {},
            active = true,
        )
    }
}
