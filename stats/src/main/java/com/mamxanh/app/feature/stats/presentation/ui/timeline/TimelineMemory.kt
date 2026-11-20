package com.mamxanh.app.feature.stats.presentation.ui.timeline

import com.mamxanh.app.feature.stats.domain.model.TimelineRelated

data class TimelineMemory(
    val id: String,
    val date: String,
    val title: String,
    val related: TimelineRelated?,
    val note: String,
)
