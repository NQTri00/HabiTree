package com.mamxanh.app

import androidx.lifecycle.SavedStateHandle
import androidx.test.platform.app.InstrumentationRegistry
import com.mamxanh.app.feature.settings.presentation.ui.backup.BackupAction
import com.mamxanh.app.feature.settings.presentation.ui.backup.BackupEvent
import com.mamxanh.app.feature.settings.presentation.ui.backup.BackupOperation
import com.mamxanh.app.feature.settings.presentation.ui.backup.BackupScreenAction
import com.mamxanh.app.feature.settings.presentation.ui.backup.BackupViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BackupMviTest {
    @Test
    fun restoredPickerAcceptsItsDocumentAndIgnoresUnrelatedResults() {
        val saved = SavedStateHandle()
        val original = BackupViewModel(saved)
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val export = BackupOperation.Export
        val diagnostics = BackupOperation.Diagnostics

        instrumentation.runOnMainSync {
            original.dispatch(BackupAction.Screen(BackupScreenAction.Export))
            original.dispatch(BackupAction.Screen(BackupScreenAction.Diagnostics))
        }
        assertEquals(BackupEvent.Run(export), runBlocking { withTimeout(5_000) { original.events.first() } })

        val restored = BackupViewModel(saved)
        instrumentation.runOnMainSync {
            restored.dispatch(BackupAction.DocumentReceived(diagnostics))
            restored.dispatch(BackupAction.Finished(export, true, "early result"))
        }
        assertEquals(export, restored.state.value.pending)
        assertTrue(restored.state.value.awaitingDocument)
        assertFalse(restored.state.value.working)

        instrumentation.runOnMainSync {
            restored.dispatch(BackupAction.DocumentReceived(export))
            restored.dispatch(BackupAction.Finished(diagnostics, true, "unrelated result"))
        }
        assertEquals(export, restored.state.value.pending)
        assertFalse(restored.state.value.awaitingDocument)
        assertTrue(restored.state.value.working)
        assertNull(BackupViewModel(saved).state.value.pending)

        instrumentation.runOnMainSync { restored.dispatch(BackupAction.Finished(export, true, "saved")) }
        assertNull(restored.state.value.pending)
        assertFalse(restored.state.value.working)
        assertEquals(BackupEvent.Completed(export, true, "saved"), runBlocking { withTimeout(5_000) { restored.events.first() } })
    }
}
