package com.mamxanh.app.feature.tasks.presentation.ui.engagement
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.mamxanh.app.core.components.*
import com.mamxanh.app.core.components.components.*
import com.mamxanh.app.core.components.design.AppColors
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppTypography
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.model.*

@Composable
internal fun FocusContent(
    state: EngagementUiState,
    onAct: (EngagementAction) -> Unit,
    onOpen: (String, Long) -> Unit,
) {
    var minutes by rememberSaveable { mutableStateOf("25") }
    var attached by rememberSaveable(state.selectedTask) { mutableLongStateOf(state.selectedTask) }
    val current = state.snapshot.focus.firstOrNull { it.state in setOf("RUNNING", "PAUSED", "BREAK") }
    GardenScreenIntro(
        AppText.text(TextKey.CopyTapTrung964002),
        AppText.text(TextKey.CopyChiMotViecThoi83ccd7),
        AppText.text(TextKey.CopyDanhMotKhoangChoDieuQuanTrongDfd9b6),
    )
    if (current != null) {
        Column(Modifier.fillMaxWidth()) {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp16),
            ) {
                Text(
                    current.title,
                    color = Deep,
                    fontSize = AppTypography.Sp14,
                    lineHeight = AppTypography.Sp18,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                Box(Modifier.size(AppDimensions.Dp240), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { current.activeSeconds.toFloat() / current.targetSeconds.coerceAtLeast(1) },
                        modifier = Modifier.fillMaxSize(),
                        color = Green,
                        strokeWidth = AppDimensions.Dp9,
                        trackColor = AppColors.SwatchFFDDEBE1,
                    )
                    Text(
                        "${current.remaining / 60}:${(current.remaining % 60).toString().padStart(2,'0')}",
                        color = Deep,
                        fontSize = AppTypography.Sp46,
                        lineHeight = AppTypography.Sp56,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Text(
                    if (current.state ==
                        "BREAK"
                    ) {
                        AppText.text(TextKey.CopyNghiMotChutKhongTinhThoiGianNghiA616c7)
                    } else if (current.state ==
                        "RUNNING"
                    ) {
                        AppText.text(TextKey.CopyDangTapTrung1c969b)
                    } else {
                        AppText.text(TextKey.CopyDaTamDungA919ce)
                    },
                    color = Muted,
                    fontSize = AppTypography.Sp11,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp16)) {
                    MintDialogAction(
                        if (current.state ==
                            "RUNNING"
                        ) {
                            AppText.text(TextKey.CopyTamDung676358)
                        } else {
                            AppText.text(TextKey.CopyTiepTuc87a7ae)
                        },
                        { onAct(EngagementAction.Focus(current.id, if (current.state == "RUNNING")"PAUSE" else "RESUME")) },
                        Modifier.weight(1f),
                        enabled = !state.busy,
                    )
                    MintDialogAction(AppText.text(TextKey.CopyKetThuc351849), {
                        onAct(EngagementAction.Focus(current.id, "END"))
                    }, Modifier.weight(1f), primary = false, enabled = !state.busy)
                }
                GardenExpandableSection(AppText.text(TextKey.CopyPhienDangLuuD6c8c9)) {
                    Text(
                        AppText.text(TextKey.CopyRoiUngDungSeTamDungPhanDaLamLuonDuocLuu381f0a),
                        color = Muted,
                        fontSize = AppTypography.Sp12,
                        lineHeight = AppTypography.Sp17,
                    )
                }
            }
        }
    } else {
        Box(Modifier.fillMaxWidth().padding(vertical = AppDimensions.Dp4), contentAlignment = Alignment.Center) {
            Box(Modifier.size(AppDimensions.Dp240), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(progress = {
                    0f
                }, modifier = Modifier.fillMaxSize(), color = Green, strokeWidth = AppDimensions.Dp9, trackColor = AppColors.SwatchFFDFE7D1)
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp8)) {
                    Text(
                        "${minutes.toIntOrNull() ?: 0}:00",
                        color = Deep,
                        fontSize = AppTypography.Sp46,
                        lineHeight = AppTypography.Sp56,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(AppText.text(TextKey.CopyBatDauKhiBanSanSang3d0a9a), color = Muted, fontSize = AppTypography.Sp11)
                }
            }
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp4)) {
            listOf(15, 25, 45, 60).forEach { n ->
                FilterChip(minutes == n.toString(), {
                    minutes = n.toString()
                }, label = { Text(AppText.text(TextKey.Copy0Phut2596d2, n), fontSize = AppTypography.Sp11) })
            }
        }
        GardenTextField(minutes, {
            minutes = it.filter(Char::isDigit).take(3)
        }, AppText.text(TextKey.CopyPhutTapTrung459fbd), placeholder = AppText.text(TextKey.Copy5120Phut0c4b29))
        Text(AppText.text(TextKey.CopyDanhChoFf565f), color = Muted, fontSize = AppTypography.Sp12)
        FilterChip(attached == 0L, {
            attached = 0
        }, label = { Text(AppText.text(TextKey.CopyTuDoKhongThuongXu113e54), fontSize = AppTypography.Sp12) })
        state.tasks
            .filter { it.config.verification == "TIMER" && it.lifecycle == "ACTIVE" && !it.doneToday && !it.finished }
            .forEach { task ->
                FilterChip(
                    selected = attached == task.id,
                    onClick = { attached = task.id },
                    enabled = task.available,
                    label = { Text(task.title, fontSize = AppTypography.Sp11) },
                )
            }
        GardenPaintedButton(AppText.text(TextKey.CopyBatDauB3cacc), {
            onAct(
                EngagementAction.StartFocus(
                    attached,
                    minutes.toIntOrNull() ?: 0,
                    java.util.UUID
                        .randomUUID()
                        .toString(),
                ),
            )
        }, Modifier.fillMaxWidth(), enabled = !state.busy && (minutes.toIntOrNull() ?: 0) in 5..120)
    }
    GardenExpandableSection(AppText.text(TextKey.CopyCacPhienDaLuu0HoanThanhBb1ef4, state.snapshot.focusCompleted)) {
        Text(
            AppText.text(TextKey.CopyDaHoanThanh0Phien3aedba, state.snapshot.focusCompleted),
            color = Deep,
            fontSize = AppTypography.Sp12,
            fontWeight = FontWeight.Bold,
        )
        state.snapshot.focus.take(10).forEach { s ->
            Text(
                AppText.text(
                    TextKey.Copy012Phut31cab4a,
                    s.date,
                    s.activeSeconds / 60,
                    s.targetSeconds / 60,
                    when (s.state) {
                        "COMPLETE" -> AppText.text(TextKey.CopyTronVen2a4501)
                        "ENDED" -> AppText.text(TextKey.CopyDaLuuPhanDaLam0f9d18)
                        "RUNNING" -> AppText.text(TextKey.CopyDangChayCd07e6)
                        else -> AppText.text(TextKey.TabPaused)
                    },
                ),
                color = Muted,
                fontSize = AppTypography.Sp13,
                lineHeight = AppTypography.Sp19,
            )
        }
    }
}
