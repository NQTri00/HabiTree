package com.mamxanh.app.core.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

interface UiState

interface UiEvent

interface UiAction

abstract class BaseViewModel<State : UiState, Event : UiEvent, Action : UiAction>(
    initialState: State,
) : ViewModel() {
    protected val mutableState = MutableStateFlow(initialState)
    val state = mutableState.asStateFlow()

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    fun dispatch(action: Action) = handleAction(action)

    protected abstract fun handleAction(action: Action)

    protected suspend fun emitEvent(event: Event) {
        eventChannel.send(event)
    }
}
