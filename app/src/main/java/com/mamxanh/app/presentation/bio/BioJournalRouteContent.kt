package com.mamxanh.app.presentation.bio

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import com.mamxanh.app.core.components.design.*
import com.mamxanh.app.feature.stats.domain.model.TimelineRelated
import com.mamxanh.app.feature.stats.presentation.navigation.TimelineRouteContent
import com.mamxanh.app.feature.stats.presentation.navigation.WeeklyReviewRouteContent
import com.mamxanh.app.feature.stats.presentation.ui.achievements.AchievementDisplay
import com.mamxanh.app.feature.stats.presentation.ui.review.WeeklyReviewDisplay
import com.mamxanh.app.feature.stats.presentation.ui.timeline.TimelineMemory
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import java.util.*

internal fun weeklyReviewRouteContent(
    world: BioWorld,
    ui: BioController,
    live: LiveJournal? = null,
): WeeklyReviewRouteContent {
    val review = world.data.o("review")
    val display =
        if (live != null) {
            live.state.weeklyReview?.let {
                WeeklyReviewDisplay(
                    startDisplay = it.start,
                    reflection = it.reflection,
                    completed = it.completed,
                    scheduled = it.scheduled,
                    focusMinutes = it.focusMinutes,
                    previousRate = it.previousRate,
                    earned = it.earned,
                    spent = it.spent,
                    careDays = it.careDays,
                    milestones = it.milestones,
                    consistent = it.consistent,
                    improve = it.improve,
                    tickets = it.tickets,
                    dailyCompletions = List(7) { index -> (it.dailyCompletions.getOrNull(index) ?: 0).coerceAtLeast(0) },
                    dayLabels = world.statisticsDayLabels(),
                )
            }
        } else {
            WeeklyReviewDisplay(
                startDisplay = review.s("start", "2026-11-16"),
                reflection = review.s("reflection", world.state.s("reflection")),
                completed = review.i("completed"),
                scheduled = review.i("scheduled"),
                focusMinutes = review.i("focusMinutes"),
                previousRate = if (review.has("previousRate")) review.i("previousRate") else null,
                earned = review.i("earned"),
                spent = review.i("spent"),
                careDays = review.i("careDays"),
                milestones = review.i("milestones"),
                consistent = review.s("consistent", AppText.text(TextKey.CopyChuaDuDuLieu414142)),
                improve = review.s("improve", AppText.text(TextKey.CopyChuaDuDuLieu414142)),
                tickets = world.state.i("tickets"),
                dailyCompletions = world.statisticsDailyCompletions(),
                dayLabels = world.statisticsDayLabels(),
            )
        }
    val week = live?.state?.weeklyReview?.start ?: review.s("start")
    return WeeklyReviewRouteContent(
        review = display,
        onTasks = { if (live == null) ui.go("tasks") else live.openTasks() },
        onSaveNote = { note -> live?.saveReflection(week, note) },
        onRest = { if (live == null) ui.go("rest") else live.openRest() },
        draft = live?.state?.reflection(week, display?.reflection.orEmpty()),
        onEdit = live?.let { route -> { note -> route.editReflection(week, note) } },
        busy = live?.state?.busy == true,
    )
}

internal fun previewAchievements(): List<AchievementDisplay> {
    val defaults =
        listOf(
            Triple(AppText.text(TextKey.CopyBuocDauTien5dbfda), AppText.text(TextKey.CopyHoanThanhMotViecHopLe912065), 1 to 1),
            Triple(
                AppText.text(TextKey.CopyBaNgayTotLanhD6a627),
                AppText.text(TextKey.Copy3NgayCoLichHoanThanh0527b6),
                3 to 3,
            ),
            Triple(AppText.text(TextKey.CopyMotTuanDeuDanD7d5ad), AppText.text(TextKey.Copy7NgayCoLichHoanThanh86a291), 5 to 7),
            Triple(
                AppText.text(TextKey.CopyKhoiDauTapTrungC3b5b9),
                AppText.text(TextKey.CopyHoanThanhMotPhienItNhat25Phut6bcfa2),
                0 to 1,
            ),
            Triple(
                AppText.text(TextKey.CopyNhipHocTapF10229),
                AppText.text(TextKey.Copy10LanHoanThanhViecHocDcd041),
                6 to 10,
            ),
            Triple(
                AppText.text(TextKey.CopyBanTayXanh9bd1dc),
                AppText.text(TextKey.CopyTuoiCayTrong10NgayKhacNhau786a07),
                3 to 10,
            ),
            Triple(
                AppText.text(TextKey.CopyBongHoaDauTien4d67d1),
                AppText.text(TextKey.CopyMotLoaiHoaDatGiaiDoanCuoi468527),
                0 to 1,
            ),
            Triple(
                AppText.text(TextKey.CopyCayTruongThanhDauTienF9a6e3),
                AppText.text(TextKey.CopyMotLoaiKhongHoaDatGiaiDoanCuoi120850),
                1 to 1,
            ),
            Triple(
                AppText.text(TextKey.CopyGocVuonDaDang8a3813),
                AppText.text(TextKey.CopyDangSoHuu3LoaiKhacNhauA27bf0),
                3 to 3,
            ),
            Triple(
                AppText.text(TextKey.CopyThangDeuDanD8b460),
                AppText.text(TextKey.Copy20NgayCoLichHoanThanh4b7346),
                5 to 20,
            ),
        )
    return defaults.map { (title, criterion, progress) ->
        AchievementDisplay(title, criterion, progress.first, progress.second)
    }
}

internal fun timelineRouteContent(
    world: BioWorld,
    ui: BioController,
    live: LiveJournal? = null,
): TimelineRouteContent {
    val notes = world.state.o("notes")
    val memories =
        live?.state?.memories?.map {
            val date =
                java.text.DateFormat
                    .getDateTimeInstance(
                        java.text.DateFormat.SHORT,
                        java.text.DateFormat.SHORT,
                        bioLocale(),
                    ).format(java.util.Date(it.at))
            TimelineMemory(it.id, date, it.title, it.related, it.note)
        } ?: world.state.a("memories").objects().map { memory ->
            val id = memory.s("id")
            TimelineMemory(
                id = id,
                date = memory.s("date"),
                title = memory.s("title"),
                related = TimelineRelated.fromStored(memory.s("type"), memory.s("entity")),
                note = notes.s(id, memory.s("note")),
            )
        }
    return TimelineRouteContent(
        memories = memories,
        onRelated = { target ->
            if (live == null) {
                val destination = target.toBioDestination()
                ui.go(destination.routeId, destination.argument)
            } else {
                live.openRelated(target)
            }
        },
        onSaveNote = { id, note -> live?.saveMemory(id, note) },
        noteFor = live?.let { route -> { memory -> route.state.memory(memory.id, memory.note) } },
        onEdit = live?.let { it::editMemory },
        busy = live?.state?.busy == true,
    )
}
