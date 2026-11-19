package com.mamxanh.app.feature.stats.data

import com.mamxanh.shared.model.Statistics
import org.junit.Assert.assertEquals
import org.junit.Test

class StatisticsMapperTest {
    @Test
    fun preservesEmptyAndPopulatedStatistics() {
        val populated = Statistics(
            coins = 37,
            total = 23,
            today = 2,
            week = 13,
            streak = 3,
            treeCount = 4,
            recentDailyCompletions = listOf(1, 2, 0, 3, 2, 3, 2),
        )
        for (statistics in listOf(Statistics(), populated)) {
            assertEquals(statistics, StatisticsMapper.toData(StatisticsMapper.toDomain(statistics)))
        }
    }
}
