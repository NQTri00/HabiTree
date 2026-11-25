package com.mamxanh.app.feature.stats.presentation.ui.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.*
import com.mamxanh.app.core.components.components.*
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppTypography
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import java.util.Calendar
import com.mamxanh.app.core.components.components.GardenRichText as Text

@Composable
fun StatsScreen(
    state: StatsState,
    onIntent: (StatsAction) -> Unit,
    modifier: Modifier = Modifier,
    onEngagement: (String) -> Unit = {},
) {
    val s = state.statistics
    val listState = rememberLazyListState()
    val firstUse = s.total == 0 && s.treeCount == 0
    val recent = s.recentDailyCompletions.takeLast(7).let { if (it.size == 7) it else List(7 - it.size) { 0 } + it }
    val days =
        (6 downTo 0).map { offset ->
            val day = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -offset) }.get(Calendar.DAY_OF_WEEK)
            if (day == Calendar.SUNDAY) "CN" else "T$day"
        }
    MintBackdrop(modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            MintCompactHeader(s.coins, title = AppText.text(TextKey.CopyMamXanh692a0a))
            GardenScreenIntro(
                AppText.text(TextKey.CopyNhatKyXanhAa1337),
                AppText.text(TextKey.CopyNhungDieuDaLonLen2c3892),
                AppText.text(TextKey.CopyMoiBuocNhoDeuDuocGhiNhanB0ce5b),
                Modifier.padding(horizontal = AppDimensions.Dp24, vertical = AppDimensions.Dp12),
            )
            LazyColumn(
                Modifier.weight(1f).mintScrollEdges(listState.canScrollBackward, listState.canScrollForward),
                state = listState,
                contentPadding = PaddingValues(horizontal = AppDimensions.Dp24, vertical = AppDimensions.Dp16),
                verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp18),
            ) {
                item {
                    MintMetricStrip(
                        listOf(
                            AppText.text(TextKey.CopyViecHoanThanhE0966b) to s.total.toString(),
                            AppText.text(TextKey.CopyCayTrongVuon6565bb) to s.treeCount.toString(),
                        ),
                    )
                }

                item {
                    GardenBotanicalFrame(Modifier.fillMaxWidth(), contentPadding = PaddingValues(AppDimensions.Dp8)) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    AppText.text(TextKey.CopyNhipXanh7Ngay03a7ab),
                                    Modifier.weight(1f),
                                    color = Deep,
                                    fontSize = AppTypography.Sp12,
                                    fontWeight = FontWeight.Bold,
                                )
                                IconButton(
                                    onClick = { onIntent(StatsAction.Refresh) },
                                    modifier =
                                        Modifier.semantics {
                                            stateDescription =
                                                if (state.isLoading) {
                                                    AppText.text(
                                                        TextKey.CopyDangLamMoiThongKe00896d,
                                                    )
                                                } else {
                                                    AppText.text(TextKey.CopySanSangLamMoi8bb7f8)
                                                }
                                            liveRegion = LiveRegionMode.Polite
                                        },
                                ) { Icon(Icons.Rounded.Refresh, AppText.text(TextKey.CopyLamMoiThongKeF1683d), tint = Green) }
                            }
                            Box {
                                WeekSproutChart(recent, days, Modifier.padding(top = AppDimensions.Dp4))
                                if (state.isLoading) {
                                    LinearProgressIndicator(
                                        Modifier.fillMaxWidth().height(AppDimensions.Dp3).clearAndSetSemantics {},
                                    )
                                }
                            }
                        }
                    }
                }
                if (!firstUse) {
                    item {
                        MintMetricStrip(
                            listOf(
                                AppText.text(TextKey.CopyTuanNayCc1611) to s.week.toString(),
                                AppText.text(TextKey.CopyHomNayE6747a) to s.today.toString(),
                                AppText.text(TextKey.CopyChuoiNgay5ad2e6) to s.streak.toString(),
                            ),
                        )
                    }
                }
                items(
                    listOf(
                        "REVIEW" to AppText.text(TextKey.CopyNhinLaiTuanFf3f0c),
                        "ACHIEVEMENTS" to AppText.text(TextKey.CopyDauMocDffd66),
                        "TIMELINE" to AppText.text(TextKey.CopyKyNiemE3e113),
                        "FOCUS" to AppText.text(TextKey.CopyTapTrung964002),
                        "SETTINGS" to AppText.text(TextKey.CopyTuyChon598c1b),
                    ),
                ) { (route, label) ->
                    GardenNavigationCard(
                        label,
                        { onEngagement(route) },
                        art =
                            when (route) {
                                "REVIEW" -> GardenArt.CALENDAR
                                "ACHIEVEMENTS" -> GardenArt.RIBBON
                                "TIMELINE" -> GardenArt.JOURNAL
                                "FOCUS" -> GardenArt.LANTERN
                                else -> GardenArt.CATEGORY
                            },
                    )
                }
                item {
                    if (firstUse) {
                        GardenBotanicalFrame(Modifier.fillMaxWidth(), skin = GardenPanelSkin.HINT) {
                            Text(
                                AppText.text(TextKey.CopyHayBatDauChamCayDeGhiLaiHanhTrinhCuaBanNE6b786),
                                color = Deep,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            )
                        }
                    } else {
                        Text(
                            AppText.text(TextKey.CopySoLieuDuocLuuTrenThietBiF6ac69),
                            Modifier.fillMaxWidth(),
                            color = Muted,
                            fontSize = AppTypography.Sp11,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun JourneyCard(
    emoji: String,
    title: String,
    value: String,
    caption: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "$emoji $value",
            Modifier.fillMaxWidth(),
            color = Green,
            fontSize = AppTypography.Sp17,
            fontWeight = FontWeight.Bold,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        Text(title, color = Deep, fontSize = AppTypography.Sp12, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        if (caption.isNotBlank()) Text(caption, color = Muted, fontSize = AppTypography.Sp11)
    }
}

@Preview(name = "Journal summary", showBackground = true)
@Composable
fun JourneyCardPreview() {
    MamXanhPreviewTheme { JourneyCard("", AppText.text(TextKey.CopyCayTrongVuon6565bb), "3", "") }
}
