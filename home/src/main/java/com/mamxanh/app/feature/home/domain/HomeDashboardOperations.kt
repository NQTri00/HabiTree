package com.mamxanh.app.feature.home.domain

import com.mamxanh.app.feature.home.domain.model.HomeDashboard

interface HomeDashboardRepository { fun load(): HomeDashboard }
class LoadHomeDashboard(private val repository: HomeDashboardRepository) { operator fun invoke() = repository.load() }
