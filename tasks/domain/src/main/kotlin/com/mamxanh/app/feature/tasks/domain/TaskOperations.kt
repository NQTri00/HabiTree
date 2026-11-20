package com.mamxanh.app.feature.tasks.domain

import com.mamxanh.app.feature.tasks.domain.model.Task
import com.mamxanh.app.feature.tasks.domain.model.TaskConfig
import kotlinx.coroutines.flow.StateFlow

data class TaskOperationResult(val success: Boolean, val message: String)

interface TaskRepository {
    val changes: StateFlow<Long>
    fun snapshot(): TaskProgress
    fun observeSnapshot(): List<Task>
    fun save(id: Long?, config: TaskConfig): TaskOperationResult
    fun lifecycle(id: Long, state: String): TaskOperationResult
    fun timer(id: Long, action: String): TaskOperationResult
    fun completeResult(id: Long): TaskOperationResult
    fun addTemplates(ids: List<String>, operationKey: String): TaskOperationResult
    fun claimDailyBonus(): Boolean
    fun isDailyBonusClaimed(): Boolean
    fun canClaimDailyBonus(): Boolean
}
