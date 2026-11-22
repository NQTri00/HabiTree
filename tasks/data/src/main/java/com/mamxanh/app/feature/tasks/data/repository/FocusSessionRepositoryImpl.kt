package com.mamxanh.app.feature.tasks.data.repository

import com.mamxanh.app.core.domain.RepositoryResult
import com.mamxanh.app.feature.tasks.domain.repository.ActiveFocusSession
import com.mamxanh.app.feature.tasks.domain.repository.ActiveFocusState
import com.mamxanh.app.feature.tasks.domain.repository.FocusControl
import com.mamxanh.app.feature.tasks.domain.repository.FocusSessionRepository
import com.mamxanh.shared.data.GameStorage
import com.mamxanh.shared.model.ActionResult
import com.mamxanh.shared.model.EngagementAction
import org.koin.dsl.module

class FocusSessionRepositoryImpl(
    private val storage: GameStorage,
) : FocusSessionRepository {
    override fun activeSessions(): List<ActiveFocusSession> =
        storage.engagementSnapshot().focus.mapNotNull { session ->
            val state =
                when (session.state) {
                    "RUNNING" -> ActiveFocusState.Running
                    "PAUSED" -> ActiveFocusState.Paused
                    "BREAK" -> ActiveFocusState.Break
                    else -> return@mapNotNull null
                }
            ActiveFocusSession(session.id, state)
        }

    override fun start(
        taskId: Long,
        minutes: Int,
        key: String,
    ): RepositoryResult<String> = storage.engage(EngagementAction.StartFocus(taskId, minutes, key)).asResult()

    override fun recover(): RepositoryResult<String> = storage.engage(EngagementAction.Focus("", "RECOVER")).asResult()

    override fun control(
        sessionId: String,
        control: FocusControl,
    ): RepositoryResult<String> {
        val action =
            when (control) {
                FocusControl.Pause -> "PAUSE"
                FocusControl.Resume -> "RESUME"
                FocusControl.End -> "END"
                FocusControl.Tick -> "TICK"
            }
        return storage.engage(EngagementAction.Focus(sessionId, action)).asResult()
    }

    private fun ActionResult.asResult(): RepositoryResult<String> =
        if (success) RepositoryResult.Success(message) else RepositoryResult.Error(message)
}

val focusSessionDataModule =
    module {
        single<FocusSessionRepository> { FocusSessionRepositoryImpl(get()) }
    }
