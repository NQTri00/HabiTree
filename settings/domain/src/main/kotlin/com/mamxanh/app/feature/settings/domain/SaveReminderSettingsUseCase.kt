package com.mamxanh.app.feature.settings.domain

import com.mamxanh.app.core.domain.RepositoryResult

class SaveReminderSettingsUseCase(private val repository: ReminderSettingsRepository) {
    operator fun invoke(settings: ReminderSettings): RepositoryResult<String> = repository.save(settings)
    operator fun invoke(settings: TaskReminderSettings): RepositoryResult<String> = repository.save(settings)
}
