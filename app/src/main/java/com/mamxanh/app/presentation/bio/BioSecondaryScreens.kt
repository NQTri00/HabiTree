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

@Preview(name = "Calendar with varied statuses", widthDp = 335, showBackground = true)
@Preview(name = "Calendar at large text", widthDp = 280, fontScale = 1.5f, showBackground = true)
@Composable
private fun BioCalendarPreview() {
    val context = LocalContext.current
    val world = remember(context) { BioWorld.reference(context) }
    BioTheme(world, "1") { BioCalendar(world, count = 14) }
}
