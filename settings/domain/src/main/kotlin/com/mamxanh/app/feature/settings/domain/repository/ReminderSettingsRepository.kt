package com.mamxanh.app.feature.settings.domain.repository

import com.mamxanh.app.core.domain.RepositoryResult
import com.mamxanh.app.feature.settings.domain.model.ReminderSettings
import com.mamxanh.app.feature.settings.domain.model.TaskReminderSettings
import kotlinx.coroutines.flow.StateFlow

interface ReminderSettingsRepository {
    val changes: StateFlow<Long>

    fun load(): ReminderSettings

    fun save(settings: ReminderSettings): RepositoryResult<String>

    fun save(settings: TaskReminderSettings): RepositoryResult<String>
}
