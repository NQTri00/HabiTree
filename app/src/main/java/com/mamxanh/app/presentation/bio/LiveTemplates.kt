package com.mamxanh.app.presentation.bio

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.mamxanh.app.core.components.design.BioButton
import com.mamxanh.app.feature.tasks.presentation.ui.templates.TemplatesAction
import com.mamxanh.app.feature.tasks.presentation.ui.templates.TemplatesEvent
import com.mamxanh.app.feature.tasks.presentation.ui.templates.TemplatesState
import com.mamxanh.app.feature.tasks.presentation.ui.templates.TemplatesViewModel
import com.mamxanh.core.ui.R
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun bindLiveTemplates(ui: BioController): LiveTemplates {
    val model = koinViewModel<TemplatesViewModel>()
    val state by model.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(model, lifecycle, ui) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            model.events.collect { event ->
                when (event) {
                    is TemplatesEvent.Feedback -> ui.message = event.message
                }
            }
        }
    }
    return LiveTemplates(state, model::dispatch)
}

internal class LiveTemplates(
    private val state: TemplatesState,
    private val dispatch: (TemplatesAction) -> Unit,
) {
    val selected: Set<String> get() = state.selected

    fun toggle(id: String) = dispatch(TemplatesAction.Toggle(id))

    @Composable
    fun Actions() {
        val label =
            if (selected.isEmpty()) {
                stringResource(R.string.copy_chon_viec_de_them_a9f4fc)
            } else {
                stringResource(R.string.copy_them_0_viec_d0390c, selected.size)
            }
        BioButton(
            label,
            { dispatch(TemplatesAction.Apply) },
            Modifier.fillMaxWidth(),
            enabled = selected.isNotEmpty() && !state.busy,
        )
    }
}
