package com.mamxanh.app.feature.stats.data

import com.mamxanh.app.data.repository.GameStorage
import com.mamxanh.app.feature.stats.domain.StatisticsRepository
import com.mamxanh.app.feature.stats.domain.model.GardenStatistics

class OfflineStatisticsRepository(private val game: GameStorage) : StatisticsRepository {
    override fun load(): GardenStatistics {
        val value = game.statistics()
        return GardenStatistics(
            coins = value.coins,
            total = value.total,
            today = value.today,
            week = value.week,
            streak = value.streak,
            treeCount = value.treeCount,
            recentDailyCompletions = value.recentDailyCompletions
        )
    }
}
