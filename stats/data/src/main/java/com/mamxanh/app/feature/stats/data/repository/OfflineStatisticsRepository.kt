package com.mamxanh.app.feature.stats.data.repository

import com.mamxanh.app.feature.stats.data.StatisticsMapper

import com.mamxanh.app.feature.stats.domain.usecase.StatisticsRepository
import com.mamxanh.app.feature.stats.domain.model.StatisticsSummary
import com.mamxanh.shared.data.GameStorage

class OfflineStatisticsRepository(private val storage: GameStorage) : StatisticsRepository {
    override fun load(): StatisticsSummary = StatisticsMapper.toDomain(storage.statistics())
}
