package com.mamxanh.app.presentation.bio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.BioButton
import com.mamxanh.app.feature.tasks.presentation.navigation.TaskFormRouteContent
import com.mamxanh.app.feature.tasks.presentation.ui.form.TaskFormAction
import com.mamxanh.app.feature.tasks.presentation.ui.form.TaskFormEvent
import com.mamxanh.app.feature.tasks.presentation.ui.form.TaskFormState
import com.mamxanh.app.feature.tasks.presentation.ui.form.TaskFormViewModel
import com.mamxanh.core.ui.R
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun bindLiveTaskForm(ui: BioController): LiveTaskForm {
    val argument = ui.param
    val model =
        koinViewModel<TaskFormViewModel>(
            key = "task-new:" + argument,
            parameters = { parametersOf(argument) },
        )
    val state by model.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(model) { model.dispatch(TaskFormAction.Load) }
    DisposableEffect(ui, model) {
        ui.liveTaskForm = model
        onDispose { if (ui.liveTaskForm == model) ui.liveTaskForm = null }
    }
    LaunchedEffect(model, lifecycle, ui) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            model.events.collect { event ->
                if (ui.route != "task-new" || ui.param != argument) return@collect
                when (event) {
                    is TaskFormEvent.Feedback -> ui.message = event.message
                }
            }
        }
    }
    LaunchedEffect(model, lifecycle, ui) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            model.state.collect {
                if (it.savedMessage != null && ui.route == "task-new" && ui.param == argument) {
                    model.acknowledgeSave()?.let { message -> ui.result(BioResult(true, message, "tasks")) }
                }
            }
        }
    }
    return LiveTaskForm(state, model::dispatch, ui::back)
}

internal class LiveTaskForm(
    private val state: TaskFormState,
    private val dispatch: (TaskFormAction) -> Unit,
    private val back: () -> Unit,
) {
    fun content(compactScreen: Boolean): TaskFormRouteContent =
        TaskFormRouteContent(
            state = state.display(),
            onFieldChanged = { field, value -> dispatch(TaskFormAction.Change(field, value)) },
            actions = if (compactScreen) null else ({ Actions() }),
        )

    @Composable
    fun Actions() {
        Row(horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp12)) {
            BioButton(stringResource(R.string.copy_huy_74fcd3), back, Modifier.weight(1f), secondary = true, enabled = !state.busy)
            BioButton(stringResource(R.string.copy_luu_viec_522a3d), {
                dispatch(TaskFormAction.Save)
            }, Modifier.weight(1f), enabled = !state.busy && state.initial != null)
        }
    }
}
