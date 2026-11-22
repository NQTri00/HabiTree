package com.mamxanh.app.feature.home.presentation.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.*
import com.mamxanh.app.core.components.components.*
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppOpacity
import com.mamxanh.app.core.components.design.AppTypography
import com.mamxanh.app.feature.home.domain.model.*
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

@Composable
internal fun HomeNextTask(
    task: HomeTaskSummary,
    onOpen: () -> Unit,
    onAction: () -> Unit,
) {
    GardenBotanicalFrame(
        Modifier.fillMaxWidth().clickable(role = Role.Button, onClick = onOpen),
        contentPadding = PaddingValues(AppDimensions.Dp14),
        cornerSize = AppDimensions.Dp18,
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp12),
        ) {
            GardenArtwork(GardenArt.BOOK, Modifier.size(AppDimensions.Dp28))
            Column(Modifier.weight(1f)) {
                Text(
                    task.title,
                    color = Deep,
                    fontSize = AppTypography.Sp14,
                    lineHeight = AppTypography.Sp20,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(AppText.category(task.category), color = Muted, fontSize = AppTypography.Sp11)
            }
            Text("+${task.reward}", color = Green, fontSize = AppTypography.Sp12)
            IconButton(
                onAction,
                enabled = task.canComplete,
                modifier =
                    Modifier.size(AppDimensions.Dp48).semantics {
                        contentDescription =
                            if (task.timer)AppText.text(TextKey.CopyBatDauTapTrungD198d9) else AppText.text(TextKey.CopyHoanThanhViecA2a386)
                    },
            ) {
                Surface(
                    Modifier.size(AppDimensions.Dp44),
                    shape = androidx.compose.foundation.shape.CircleShape,
                    color = androidx.compose.ui.graphics.Color.Transparent,
                    border = androidx.compose.foundation.BorderStroke(AppDimensions.Dp1, Muted.copy(alpha = AppOpacity.VALUE_6)),
                ) {
                    Box(contentAlignment = Alignment.Center) { if (task.timer)Text("▷", color = Green, fontSize = AppTypography.Sp20) }
                }
            }
        }
    }
}

@Composable
internal fun HomeLink(
    art: GardenArt,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    GardenBotanicalFrame(
        Modifier
            .fillMaxWidth()
            .heightIn(min = AppDimensions.Dp48)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentPadding = PaddingValues(AppDimensions.Dp14),
        cornerSize = AppDimensions.Dp18,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp12)) {
            GardenArtwork(art, Modifier.size(AppDimensions.Dp28))
            Column(Modifier.weight(1f)) {
                Text(title, color = Deep, fontWeight = FontWeight.Bold, fontSize = AppTypography.Sp14, lineHeight = AppTypography.Sp20)
                if (subtitle.isNotEmpty()) Text(subtitle, color = Deep, fontSize = AppTypography.Sp11, lineHeight = AppTypography.Sp14)
            }
            if (enabled) Text("›", color = Green, fontSize = AppTypography.Sp16)
        }
    }
}

@Preview(name = "Home task and shortcut", widthDp = 335, showBackground = true)
@Composable
private fun HomeTaskAndLinksPreview() {
    val title = AppText.text(TextKey.CopyDoc10TrangSachA3c873)
    val category = AppText.canonical(TextKey.CopyHocTapA51516)
    MamXanhPreviewTheme {
        Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp12)) {
            HomeNextTask(HomeTaskSummary(1, title, category, 8), {}, {})
            HomeLink(GardenArt.BOOK, title, category, {})
        }
    }
}
