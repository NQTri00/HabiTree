package com.mamxanh.app.feature.home.data

import com.mamxanh.app.data.repository.OfflineGameRepository
import com.mamxanh.app.feature.home.domain.HomeDashboardRepository
import com.mamxanh.app.feature.home.domain.model.HomeDashboard
import com.mamxanh.app.feature.home.domain.model.HomeTree

class OfflineHomeDashboardRepository(private val game: OfflineGameRepository) : HomeDashboardRepository {
    override fun load(): HomeDashboard {
        val tasks = game.tasks()
        val garden = game.garden()
        val statistics = game.statistics()
        return HomeDashboard(
            trees = garden.trees.map { HomeTree(it.id, it.speciesId, it.name, it.growth, it.decoration) },
            completedTasks = tasks.count { it.doneToday },
            totalTasks = tasks.size,
            seedCount = garden.inventory.filterKeys { it != "fertilizer" }.values.sum(),
            coins = statistics.coins
        )
    }
}
