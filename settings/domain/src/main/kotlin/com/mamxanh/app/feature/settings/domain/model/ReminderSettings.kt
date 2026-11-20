package com.mamxanh.app.feature.settings.domain.model

data class ReminderSettings(
    val paused: Boolean,
    val quietStartMinute: Int,
    val quietEndMinute: Int,
    val treeReminders: Boolean,
    val reducedMotion: Boolean,
    val breakPrompts: Boolean,
)
