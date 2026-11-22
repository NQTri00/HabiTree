package com.mamxanh.app.feature.home.presentation.ui.home

import androidx.lifecycle.viewModelScope
import com.mamxanh.app.core.presentation.BaseViewModel
import com.mamxanh.app.feature.home.domain.usecase.HomeDashboardRepository
import com.mamxanh.app.feature.home.domain.usecase.LoadHomeDashboard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val repository: HomeDashboardRepository,
) : BaseViewModel<HomeState, HomeEvent, HomeAction>(
        initialState = HomeState(),
    ) {
    private var dismissedRewardDate = ""
    val uiState get() = state
    val effects get() = events

    init {
        dispatch(HomeAction.Refresh)
    }

    override fun handleAction(action: HomeAction) {
        val intent = action
        when (intent) {
            HomeAction.DismissDailyClear -> {
                dismissedRewardDate = mutableState.value.dashboard.dailyRewardDate
                mutableState.value = mutableState.value.copy(isDailyClearVisible = false)
            }
            HomeAction.Refresh ->
                viewModelScope.launch {
                    val dashboard = withContext(Dispatchers.IO) { LoadHomeDashboard(repository)() }
                    mutableState.value =
                        mutableState.value.copy(
                            dashboard = dashboard,
                            isLoading = false,
                            isDailyClearVisible =
                                dashboard.dailyRewardDate.isNotBlank() && dashboard.dailyRewardDate != dismissedRewardDate,
                        )
                }
            is HomeAction.OpenTree -> viewModelScope.launch { emitEvent(HomeEvent.OpenTree(intent.id)) }
            HomeAction.OpenTasks -> viewModelScope.launch { emitEvent(HomeEvent.OpenTasks) }
            HomeAction.OpenGarden -> viewModelScope.launch { emitEvent(HomeEvent.OpenGarden) }
            HomeAction.OpenShop -> viewModelScope.launch { emitEvent(HomeEvent.OpenShop) }
            is HomeAction.WaterTree -> viewModelScope.launch { emitEvent(HomeEvent.WaterTree(intent.id)) }
            is HomeAction.FertilizeTree -> viewModelScope.launch { emitEvent(HomeEvent.FertilizeTree(intent.id)) }
        }
    }
}
