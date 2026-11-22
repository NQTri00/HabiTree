package com.mamxanh.app.feature.stats.presentation.ui.achievements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.design.*
import com.mamxanh.core.ui.R
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

@Composable
fun AchievementsScreen(entries: List<AchievementDisplay>) {
    Bt(
        stringResource(R.string.copy_tung_dau_moc_nho_a0b175),
        size = AppTypography.SIZE_27,
        bold = true,
        spacing = AppTypography.TRACKING_MINUS_1,
    )
    Bt(stringResource(R.string.copy_ghi_nhan_hanh_trinh_khong_thuong_xu_061899), size = AppTypography.SIZE_12, tint = bp.muted)
    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp12)) {
        entries.forEach { item ->
            val n = item.progress
            val max = item.target
            val unlocked =
                item.unlocked || n >= max
            BioCard(Modifier.fillMaxWidth(), padding = AppSpacing.VALUE_18) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp12)) {
                    Box(
                        Modifier
                            .size(
                                AppDimensions.Dp48,
                            ).background(if (unlocked)AppColors.RewardSurface else AppColors.SwatchFFF0F0F0, RoundedCornerShape(50)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Bi(
                            if (unlocked)"award"else "lock",
                            size = AppIcons.SIZE_22,
                            tint = if (unlocked)AppColors.SwatchFF947A30 else bp.muted,
                        )
                    }
                    Column(Modifier.weight(1f)) {
                        Bt(item.title, size = AppTypography.SIZE_14, semi = true)
                        Bt(item.criterion, size = AppTypography.SIZE_10, tint = bp.muted)
                        Spacer(Modifier.height(AppDimensions.Dp6))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp10),
                        ) {
                            BioProgress(
                                n.toFloat() / max,
                                Modifier.weight(1f),
                            )
                            ;Bt(
                                "${minOf(n,max)}/$max" + if (unlocked)AppText.text(TextKey.CopyDaGhiNhanEbd27e)else "",
                                size = AppTypography.SIZE_11,
                                tint = bp.muted,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun AchievementsScreenPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp18)) {
        AchievementsScreen(
            listOf(AchievementDisplay("First step", "Complete a task", 1, 1)),
        )
    }
}
