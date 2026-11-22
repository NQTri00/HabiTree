package com.mamxanh.app.feature.tasks.presentation.ui.engagement
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.*
import com.mamxanh.app.core.components.components.*
import com.mamxanh.app.core.components.design.AppColors
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppTypography
import com.mamxanh.app.feature.tasks.presentation.ui.form.message
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.model.*

@Composable
fun EngagementScreen(
    state: EngagementUiState,
    onOpen: (String, Long) -> Unit,
    onAct: (EngagementAction) -> Unit,
    onAdjust: (Task) -> Unit,
    onPermission: () -> Unit,
    onEntity: (MemoryEvent) -> Unit,
    onDismissMessage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MintBackdrop(modifier.fillMaxSize()) {
        ProvideTextStyle(MaterialTheme.typography.bodySmall.copy(fontSize = AppTypography.Sp15, lineHeight = AppTypography.Sp21)) {
            if (state.route == "ONBOARD") {
                OnboardingScreen(state, onAct)
            } else {
                Column(Modifier.fillMaxSize()) {
                    Row(Modifier.fillMaxWidth().padding(horizontal = AppDimensions.Dp8), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { onOpen("", 0) }) { Text("‹", fontSize = AppTypography.Sp24, color = Deep) }
                        Text(
                            when (state.route) {
                                "FOCUS" -> AppText.text(TextKey.CopyTapTrung964002)
                                "TEMPLATES" -> AppText.text(TextKey.CopyViecMau535dc1)
                                "REVIEW" -> AppText.text(TextKey.CopyNhinLaiTuanFf3f0c)
                                "REST" -> AppText.text(TextKey.CopyLichNghi588c3f)
                                "ACHIEVEMENTS" -> AppText.text(TextKey.CopyDauMocDffd66)
                                "TIMELINE" -> AppText.text(TextKey.CopyKyNiemE3e113)
                                "SETTINGS" -> AppText.text(TextKey.CopyTuyChon598c1b)
                                "INSIGHTS" -> AppText.text(TextKey.CopyNhipThoiQuen1b8324)
                                else -> AppText.text(TextKey.CopyHanhTrinh566b59)
                            },
                            color = Deep,
                            fontSize = AppTypography.Sp15,
                            lineHeight = AppTypography.Sp20,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    val scroll = key(state.route, state.selectedTask) { rememberScrollState() }
                    Column(
                        Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .mintScrollEdges(scroll.canScrollBackward, scroll.canScrollForward)
                            .verticalScroll(scroll)
                            .padding(horizontal = AppDimensions.Dp24, vertical = AppDimensions.Dp12),
                        verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp16),
                    ) {
                        when (state.route) {
                            "FOCUS" -> FocusContent(state, onAct, onOpen)
                            "TEMPLATES" -> TemplateContent(state, onAct)
                            "REVIEW" -> {
                                ReviewContent(state, onAct)
                                GardenNavigationCard(AppText.text(TextKey.CopyLichNghiBaoVe8eda57), {
                                    onOpen("REST", 0)
                                }, AppText.text(TextKey.Copy0VeBaoVe8bf912, state.snapshot.protectionTickets))
                            }
                            "REST" -> RestContent(state, onAct)
                            "ACHIEVEMENTS" -> {
                                GardenScreenIntro(
                                    AppText.text(TextKey.CopyTungDauMocNhoA0b175),
                                    subtitle = AppText.text(TextKey.CopyGhiNhanHanhTrinhKhongThuongXu061899),
                                )
                                state.snapshot.achievements.forEach { badge ->
                                    GardenBotanicalFrame(
                                        Modifier.fillMaxWidth(),
                                        contentPadding = PaddingValues(AppDimensions.Dp16),
                                        cornerSize = AppDimensions.Dp20,
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp14),
                                        ) {
                                            Box(
                                                Modifier.size(AppDimensions.Dp44).background(
                                                    if (badge.unlockedAt >
                                                        0
                                                    ) {
                                                        Gold
                                                    } else {
                                                        AppColors.SwatchFFEEEEEE
                                                    },
                                                    androidx.compose.foundation.shape.CircleShape,
                                                ),
                                                contentAlignment = Alignment.Center,
                                            ) { GardenArtwork(GardenArt.RIBBON, Modifier.size(AppDimensions.Dp24)) }
                                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp6)) {
                                                Text(
                                                    badge.title,
                                                    color = Deep,
                                                    fontSize = AppTypography.Sp14,
                                                    lineHeight = AppTypography.Sp20,
                                                    fontWeight = FontWeight.SemiBold,
                                                )
                                                Text(
                                                    badge.criterion,
                                                    color = Muted,
                                                    fontSize = AppTypography.Sp11,
                                                    lineHeight = AppTypography.Sp16,
                                                )
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp8),
                                                ) {
                                                    MintProgress(badge.progress.toFloat() / badge.target, Modifier.weight(1f))
                                                    Text(
                                                        "${badge.progress.coerceAtMost(badge.target)}/${badge.target}" +
                                                            if (badge.unlockedAt >
                                                                0
                                                            ) {
                                                                AppText.text(TextKey.CopyDaGhiNhanEbd27e)
                                                            } else {
                                                                ""
                                                            },
                                                        color = Green,
                                                        fontSize = AppTypography.Sp10,
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            "TIMELINE" -> {
                                GardenScreenIntro(AppText.text(TextKey.CopyDieuMinhMuonNho665bc8))
                                if (state.snapshot.memories.isEmpty()) {
                                    Text(
                                        AppText.text(TextKey.CopyKyNiemSeXuatHienTuNhungViecBanThucSuDaLaDf826a),
                                        color = Muted,
                                        fontSize = AppTypography.Sp12,
                                    )
                                }
                                state.snapshot.memories.forEach { event ->
                                    var note by rememberSaveable(event.id, event.note) { mutableStateOf(event.note) }
                                    Row(
                                        Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                                        horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp14),
                                    ) {
                                        Column(
                                            Modifier.width(AppDimensions.Dp10).fillMaxHeight().padding(top = AppDimensions.Dp4),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                        ) {
                                            Box(
                                                Modifier
                                                    .size(
                                                        AppDimensions.Dp8,
                                                    ).background(Green, androidx.compose.foundation.shape.CircleShape),
                                            )
                                            Spacer(Modifier.height(AppDimensions.Dp5))
                                            Box(Modifier.width(AppDimensions.Dp1).weight(1f).background(AppColors.SwatchFFD8DFD0))
                                        }
                                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp10)) {
                                            if (event.at >
                                                0
                                            ) {
                                                Text(
                                                    java.text
                                                        .SimpleDateFormat(
                                                            "dd/MM · HH:mm",
                                                            java.util.Locale.getDefault(),
                                                        ).format(java.util.Date(event.at)),
                                                    color = Muted,
                                                    fontSize = AppTypography.Sp11,
                                                )
                                            }
                                            Text(
                                                event.title,
                                                color = Deep,
                                                fontSize = AppTypography.Sp14,
                                                lineHeight = AppTypography.Sp20,
                                                fontWeight = FontWeight.SemiBold,
                                            )
                                            if (event.entity >
                                                0
                                            ) {
                                                TextButton(
                                                    onClick = { onEntity(event) },
                                                ) { Text(AppText.text(TextKey.CopyXemLienQuan127914), fontSize = AppTypography.Sp13) }
                                            }
                                            OutlinedTextField(
                                                note,
                                                { note = it.take(1000) },
                                                Modifier.fillMaxWidth().semantics {
                                                    contentDescription =
                                                        AppText.text(TextKey.CopyGhiChu0E5b4bc, event.title)
                                                },
                                                placeholder = {
                                                    Text(
                                                        AppText.text(TextKey.CopyMotDieuMuonNho200cc4),
                                                        fontSize = AppTypography.Sp12,
                                                    )
                                                },
                                                minLines = 3,
                                                maxLines = 6,
                                                shape =
                                                    androidx.compose.foundation.shape
                                                        .RoundedCornerShape(AppDimensions.Dp14),
                                                textStyle = MaterialTheme.typography.bodySmall,
                                                colors =
                                                    OutlinedTextFieldDefaults.colors(
                                                        unfocusedContainerColor = androidx.compose.ui.graphics.Color.White,
                                                        focusedContainerColor = androidx.compose.ui.graphics.Color.White,
                                                    ),
                                            )
                                            TextButton(onClick = {
                                                onAct(EngagementAction.MemoryNote(event.id, note))
                                            }, enabled = !state.busy) {
                                                Text(
                                                    AppText.text(TextKey.CopyLuuGhiChuE3d8bd),
                                                    fontSize = AppTypography.Sp13,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            "SETTINGS" ->
                                SettingsContent(
                                    state,
                                    onAct,
                                    onPermission,
                                    onData = { onOpen("DATA", 0) },
                                    onTaskReminders = { onOpen("INSIGHTS", 0) },
                                )
                            "INSIGHTS" -> InsightsContent(state, onAct, onAdjust, onOpen)
                            else -> {
                                GardenScreenIntro(
                                    AppText.text(TextKey.CopyChamMotChutMoiNgayE8732c),
                                    subtitle = AppText.text(TextKey.CopyMotNhipSongVuaSucTheoCachCuaBan0f4521),
                                )
                                listOf(
                                    "FOCUS" to AppText.text(TextKey.CopyTapTrung964002),
                                    "TEMPLATES" to AppText.text(TextKey.CopyThemTuViecMau4e3260),
                                    "REVIEW" to AppText.text(TextKey.CopyNhinLaiTuanFf3f0c),
                                    "ACHIEVEMENTS" to AppText.text(TextKey.CopyDauMocDffd66),
                                    "TIMELINE" to AppText.text(TextKey.CopyKyNiemE3e113),
                                    "INSIGHTS" to AppText.text(TextKey.CopyNhipTungThoiQuen8481ee),
                                    "SETTINGS" to AppText.text(TextKey.CopyTuyChonNhacViecA44d85),
                                ).forEach { (route, title) ->
                                    GardenNavigationCard(
                                        title,
                                        { onOpen(route, 0) },
                                        art =
                                            when (route) {
                                                "FOCUS" -> GardenArt.LANTERN
                                                "REVIEW" -> GardenArt.CALENDAR
                                                "TIMELINE" -> GardenArt.JOURNAL
                                                "TEMPLATES" -> GardenArt.CHECKLIST
                                                "ACHIEVEMENTS" -> GardenArt.RIBBON
                                                else -> GardenArt.CATEGORY
                                            },
                                    )
                                }
                            }
                        }
                    }
                }
            }
            if (state.message.isNotBlank())MintFeedback(state.message, onDismissMessage)
        }
    }
}

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
