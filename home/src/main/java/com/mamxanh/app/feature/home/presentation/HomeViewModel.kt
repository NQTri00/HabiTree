package com.mamxanh.app.feature.home.presentation

import androidx.lifecycle.viewModelScope
import com.mamxanh.app.core.presentation.BaseViewModel
import com.mamxanh.app.feature.home.domain.HomeDashboardRepository
import com.mamxanh.app.feature.home.domain.LoadHomeDashboard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(private val repository: HomeDashboardRepository)  : BaseViewModel<HomeUiState, HomeEffect, HomeIntent>(
    initialState = HomeUiState(),
) {
    private var dismissedRewardDate = ""
    val uiState get() = state
    val effects get() = events
    init { dispatch(HomeIntent.Refresh) }

    override fun handleAction(action: HomeIntent) {
        val intent = action
        when (intent) {
            HomeIntent.DismissDailyClear -> {
                dismissedRewardDate = mutableState.value.dashboard.dailyRewardDate
                mutableState.value = mutableState.value.copy(isDailyClearVisible = false)
            }
            HomeIntent.Refresh -> viewModelScope.launch {
                val dashboard = withContext(Dispatchers.IO) { LoadHomeDashboard(repository)() }
                mutableState.value = mutableState.value.copy(dashboard = dashboard, isLoading = false,
                    isDailyClearVisible = dashboard.dailyRewardDate.isNotBlank() && dashboard.dailyRewardDate != dismissedRewardDate)
            }
            is HomeIntent.OpenTree -> viewModelScope.launch { emitEvent(HomeEffect.OpenTree(intent.id)) }
            HomeIntent.OpenTasks -> viewModelScope.launch { emitEvent(HomeEffect.OpenTasks) }
            HomeIntent.OpenGarden -> viewModelScope.launch { emitEvent(HomeEffect.OpenGarden) }
            HomeIntent.OpenShop -> viewModelScope.launch { emitEvent(HomeEffect.OpenShop) }
            is HomeIntent.WaterTree -> viewModelScope.launch { emitEvent(HomeEffect.WaterTree(intent.id)) }
            is HomeIntent.FertilizeTree -> viewModelScope.launch { emitEvent(HomeEffect.FertilizeTree(intent.id)) }
        }
    }
}
