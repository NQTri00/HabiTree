package com.mamxanh.app.feature.home.presentation.ui.onboarding

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mamxanh.app.core.domain.RepositoryResult
import com.mamxanh.app.core.presentation.BaseViewModel
import com.mamxanh.app.feature.home.domain.repository.OnboardingRepository
import com.mamxanh.app.feature.home.domain.repository.OnboardingRequest
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class OnboardingViewModel(
    private val repository: OnboardingRepository,
    private val savedState: SavedStateHandle,
) : BaseViewModel<OnboardingState, OnboardingEvent, OnboardingAction>(
        OnboardingState(
            step = savedState.get<Int>("step") ?: 0,
            seed = savedState["seed"] ?: OnboardingSelectionState.DEFAULT_SEED,
            category = savedState["category"] ?: OnboardingSelectionState.defaultCategory(),
            templateIds = savedState.get<ArrayList<String>>("templates") ?: listOf("study25", "water"),
            reminders = savedState["onboard-remind"] ?: false,
            recoveringSubmission = savedState.get<String>("operationKey") != null,
        ),
    ) {
    override fun handleAction(action: OnboardingAction) {
        val current = state.value
        if (current.submitting || current.completed) return
        when (action) {
            OnboardingAction.Back -> update(current.copy(step = (current.step - 1).coerceAtLeast(0)))
            OnboardingAction.Next -> if (current.step < 4) update(current.copy(step = current.step + 1)) else submit(current, false)
            OnboardingAction.Skip -> submit(current, true)
            OnboardingAction.ContinueCompleted -> {
                mutableState.value = current.copy(completed = true)
                viewModelScope.launch { emitEvent(OnboardingEvent.Finished("")) }
            }
            is OnboardingAction.Choose ->
                update(if (action.step == 1) current.copy(seed = action.id) else current.copy(category = action.id))
            is OnboardingAction.Remind -> update(current.copy(reminders = action.enabled))
            is OnboardingAction.ToggleTemplate -> {
                val selected = current.templateIds
                when {
                    action.id in selected -> update(current.copy(templateIds = selected - action.id))
                    selected.size < 5 -> update(current.copy(templateIds = selected + action.id))
                    else ->
                        viewModelScope.launch {
                            emitEvent(OnboardingEvent.Feedback(AppText.text(TextKey.CopyBanDaChon5ViecBoChonMotViecDeDoi95f1ae)))
                        }
                }
            }
        }
    }

    fun acknowledgeCompletion() {
        savedState.remove<String>("operationKey")
        update(OnboardingState())
    }

    private fun update(value: OnboardingState) {
        savedState["step"] = value.step
        savedState["seed"] = value.seed
        savedState["category"] = value.category
        savedState["templates"] = ArrayList(value.templateIds)
        savedState["onboard-remind"] = value.reminders
        mutableState.value = value
    }

    private fun submit(
        draft: OnboardingState,
        skip: Boolean,
    ) {
        mutableState.value = draft.copy(submitting = true)
        val key = savedState.get<String>("operationKey") ?: UUID.randomUUID().toString()
        savedState["operationKey"] = key
        val request = OnboardingRequest(draft.seed, draft.category, draft.templateIds, key, skip, draft.reminders)
        viewModelScope.launch {
            val result =
                try {
                    withContext(Dispatchers.IO) { repository.finish(request) }
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    RepositoryResult.Error(AppText.text(TextKey.CopyChuaTheCapNhatVuiLongThuLai42c8ac))
                }
            when (result) {
                is RepositoryResult.Success -> {
                    mutableState.value = draft.copy(completed = true)
                    emitEvent(OnboardingEvent.Finished(result.value))
                }
                is RepositoryResult.Error -> {
                    savedState.remove<String>("operationKey")
                    mutableState.value = draft
                    emitEvent(OnboardingEvent.Feedback(result.message))
                }
            }
        }
    }
}
