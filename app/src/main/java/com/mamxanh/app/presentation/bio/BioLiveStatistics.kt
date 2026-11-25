package com.mamxanh.app.presentation.bio

import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.mamxanh.app.feature.stats.domain.model.StatisticsSummary
import com.mamxanh.app.feature.stats.presentation.navigation.StatisticsRouteContent
import com.mamxanh.app.feature.stats.presentation.ui.stats.StatisticsLink
import com.mamxanh.app.feature.stats.presentation.ui.stats.StatisticsScreenState
import com.mamxanh.app.feature.stats.presentation.ui.stats.StatsAction
import com.mamxanh.app.feature.stats.presentation.ui.stats.StatsEvent
import com.mamxanh.app.feature.stats.presentation.ui.stats.StatsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun liveStatisticsRouteContent(
    world: BioWorld,
    ui: BioController,
): StatisticsRouteContent {
    val model = koinViewModel<StatsViewModel>()
    val state by model.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(model, world) { if (!world.loading) model.dispatch(StatsAction.Refresh) }
    LaunchedEffect(model, lifecycle, ui) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            model.events.collect { event ->
                if (ui.route != "stats") return@collect
                when (event) {
                    is StatsEvent.Open -> ui.go(event.link.destination().id)
                    is StatsEvent.Feedback -> ui.message = event.message
                }
            }
        }
    }
    val statistics = state.statistics
    if (state.isLoading) LinearProgressIndicator()
    return StatisticsRouteContent(
        state =
            StatisticsScreenState(
                total = statistics.total,
                treeCount = statistics.treeCount,
                week = statistics.week,
                today = statistics.today,
                streak = statistics.streak,
                dailyCompletions = statistics.displayCompletions(),
                dayLabels = world.statisticsDayLabels(),
            ),
        onNavigate = { model.dispatch(StatsAction.Open(it)) },
        onRefresh = { if (!state.isLoading) model.dispatch(StatsAction.RefreshFromScreen) },
    )
}

internal fun StatisticsLink.destination(): BioRoute =
    when (this) {
        StatisticsLink.Review -> BioRoute.REVIEW
        StatisticsLink.Achievements -> BioRoute.ACHIEVEMENTS
        StatisticsLink.Timeline -> BioRoute.TIMELINE
        StatisticsLink.Focus -> BioRoute.FOCUS
        StatisticsLink.Settings -> BioRoute.SETTINGS
        StatisticsLink.More -> BioRoute.MORE
    }

private fun StatisticsSummary.displayCompletions(): List<Int> = List(7) { (recentDailyCompletions.getOrNull(it) ?: 0).coerceAtLeast(0) }
