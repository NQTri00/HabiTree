package com.mamxanh.app.feature.stats.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mamxanh.app.feature.stats.domain.LoadGardenStatistics
import com.mamxanh.app.feature.stats.domain.StatisticsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StatsViewModel(private val repository: StatisticsRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState = _uiState.asStateFlow()
    init { dispatch(StatsIntent.Refresh) }
    fun dispatch(intent: StatsIntent) {
        when (intent) {
            StatsIntent.Refresh -> viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val statistics = withContext(Dispatchers.IO) { LoadGardenStatistics(repository)() }
                _uiState.value = StatsUiState(statistics, false)
            }
        }
    }
}
