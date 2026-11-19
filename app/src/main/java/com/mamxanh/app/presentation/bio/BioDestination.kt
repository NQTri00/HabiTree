package com.mamxanh.app.presentation.bio

import com.mamxanh.app.core.presentation.navigation.HabiTreeNavKey
import com.mamxanh.app.feature.stats.domain.TimelineRelated
import kotlinx.serialization.Serializable

@Serializable
internal enum class BioRoute(val id: String) {
    HOME("home"),
    TASKS("tasks"),
    GARDEN("garden"),
    SHOP("shop"),
    STATS("stats"),
    TASK("task"),
    TASK_NEW("task-new"),
    INVENTORY("inventory"),
    TREE("tree"),
    FOCUS("focus"),
    TEMPLATES("templates"),
    REVIEW("review"),
    REST("rest"),
    INSIGHTS("insights"),
    ACHIEVEMENTS("achievements"),
    TIMELINE("timeline"),
    SETTINGS("settings"),
    DATA("data"),
    ENCYCLOPEDIA("encyclopedia"),
    SPECIES("species"),
    ONBOARD("onboard"),
    MORE("more");

    companion object {
        fun fromId(id: String): BioRoute = entries.firstOrNull { it.id == id }
            ?: error("Unknown HabiTree route: $id")
    }
}

@Serializable
internal data class BioDestination(
    val route: BioRoute,
    override val argument: String = "",
) : HabiTreeNavKey {
    override val routeId: String get() = route.id
    constructor(route: String, argument: String) : this(BioRoute.fromId(route), argument)
}

internal fun TimelineRelated.toBioDestination(): BioDestination = when (this) {
    is TimelineRelated.Tree -> BioDestination(BioRoute.TREE, entityId)
    is TimelineRelated.TaskInsights -> BioDestination(BioRoute.INSIGHTS, entityId)
}
