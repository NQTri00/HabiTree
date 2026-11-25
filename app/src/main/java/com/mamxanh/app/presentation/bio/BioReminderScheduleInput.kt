package com.mamxanh.app.presentation.bio

import com.mamxanh.shared.model.EngagementSettings
import com.mamxanh.shared.model.ReminderPreference
import com.mamxanh.shared.model.Task

internal data class BioReminderScheduleInput(
    val tasks: List<Task>,
    val settings: EngagementSettings,
    val reminders: List<ReminderPreference>,
)
