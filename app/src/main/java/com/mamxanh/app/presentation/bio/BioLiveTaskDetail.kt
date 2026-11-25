package com.mamxanh.app.presentation.bio

import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.mamxanh.app.feature.tasks.presentation.navigation.TaskDetailRouteContent
import com.mamxanh.app.feature.tasks.presentation.ui.detail.TaskDetailAction
import com.mamxanh.app.feature.tasks.presentation.ui.detail.TaskDetailEvent
import com.mamxanh.app.feature.tasks.presentation.ui.detail.TaskDetailViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun taskDetailViewModel(id: String): TaskDetailViewModel = koinViewModel(key = "task:" + id, parameters = { parametersOf(id) })

@Composable
internal fun liveTaskDetailRouteContent(
    ui: BioController,
    id: String,
): TaskDetailRouteContent {
    val model = taskDetailViewModel(id)
    val state by model.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(model, lifecycle, ui) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            model.events.collect { event ->
                if (ui.route != "task" || ui.param != id) return@collect
                when (event) {
                    TaskDetailEvent.OpenList -> ui.go("tasks")
                    TaskDetailEvent.Edit -> ui.go("task-new", id)
                    TaskDetailEvent.Insights -> ui.go("insights", id)
                    TaskDetailEvent.Focus -> ui.go("focus", id)
                    TaskDetailEvent.ConfirmArchive -> ui.show("archive", id)
                    is TaskDetailEvent.Feedback -> {
                        if (event.archived) {
                            ui.modal = ""
                            ui.go("tasks", root = true)
                        }
                        if (ui.modal == "archive") ui.modalMessage = event.message else ui.message = event.message
                    }
                }
            }
        }
    }
    if (state.loading || state.busy) LinearProgressIndicator()
    return TaskDetailRouteContent(
        t = state.task,
        onTasks = { model.dispatch(TaskDetailAction.OpenList) },
        onComplete = { model.dispatch(TaskDetailAction.Complete) },
        onEdit = { model.dispatch(TaskDetailAction.Edit) },
        onInsights = { model.dispatch(TaskDetailAction.Insights) },
        onLifecycle = { model.dispatch(TaskDetailAction.ChangeLifecycle(it)) },
        onArchive = { model.dispatch(TaskDetailAction.RequestArchive) },
    )
}
