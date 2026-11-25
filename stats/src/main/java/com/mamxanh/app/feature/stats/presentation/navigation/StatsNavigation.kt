package com.mamxanh.app.feature.stats.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureDestination
import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureModule
import com.mamxanh.app.core.presentation.navigation.HabiTreeNavKey
import com.mamxanh.app.feature.stats.domain.model.TimelineRelated
import com.mamxanh.app.feature.stats.presentation.ui.achievements.AchievementDisplay
import com.mamxanh.app.feature.stats.presentation.ui.achievements.AchievementsScreen
import com.mamxanh.app.feature.stats.presentation.ui.review.WeeklyReviewDisplay
import com.mamxanh.app.feature.stats.presentation.ui.review.WeeklyReviewScreen
import com.mamxanh.app.feature.stats.presentation.ui.stats.StatisticsLink
import com.mamxanh.app.feature.stats.presentation.ui.stats.StatisticsScreen
import com.mamxanh.app.feature.stats.presentation.ui.stats.StatisticsScreenState
import com.mamxanh.app.feature.stats.presentation.ui.timeline.TimelineMemory
import com.mamxanh.app.feature.stats.presentation.ui.timeline.TimelineScreen
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
internal data class AchievementsKey(
    override val argument: String,
) : HabiTreeNavKey {
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

val LocalStatisticsRouteContent =
    compositionLocalOf<@Composable () -> StatisticsRouteContent> {
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
    val draft: String? = null,
    val onEdit: ((String) -> Unit)? = null,
    val busy: Boolean = false,
)

class TimelineRouteContent(
    val memories: List<TimelineMemory>,
    val onRelated: (TimelineRelated) -> Unit,
    val onSaveNote: (String, String) -> Unit,
    val noteFor: ((TimelineMemory) -> String)? = null,
    val onEdit: ((String, String) -> Unit)? = null,
    val busy: Boolean = false,
)

val LocalWeeklyReviewRouteContent =
    compositionLocalOf<@Composable () -> WeeklyReviewRouteContent> {
        error("Weekly Review route requires its state and actions")
    }

val LocalAchievementsRouteContent =
    compositionLocalOf<@Composable () -> List<AchievementDisplay>> {
        error("Achievements route requires its state")
    }

val LocalTimelineRouteContent =
    compositionLocalOf<@Composable () -> TimelineRouteContent> {
        error("Timeline route requires its state and actions")
    }

@Composable
private fun WeeklyReviewEntry() {
    val content = LocalWeeklyReviewRouteContent.current()
    WeeklyReviewScreen(content.review, content.onTasks, content.onSaveNote, content.onRest, content.draft, content.onEdit, content.busy)
}

@Composable
private fun TimelineEntry() {
    val content = LocalTimelineRouteContent.current()
    TimelineScreen(content.memories, content.onRelated, content.onSaveNote, content.noteFor, content.onEdit, content.busy)
}

val statsFeatureNavigation =
    HabiTreeFeatureModule(
        HabiTreeFeatureDestination("stats", content = { StatisticsEntry() }) { StatisticsKey.takeIf { _ -> it.isEmpty() } },
        HabiTreeFeatureDestination("review", content = { WeeklyReviewEntry() }) { WeeklyReviewKey.takeIf { _ -> it.isEmpty() } },
        HabiTreeFeatureDestination("achievements", content = {
            AchievementsScreen(LocalAchievementsRouteContent.current())
        }) { AchievementsKey(it) },
        HabiTreeFeatureDestination("timeline", content = { TimelineEntry() }) { TimelineKey.takeIf { _ -> it.isEmpty() } },
    )
