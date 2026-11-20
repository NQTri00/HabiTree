package com.mamxanh.app.feature.tasks.data.di

import com.mamxanh.app.feature.tasks.domain.repository.TaskOperationResult
import com.mamxanh.app.feature.tasks.domain.model.TaskProgress
import com.mamxanh.app.feature.tasks.domain.repository.TaskRepository
import com.mamxanh.app.feature.tasks.domain.model.TaskTimerProgress
import com.mamxanh.app.feature.tasks.domain.model.Task
import com.mamxanh.app.feature.tasks.domain.model.TaskHistoryEntry
import com.mamxanh.shared.feature.tasks.domain.TaskRepository as SharedTaskRepository
import com.mamxanh.shared.data.GameStorage
import com.mamxanh.shared.model.EngagementAction
import com.mamxanh.shared.model.ActionResult
import com.mamxanh.shared.model.TaskConfig as SharedTaskConfig
import com.mamxanh.app.feature.tasks.domain.model.TaskConfig
import org.koin.dsl.module

private class AndroidTaskRepository(
    private val source: SharedTaskRepository,
    private val storage: GameStorage,
) : TaskRepository {
    override val changes get() = source.changes
    override fun snapshot(): TaskProgress {
        val phase = source.snapshot()
        return TaskProgress(
            earnedToday = phase.earnedToday,
            required = phase.required,
            completed = phase.completed,
            streak = phase.streak,
            sessions = phase.sessions.map { session ->
                TaskTimerProgress(
                    id = session.id,
                    taskId = session.taskId,
                    activeSeconds = session.activeSeconds,
                    requiredSeconds = session.requiredSeconds,
                    running = session.running,
                )
            },
        )
    }
    override fun observeSnapshot(): List<Task> = source.observeSnapshot().map { task ->
        Task(
            id = task.id,
            title = task.title,
            kind = task.kind,
            category = task.category,
            reward = task.reward,
            frequency = task.frequency,
            due = task.due,
            completed = task.completed,
            streak = task.streak,
            lastDone = task.lastDone,
            config = task.config.toDomain(),
            lifecycle = task.lifecycle,
            scheduledToday = task.scheduledToday,
            available = task.available,
            availability = task.availability,
            history = task.history.map { TaskHistoryEntry(it.date, it.nominal, it.granted) },
            doneToday = task.doneToday,
        )
    }
    override fun save(id: Long?, config: TaskConfig) = source.save(id, config.toStorage()).toTaskResult()
    override fun lifecycle(id: Long, state: String) = source.lifecycle(id, state).toTaskResult()
    override fun timer(id: Long, action: String) = source.timer(id, action).toTaskResult()
    override fun completeResult(id: Long) = source.completeResult(id).toTaskResult()
    override fun addTemplates(ids: List<String>, operationKey: String): TaskOperationResult =
        storage.engage(EngagementAction.Templates(ids, operationKey)).toTaskResult()

    override fun claimDailyBonus() = source.claimDailyBonus()
    override fun isDailyBonusClaimed() = source.isDailyBonusClaimed()
    override fun canClaimDailyBonus() = source.canClaimDailyBonus()
}

private fun ActionResult.toTaskResult() = TaskOperationResult(success, message)

private fun SharedTaskConfig.toDomain() = TaskConfig(
    title, kind, category, description, frequency, days, due, verification, minutes, startMinute, endMinute, reminderMinute,
)

private fun TaskConfig.toStorage() = SharedTaskConfig(
    title, kind, category, description, frequency, days, due, verification, minutes, startMinute, endMinute, reminderMinute,
)

val taskDataModule = module {
    single<TaskRepository> { AndroidTaskRepository(get(), get()) }
}
