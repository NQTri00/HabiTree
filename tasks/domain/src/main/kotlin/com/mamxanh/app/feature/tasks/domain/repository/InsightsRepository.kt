package com.mamxanh.app.feature.tasks.domain.repository

data class InsightTask(
    val id: String,
    val title: String,
)

data class InsightDetail(
    val task: InsightTask,
    val streak: Int,
    val longest: Int,
    val rate: Int?,
    val focusMinutes: Int,
    val commonHour: String,
    val suggestion: String,
    val minute: String,
    val mode: String,
    val followUp: Boolean,
    val days: Map<String, String>,
)

data class InsightsSnapshot(
    val tasks: List<InsightTask> = emptyList(),
    val detail: InsightDetail? = null,
)

fun interface InsightsRepository {
    fun load(taskId: String): InsightsSnapshot
}
