package com.mamxanh.app.presentation.ui

import androidx.compose.runtime.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mamxanh.app.feature.tasks.presentation.ui.engagement.EngagementViewModel
import com.mamxanh.app.feature.tasks.presentation.ui.list.TasksIntent
import com.mamxanh.app.feature.tasks.presentation.ui.list.TasksViewModel
import com.mamxanh.app.presentation.bio.BioNativeHost
import com.mamxanh.shared.data.GameStorage

/** The native UI owns screen snapshots; only session timers need legacy coordinators. */
@Composable
fun MamXanhApp(
    tasksVm: TasksViewModel,
    engagementVm: EngagementViewModel,
    incomingEntity: Long? = null,
    onIncomingHandled: () -> Unit = {},
) {
    val tasks by tasksVm.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val storage = org.koin.compose.koinInject<GameStorage>()
    DisposableEffect(lifecycleOwner, tasksVm, engagementVm) {
        fun foreground(active: Boolean) {
            tasksVm.dispatch(TasksIntent.Foreground(active))
            engagementVm.foreground(active)
        }
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) foreground(true)
            if (event == Lifecycle.Event.ON_STOP) foreground(false)
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        foreground(lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED))
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            foreground(false)
        }
    }
    val incomingTask = incomingEntity?.takeIf { it > 0 && !tasks.isLoading }
    val task = tasks.tasks.firstOrNull { it.id == incomingTask }
    LaunchedEffect(incomingTask, task) {
        if (incomingTask != null && task == null) onIncomingHandled()
    }
    BioNativeHost(
        storage,
        externalTree = incomingEntity?.takeIf { it < 0 }?.let { -it },
        externalRoute = if (task == null) "" else if (task.config.verification == "TIMER") "FOCUS" else "INSIGHTS",
        externalTask = task?.id ?: 0,
        onExternalHandled = onIncomingHandled,
    )
}
