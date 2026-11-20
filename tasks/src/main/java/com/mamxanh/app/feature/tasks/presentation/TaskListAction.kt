package com.mamxanh.app.feature.tasks.presentation

sealed interface TaskListAction {
    data object Add : TaskListAction
    data object Focus : TaskListAction
    data object Templates : TaskListAction
    data object Insights : TaskListAction
    data class Filter(val value: String) : TaskListAction
    data class Open(val id: String) : TaskListAction
    data class Complete(val id: String, val timer: Boolean) : TaskListAction
}
