package com.mamxanh.app.presentation.bio

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.mamxanh.app.core.components.design.*
import com.mamxanh.app.feature.catalog.presentation.model.SpeciesTreeDisplay
import com.mamxanh.app.feature.catalog.presentation.model.speciesDisplay
import com.mamxanh.app.feature.catalog.presentation.navigation.EncyclopediaRouteContent
import com.mamxanh.app.feature.catalog.presentation.navigation.SpeciesRouteContent
import com.mamxanh.app.feature.catalog.presentation.ui.encyclopedia.CollectionGoalDisplay
import com.mamxanh.app.feature.catalog.presentation.ui.encyclopedia.toEncyclopediaGoals
import com.mamxanh.app.feature.stats.domain.model.TimelineRelated
import com.mamxanh.app.feature.stats.presentation.navigation.TimelineRouteContent
import com.mamxanh.app.feature.stats.presentation.navigation.WeeklyReviewRouteContent
import com.mamxanh.app.feature.stats.presentation.ui.achievements.AchievementDisplay
import com.mamxanh.app.feature.stats.presentation.ui.review.WeeklyReviewDisplay
import com.mamxanh.app.feature.stats.presentation.ui.timeline.TimelineMemory
import com.mamxanh.app.feature.tasks.presentation.component.TaskCalendar
import com.mamxanh.app.feature.tasks.presentation.component.TaskCalendarDay
import com.mamxanh.app.feature.tasks.presentation.component.TaskCalendarStatus
import com.mamxanh.app.feature.tasks.presentation.navigation.InsightsRouteContent
import com.mamxanh.app.feature.tasks.presentation.navigation.RestRouteContent
import com.mamxanh.app.feature.tasks.presentation.ui.insights.InsightTaskLink
import com.mamxanh.app.feature.tasks.presentation.ui.insights.TaskInsightsDisplay
import com.mamxanh.app.feature.tasks.presentation.ui.rest.RestScreenState
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*

internal fun bioDates(
    date: String,
    count: Int,
): List<String> {
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val c = Calendar.getInstance()
    try {
        c.time =
            format.parse(date)!!
    } catch (
        _: Exception,
    ) {
    }
    c.add(Calendar.DATE, 1 - count)
    return (0 until count).map { format.format(c.time).also { c.add(Calendar.DATE, 1) } }
}

@Composable
private fun BioCalendar(
    world: BioWorld,
    count: Int = 7,
    recordOverride: JSONArray? = null,
) {
    val records = recordOverride ?: world.data.optJSONArray("days")
    val byDate = records?.objects()?.associateBy { it.s("date") }.orEmpty()
    val days =
        bioDates(world.data.s("date"), count).mapIndexed { index, date ->
            val record = byDate[date]
            val status =
                when {
                    record != null ->
                        when (record.s("state")) {
                            "SUCCESS" -> TaskCalendarStatus.Completed
                            "REST" -> TaskCalendarStatus.Rest
                            "PROTECTED" -> TaskCalendarStatus.Protected
                            "MISSED" -> TaskCalendarStatus.Missed
                            else -> TaskCalendarStatus.Unrecorded
                        }
                    records != null || world.native -> TaskCalendarStatus.Unrecorded
                    index % 7 == 3 -> TaskCalendarStatus.Rest
                    index % 9 == 5 -> TaskCalendarStatus.Protected
                    index % 3 != 1 -> TaskCalendarStatus.Completed
                    else -> TaskCalendarStatus.Missed
                }
            TaskCalendarDay(date, status)
        }
    TaskCalendar(days)
}

