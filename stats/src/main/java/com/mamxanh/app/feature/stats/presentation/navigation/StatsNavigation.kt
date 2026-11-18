package com.mamxanh.app.feature.stats.presentation.navigation

import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureDestination
import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureModule
import com.mamxanh.app.core.presentation.navigation.HabiTreeNavKey
import kotlinx.serialization.Serializable

@Serializable
internal data object StatisticsKey : HabiTreeNavKey {
    override val routeId: String get() = "stats"
}

@Serializable
internal data object WeeklyReviewKey : HabiTreeNavKey {
    override val routeId: String get() = "review"
}

@Serializable
internal data class AchievementsKey(override val argument: String) : HabiTreeNavKey {
    override val routeId: String get() = "achievements"
}

@Serializable
internal data object TimelineKey : HabiTreeNavKey {
    override val routeId: String get() = "timeline"
}

val statsFeatureNavigation = HabiTreeFeatureModule(
    HabiTreeFeatureDestination("stats") { StatisticsKey.takeIf { _ -> it.isEmpty() } },
    HabiTreeFeatureDestination("review") { WeeklyReviewKey.takeIf { _ -> it.isEmpty() } },
    HabiTreeFeatureDestination("achievements") { AchievementsKey(it) },
    HabiTreeFeatureDestination("timeline") { TimelineKey.takeIf { _ -> it.isEmpty() } },
)
