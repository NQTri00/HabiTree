package com.mamxanh.app.feature.settings.data

import com.mamxanh.app.core.domain.RepositoryResult
import com.mamxanh.app.feature.settings.domain.ReminderSettings
import com.mamxanh.app.feature.settings.domain.ReminderSettingsRepository
import com.mamxanh.shared.data.GameStorage
import com.mamxanh.shared.model.EngagementAction
import com.mamxanh.shared.model.EngagementSettings

internal class OfflineReminderSettingsRepositoryImpl(
    private val storage: GameStorage,
) : ReminderSettingsRepository {
    override fun save(settings: ReminderSettings): RepositoryResult<String> {
        val stored = EngagementSettings(
            paused = settings.paused,
            quietStart = settings.quietStartMinute,
            quietEnd = settings.quietEndMinute,
            treeReminders = settings.treeReminders,
            reducedMotion = settings.reducedMotion,
            breakPrompts = settings.breakPrompts,
        )
        val result = storage.engage(EngagementAction.Settings(stored))
        return if (result.success) RepositoryResult.Success(result.message) else RepositoryResult.Error(result.message)
    }
}
