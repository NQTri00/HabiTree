package com.mamxanh.app.presentation.ui
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.MamXanhPreviewTheme
import com.mamxanh.app.core.components.components.MintFeedback
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

@Composable
fun GardenMessageDialog(
    message: String,
    onDismiss: () -> Unit,
) {
    MintFeedback(message, onDismiss, Modifier.navigationBarsPadding().padding(bottom = AppDimensions.Dp80))
}

@Preview(widthDp = 360, heightDp = 160)
@Composable
fun GardenMessageDialogPreview() {
    MamXanhPreviewTheme { MintFeedback(AppText.text(TextKey.CopyDaThemNhiemVuB295d0), {}) }
}
