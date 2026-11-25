package com.mamxanh.app.presentation.bio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.mamxanh.app.feature.tasks.presentation.ui.rest.RestAction
import com.mamxanh.app.feature.tasks.presentation.ui.rest.RestControls
import com.mamxanh.app.feature.tasks.presentation.ui.rest.RestEvent
import com.mamxanh.app.feature.tasks.presentation.ui.rest.RestState
import com.mamxanh.app.feature.tasks.presentation.ui.rest.RestViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun restViewModel(): RestViewModel = koinViewModel()

@Composable
internal fun bindLiveRest(
    world: BioWorld,
    ui: BioController,
): LiveRest {
    val model = restViewModel()
    val state by model.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(model, world) { if (!world.loading) model.dispatch(RestAction.Refresh) }
    LaunchedEffect(model, lifecycle, ui) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            model.events.collect { event ->
                if (ui.route != "rest") return@collect
                when (event) {
                    RestEvent.ConfirmTicket -> ui.show("ticket")
                    is RestEvent.Feedback -> {
                        if (event.ticket && ui.modal == "ticket") {
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
    return LiveRest(state, model::dispatch)
}

internal class LiveRest(
    val state: RestState,
    private val dispatch: (RestAction) -> Unit,
) {
    fun controls(tomorrow: String) =
        RestControls(
            restDate = state.restDate ?: tomorrow,
            protectionDate = state.protectionDate,
            busy = state.busy,
            onRestDate = { dispatch(RestAction.SelectRestDate(it)) },
            onProtectionDate = { dispatch(RestAction.SelectProtectionDate(it)) },
        )

    fun schedule(date: String) = dispatch(RestAction.Schedule(date))

    fun protect(date: String) = dispatch(RestAction.Protect(date))

    fun requestTicket() = dispatch(RestAction.RequestTicket)
}
