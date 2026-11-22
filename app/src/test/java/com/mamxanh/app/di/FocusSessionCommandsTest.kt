package com.mamxanh.app.di

import com.mamxanh.app.core.domain.RepositoryResult
import com.mamxanh.app.feature.tasks.domain.repository.ActiveFocusSession
import com.mamxanh.app.feature.tasks.domain.repository.ActiveFocusState
import com.mamxanh.app.feature.tasks.domain.repository.FocusCommandResult
import com.mamxanh.app.feature.tasks.domain.repository.FocusControl
import com.mamxanh.app.feature.tasks.domain.repository.FocusSessionCommands
import com.mamxanh.app.feature.tasks.domain.repository.FocusSessionRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FocusSessionCommandsTest {
    private val repository = FakeFocusRepository()
    private val commands = FocusSessionCommands(repository)

    @Test
    fun lifecyclePauseTargetsOnlyRunningSessionAndSuppressesSuccessMessage() {
        repository.sessions =
            listOf(
                ActiveFocusSession("paused", ActiveFocusState.Paused),
                ActiveFocusSession("running", ActiveFocusState.Running),
            )
        assertEquals(RepositoryResult.Success(""), commands.pauseIfRunning())
        assertEquals(listOf("running" to FocusControl.Pause), repository.controls)
        repository.result = RepositoryResult.Error("write failed")
        assertEquals(repository.result, commands.pauseIfRunning())
    }

    @Test
    fun toggleAndEndTargetTheFirstActiveSession() {
        repository.sessions = listOf(ActiveFocusSession("restored", ActiveFocusState.Break))
        val expected = FocusCommandResult.Handled(repository.result)

        assertEquals(expected, commands.toggle())
        assertEquals(expected, commands.end())
        assertEquals(listOf("restored" to FocusControl.Resume, "restored" to FocusControl.End), repository.controls)

        repository.sessions = listOf(ActiveFocusSession("running", ActiveFocusState.Running))
        assertEquals(expected, commands.toggle())
        assertEquals("running" to FocusControl.Pause, repository.controls.last())
    }

    @Test
    fun absentSessionDoesNotWrite() {
        assertEquals(FocusCommandResult.NoActiveSession, commands.toggle())
        assertEquals(FocusCommandResult.NoActiveSession, commands.end())
        assertEquals(RepositoryResult.Success(""), commands.pauseIfRunning())
        assertTrue(repository.controls.isEmpty())
    }

    @Test
    fun lifecycleTickAndRecoveryUseTheSameRepository() {
        assertEquals(repository.result, commands.tick("running"))
        assertEquals(repository.result, commands.recover())
        assertEquals(listOf("running" to FocusControl.Tick), repository.controls)
        assertEquals(1, repository.recoveries)
    }

    private class FakeFocusRepository : FocusSessionRepository {
        var sessions = emptyList<ActiveFocusSession>()
        var result: RepositoryResult<String> = RepositoryResult.Success("updated")
        val controls = mutableListOf<Pair<String, FocusControl>>()
        var recoveries = 0
            private set

        override fun activeSessions() = sessions

        override fun start(
            taskId: Long,
            minutes: Int,
            key: String,
        ) = result

        override fun recover(): RepositoryResult<String> {
            recoveries++
            return result
        }

        override fun control(
            sessionId: String,
            control: FocusControl,
        ): RepositoryResult<String> {
            controls += sessionId to control
            return result
        }
    }
}
