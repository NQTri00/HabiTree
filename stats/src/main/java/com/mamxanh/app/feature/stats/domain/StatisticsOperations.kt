package com.mamxanh.app.feature.stats.domain

import com.mamxanh.app.feature.stats.domain.model.GardenStatistics

interface StatisticsRepository { fun load(): GardenStatistics }
class LoadGardenStatistics(private val repository: StatisticsRepository) { operator fun invoke() = repository.load() }
