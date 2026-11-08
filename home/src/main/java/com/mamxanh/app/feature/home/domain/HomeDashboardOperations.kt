package com.mamxanh.app.feature.home.domain

import com.mamxanh.app.feature.home.domain.model.HomeDashboard

interface HomeDashboardRepository {
    fun load(): HomeDashboard
    fun refillWater(): Int
}
class LoadHomeDashboard(private val repository: HomeDashboardRepository) {
    operator fun invoke() = repository.load()
}
class RefillWater(private val repository: HomeDashboardRepository) {
    operator fun invoke() = repository.refillWater()
}
