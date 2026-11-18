package com.mamxanh.app.feature.tasks.presentation

sealed interface TasksEffect {
    data class Feedback(val message: String) : TasksEffect
    data object DataChanged : TasksEffect
}
