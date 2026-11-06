package com.mamxanh.app.feature.stats.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mamxanh.app.feature.stats.domain.LoadGardenStatistics
import com.mamxanh.app.feature.stats.domain.StatisticsRepository
import com.mamxanh.app.feature.stats.domain.model.GardenStatistics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StatsViewModel(private val repository: StatisticsRepository) : ViewModel() {
    private val _statistics = MutableStateFlow(GardenStatistics())
    val statistics = _statistics.asStateFlow()
    init { refresh() }
    fun refresh() = viewModelScope.launch { _statistics.value = withContext(Dispatchers.IO) { LoadGardenStatistics(repository)() } }
}
