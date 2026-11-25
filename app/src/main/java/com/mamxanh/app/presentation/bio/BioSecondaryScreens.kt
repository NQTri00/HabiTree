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
import com.mamxanh.app.feature.tasks.presentation.ui.insights.InsightsReminderDraft
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
    dayStates: Map<String, String>? = null,
) {
    val records = recordOverride ?: world.data.optJSONArray("days")
    val byDate = records?.objects()?.associateBy { it.s("date") }.orEmpty()
    val days =
        bioDates(world.data.s("date"), count).mapIndexed { index, date ->
            val record = if (dayStates == null) byDate[date]?.s("state") else dayStates[date]
            val status =
                when {
                    record != null ->
                        when (record) {
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

internal fun restRouteContent(
    world: BioWorld,
    ui: BioController,
    live: LiveRest? = null,
): RestRouteContent =
    RestRouteContent(
        state =
            RestScreenState(
                tomorrow =
                    if (live == null) {
                        world.data.s("tomorrow", "2026-11-18")
                    } else {
                        live.state.schedule
                            ?.tomorrow
                            .orEmpty()
                    },
                scheduledDays =
                    if (live == null) {
                        world.state.a("rest").strings()
                    } else {
                        live.state.schedule
                            ?.days
                            ?.filterValues { it == "REST" }
                            ?.keys
                            ?.toList()
                            .orEmpty()
                    },
                tickets = if (live == null) world.state.i("tickets") else live.state.schedule?.tickets ?: 0,
                canBuy = if (live == null) world.coins >= 250 else live.state.schedule?.canBuyTicket == true,
            ),
        onSchedule = { live?.schedule(it) },
        onBuy = { if (live == null) ui.show("ticket") else live.requestTicket() },
        onProtect = { live?.protect(it) },
        calendar = { BioCalendar(world, 30, dayStates = live?.state?.schedule?.days) },
        controls =
            live?.controls(
                live.state.schedule
                    ?.tomorrow
                    .orEmpty(),
            ),
    )

internal fun insightsRouteContent(
    world: BioWorld,
    ui: BioController,
    argument: String,
    live: LiveInsights? = null,
): InsightsRouteContent {
    if (live != null) return liveInsightsContent(world, argument, live)
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
        onSaveReminder = { _, _, _ -> },
        onSettings = { ui.go("settings") },
        calendar = { BioCalendar(world, period.toInt(), insight?.optJSONArray("days")) },
    )
}

private fun liveInsightsContent(
    world: BioWorld,
    id: String,
    live: LiveInsights,
): InsightsRouteContent {
    val detail =
        live.state.snapshot.detail
            ?.takeIf { it.task.id == id }
    val display =
        detail?.let {
            TaskInsightsDisplay(
                it.task.id,
                it.task.title,
                it.streak,
                it.longest,
                it.rate,
                it.focusMinutes,
                it.commonHour,
                it.suggestion,
                it.minute,
                it.mode,
                it.followUp,
            )
        }
    return InsightsRouteContent(
        task = display,
        tasks =
            live.state.snapshot.tasks
                .map { InsightTaskLink(it.id, it.title) },
        period = live.period,
        onPeriod = live::setPeriod,
        onAddTask = live::addTask,
        onTask = live::openTask,
        onAdjust = { live.requestAdjustment(id) },
        onSaveReminder = { minute, mode, follow -> live.saveReminder(id, InsightsReminderDraft(minute, mode, follow)) },
        onSettings = live::openSettings,
        calendar = { BioCalendar(world, live.period.toInt(), dayStates = detail?.days.orEmpty()) },
        reminderDraft = display?.let { live.state.reminders[id] ?: InsightsReminderDraft(it.minute, it.mode, it.followUp) },
        editReminder = { live.editReminder(id, it) },
        busy = live.state.busy,
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

internal fun encyclopediaRouteContent(
    world: BioWorld,
    ui: BioController,
    live: LiveCatalog? = null,
): EncyclopediaRouteContent {
    val species = world.species
    val collection = live?.state?.collection
    return EncyclopediaRouteContent(
        discovered =
            if (live == null) {
                world.records.count { it.b("discovered") }
            } else {
                collection?.species?.count { it.discovered } ?: 0
            },
        speciesCount = species.size,
        goals =
            if (live != null) {
                collection?.milestones?.toEncyclopediaGoals().orEmpty()
            } else {
                world.data.a("goals").objects().filter { it.s("id") !in listOf("expansion", "section") }.map {
                    CollectionGoalDisplay(it.s("title"), it.i("n"), it.i("target", 1), it.s("reward"))
                }
            },
        item = { index, modifier ->
            val item = species[index]
            val entry = live?.state?.species(item.s("id"))
            BioProduct(
                world,
                ui,
                item,
                modifier,
                encyclopedia = true,
                catalogStatus = if (live == null) null else entry?.status ?: AppText.text(TextKey.CopyChuaKhamPhaC0b0cb),
                catalogKnown = if (live == null) null else entry?.let { it.discovered || it.planted > 0 || it.ownedSeeds > 0 } ?: false,
                onProductClick = live?.let { binding -> { binding.openSpecies(item.s("id")) } },
            )
        },
    )
}

internal fun speciesRouteContent(
    world: BioWorld,
    ui: BioController,
    argument: String,
    live: LiveCatalog? = null,
): SpeciesRouteContent {
    val species = world.species(argument)
    val id = species.s("id")
    val record = if (live == null) world.record(id) else org.json.JSONObject()
    val collected = live?.state?.species(id)
    val trees =
        if (live == null) {
            world.trees.filter { it.s("species") == id }.map {
                SpeciesTreeDisplay(it.s("id"), it.s("name"), living = it.s("state") != "dead", stage = it.i("stage"))
            }
        } else {
            live.state.trees.takeIf { live.state.treeSpeciesId == id }.orEmpty().map {
                SpeciesTreeDisplay(it.id.toString(), it.name, living = it.living, stage = it.stage)
            }
        }
    val noRecord = AppText.text(TextKey.CopyChuaGhiNhanF7654d)

    fun recordDate(timestamp: Long?): String =
        timestamp
            ?.takeIf { it > 0 }
            ?.let { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(it)) } ?: noRecord
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
                owned = if (live == null) world.count(id) else collected?.ownedSeeds ?: 0,
                firstPlanted = if (live == null) record.s("firstPlanted", noRecord) else recordDate(collected?.firstPlanted),
                firstMatured = if (live == null) record.s("firstMatured", noRecord) else recordDate(collected?.firstMatured),
                trees = trees,
                collection = collected,
            ),
        onTree = { if (live == null) ui.go("tree", it) else live.openTree(it) },
        onShop = {
            if (live == null) {
                ui.shopSelection.shopTab = "seeds"
                ui.go("shop")
            } else {
                live.openSeeds()
            }
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
