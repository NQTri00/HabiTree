package com.mamxanh.app.feature.tasks.presentation.ui.engagement

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.mamxanh.app.core.components.*
import com.mamxanh.app.core.components.components.*
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppTypography
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.model.*

@Composable
internal fun ReviewContent(
    state: EngagementUiState,
    onAct: (EngagementAction) -> Unit,
) {
    val r = state.snapshot.review ?: return
    var note by rememberSaveable(r.start, r.reflection) { mutableStateOf(r.reflection) }
    GardenScreenIntro(AppText.text(TextKey.CopyTuanCuaBanC82d43), AppText.text(TextKey.CopyTuanTu055af46, r.start))
    MintMetricStrip(
        listOf(
            AppText.text(TextKey.CopyHoanThanh5eb36f) to "${r.completed}/${r.scheduled}",
            AppText.text(TextKey.CopyTyLeE86c12) to (r.rate?.let { "$it%" } ?: "—"),
            AppText.text(TextKey.CopyTapTrung964002) to AppText.text(TextKey.Copy0Phut2596d2, r.focusMinutes),
        ),
    )
    Text(
        if (r.previousRate == null) {
            AppText.text(TextKey.CopyChuaDuDuLieuDeSoSanhTuanTruoc63f9c2)
        } else {
            AppText.text(TextKey.CopyTuanTruoc0SoLieuDenHomNay1201e0, r.previousRate)
        },
        color = Muted,
        fontSize = AppTypography.Sp11,
        lineHeight = AppTypography.Sp16,
    )
    GardenExpandableSection(AppText.text(TextKey.CopyNhungDieuDaLam9f52b2)) {
        Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp12)) {
            Text(AppText.text(TextKey.CopyThu0XuChi1XuD34d57, r.earned, r.spent), color = Deep, fontSize = AppTypography.Sp12)
            Text(
                AppText.text(TextKey.Copy0NgayChamCay1MocTruongThanhEb3af0, r.careDays, r.milestones),
                color = Deep,
                fontSize = AppTypography.Sp11,
            )
            Text(
                if (r.consistent.isBlank()) {
                    AppText.text(
                        TextKey.CopyChuaDuDuLieuVeNhomThoiQuen2068bb,
                    )
                } else {
                    AppText.text(TextKey.CopyDeuDanNhat0B18f80, r.consistent)
                },
                color = Muted,
                fontSize = AppTypography.Sp11,
            )
            if (r.improve.isNotBlank()) {
                Text(
                    AppText.text(TextKey.CopyCoTheDieuChinhNhe0C674c2, r.improve),
                    color = Muted,
                    fontSize = AppTypography.Sp11,
                )
            }
        }
    }
    Text(
        AppText.text(TextKey.CopyDieuGiKhienTuanNayDeDangHon72fdd9),
        color = Deep,
        fontSize = AppTypography.Sp12,
        fontWeight = FontWeight.Bold,
    )
    GardenTextField(note, {
        note = it.take(2000)
    }, AppText.text(TextKey.CopySuyNgamTuan75229e), placeholder = AppText.text(TextKey.CopyGhiLaiChoRiengBanDa0345))
    GardenPaintedButton(AppText.text(TextKey.CopyLuuSuyNgam18cdd3), {
        onAct(EngagementAction.Reflection(r.start, note))
    }, Modifier.fillMaxWidth(), enabled = !state.busy)
}

@Composable
internal fun RestContent(
    state: EngagementUiState,
    onAct: (EngagementAction) -> Unit,
) {
    var rest by rememberSaveable(state.snapshot.tomorrow) { mutableStateOf(state.snapshot.tomorrow) }
    var protect by rememberSaveable { mutableStateOf("") }
    GardenScreenIntro(AppText.text(TextKey.CopyNghiCungLaMotNhipE4f3b6))
    DayCalendar(state.snapshot.days)
    Text(AppText.text(TextKey.CopyNgayNghiSapToiA8efda), color = Deep, fontSize = AppTypography.Sp12)
    GardenTextField(rest, { rest = it }, AppText.text(TextKey.CopyNgayNghiYyyyMmDd2d6975))
    Text(
        AppText.text(TextKey.CopyToiDa2NgayThangDatTruocNgayNghiKhongThuong8e36f0),
        color = Muted,
        fontSize = AppTypography.Sp12,
        lineHeight = AppTypography.Sp18,
    )
    GardenPaintedButton(AppText.text(TextKey.CopyLenLichNghiD71876), {
        onAct(EngagementAction.Rest(rest))
    }, Modifier.fillMaxWidth(), enabled = !state.busy)
    Text(AppText.text(TextKey.CopyVeBaoVe5a852e), color = Deep, fontSize = AppTypography.Sp18, fontWeight = FontWeight.SemiBold)
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        MintStatusPill(AppText.text(TextKey.Copy0Ve604049, state.snapshot.protectionTickets))
        GardenPaintedButton(AppText.text(TextKey.CopyMua250XuAa5fe3), {
            onAct(
                EngagementAction.BuyProtection(
                    java.util.UUID
                        .randomUUID()
                        .toString(),
                ),
            )
        }, primary = false, enabled = !state.busy)
    }
    GardenTextField(protect, {
        protect = it
    }, AppText.text(TextKey.CopyNgayDaBoLoYyyyMmDdD52a2a), placeholder = AppText.text(TextKey.DateIsoPlaceholder))
    GardenPaintedButton(
        AppText.text(TextKey.CopyDungVe767d96),
        {
            onAct(
                EngagementAction.Protect(
                    protect,
                    java.util.UUID
                        .randomUUID()
                        .toString(),
                ),
            )
        },
        Modifier.fillMaxWidth(),
        primary = false,
        enabled =
            !state.busy && state.snapshot.protectionTickets > 0,
    )
}

