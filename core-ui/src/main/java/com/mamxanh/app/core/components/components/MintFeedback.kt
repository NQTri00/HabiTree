package com.mamxanh.app.core.components.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.Deep
import com.mamxanh.app.core.components.design.AppColors
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppTypography
import com.mamxanh.core.ui.R
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

/** Brief feedback that never opens a modal or blocks the rest of the screen. */
@Composable
fun MintFeedback(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dismiss by rememberUpdatedState(onDismiss)
    LaunchedEffect(message) {
        kotlinx.coroutines.delay(3500)
        dismiss()
    }
    Box(modifier.fillMaxSize().padding(AppDimensions.Dp10), contentAlignment = Alignment.BottomCenter) {
        Surface(
            Modifier.widthIn(max = AppDimensions.Dp440).fillMaxWidth().semantics { liveRegion = LiveRegionMode.Polite },
            shape = RoundedCornerShape(AppDimensions.Dp18),
            color = Deep,
            contentColor = AppColors.White,
            shadowElevation = AppDimensions.Dp6,
        ) {
            Row(Modifier.padding(start = AppDimensions.Dp14), verticalAlignment = Alignment.CenterVertically) {
                val copy =
                    message.substringAfter("\n", message).let {
                        if (message.startsWith(AppText.text(TextKey.CopyDa64a504))) it.substringBefore(" · ") else it
                    }
                GardenRichText(
                    copy,
                    Modifier.weight(1f).padding(vertical = AppDimensions.Dp8),
                    color = AppColors.White,
                    fontSize = AppTypography.Sp12,
                    lineHeight = AppTypography.Sp16,
                    maxLines = 3,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                )
                TextButton(onClick = onDismiss, contentPadding = PaddingValues(AppDimensions.Dp8)) {
                    Text("×", color = AppColors.White, fontSize = AppTypography.Sp20)
                }
            }
        }
    }
}

@Preview(name = "Watering feedback", widthDp = 335, showBackground = true)
@Preview(name = "Feedback at large text", widthDp = 280, fontScale = 1.5f, showBackground = true)
@Composable
private fun MintFeedbackPreview() {
    MintFeedback(stringResource(R.string.copy_da_tuoi_de2042), onDismiss = {})
}
