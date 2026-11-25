package com.mamxanh.app.feature.stats.presentation.ui.achievements

import androidx.lifecycle.viewModelScope
import com.mamxanh.app.core.presentation.BaseViewModel
import com.mamxanh.app.feature.stats.domain.usecase.AchievementRepository
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AchievementsViewModel(
    private val repository: AchievementRepository,
) : BaseViewModel<AchievementsState, AchievementsEvent, AchievementsAction>(AchievementsState()) {
    private var refresh: Job? = null

    override fun handleAction(action: AchievementsAction) {
        refresh?.cancel()
        refresh =
            viewModelScope.launch {
                try {
                    val entries = withContext(Dispatchers.IO) { repository.load() }
                    mutableState.value =
                        AchievementsState(
                            entries.map { AchievementDisplay(it.title, it.criterion, it.progress, it.target, it.unlocked) },
                            loading = false,
                        )
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    mutableState.value = state.value.copy(loading = false)
                    emitEvent(AchievementsEvent.Feedback(AppText.text(TextKey.CopyChuaTheCapNhatVuiLongThuLai42c8ac)))
                }
            }
    }
}
