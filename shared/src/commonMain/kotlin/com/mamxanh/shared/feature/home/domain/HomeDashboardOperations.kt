package com.mamxanh.shared.feature.home.domain

import com.mamxanh.shared.feature.home.domain.model.HomeDashboard

interface HomeDashboardRepository {
    fun load(): HomeDashboard
}
class LoadHomeDashboard(private val repository: HomeDashboardRepository) {
    operator fun invoke() = repository.load()
}
