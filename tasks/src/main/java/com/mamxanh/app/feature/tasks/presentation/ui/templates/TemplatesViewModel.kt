package com.mamxanh.app.feature.tasks.presentation.ui.templates

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mamxanh.app.core.presentation.BaseViewModel
import com.mamxanh.app.feature.tasks.domain.repository.TaskRepository
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class TemplatesViewModel(
    private val repository: TaskRepository,
    private val savedState: SavedStateHandle,
) : BaseViewModel<TemplatesState, TemplatesEvent, TemplatesAction>(
        TemplatesState(savedState.get<ArrayList<String>>("templates").orEmpty().toSet()),
    ) {
    override fun handleAction(action: TemplatesAction) {
        val current = state.value
        if (current.busy) return
        when (action) {
            is TemplatesAction.Toggle -> {
                val selection = current.selected
                val next = if (action.id in selection) selection - action.id else selection + action.id
                savedState["templates"] = ArrayList(next)
                mutableState.value = current.copy(selected = next)
            }
            TemplatesAction.Apply -> {
                if (current.selected.isEmpty()) return
                mutableState.value = current.copy(busy = true)
                val key = UUID.randomUUID().toString()
                viewModelScope.launch {
                    try {
                        val result = withContext(Dispatchers.IO) { repository.addTemplates(current.selected.toList(), key) }
                        emitEvent(TemplatesEvent.Feedback(result.message))
                    } catch (cancelled: CancellationException) {
                        throw cancelled
                    } catch (_: Exception) {
                        emitEvent(TemplatesEvent.Feedback(AppText.text(TextKey.CopyChuaTheCapNhatVuiLongThuLai42c8ac)))
                    } finally {
                        mutableState.value = state.value.copy(busy = false)
                    }
                }
            }
        }
    }
}
