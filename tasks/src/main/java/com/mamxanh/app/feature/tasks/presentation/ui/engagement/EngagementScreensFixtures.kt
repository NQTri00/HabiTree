package com.mamxanh.app.feature.tasks.presentation.ui.engagement
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.*
import com.mamxanh.app.core.components.components.*
import com.mamxanh.shared.model.*

@Preview(name = "Focus engagement", widthDp = 375, heightDp = 812)
@Preview(name = "Focus engagement at large text", widthDp = 320, heightDp = 812, fontScale = 1.5f)
@Composable
private fun EngagementScreenPreview() {
    MamXanhPreviewTheme {
        EngagementScreen(
            state = EngagementUiState(route = "FOCUS", loading = false),
            onOpen = { _, _ -> },
            onAct = {},
            onAdjust = {},
            onPermission = {},
            onEntity = {},
            onDismissMessage = {},
        )
    }
}
