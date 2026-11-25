package com.mamxanh.app.presentation.bio

import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.mamxanh.app.feature.home.presentation.ui.home.HomeReward
import com.mamxanh.app.feature.tasks.presentation.navigation.TaskListRouteContent
import com.mamxanh.app.feature.tasks.presentation.ui.list.TaskListAction
import com.mamxanh.app.feature.tasks.presentation.ui.list.TaskListEvent
import com.mamxanh.app.feature.tasks.presentation.ui.list.TaskListViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun liveTaskListRouteContent(ui: BioController): TaskListRouteContent {
    val viewModel = koinViewModel<TaskListViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(viewModel, lifecycle, ui) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.events.collect { event ->
                when (event) {
                    TaskListEvent.Add -> ui.go("task-new")
                    TaskListEvent.Templates -> ui.go("templates")
                    TaskListEvent.Insights -> ui.go("insights")
                    is TaskListEvent.Focus -> ui.go("focus", event.taskId)
                    is TaskListEvent.Detail -> ui.go("task", event.taskId)
                    is TaskListEvent.Feedback -> ui.message = event.message
                }
            }
        }
    }
    if (state.loading) LinearProgressIndicator()
    return TaskListRouteContent(
        state.copy(busy = state.busy || ui.busy),
        viewModel::dispatch,
        {
            HomeReward(state.rewardClaimed, state.canClaimReward && !state.busy && !ui.busy) {
                viewModel.dispatch(TaskListAction.ClaimReward)
            }
        },
    )
}
