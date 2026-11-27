package com.mamxanh.app.feature.settings.data.repository

import com.mamxanh.app.core.domain.RepositoryResult
import com.mamxanh.app.feature.settings.domain.model.ReminderSettings
import com.mamxanh.app.feature.settings.domain.model.TaskReminderSettings
import com.mamxanh.app.feature.settings.domain.repository.ReminderSettingsRepository
import com.mamxanh.shared.data.GameStorage
import com.mamxanh.shared.model.EngagementAction
import com.mamxanh.shared.model.EngagementSettings
import com.mamxanh.shared.model.ReminderPreference

internal class OfflineReminderSettingsRepositoryImpl(
    private val storage: GameStorage,
) : ReminderSettingsRepository {
    override val changes get() = storage.changes

    override fun load(): ReminderSettings {
        val settings = storage.engagementSnapshot().settings
        return ReminderSettings(
            settings.paused,
            settings.quietStart,
            settings.quietEnd,
            settings.treeReminders,
            settings.reducedMotion,
            settings.breakPrompts,
        )
    }

    override fun save(settings: ReminderSettings): RepositoryResult<String> {
        val stored =
            EngagementSettings(
                paused = settings.paused,
                quietStart = settings.quietStartMinute,
                quietEnd = settings.quietEndMinute,
                treeReminders = settings.treeReminders,
                reducedMotion = settings.reducedMotion,
                breakPrompts = settings.breakPrompts,
            )
        return save(EngagementAction.Settings(stored))
    }

    override fun save(settings: TaskReminderSettings): RepositoryResult<String> =
        save(EngagementAction.Reminder(ReminderPreference(settings.taskId, settings.mode, settings.minute, settings.followUp)))

    private fun save(action: EngagementAction): RepositoryResult<String> {
        val result = storage.engage(action)
        return if (result.success) RepositoryResult.Success(result.message) else RepositoryResult.Error(result.message)
    }
}
