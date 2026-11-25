package com.mamxanh.app.feature.stats.domain.usecase

import com.mamxanh.app.feature.stats.domain.model.TimelineRelated

data class JournalMemory(
    val id: String,
    val at: Long,
    val title: String,
    val related: TimelineRelated?,
    val note: String,
)

fun interface TimelineRepository {
    fun load(): List<JournalMemory>
}
