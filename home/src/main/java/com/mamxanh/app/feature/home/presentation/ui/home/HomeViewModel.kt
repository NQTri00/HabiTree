package com.mamxanh.app.feature.home.presentation.ui.home

import androidx.lifecycle.viewModelScope
import com.mamxanh.app.core.presentation.BaseViewModel
import com.mamxanh.app.feature.home.domain.usecase.HomeDashboardRepository
import com.mamxanh.app.feature.home.domain.usecase.HomeTreeSelection
import com.mamxanh.app.feature.home.domain.usecase.LoadHomeDashboard
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val repository: HomeDashboardRepository,
    private val treeSelection: HomeTreeSelection,
) : BaseViewModel<HomeState, HomeEvent, HomeAction>(
        initialState = HomeState(),
    ) {
    private var dismissedRewardDate = ""
    private var refreshJob: Job? = null
    val uiState get() = state
    val effects get() = events

    init {
        dispatch(HomeAction.Refresh)
    }

    override fun handleAction(action: HomeAction) {
        val intent = action
        when (intent) {
            is HomeAction.Shortcut -> viewModelScope.launch { emitEvent(HomeEvent.Shortcut(intent.action)) }
            HomeAction.ClaimReward -> {
                if (state.value.dashboard.canClaimReward) viewModelScope.launch { emitEvent(HomeEvent.ClaimReward) }
            }
            HomeAction.ChooseTree -> viewModelScope.launch { emitEvent(HomeEvent.ChooseTree) }
            is HomeAction.SelectTree -> {
                treeSelection.save(intent.id)
                mutableState.value = state.value.copy(selectedTreeId = intent.id)
                viewModelScope.launch { emitEvent(HomeEvent.TreeSelected(intent.id)) }
            }
            is HomeAction.OpenTask -> viewModelScope.launch { emitEvent(HomeEvent.OpenTask(intent.id, intent.startTimer)) }
            is HomeAction.CompleteTask -> viewModelScope.launch { emitEvent(HomeEvent.CompleteTask(intent.id)) }
            is HomeAction.DecorateTree -> viewModelScope.launch { emitEvent(HomeEvent.DecorateTree(intent.id)) }
            HomeAction.DismissDailyClear -> {
                dismissedRewardDate = mutableState.value.dashboard.dailyRewardDate
                mutableState.value = mutableState.value.copy(isDailyClearVisible = false)
            }
            HomeAction.Refresh -> refresh()
            is HomeAction.OpenTree -> viewModelScope.launch { emitEvent(HomeEvent.OpenTree(intent.id)) }
            HomeAction.OpenTasks -> viewModelScope.launch { emitEvent(HomeEvent.OpenTasks) }
            HomeAction.OpenGarden -> viewModelScope.launch { emitEvent(HomeEvent.OpenGarden) }
            HomeAction.OpenShop -> viewModelScope.launch { emitEvent(HomeEvent.OpenShop) }
            is HomeAction.WaterTree -> viewModelScope.launch { emitEvent(HomeEvent.WaterTree(intent.id)) }
            is HomeAction.FertilizeTree -> viewModelScope.launch { emitEvent(HomeEvent.FertilizeTree(intent.id)) }
        }
    }

    private fun refresh() {
        refreshJob?.cancel()
        refreshJob =
            viewModelScope.launch {
                try {
                    val dashboard = withContext(Dispatchers.IO) { LoadHomeDashboard(repository)() }
                    mutableState.value =
                        state.value.copy(
                            dashboard = dashboard,
                            selectedTreeId = treeSelection.resolve(treeSelection.savedId(), dashboard.trees.map { it.id.toString() }),
                            isLoading = false,
                            isDailyClearVisible =
                                dashboard.dailyRewardDate.isNotBlank() && dashboard.dailyRewardDate != dismissedRewardDate,
                        )
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    mutableState.value = state.value.copy(isLoading = false)
                    emitEvent(HomeEvent.Feedback(AppText.text(TextKey.CopyChuaTheCapNhatVuiLongThuLai42c8ac)))
                }
            }
    }
}
