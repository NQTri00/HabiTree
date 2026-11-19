package com.mamxanh.app.feature.tasks.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureDestination
import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureModule
import com.mamxanh.app.core.presentation.navigation.HabiTreeNavKey
import com.mamxanh.app.feature.tasks.presentation.FocusDisplay
import com.mamxanh.app.feature.tasks.presentation.FocusScreen
import com.mamxanh.app.feature.tasks.presentation.TaskDetailDisplay
import com.mamxanh.app.feature.tasks.presentation.TaskDetailScreen
import com.mamxanh.app.feature.tasks.presentation.TaskFormDisplay
import com.mamxanh.app.feature.tasks.presentation.TaskFormField
import com.mamxanh.app.feature.tasks.presentation.TaskFormScreen
import com.mamxanh.app.feature.tasks.presentation.TaskListAction
import com.mamxanh.app.feature.tasks.presentation.TaskListScreen
import com.mamxanh.app.feature.tasks.presentation.TaskListState
import com.mamxanh.app.feature.tasks.presentation.InsightTaskLink
import com.mamxanh.app.feature.tasks.presentation.RestScreen
import com.mamxanh.app.feature.tasks.presentation.RestScreenState
import com.mamxanh.app.feature.tasks.presentation.TaskInsightsDisplay
import com.mamxanh.app.feature.tasks.presentation.TaskInsightsScreen
import com.mamxanh.app.feature.tasks.presentation.TaskTemplateDisplay
import com.mamxanh.app.feature.tasks.presentation.TaskTemplatesScreen
import kotlinx.serialization.Serializable

@Serializable
internal data object TasksKey : HabiTreeNavKey {
    override val routeId: String get() = "tasks"
}

@Serializable
internal data class TaskDetailsKey(override val argument: String) : HabiTreeNavKey {
    override val routeId: String get() = "task"
}

@Serializable
internal data class TaskFormKey(override val argument: String) : HabiTreeNavKey {
    override val routeId: String get() = "task-new"
}

@Serializable
internal data class FocusKey(override val argument: String) : HabiTreeNavKey {
    override val routeId: String get() = "focus"
}

@Serializable
internal data object TemplatesKey : HabiTreeNavKey {
    override val routeId: String get() = "templates"
}

@Serializable
internal data object RestKey : HabiTreeNavKey {
    override val routeId: String get() = "rest"
}

@Serializable
internal data class InsightsKey(override val argument: String) : HabiTreeNavKey {
    override val routeId: String get() = "insights"
}

class RestRouteContent(
    val state: RestScreenState,
    val onSchedule: (String) -> Unit,
    val onBuy: () -> Unit,
    val onProtect: (String) -> Unit,
    val calendar: @Composable () -> Unit,
)

val LocalRestRouteContent = compositionLocalOf<() -> RestRouteContent> {
    error("Rest route requires its state and actions")
}

class InsightsRouteContent(
    val task: TaskInsightsDisplay?,
    val tasks: List<InsightTaskLink>,
    val period: String,
    val onPeriod: (String) -> Unit,
    val onAddTask: () -> Unit,
    val onTask: (String) -> Unit,
    val onAdjust: () -> Unit,
    val onSaveReminder: (String, String, Boolean) -> Unit,
    val onSettings: () -> Unit,
    val calendar: @Composable () -> Unit,
)

val LocalInsightsRouteContent = compositionLocalOf<(String) -> InsightsRouteContent> {
    error("Insights route requires its state and actions")
}

class TemplatesRouteContent(
    val templates: List<TaskTemplateDisplay>,
    val chosen: Set<String>,
    val onToggle: (String) -> Unit,
)

val LocalTemplatesRouteContent = compositionLocalOf<() -> TemplatesRouteContent> {
    error("Templates route requires its state and actions")
}

class TaskListRouteContent(
    val state: TaskListState,
    val onAction: (TaskListAction) -> Unit,
    val reward: @Composable () -> Unit,
)

val LocalTaskListRouteContent = compositionLocalOf<() -> TaskListRouteContent> {
    error("TaskList route requires its state and actions")
}

class TaskDetailRouteContent(
    val t: TaskDetailDisplay?,
    val onTasks: () -> Unit,
    val onComplete: () -> Unit,
    val onEdit: () -> Unit,
    val onInsights: () -> Unit,
    val onLifecycle: (String) -> Unit,
    val onArchive: () -> Unit,
)

val LocalTaskDetailRouteContent = compositionLocalOf<(String) -> TaskDetailRouteContent> {
    error("TaskDetail route requires its state and actions")
}

class TaskFormRouteContent(
    val state: TaskFormDisplay,
    val onFieldChanged: (TaskFormField, String) -> Unit,
)

val LocalTaskFormRouteContent = compositionLocalOf<(String) -> TaskFormRouteContent> {
    error("TaskForm route requires its state and actions")
}

class FocusRouteContent(
    val state: FocusDisplay,
    val onTask: (String) -> Unit,
    val onMinutes: (String) -> Unit,
)

val LocalFocusRouteContent = compositionLocalOf<(String) -> FocusRouteContent> {
    error("Focus route requires its state and actions")
}

val tasksFeatureNavigation = HabiTreeFeatureModule(
    HabiTreeFeatureDestination("tasks", content = {
        val content = LocalTaskListRouteContent.current()
        TaskListScreen(
            state = content.state,
            onAction = content.onAction,
            reward = content.reward,
        )
    }) { TasksKey.takeIf { _ -> it.isEmpty() } },
    HabiTreeFeatureDestination("task", content = {
        val content = LocalTaskDetailRouteContent.current(it.argument)
        TaskDetailScreen(
            t = content.t,
            onTasks = content.onTasks,
            onComplete = content.onComplete,
            onEdit = content.onEdit,
            onInsights = content.onInsights,
            onLifecycle = content.onLifecycle,
            onArchive = content.onArchive,
        )
    }) { TaskDetailsKey(it) },
    HabiTreeFeatureDestination("task-new", content = {
        val content = LocalTaskFormRouteContent.current(it.argument)
        TaskFormScreen(
            state = content.state,
            onFieldChanged = content.onFieldChanged,
        )
    }) { TaskFormKey(it) },
    HabiTreeFeatureDestination("focus", content = {
        val content = LocalFocusRouteContent.current(it.argument)
        FocusScreen(
            state = content.state,
            onTask = content.onTask,
            onMinutes = content.onMinutes,
        )
    }) { FocusKey(it) },
    HabiTreeFeatureDestination("templates", content = {
        val content = LocalTemplatesRouteContent.current()
        TaskTemplatesScreen(
            templates = content.templates,
            chosen = content.chosen,
            onToggle = content.onToggle,
        )
    }) { TemplatesKey.takeIf { _ -> it.isEmpty() } },
    HabiTreeFeatureDestination("rest", content = {
        val content = LocalRestRouteContent.current()
        RestScreen(
            state = content.state,
            onSchedule = content.onSchedule,
            onBuy = content.onBuy,
            onProtect = content.onProtect,
            calendar = content.calendar,
        )
    }) { RestKey.takeIf { _ -> it.isEmpty() } },
    HabiTreeFeatureDestination("insights", content = {
        val content = LocalInsightsRouteContent.current(it.argument)
        TaskInsightsScreen(
            task = content.task,
            tasks = content.tasks,
            period = content.period,
            onPeriod = content.onPeriod,
            onAddTask = content.onAddTask,
            onTask = content.onTask,
            onAdjust = content.onAdjust,
            onSaveReminder = content.onSaveReminder,
            onSettings = content.onSettings,
            calendar = content.calendar,
        )
    }) { InsightsKey(it) },
)
