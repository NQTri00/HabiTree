package com.mamxanh.shared.feature.stats.data

import com.mamxanh.shared.data.GameStorage
import com.mamxanh.shared.feature.stats.domain.StatisticsRepository
import com.mamxanh.shared.feature.stats.domain.model.GardenStatistics

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
