package com.mamxanh.shared.feature.home.data

import com.mamxanh.shared.data.GameStorage
import com.mamxanh.shared.feature.home.domain.HomeDashboardRepository
import com.mamxanh.shared.feature.home.domain.model.HomeDashboard
import com.mamxanh.shared.feature.home.domain.model.HomeTree

class OfflineHomeDashboardRepository(private val game: GameStorage) : HomeDashboardRepository {
    override fun load(): HomeDashboard {
        val tasks = game.tasks()
        val garden = game.garden()
        val statistics = game.statistics()
        return HomeDashboard(
            trees = garden.trees.map { HomeTree(it.id, it.speciesId, it.name, it.growth, it.decoration, it.wateredToday) },
            completedTasks = tasks.count { it.doneToday },
            totalTasks = tasks.size,
            seedCount = garden.inventory.filterKeys { it != "fertilizer" }.values.sum(),
            coins = statistics.coins,
            wateringCanMl = game.waterMl()
        )
    }

    override fun refillWater(): Int = game.refillWater(100)
}
