package com.mamxanh.app.feature.settings.domain.usecase
import com.mamxanh.app.feature.settings.domain.model.ReminderSettings
import com.mamxanh.app.feature.settings.domain.model.TaskReminderSettings
import com.mamxanh.app.feature.settings.domain.repository.ReminderSettingsRepository

import com.mamxanh.app.core.domain.RepositoryResult

class SaveReminderSettingsUseCase(private val repository: ReminderSettingsRepository) {
    operator fun invoke(settings: ReminderSettings): RepositoryResult<String> = repository.save(settings)
    operator fun invoke(settings: TaskReminderSettings): RepositoryResult<String> = repository.save(settings)
}
