package com.mamxanh.app.presentation.ui

import com.mamxanh.app.presentation.ui.design.AppDimensions

import com.mamxanh.shared.localization.TextKey

import com.mamxanh.shared.localization.AppText

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mamxanh.app.presentation.ui.components.MintFeedback

@Composable
fun GardenMessageDialog(message: String, onDismiss: () -> Unit) {
    MintFeedback(message, onDismiss, Modifier.navigationBarsPadding().padding(bottom = AppDimensions.Dp80))
}
@Preview(widthDp = 360, heightDp = 160)
@Composable
fun GardenMessageDialogPreview() {
    MamXanhPreviewTheme { MintFeedback(AppText.text(TextKey.CopyDaThemNhiemVuB295d0), {}) }
}
