package com.mamxanh.shared.feature.stats.domain

import com.mamxanh.shared.feature.stats.domain.model.GardenStatistics

interface StatisticsRepository { fun load(): GardenStatistics }
class LoadGardenStatistics(private val repository: StatisticsRepository) { operator fun invoke() = repository.load() }
