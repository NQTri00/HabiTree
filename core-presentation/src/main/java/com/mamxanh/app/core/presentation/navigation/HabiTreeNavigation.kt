package com.mamxanh.app.core.presentation.navigation

import androidx.navigation3.runtime.NavKey
import org.koin.core.qualifier.named
import org.koin.dsl.module

interface HabiTreeNavKey : NavKey {
    val routeId: String
    val argument: String get() = ""
}

class HabiTreeFeatureDestination(
    val routeId: String,
    private val createKey: (String) -> HabiTreeNavKey?,
) {
    fun keyFor(argument: String): HabiTreeNavKey? = createKey(argument)
}

class HabiTreeFeatureModule(vararg destinations: HabiTreeFeatureDestination) {
    val destinations = destinations.toList()

    val navigationModule = module {
        this@HabiTreeFeatureModule.destinations.forEach { destination ->
            single<HabiTreeFeatureDestination>(named(destination.routeId)) { destination }
        }
    }
}

class HabiTreeRouteRegistry(destinations: List<HabiTreeFeatureDestination>) {
    private val byRoute = destinations.associateBy { it.routeId }

    init {
        require(byRoute.size == destinations.size) { "Duplicate feature route registration" }
    }

    fun keyFor(fallback: HabiTreeNavKey): HabiTreeNavKey =
        byRoute[fallback.routeId]?.keyFor(fallback.argument) ?: fallback
}
