package com.mamxanh.app.feature.stats.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import com.mamxanh.app.feature.stats.presentation.ui.stats.StatisticsLink
import com.mamxanh.app.feature.stats.presentation.ui.stats.StatisticsScreen
import com.mamxanh.app.feature.stats.presentation.ui.stats.StatisticsScreenState
import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureDestination
import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureModule
import com.mamxanh.app.core.presentation.navigation.HabiTreeNavKey
import com.mamxanh.app.feature.stats.presentation.ui.achievements.AchievementDisplay
import com.mamxanh.app.feature.stats.presentation.ui.achievements.AchievementsScreen
import com.mamxanh.app.feature.stats.presentation.ui.timeline.TimelineMemory
import com.mamxanh.app.feature.stats.domain.model.TimelineRelated
import com.mamxanh.app.feature.stats.presentation.ui.timeline.TimelineScreen
import com.mamxanh.app.feature.stats.presentation.ui.review.WeeklyReviewDisplay
import com.mamxanh.app.feature.stats.presentation.ui.review.WeeklyReviewScreen
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

class WeeklyReviewRouteContent(
    val review: WeeklyReviewDisplay?,
    val onTasks: () -> Unit,
    val onSaveNote: (String) -> Unit,
    val onRest: () -> Unit,
)

class TimelineRouteContent(
    val memories: List<TimelineMemory>,
    val onRelated: (TimelineRelated) -> Unit,
    val onSaveNote: (String, String) -> Unit,
)

val LocalWeeklyReviewRouteContent = compositionLocalOf<() -> WeeklyReviewRouteContent> {
    error("Weekly Review route requires its state and actions")
}

val LocalAchievementsRouteContent = compositionLocalOf<() -> List<AchievementDisplay>> {
    error("Achievements route requires its state")
}

val LocalTimelineRouteContent = compositionLocalOf<() -> TimelineRouteContent> {
    error("Timeline route requires its state and actions")
}

@Composable
private fun WeeklyReviewEntry() {
    val content = LocalWeeklyReviewRouteContent.current()
    WeeklyReviewScreen(content.review, content.onTasks, content.onSaveNote, content.onRest)
}

@Composable
private fun TimelineEntry() {
    val content = LocalTimelineRouteContent.current()
    TimelineScreen(content.memories, content.onRelated, content.onSaveNote)
}

val statsFeatureNavigation = HabiTreeFeatureModule(
    HabiTreeFeatureDestination("stats", content = { StatisticsEntry() }) { StatisticsKey.takeIf { _ -> it.isEmpty() } },
    HabiTreeFeatureDestination("review", content = { WeeklyReviewEntry() }) { WeeklyReviewKey.takeIf { _ -> it.isEmpty() } },
    HabiTreeFeatureDestination("achievements", content = { AchievementsScreen(LocalAchievementsRouteContent.current()) }) { AchievementsKey(it) },
    HabiTreeFeatureDestination("timeline", content = { TimelineEntry() }) { TimelineKey.takeIf { _ -> it.isEmpty() } },
)
