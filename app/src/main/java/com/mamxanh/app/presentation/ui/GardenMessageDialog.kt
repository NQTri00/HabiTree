package com.mamxanh.app.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mamxanh.app.presentation.ui.components.MintFeedback

@Composable
fun GardenMessageDialog(message: String, onDismiss: () -> Unit) {
    MintFeedback(message, onDismiss, Modifier.navigationBarsPadding().padding(bottom = 80.dp))
}
@Preview(widthDp = 360, heightDp = 160)
@Composable
fun GardenMessageDialogPreview() {
    MamXanhPreviewTheme { MintFeedback("Đã thêm nhiệm vụ", {}) }
}
