package com.mamxanh.app.presentation.bio

import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.mamxanh.app.feature.stats.presentation.ui.achievements.AchievementDisplay
import com.mamxanh.app.feature.stats.presentation.ui.achievements.AchievementsAction
import com.mamxanh.app.feature.stats.presentation.ui.achievements.AchievementsEvent
import com.mamxanh.app.feature.stats.presentation.ui.achievements.AchievementsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun liveAchievements(
    world: BioWorld,
    ui: BioController,
): List<AchievementDisplay> {
    val model = koinViewModel<AchievementsViewModel>()
    val state by model.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(model, world) { if (!world.loading) model.dispatch(AchievementsAction.Refresh) }
    LaunchedEffect(model, lifecycle, ui) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            model.events.collect { event ->
                if (ui.route == "achievements" && event is AchievementsEvent.Feedback) ui.message = event.message
            }
        }
    }
    if (state.loading) LinearProgressIndicator()
    return state.entries
}