internal fun weeklyReviewRouteContent(
    world: BioWorld,
    ui: BioController,
): WeeklyReviewRouteContent {
    val review = world.data.o("review")
    val display =
        if (world.native && review.length() == 0) {
            null
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
    return WeeklyReviewRouteContent(
        review = display,
        onTasks = { ui.go("tasks") },
        onSaveNote = { note -> ui.send("reflection", mapOf("note" to note, "week" to review.s("start"))) },
        onRest = { ui.go("rest") },
    )
}

internal fun restRouteContent(
    world: BioWorld,
    ui: BioController,
): RestRouteContent =
    RestRouteContent(
        state =
            RestScreenState(
                tomorrow = world.data.s("tomorrow", "2026-11-18"),
                scheduledDays = world.state.a("rest").strings(),
                tickets = world.state.i("tickets"),
                canBuy = world.coins >= 250,
            ),
        onSchedule = { ui.send("rest", mapOf("date" to it)) },
        onBuy = { ui.show("ticket") },
        onProtect = { ui.send("protect", mapOf("date" to it)) },
        calendar = { BioCalendar(world, 30) },
    )

internal fun insightsRouteContent(
    world: BioWorld,
    ui: BioController,
    argument: String,
): InsightsRouteContent {
    val task = world.task(argument)
    val id = task?.s("id").orEmpty()
    val insight =
        if (task == null) {
            null
        } else {
            world.data.a("insights").objects().firstOrNull {
                it.s("taskId") == id
            } ?: json(
                "longest" to 7,
                "rate" to 72,
                "focusMinutes" to 125,
                "commonHour" to "19:00",
                "suggestion" to AppText.text(TextKey.CopyDieuChinhSangNgayThuong939cdd),
            )
        }
    val display =
        if (task == null || insight == null) {
            null
        } else {
            val reminder = world.state.o("reminders").o(id)
            TaskInsightsDisplay(
                id = id,
                title = task.s("title"),
                streak = task.i("streak"),
                longest = insight.i("longest"),
                rate = if (insight.has("rate")) insight.i("rate") else null,
                focusMinutes = insight.i("focusMinutes"),
                commonHour = insight.s("commonHour", AppText.text(TextKey.CopyChuaDuDuLieu414142)),
                suggestion = insight.s("suggestion"),
                minute = reminder.s("minute", "09:00"),
                mode = reminder.s("mode", "OFF"),
                followUp = reminder.b("followUp"),
            )
        }
    val period = ui.tabs["insightDays"] ?: "7"
    return InsightsRouteContent(
        task = display,
        tasks = if (display == null) world.tasks.map { InsightTaskLink(it.s("id"), it.s("title")) } else emptyList(),
        period = period,
        onPeriod = { ui.tabs["insightDays"] = it },
        onAddTask = { ui.go("task-new") },
        onTask = { ui.go("insights", it) },
        onAdjust = { ui.show("adjust", id) },
        onSaveReminder = { minute, mode, follow ->
            ui.send("reminder", mapOf("id" to id, "minute" to minute, "mode" to mode, "followUp" to follow.toString()))
        },
        onSettings = { ui.go("settings") },
        calendar = { BioCalendar(world, period.toInt(), insight?.optJSONArray("days")) },
    )
}

internal fun achievementEntries(world: BioWorld): List<AchievementDisplay> =
    if (world.native) {
        world.data.a("achievements").objects().map {
            AchievementDisplay(it.s("title"), it.s("criterion"), it.i("progress"), it.i("target", 1), it.l("unlockedAt") > 0)
        }
    } else {
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
        defaults.map { (title, criterion, progress) ->
            AchievementDisplay(title, criterion, progress.first, progress.second)
        }
    }

internal fun timelineRouteContent(
    world: BioWorld,
    ui: BioController,
): TimelineRouteContent {
    val notes = world.state.o("notes")
    val memories =
        world.state.a("memories").objects().map { memory ->
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
            val destination = target.toBioDestination()
            ui.go(destination.routeId, destination.argument)
        },
        onSaveNote = { id, note -> ui.send("memory-note", mapOf("id" to id, "note" to note)) },
    )
}

internal fun encyclopediaRouteContent(
    world: BioWorld,
    ui: BioController,
): EncyclopediaRouteContent {
    val species = world.species
    val collection = world.gardenCollection
    return EncyclopediaRouteContent(
        discovered =
            collection?.species?.count { it.discovered }
                ?: world.records.count { it.b("discovered") },
        speciesCount = species.size,
        goals =
            collection?.milestones?.toEncyclopediaGoals()
                ?: world.data.a("goals").objects().filter { it.s("id") !in listOf("expansion", "section") }.map {
                    CollectionGoalDisplay(it.s("title"), it.i("n"), it.i("target", 1), it.s("reward"))
                },
        item = { index, modifier -> BioProduct(world, ui, species[index], modifier, encyclopedia = true) },
    )
}

internal fun speciesRouteContent(
    world: BioWorld,
    ui: BioController,
    argument: String,
): SpeciesRouteContent {
    val species = world.species(argument)
    val id = species.s("id")
    val record = world.record(id)
    val collected = world.gardenCollection?.species?.firstOrNull { it.id == id }
    val trees = world.trees.filter { it.s("species") == id }
    val noRecord = AppText.text(TextKey.CopyChuaGhiNhanF7654d)
    return SpeciesRouteContent(
        state =
            speciesDisplay(
                id = id,
                name = species.s("name"),
                description = species.s("description"),
                tier = bioTier(species.s("tier")),
                status = record.s("status", AppText.text(TextKey.CopyChuaKhamPhaC0b0cb)),
                discovered = record.b("discovered"),
                highest = record.i("highest"),
                stages = species.i("stages"),
                planted = record.i("planted"),
                owned = world.count(id),
                firstPlanted = record.s("firstPlanted", noRecord),
                firstMatured = record.s("firstMatured", noRecord),
                trees =
                    trees.map {
                        SpeciesTreeDisplay(it.s("id"), it.s("name"), living = it.s("state") != "dead", stage = it.i("stage"))
                    },
                collection = collected,
            ),
        onTree = { ui.go("tree", it) },
        onShop = {
            ui.shopSelection.shopTab = "seeds"
            ui.go("shop")
        },
    )
}

@Preview(name = "Calendar with varied statuses", widthDp = 335, showBackground = true)
@Preview(name = "Calendar at large text", widthDp = 280, fontScale = 1.5f, showBackground = true)
@Composable
private fun BioCalendarPreview() {
    val context = LocalContext.current
    val world = remember(context) { BioWorld.reference(context) }
    BioTheme(world, "1") { BioCalendar(world, count = 14) }
}
