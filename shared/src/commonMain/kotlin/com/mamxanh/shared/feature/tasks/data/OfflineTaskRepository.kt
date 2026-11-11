package com.mamxanh.shared.feature.tasks.data
import com.mamxanh.shared.data.GameStorage
import com.mamxanh.shared.feature.tasks.domain.TaskRepository
import com.mamxanh.shared.model.TaskConfig
class OfflineTaskRepository(private val game: GameStorage) : TaskRepository {
    override val changes get() = game.changes
    override fun snapshot() = game.phaseSnapshot()
    override fun save(id: Long?,config: TaskConfig) = game.saveTask(id,config)
    override fun lifecycle(id: Long,state: String) = game.taskLifecycle(id,state)
    override fun timer(id: Long,action: String) = game.taskTimer(id,action)
    override fun completeResult(id: Long) = game.completeTask(id)
    override fun observeSnapshot() = game.tasks()
    override fun add(title: String,kind: String,category: String,reward: Int,frequency: String,due: String) =
        game.addTask(title,kind,category,reward,frequency,due)
    override fun delete(id: Long) = game.deleteTask(id)
    override fun complete(id: Long) = game.completeTask(id).success
    override fun claimDailyBonus() = game.dailyBonus()
    override fun isDailyBonusClaimed() = game.isDailyBonusClaimed()
    override fun canClaimDailyBonus() = game.canClaimDailyBonus()
}
