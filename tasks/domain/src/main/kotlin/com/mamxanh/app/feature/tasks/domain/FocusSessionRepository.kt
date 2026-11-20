package com.mamxanh.app.feature.tasks.domain

import com.mamxanh.app.core.domain.RepositoryResult

enum class ActiveFocusState { Running, Paused, Break }

data class ActiveFocusSession(val id: String, val state: ActiveFocusState)

enum class FocusControl { Pause, Resume, End, Tick }

interface FocusSessionRepository {
    fun activeSessions(): List<ActiveFocusSession>
    fun start(taskId: Long, minutes: Int, key: String): RepositoryResult<String>
    fun control(sessionId: String, control: FocusControl): RepositoryResult<String>
    fun recover(): RepositoryResult<String>
}

sealed interface FocusCommandResult {
    data object NoActiveSession : FocusCommandResult
    data class Handled(val result: RepositoryResult<String>) : FocusCommandResult
}

class FocusSessionCommands(private val repository: FocusSessionRepository) {
    fun start(taskId: Long, minutes: Int, key: String) = repository.start(taskId, minutes, key)
    fun tick(sessionId: String) = repository.control(sessionId, FocusControl.Tick)
    fun recover() = repository.recover()

    fun pauseIfRunning(): RepositoryResult<String> {
        val session = repository.activeSessions().firstOrNull { it.state == ActiveFocusState.Running }
            ?: return RepositoryResult.Success("")
        return when (val result = repository.control(session.id, FocusControl.Pause)) {
            is RepositoryResult.Success -> RepositoryResult.Success("")
            is RepositoryResult.Error -> result
        }
    }

    fun toggle(): FocusCommandResult {
        val session = repository.activeSessions().firstOrNull() ?: return FocusCommandResult.NoActiveSession
        val action = if (session.state == ActiveFocusState.Running) FocusControl.Pause else FocusControl.Resume
        return FocusCommandResult.Handled(repository.control(session.id, action))
    }

    fun end(): FocusCommandResult {
        val session = repository.activeSessions().firstOrNull() ?: return FocusCommandResult.NoActiveSession
        return FocusCommandResult.Handled(repository.control(session.id, FocusControl.End))
    }
}
