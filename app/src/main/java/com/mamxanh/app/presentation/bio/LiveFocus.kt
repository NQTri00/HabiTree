package com.mamxanh.app.presentation.bio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.mamxanh.app.feature.tasks.presentation.ui.engagement.EngagementUiState
import com.mamxanh.app.feature.tasks.presentation.ui.engagement.EngagementViewModel
import com.mamxanh.app.feature.tasks.presentation.ui.focus.FocusAction
import com.mamxanh.app.feature.tasks.presentation.ui.focus.FocusDock
import com.mamxanh.app.feature.tasks.presentation.ui.focus.FocusDockAction
import com.mamxanh.app.feature.tasks.presentation.ui.focus.FocusDockState
import com.mamxanh.app.feature.tasks.presentation.ui.focus.FocusEvent
import com.mamxanh.app.feature.tasks.presentation.ui.focus.FocusState
import com.mamxanh.app.feature.tasks.presentation.ui.focus.FocusViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun bindLiveFocus(ui: BioController): LiveFocus {
    val argument = ui.param
    val model =
        koinViewModel<FocusViewModel>(
            key = "focus:" + argument,
            parameters = { parametersOf(argument) },
        )
    val state by model.state.collectAsStateWithLifecycle()
    val engagement by koinViewModel<EngagementViewModel>().state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(ui, model) {
        ui.liveFocus = model
        onDispose { if (ui.liveFocus == model) ui.liveFocus = null }
    }
    LaunchedEffect(model, lifecycle, ui) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            model.events.collect { event ->
                if (ui.route != "focus" || ui.param != argument) return@collect
                when (event) {
                    FocusEvent.ConfirmEnd -> ui.show("focus-end")
                    is FocusEvent.Feedback -> {
                        if (!event.success && ui.modal == "focus-end") {
                            ui.modalMessage = event.message
                        } else {
                            if (event.success && ui.modal == "focus-end") ui.modal = ""
                            ui.message = event.message
                        }
                    }
                }
            }
        }
    }
    return LiveFocus(state, engagement, model::dispatch)
}

internal class LiveFocus(
    private val state: FocusState,
    private val engagement: EngagementUiState,
    private val dispatch: (FocusAction) -> Unit,
) {
    fun display(date: String) = state.display(engagement, date)

    fun selectTask(id: String) = dispatch(FocusAction.SelectTask(id))

    fun setMinutes(value: String) = dispatch(FocusAction.SetMinutes(value))

    @Composable
    fun Actions(world: BioWorld) {
        val display = state.display(engagement, "")
        val task = engagement.tasks.firstOrNull { it.id.toString() == state.taskId }
        val duration = display.minutes.toIntOrNull()
        FocusDock(
            FocusDockState(
                hasSession = display.present,
                running = display.running,
                canStart =
                    !world.loading &&
                        !engagement.loading &&
                        (task?.let { it.available && !it.doneToday } ?: (duration != null && duration in 5..120)),
                busy = state.busy,
            ),
        ) { action ->
            when (action) {
                FocusDockAction.Start -> {
                    if (task != null) {
                        dispatch(FocusAction.Start(task.id, 25))
                    } else if (duration != null && duration in 5..120) {
                        dispatch(FocusAction.Start(0L, duration))
                    }
                }
                FocusDockAction.Toggle -> dispatch(FocusAction.Toggle)
                FocusDockAction.End -> dispatch(FocusAction.RequestEnd)
            }
        }
    }
}
