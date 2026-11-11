package com.mamxanh.shared.feature.tasks.domain

import com.mamxanh.shared.feature.tasks.domain.model.Task

interface TaskRepository {
    val changes: kotlinx.coroutines.flow.StateFlow<Long>
    fun snapshot(): com.mamxanh.shared.model.PhaseSnapshot
    fun save(id: Long?,config: com.mamxanh.shared.model.TaskConfig): com.mamxanh.shared.model.ActionResult
    fun lifecycle(id: Long,state: String): com.mamxanh.shared.model.ActionResult
    fun timer(id: Long,action: String): com.mamxanh.shared.model.ActionResult
    fun completeResult(id: Long): com.mamxanh.shared.model.ActionResult
    fun observeSnapshot(): List<Task>
    fun add(title: String, kind: String, category: String, reward: Int, frequency: String, due: String)
    fun delete(id: Long)
    fun complete(id: Long): Boolean
    fun claimDailyBonus(): Boolean
    fun isDailyBonusClaimed(): Boolean
    fun canClaimDailyBonus(): Boolean
}
class LoadTasks(private val repository: TaskRepository) { operator fun invoke() = repository.observeSnapshot() }
class AddTask(private val repository: TaskRepository) {
    operator fun invoke(title: String, kind: String, category: String, reward: Int, frequency: String, due: String) =
        repository.add(title, kind, category, reward, frequency, due)
}
class DeleteTask(private val repository: TaskRepository) { operator fun invoke(id: Long) = repository.delete(id) }
class CompleteTask(private val repository: TaskRepository) { operator fun invoke(id: Long) = repository.complete(id) }
class ClaimDailyBonus(private val repository: TaskRepository) { operator fun invoke() = repository.claimDailyBonus() }
