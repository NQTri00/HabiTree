package com.mamxanh.app.feature.stats.presentation.ui.stats

import androidx.lifecycle.viewModelScope
import com.mamxanh.app.core.presentation.BaseViewModel
import com.mamxanh.app.feature.stats.domain.usecase.LoadGardenStatistics
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class StatsViewModel(
    private val loadStatistics: LoadGardenStatistics,
) : BaseViewModel<StatsState, StatsEvent, StatsAction>(StatsState()) {
    val uiState get() = state
    private val refreshLock = Mutex()

    init {
        dispatch(StatsAction.Refresh)
    }

    override fun handleAction(action: StatsAction) {
        when (action) {
            StatsAction.Refresh -> refresh(showFeedback = false)
            StatsAction.RefreshFromScreen -> refresh(showFeedback = true)
            is StatsAction.Open -> viewModelScope.launch { emitEvent(StatsEvent.Open(action.link)) }
        }
    }

    private fun refresh(showFeedback: Boolean) {
        viewModelScope.launch {
            refreshLock.withLock {
                if (showFeedback) mutableState.value = state.value.copy(isLoading = true)
                try {
                    val statistics = withContext(Dispatchers.IO) { loadStatistics() }
                    mutableState.value = StatsState(statistics, false)
                    if (showFeedback) emitEvent(StatsEvent.Feedback(AppText.text(TextKey.CopyDaLamMoiThongKeA8b494)))
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    mutableState.value = state.value.copy(isLoading = false)
                    emitEvent(StatsEvent.Feedback(AppText.text(TextKey.CopyChuaTheCapNhatVuiLongThuLai42c8ac)))
                }
            }
        }
    }
}
