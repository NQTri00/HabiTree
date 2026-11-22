package com.mamxanh.app.feature.stats.presentation.ui.stats

import androidx.lifecycle.viewModelScope
import com.mamxanh.app.core.presentation.BaseViewModel
import com.mamxanh.app.core.presentation.UiEvent
import com.mamxanh.app.feature.stats.domain.usecase.LoadGardenStatistics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StatsViewModel(
    private val loadStatistics: LoadGardenStatistics,
) : BaseViewModel<StatsState, UiEvent, StatsAction>(
        initialState = StatsState(),
    ) {
    val uiState get() = state

    init {
        dispatch(StatsAction.Refresh)
    }

    override fun handleAction(action: StatsAction) {
        val intent = action
        when (intent) {
            StatsAction.Refresh ->
                viewModelScope.launch {
                    mutableState.value = mutableState.value.copy(isLoading = true)
                    val statistics = withContext(Dispatchers.IO) { loadStatistics() }
                    mutableState.value = StatsState(statistics, false)
                }
        }
    }
}
