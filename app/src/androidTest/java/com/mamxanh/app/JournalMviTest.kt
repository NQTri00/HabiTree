package com.mamxanh.app

import androidx.lifecycle.SavedStateHandle
import androidx.test.platform.app.InstrumentationRegistry
import com.mamxanh.app.core.domain.RepositoryResult
import com.mamxanh.app.feature.stats.domain.model.TimelineRelated
import com.mamxanh.app.feature.stats.domain.usecase.JournalNote
import com.mamxanh.app.feature.stats.domain.usecase.JournalRepository
import com.mamxanh.app.feature.stats.domain.usecase.SaveJournalNoteUseCase
import com.mamxanh.app.feature.stats.presentation.ui.journal.JournalAction
import com.mamxanh.app.feature.stats.presentation.ui.journal.JournalEvent
import com.mamxanh.app.feature.stats.presentation.ui.journal.JournalViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Test

class JournalMviTest {
    @Test
    fun draftRestorationAndSaveKeepOtherNotesAndRelatedIds() {
        val saved = SavedStateHandle()
        val written = CompletableDeferred<JournalNote>()
        val repository =
            object : JournalRepository {
                override fun save(note: JournalNote): RepositoryResult<String> {
                    written.complete(note)
                    return RepositoryResult.Success("saved")
                }
            }
        val save = SaveJournalNoteUseCase(repository)
        val original = JournalViewModel(save, saved, { emptyList() }, { null })
        val week = JournalNote.WeeklyReflection("2026-11-23", "Weekly draft")
        val memory = JournalNote.TimelineMemory("31", "Memory draft")
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.runOnMainSync {
            original.dispatch(JournalAction.Edit(week))
            original.dispatch(JournalAction.Edit(memory))
        }

        val restored = JournalViewModel(save, saved, { emptyList() }, { null })
        assertEquals(week.text, restored.state.value.reflection(week.weekStart, ""))
        assertEquals(memory.text, restored.state.value.memory(memory.memoryId, ""))
        instrumentation.runOnMainSync { restored.dispatch(JournalAction.Save(week)) }
        val (note, feedback) =
            runBlocking {
                withTimeout(5_000) { written.await() to restored.events.first() }
            }
        assertEquals(JournalEvent.Feedback("saved"), feedback)
        assertEquals(week, note)
        assertEquals("persisted", restored.state.value.reflection(week.weekStart, "persisted"))
        assertEquals(memory.text, restored.state.value.memory(memory.memoryId, ""))

        val related = TimelineRelated.TaskInsights("42")
        instrumentation.runOnMainSync { restored.dispatch(JournalAction.OpenRelated(related)) }
        assertEquals(JournalEvent.OpenRelated(related), runBlocking { withTimeout(5_000) { restored.events.first() } })
    }
}
