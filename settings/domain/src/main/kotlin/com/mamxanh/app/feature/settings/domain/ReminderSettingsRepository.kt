package com.mamxanh.app.feature.settings.domain

import com.mamxanh.app.core.domain.RepositoryResult

interface ReminderSettingsRepository {
    fun save(settings: ReminderSettings): RepositoryResult<String>
}
