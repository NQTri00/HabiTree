package com.mamxanh.app.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mamxanh.app.feature.home.domain.LoadHomeDashboard
import com.mamxanh.app.feature.home.domain.RefillWater
import com.mamxanh.app.feature.home.domain.HomeDashboardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(private val repository: HomeDashboardRepository) : ViewModel() {
    private var dismissedRewardDate = ""
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()
    private val effectChannel = Channel<HomeEffect>(Channel.BUFFERED)
    val effects = effectChannel.receiveAsFlow()
    init { dispatch(HomeIntent.Refresh) }

    fun dispatch(intent: HomeIntent) {
        when (intent) {
            HomeIntent.DismissDailyClear -> {
                dismissedRewardDate = _uiState.value.dashboard.dailyRewardDate
                _uiState.value = _uiState.value.copy(isDailyClearVisible = false)
            }
            HomeIntent.Refresh -> viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val dashboard = withContext(Dispatchers.IO) { LoadHomeDashboard(repository)() }
                _uiState.value = _uiState.value.copy(dashboard = dashboard, isLoading = false,
                    isDailyClearVisible = dashboard.dailyRewardDate.isNotBlank() && dashboard.dailyRewardDate != dismissedRewardDate)
            }
            HomeIntent.RefillWater -> viewModelScope.launch {
                val before = _uiState.value.dashboard.wateringCanMl
                val amount = withContext(Dispatchers.IO) { RefillWater(repository)() }
                val dashboard = withContext(Dispatchers.IO) { LoadHomeDashboard(repository)() }
                _uiState.value = _uiState.value.copy(dashboard = dashboard, isLoading = false,
                    isDailyClearVisible = dashboard.dailyRewardDate.isNotBlank() && dashboard.dailyRewardDate != dismissedRewardDate)
                effectChannel.send(
                    HomeEffect.Feedback(
                        if (amount > before) "Đã bơm thêm nước vào bình."
                        else "Bình tưới đã đầy."
                    )
                )
            }
            is HomeIntent.OpenTree -> viewModelScope.launch { effectChannel.send(HomeEffect.OpenTree(intent.id)) }
            HomeIntent.OpenTasks -> viewModelScope.launch { effectChannel.send(HomeEffect.OpenTasks) }
            HomeIntent.OpenGarden -> viewModelScope.launch { effectChannel.send(HomeEffect.OpenGarden) }
            HomeIntent.OpenShop -> viewModelScope.launch { effectChannel.send(HomeEffect.OpenShop) }
            is HomeIntent.WaterTree -> viewModelScope.launch { effectChannel.send(HomeEffect.WaterTree(intent.id)) }
            is HomeIntent.FertilizeTree -> viewModelScope.launch { effectChannel.send(HomeEffect.FertilizeTree(intent.id)) }
        }
    }
}
