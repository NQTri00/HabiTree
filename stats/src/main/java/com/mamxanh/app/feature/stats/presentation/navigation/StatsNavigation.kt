package com.mamxanh.app.feature.stats.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import com.mamxanh.app.feature.stats.presentation.StatisticsLink
import com.mamxanh.app.feature.stats.presentation.StatisticsScreen
import com.mamxanh.app.feature.stats.presentation.StatisticsScreenState
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

class StatisticsRouteContent(
    val state: StatisticsScreenState,
    val onNavigate: (StatisticsLink) -> Unit,
    val onRefresh: () -> Unit,
)

val LocalStatisticsRouteContent = compositionLocalOf<() -> StatisticsRouteContent> {
    error("Statistics route requires its state and actions")
}

@Composable
private fun StatisticsEntry() {
    val content = LocalStatisticsRouteContent.current()
    StatisticsScreen(content.state, content.onNavigate, content.onRefresh)
}

val statsFeatureNavigation = HabiTreeFeatureModule(
    HabiTreeFeatureDestination("stats", content = { StatisticsEntry() }) { StatisticsKey.takeIf { _ -> it.isEmpty() } },
    HabiTreeFeatureDestination("review") { WeeklyReviewKey.takeIf { _ -> it.isEmpty() } },
    HabiTreeFeatureDestination("achievements") { AchievementsKey(it) },
    HabiTreeFeatureDestination("timeline") { TimelineKey.takeIf { _ -> it.isEmpty() } },
)
