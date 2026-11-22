package com.mamxanh.app.feature.stats.domain.usecase

import com.mamxanh.app.feature.stats.domain.model.StatisticsSummary

interface StatisticsRepository {
    fun load(): StatisticsSummary
}

class LoadGardenStatistics(
    private val repository: StatisticsRepository,
) {
    operator fun invoke(): StatisticsSummary = repository.load()
}
