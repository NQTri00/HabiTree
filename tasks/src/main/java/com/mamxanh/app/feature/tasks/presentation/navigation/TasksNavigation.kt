package com.mamxanh.app.feature.tasks.presentation.navigation

import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureDestination
import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureModule
import com.mamxanh.app.core.presentation.navigation.HabiTreeNavKey
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

val tasksFeatureNavigation = HabiTreeFeatureModule(
    HabiTreeFeatureDestination("tasks") { TasksKey.takeIf { _ -> it.isEmpty() } },
    HabiTreeFeatureDestination("task") { TaskDetailsKey(it) },
    HabiTreeFeatureDestination("task-new") { TaskFormKey(it) },
    HabiTreeFeatureDestination("focus") { FocusKey(it) },
    HabiTreeFeatureDestination("templates") { TemplatesKey.takeIf { _ -> it.isEmpty() } },
    HabiTreeFeatureDestination("rest") { RestKey.takeIf { _ -> it.isEmpty() } },
    HabiTreeFeatureDestination("insights") { InsightsKey(it) },
)