@Composable
internal fun InsightsContent(
    state: EngagementUiState,
    onAct: (EngagementAction) -> Unit,
    onAdjust: (Task) -> Unit,
    onOpen: (String, Long) -> Unit,
) {
    val insight = state.snapshot.insights.firstOrNull { it.taskId == state.selectedTask }
    if (insight == null) {
        GardenScreenIntro(AppText.text(TextKey.CopyMoiNgayMotChutF24459), AppText.text(TextKey.CopyNhipThoiQuen9c03a7))
        state.snapshot.insights.forEach { i -> GardenNavigationCard(i.title, { onOpen("INSIGHTS", i.taskId) }, art = GardenArt.CALENDAR) }
        if (state.snapshot.insights.isEmpty()) {
            Text(
                AppText.text(TextKey.CopyThemMotViecDeBatDauGhiLaiNhipThoiQuen5bc1b9),
                color = Muted,
                fontSize = AppTypography.Sp12,
            )
        }
        return
    }
    val task = state.tasks.firstOrNull { it.id == insight.taskId } ?: return
    var days by rememberSaveable { mutableIntStateOf(7) }
    var confirm by remember { mutableStateOf(false) }
    GardenScreenIntro(insight.title)
    Row(horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp12)) {
        listOf(7, 30).forEach { n ->
            FilterChip(days == n, { days = n }, label = { Text(AppText.text(TextKey.Copy0Ngay45fa48, n), fontSize = AppTypography.Sp11) })
        }
    }
    DayCalendar(insight.days.takeLast(days))
    MintMetricStrip(
        listOf(
            AppText.text(TextKey.CopyHienTai027390) to "${insight.streak}",
            AppText.text(TextKey.CopyDaiNhat67c21e) to "${insight.longest}",
            AppText.text(TextKey.CopyTyLe30Ngay84d03d) to (insight.rate?.let { "$it%" } ?: "—"),
        ),
    )
    GardenExpandableSection(AppText.text(TextKey.CopyTapTrungGoiY2462a6)) {
        Text(
            AppText.text(
                TextKey.CopyTapTrung0PhutGioThuongHoanThanh1E3e85d,
                insight.focusMinutes,
                insight.commonHour.ifBlank {
                    AppText.text(TextKey.CopyChuaDuDuLieu348596)
                },
            ),
            color = Muted,
            fontSize = AppTypography.Sp11,
            lineHeight = AppTypography.Sp16,
        )
        if (insight.suggestion.isNotBlank()) {
            Text(insight.suggestion, color = Muted, fontSize = AppTypography.Sp11)
            GardenPaintedButton(AppText.text(TextKey.CopyXemDieuChinhA56d7a), { confirm = true }, Modifier.fillMaxWidth(), primary = false)
        }
    }
    val pref = state.snapshot.reminders.firstOrNull { it.taskId == task.id } ?: ReminderPreference(task.id)
    var minute by rememberSaveable(task.id, pref.minute) { mutableStateOf(clockLabel(pref.minute)) }
    Text(AppText.text(TextKey.CopyNhacViec9e9055), color = Deep, fontSize = AppTypography.Sp12, fontWeight = FontWeight.Bold)
    Text(
        AppText.text(TextKey.CopyMacDinhTatBatQuyenVaNhacToanCucTrongTuyCh703a98),
        color = Muted,
        fontSize = AppTypography.Sp11,
        lineHeight = AppTypography.Sp16,
    )
    GardenTextField(minute, { minute = it }, AppText.text(TextKey.CopyGioNhacHhMmAb3321))
    TextButton(onClick = {
        parseMinute(minute)?.let {
            onAct(EngagementAction.Reminder(pref.copy(minute = it)))
        }
    }) { Text(AppText.text(TextKey.CopyLuuGioNhacBc709c), fontSize = AppTypography.Sp11) }
    FlowRow(horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp4)) {
        listOf(
            "OFF" to AppText.text(TextKey.CopyTat328606),
            "AT" to AppText.text(TextKey.CopyTheoGio4bd1ce),
            "BEFORE" to AppText.text(TextKey.CopyTruocKhungGioC97c51),
        ).forEach { (mode, label) ->
            FilterChip(
                pref.mode == mode,
                { onAct(EngagementAction.Reminder(pref.copy(mode = mode, minute = parseMinute(minute) ?: pref.minute))) },
                label = { Text(label, fontSize = AppTypography.Sp11) },
            )
        }
    }
    TextButton(onClick = { onAct(EngagementAction.Reminder(pref.copy(followUp = !pref.followUp))) }) {
        Text(
            if (pref.followUp) {
                AppText.text(
                    TextKey.CopyMotLanNhacNheNeuChuaXong1e2491,
                )
            } else {
                AppText.text(TextKey.CopyThemMotLanNhacNhe2b429d)
            },
            fontSize = AppTypography.Sp11,
        )
    }
    if (confirm) {
        MintDialog(AppText.text(TextKey.CopyDieuChinhLich513357), { confirm = false }, AppText.text(TextKey.CopyXacNhan1503b0), {
            onAdjust(task)
            confirm =
                false
        }) {
            Text(
                AppText.text(TextKey.CopyChuyenSangNgayThuongTuNgayMaiLichSuVaKeHoEbd07c),
                color = Muted,
                fontSize = AppTypography.Sp12,
                lineHeight = AppTypography.Sp17,
            )
        }
    }
}

internal fun parseMinute(s: String): Int? {
    val p = s.split(':')
    if (p.size != 2)return null
    val h = p[0].toIntOrNull() ?: return null
    val m = p[1].toIntOrNull() ?: return null
    return if (h in 0..23 && m in 0..59)h * 60 + m else null
}
