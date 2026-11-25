package com.mamxanh.app.presentation.bio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.mamxanh.app.feature.stats.domain.model.TimelineRelated
import com.mamxanh.app.feature.stats.domain.usecase.JournalNote
import com.mamxanh.app.feature.stats.presentation.ui.journal.JournalAction
import com.mamxanh.app.feature.stats.presentation.ui.journal.JournalEvent
import com.mamxanh.app.feature.stats.presentation.ui.journal.JournalState
import com.mamxanh.app.feature.stats.presentation.ui.journal.JournalViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun bindLiveJournal(
    ui: BioController,
    world: BioWorld,
    loadTimeline: Boolean,
): LiveJournal {
    val model = koinViewModel<JournalViewModel>()
    val state by model.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(model, world, loadTimeline) {
        if (!world.loading) model.dispatch(if (loadTimeline) JournalAction.LoadTimeline else JournalAction.LoadWeeklyReview)
    }
    LaunchedEffect(model, lifecycle, ui) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            model.events.collect { event ->
                when (event) {
                    JournalEvent.OpenTasks -> if (ui.route == "review") ui.go("tasks") else Unit
                    JournalEvent.OpenRest -> if (ui.route == "review") ui.go("rest") else Unit
                    is JournalEvent.OpenRelated -> {
                        if (ui.route == "timeline") {
                            val destination = event.target.toBioDestination()
                            ui.go(destination.routeId, destination.argument)
                        }
                    }
                    is JournalEvent.Feedback -> ui.message = event.message
                }
            }
        }
    }
    return LiveJournal(state, model::dispatch)
}

internal class LiveJournal(
    val state: JournalState,
    private val dispatch: (JournalAction) -> Unit,
) {
    fun openTasks() = dispatch(JournalAction.OpenTasks)

    fun openRest() = dispatch(JournalAction.OpenRest)

    fun openRelated(target: TimelineRelated) = dispatch(JournalAction.OpenRelated(target))

    fun editReflection(
        week: String,
        note: String,
    ) = dispatch(JournalAction.Edit(JournalNote.WeeklyReflection(week, note)))

    fun saveReflection(
        week: String,
        note: String,
    ) = dispatch(JournalAction.Save(JournalNote.WeeklyReflection(week, note)))

    fun editMemory(
        id: String,
        note: String,
    ) = dispatch(JournalAction.Edit(JournalNote.TimelineMemory(id, note)))

    fun saveMemory(
        id: String,
        note: String,
    ) = dispatch(JournalAction.Save(JournalNote.TimelineMemory(id, note)))
}
