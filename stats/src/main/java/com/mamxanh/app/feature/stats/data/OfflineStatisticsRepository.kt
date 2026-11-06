package com.mamxanh.app.feature.stats.data

import com.mamxanh.app.data.repository.GameStorage
import com.mamxanh.app.feature.stats.domain.StatisticsRepository
import com.mamxanh.app.feature.stats.domain.model.GardenStatistics

class OfflineStatisticsRepository(private val game: GameStorage) : StatisticsRepository {
    override fun load(): GardenStatistics {
        val value = game.statistics()
        return GardenStatistics(value.coins, value.total, value.today, value.week, value.streak, value.treeCount)
    }
}
