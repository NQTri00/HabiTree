package com.mamxanh.app.feature.stats.presentation.ui.stats

import androidx.lifecycle.viewModelScope
import com.mamxanh.app.core.presentation.BaseViewModel
import com.mamxanh.app.core.presentation.UiEvent
import com.mamxanh.app.feature.stats.domain.usecase.LoadGardenStatistics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StatsViewModel(private val loadStatistics: LoadGardenStatistics) : BaseViewModel<StatsUiState, UiEvent, StatsIntent>(
    initialState = StatsUiState(),
) {
    val uiState get() = state
    init { dispatch(StatsIntent.Refresh) }
    override fun handleAction(action: StatsIntent) {
        val intent = action
        when (intent) {
            StatsIntent.Refresh -> viewModelScope.launch {
                mutableState.value = mutableState.value.copy(isLoading = true)
                val statistics = withContext(Dispatchers.IO) { loadStatistics() }
                mutableState.value = StatsUiState(statistics, false)
            }
        }
    }
}
