package com.mamxanh.app.feature.tasks.presentation.ui.engagement

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.*
import com.mamxanh.app.core.components.components.*
import com.mamxanh.app.core.components.design.AppColors
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppTypography
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.model.*

@Composable
internal fun DayCalendar(days: List<ReviewDay>) {
    GardenBotanicalFrame(Modifier.fillMaxWidth(), contentPadding = PaddingValues(AppDimensions.Dp14), cornerSize = AppDimensions.Dp20) {
        Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp12)) {
            Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp6)) {
                days.chunked(7).forEach { week ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp6)) {
                        week.forEach { d ->
                            Surface(
                                Modifier.weight(1f).heightIn(min = AppDimensions.Dp36).semantics(mergeDescendants = true) {
                                    contentDescription =
                                        "${d.date}: ${when (d.state){
                                            "SUCCESS" -> AppText.text(TextKey.CopyHoanThanh5eb36f)
                                            "REST" -> AppText.text(TextKey.CopyNghiF0aa0a)
                                            "PROTECTED" -> AppText.text(TextKey.CopyDaBaoVeF1dfce)
                                            "MISSED" -> AppText.text(TextKey.CopyBoLoF8cf8f)
                                            else -> AppText.text(TextKey.CopyChuaGhiNhanF7654d)
                                        }}"
                                },
                                shape =
                                    androidx.compose.foundation.shape
                                        .RoundedCornerShape(AppDimensions.Dp10),
                                color =
                                    when (d.state) {
                                        "SUCCESS" -> Green
                                        "REST" -> AppColors.SwatchFFE5DFF6
                                        "PROTECTED" -> Gold
                                        else -> AppColors.SwatchFFF0F4F1
                                    },
                            ) {
                                Text(
                                    d.date.takeLast(2) +
                                        when (d.state) {
                                            "SUCCESS" -> "✓"
                                            "REST" -> " N"
                                            "PROTECTED" -> " ◇"
                                            "MISSED" -> " ·"
                                            else -> ""
                                        },
                                    Modifier.padding(vertical = AppDimensions.Dp10),
                                    textAlign = TextAlign.Center,
                                    color =
                                        if (d.state ==
                                            "SUCCESS"
                                        ) {
                                            androidx.compose.ui.graphics.Color.White
                                        } else {
                                            Deep
                                        },
                                    fontSize = AppTypography.Sp10,
                                )
                            }
                        }
                        repeat(7 - week.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }
            Text(
                AppText.text(TextKey.CopyHoanThanhNNghiBaoVeChamDaBoLoEc2735),
                color = Muted,
                fontSize = AppTypography.Sp12,
                lineHeight = AppTypography.Sp17,
            )
        }
    }
}

@Preview(name = "Calendar without records", widthDp = 335, showBackground = true)
@Composable
private fun DayCalendarPreview() {
    MamXanhPreviewTheme { DayCalendar(emptyList()) }
}
