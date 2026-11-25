package com.mamxanh.app.feature.stats.presentation.ui.journal

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mamxanh.app.core.domain.RepositoryResult
import com.mamxanh.app.core.presentation.BaseViewModel
import com.mamxanh.app.feature.stats.domain.usecase.JournalNote
import com.mamxanh.app.feature.stats.domain.usecase.SaveJournalNoteUseCase
import com.mamxanh.app.feature.stats.domain.usecase.TimelineRepository
import com.mamxanh.app.feature.stats.domain.usecase.WeeklyReviewRepository
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class JournalViewModel(
    private val saveNote: SaveJournalNoteUseCase,
    private val savedState: SavedStateHandle,
    private val timeline: TimelineRepository,
    private val weeklyReview: WeeklyReviewRepository,
) : BaseViewModel<JournalState, JournalEvent, JournalAction>(
        JournalState(
            savedState
                .get<Bundle>("journalDrafts")
                ?.let { bundle ->
                    bundle.keySet().associateWith { bundle.getString(it).orEmpty() }
                }.orEmpty(),
        ),
    ) {
    override fun handleAction(action: JournalAction) {
        when (action) {
            JournalAction.LoadTimeline -> load(timeline::load) { state.value.copy(memories = it) }
            JournalAction.LoadWeeklyReview -> load(weeklyReview::load) { state.value.copy(weeklyReview = it) }
            is JournalAction.Edit -> {
                val note = action.note
                val maxLength = if (note is JournalNote.WeeklyReflection) 2000 else 1000
                persist(state.value.drafts + (note.key() to note.text.take(maxLength)))
            }
            is JournalAction.Save -> save(action.note)
            is JournalAction.OpenRelated -> send(JournalEvent.OpenRelated(action.target))
            JournalAction.OpenTasks -> send(JournalEvent.OpenTasks)
            JournalAction.OpenRest -> send(JournalEvent.OpenRest)
        }
    }

    private fun <T> load(
        query: () -> T,
        update: (T) -> JournalState,
    ) {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) { query() }
                mutableState.value = update(result)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                emitEvent(JournalEvent.Feedback(AppText.text(TextKey.CopyChuaTheCapNhatVuiLongThuLai42c8ac)))
            }
        }
    }

    private fun send(event: JournalEvent) {
        viewModelScope.launch { emitEvent(event) }
    }

    private fun save(note: JournalNote) {
        if (state.value.busy) return
        mutableState.value = state.value.copy(busy = true)
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) { saveNote(note) }
                val message =
                    when (result) {
                        is RepositoryResult.Success -> {
                            if (state.value.drafts[note.key()] == note.text) persist(state.value.drafts - note.key())
                            result.value
                        }
                        is RepositoryResult.Error -> result.message
                    }
                emitEvent(JournalEvent.Feedback(message))
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                emitEvent(JournalEvent.Feedback(AppText.text(TextKey.CopyChuaTheCapNhatVuiLongThuLai42c8ac)))
            } finally {
                mutableState.value = state.value.copy(busy = false)
            }
        }
    }

    private fun persist(drafts: Map<String, String>) {
        savedState["journalDrafts"] =
            Bundle().apply {
                drafts.forEach { (key, value) -> putString(key, value) }
            }
        mutableState.value = state.value.copy(drafts = drafts)
    }

    private fun JournalNote.key(): String =
        when (this) {
            is JournalNote.WeeklyReflection -> "week:" + weekStart
            is JournalNote.TimelineMemory -> "memory:" + memoryId
        }
}
