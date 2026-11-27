package com.mamxanh.app.feature.settings.presentation.ui.settings

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mamxanh.app.core.domain.RepositoryResult
import com.mamxanh.app.core.presentation.BaseViewModel
import com.mamxanh.app.feature.settings.domain.model.ReminderSettings
import com.mamxanh.app.feature.settings.domain.repository.ReminderSettingsRepository
import com.mamxanh.app.feature.settings.domain.usecase.SaveReminderSettingsUseCase
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel(
    private val saveReminders: SaveReminderSettingsUseCase,
    private val repository: ReminderSettingsRepository,
    private val savedState: SavedStateHandle,
) : BaseViewModel<SettingsState, SettingsEvent, SettingsAction>(
        SettingsState(
            overrides =
                savedState
                    .get<Bundle>("settingsOptions")
                    ?.let { bundle ->
                        bundle.keySet().associateWith { bundle.getBoolean(it) }
                    }.orEmpty(),
            quietStart = savedState["quietStart"],
            quietEnd = savedState["quietEnd"],
        ),
    ) {
    private var refreshJob: Job? = null

    override fun handleAction(action: SettingsAction) {
        val current = state.value
        when (action) {
            SettingsAction.Refresh -> refresh()
            is SettingsAction.Toggle -> {
                val options = current.overrides + (action.key to action.enabled)
                savedState["settingsOptions"] = Bundle().apply { options.forEach { (key, enabled) -> putBoolean(key, enabled) } }
                mutableState.value = current.copy(overrides = options)
            }
            is SettingsAction.EditQuietHours -> {
                savedState["quietStart"] = action.start
                savedState["quietEnd"] = action.end
                mutableState.value = current.copy(quietStart = action.start, quietEnd = action.end)
            }
            SettingsAction.Save -> save(current)
            is SettingsAction.ChangeLanguage -> send(SettingsEvent.ChangeLanguage(action.tag))
            is SettingsAction.Open -> send(SettingsEvent.Open(action.link))
            SettingsAction.RequestPermission -> send(SettingsEvent.RequestPermission)
            SettingsAction.OpenPermissionSettings -> send(SettingsEvent.OpenPermissionSettings)
            SettingsAction.PermissionRequiresSettings -> send(SettingsEvent.ShowPermissionSettings)
            is SettingsAction.PermissionFinished -> send(SettingsEvent.Feedback(action.result.message()))
        }
    }

    private fun refresh() {
        refreshJob?.cancel()
        refreshJob =
            viewModelScope.launch {
                try {
                    val settings = withContext(Dispatchers.IO) { repository.load() }
                    mutableState.value = state.value.copy(persisted = settings)
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    emitEvent(SettingsEvent.Feedback(AppText.text(TextKey.CopyChuaTheCapNhatVuiLongThuLai42c8ac)))
                }
            }
    }

    private fun send(event: SettingsEvent) {
        viewModelScope.launch { emitEvent(event) }
    }

    private fun save(current: SettingsState) {
        if (current.busy || current.persisted == null) return
        val display = current.display()
        val options = display.options
        val request =
            ReminderSettings(
                paused = options["paused"] == true,
                quietStartMinute = parseClock(display.quietStart),
                quietEndMinute = parseClock(display.quietEnd),
                treeReminders = options["treeReminders"] == true,
                reducedMotion = options["reducedMotion"] == true,
                breakPrompts = options["breakPrompts"] == true,
            )
        mutableState.value = current.copy(busy = true)
        viewModelScope.launch {
            try {
                val message =
                    when (val result = withContext(Dispatchers.IO) { saveReminders(request) }) {
                        is RepositoryResult.Success -> result.value
                        is RepositoryResult.Error -> result.message
                    }
                emitEvent(SettingsEvent.Feedback(message))
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                emitEvent(SettingsEvent.Feedback(AppText.text(TextKey.CopyChuaTheCapNhatVuiLongThuLai42c8ac)))
            } finally {
                mutableState.value = state.value.copy(busy = false)
            }
        }
    }

    private fun parseClock(value: String): Int {
        if (value.isBlank()) return -1
        val match = Regex("^(\\d{1,2}):(\\d{2})$").matchEntire(value) ?: return -2
        val hour = match.groupValues[1].toInt()
        val minute = match.groupValues[2].toInt()
        return if (hour in 0..23 && minute in 0..59) hour * 60 + minute else -2
    }
}
