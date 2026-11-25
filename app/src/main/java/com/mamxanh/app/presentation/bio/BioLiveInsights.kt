package com.mamxanh.app.presentation.bio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.mamxanh.app.feature.tasks.presentation.ui.insights.InsightsAction
import com.mamxanh.app.feature.tasks.presentation.ui.insights.InsightsEvent
import com.mamxanh.app.feature.tasks.presentation.ui.insights.InsightsReminderDraft
import com.mamxanh.app.feature.tasks.presentation.ui.insights.InsightsState
import com.mamxanh.app.feature.tasks.presentation.ui.insights.InsightsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun insightsViewModel(): InsightsViewModel = koinViewModel()

@Composable
internal fun bindLiveInsights(
    world: BioWorld,
    ui: BioController,
    id: String,
): LiveInsights {
    val model = insightsViewModel()
    val state by model.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(model, world, id) { if (!world.loading) model.dispatch(InsightsAction.Load(id)) }
    LaunchedEffect(model, lifecycle, ui) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            model.events.collect { event ->
                if (ui.route != "insights") return@collect
                when (event) {
                    InsightsEvent.AddTask -> ui.go("task-new")
                    InsightsEvent.OpenSettings -> ui.go("settings")
                    is InsightsEvent.OpenTask -> ui.go("insights", event.id)
                    is InsightsEvent.ConfirmAdjustment -> {
                        if (ui.param == event.id) ui.show("adjust", event.id)
                    }
                    is InsightsEvent.LoadFailed -> if (ui.param == event.id) ui.message = event.message else Unit
                    is InsightsEvent.ReminderResult -> if (ui.param == event.id) ui.message = event.message else Unit
                    is InsightsEvent.AdjustmentResult -> {
                        if (ui.param != event.id) return@collect
                        if (ui.modal == "adjust" && ui.modalId == event.id) {
                            if (event.success) {
                                ui.modal = ""
                                ui.message = event.message
                            } else {
                                ui.modalMessage = event.message
                            }
                        } else {
                            ui.message = event.message
                        }
                    }
                }
            }
        }
    }
    return LiveInsights(state, model::dispatch)
}

internal class LiveInsights(
    val state: InsightsState,
    private val dispatch: (InsightsAction) -> Unit,
) {
    val period: String get() = state.period

    fun editReminder(
        id: String,
        draft: InsightsReminderDraft,
    ) = dispatch(InsightsAction.EditReminder(id, draft))

    fun saveReminder(
        id: String,
        draft: InsightsReminderDraft,
    ) = dispatch(InsightsAction.SaveReminder(id, draft))

    fun setPeriod(value: String) = dispatch(InsightsAction.SetPeriod(value))

    fun addTask() = dispatch(InsightsAction.AddTask)

    fun openTask(id: String) = dispatch(InsightsAction.OpenTask(id))

    fun openSettings() = dispatch(InsightsAction.OpenSettings)

    fun requestAdjustment(id: String) = dispatch(InsightsAction.RequestAdjustment(id))
}
