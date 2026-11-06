package com.mamxanh.app.feature.tasks.data

import com.mamxanh.app.data.repository.OfflineGameRepository
import com.mamxanh.app.feature.tasks.domain.TaskRepository
import com.mamxanh.app.feature.tasks.domain.model.Task

class OfflineTaskRepository(private val game: OfflineGameRepository) : TaskRepository {
    override fun observeSnapshot() = game.tasks().map {
        Task(it.id, it.title, it.kind, it.category, it.reward, it.frequency, it.due, it.completed, it.streak, it.lastDone)
    }
    override fun add(title: String, kind: String, category: String, reward: Int, frequency: String, due: String) =
        game.addTask(title, kind, category, reward, frequency, due)
    override fun delete(id: Long) = game.deleteTask(id)
    override fun complete(id: Long) = game.complete(id)
    override fun claimDailyBonus() = game.dailyBonus()
}
