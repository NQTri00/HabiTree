package com.mamxanh.app.feature.tasks.data.repository

import com.mamxanh.app.feature.tasks.domain.repository.InsightDetail
import com.mamxanh.app.feature.tasks.domain.repository.InsightTask
import com.mamxanh.app.feature.tasks.domain.repository.InsightsRepository
import com.mamxanh.app.feature.tasks.domain.repository.InsightsSnapshot
import com.mamxanh.shared.data.GameStorage
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.model.clockLabel

internal class InsightsRepositoryImpl(
    private val storage: GameStorage,
) : InsightsRepository {
    override fun load(taskId: String): InsightsSnapshot {
        val tasks = storage.tasks()
        val task =
            tasks.firstOrNull { it.id.toString() == taskId }
                ?: return InsightsSnapshot(tasks.map { InsightTask(it.id.toString(), it.title) })
        val snapshot = storage.engagementSnapshot()
        val insight = snapshot.insights.firstOrNull { it.taskId == task.id }
        val reminder = snapshot.reminders.firstOrNull { it.taskId == task.id }
        return InsightsSnapshot(
            detail =
                InsightDetail(
                    task = InsightTask(taskId, task.title),
                    streak = task.streak,
                    longest = insight?.longest ?: 0,
                    rate = insight?.rate,
                    focusMinutes = insight?.focusMinutes?.toInt() ?: 0,
                    commonHour = insight?.commonHour ?: AppText.text(TextKey.CopyChuaDuDuLieu414142),
                    suggestion = insight?.suggestion.orEmpty(),
                    minute = reminder?.let { clockLabel(it.minute) } ?: "09:00",
                    mode = reminder?.mode ?: "OFF",
                    followUp = reminder?.followUp ?: false,
                    days = (insight?.days ?: snapshot.days).associate { it.date to it.state },
                ),
        )
    }
}
