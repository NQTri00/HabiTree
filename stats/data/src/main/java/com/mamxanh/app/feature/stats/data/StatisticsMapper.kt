package com.mamxanh.app.feature.stats.data

import com.mamxanh.app.core.data.DataMapper
import com.mamxanh.app.feature.stats.domain.model.StatisticsSummary
import com.mamxanh.shared.model.Statistics

internal object StatisticsMapper : DataMapper<StatisticsSummary, Statistics> {
    override fun toDomain(value: Statistics) = StatisticsSummary(
        coins = value.coins,
        total = value.total,
        today = value.today,
        week = value.week,
        streak = value.streak,
        treeCount = value.treeCount,
        recentDailyCompletions = value.recentDailyCompletions.toList(),
    )

    override fun toData(value: StatisticsSummary) = Statistics(
        coins = value.coins,
        total = value.total,
        today = value.today,
        week = value.week,
        streak = value.streak,
        treeCount = value.treeCount,
        recentDailyCompletions = value.recentDailyCompletions.toList(),
    )
}
