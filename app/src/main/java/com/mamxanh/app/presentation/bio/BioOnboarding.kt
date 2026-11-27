package com.mamxanh.app.presentation.bio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.mamxanh.app.feature.home.presentation.navigation.OnboardingRouteContent
import com.mamxanh.app.feature.home.presentation.ui.onboarding.OnboardingAction
import com.mamxanh.app.feature.home.presentation.ui.onboarding.OnboardingDisplay
import com.mamxanh.app.feature.home.presentation.ui.onboarding.OnboardingEvent
import com.mamxanh.app.feature.home.presentation.ui.onboarding.OnboardingState
import com.mamxanh.app.feature.home.presentation.ui.onboarding.OnboardingViewModel
import com.mamxanh.app.feature.tasks.presentation.ui.templates.TemplateRow
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.model.catalog
import kotlinx.coroutines.CancellationException
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun liveOnboardingRouteContent(
    world: BioWorld,
    ui: BioController,
    gutter: Int,
    refreshWorld: suspend () -> Unit,
): OnboardingRouteContent {
    val viewModel = koinViewModel<OnboardingViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val refresh by rememberUpdatedState(refreshWorld)
    LaunchedEffect(viewModel, lifecycle) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.events.collect { event ->
                when (event) {
                    is OnboardingEvent.Feedback -> ui.message = event.message
                    is OnboardingEvent.Finished -> {
                        try {
                            refresh()
                        } catch (cancelled: CancellationException) {
                            throw cancelled
                        } catch (failure: Exception) {
                            android.util.Log.w("BioOnboarding", "Onboarding completed before display refresh failed", failure)
                        }
                        if (ui.route == "onboard") {
                            ui.result(BioResult(true, event.message, "home"))
                            viewModel.acknowledgeCompletion()
                        }
                    }
                }
            }
        }
    }
    val finished = !world.loading && world.onboarded
    LaunchedEffect(finished, state.submitting, state.completed, state.recoveringSubmission) {
        if (finished && !state.submitting && (state.completed || state.recoveringSubmission) && ui.route == "onboard") {
            ui.go("home", root = true)
            viewModel.acknowledgeCompletion()
        }
    }
    return onboardingRouteContent(world, ui, gutter, state) { action ->
        val finish = action == OnboardingAction.Skip || (action == OnboardingAction.Next && state.step >= 4)
        viewModel.dispatch(if (finished && finish) OnboardingAction.ContinueCompleted else action)
    }
}

internal fun onboardingRouteContent(
    world: BioWorld,
    ui: BioController,
    gutter: Int,
    liveState: OnboardingState? = null,
    dispatch: ((OnboardingAction) -> Unit)? = null,
): OnboardingRouteContent {
    val templates = displayedTemplates(world.takeIf { liveState == null })
    val category = liveState?.category ?: ui.preview.onboarding.category
    val chosen = (liveState?.templateIds ?: ui.preview.onboarding.selectedTemplates).toSet()
    val seed = liveState?.seed ?: ui.preview.onboarding.seed
    val seedNames = if (liveState == null) emptyMap() else catalog.associate { it.id to it.name }
    val byCategory = templates.sortedBy { it.category != category }

    return OnboardingRouteContent(
        state =
            OnboardingDisplay(
                step = liveState?.step ?: ui.preview.onboarding.step,
                seed = seed,
                category = category,
                selectedCount = chosen.size,
                selectedTitles = templates.filter { it.id in chosen }.map { it.title },
                seedNames =
                    (listOf("succulent", "monstera", "snake") + seed).associateWith {
                        if (liveState == null) world.species(it).s("name") else seedNames[it] ?: seedNames.values.first()
                    },
                templateCount = byCategory.size,
                remind = liveState?.reminders ?: (ui.fields["onboard-remind"] == "true"),
            ),
        gutter = gutter,
        onAction =
            dispatch ?: { action ->
                when (action) {
                    OnboardingAction.Back -> ui.preview.onboarding.previousStep()
                    OnboardingAction.Next -> {
                        ui.preview.onboarding.advance()
                    }
                    OnboardingAction.Skip -> ui.go("home", root = true)
                    OnboardingAction.ContinueCompleted -> ui.go("home", root = true)
                    is OnboardingAction.Choose ->
                        if (action.step ==
                            1
                        ) {
                            ui.preview.onboarding.seed = action.id
                        } else {
                            ui.preview.onboarding.category = action.id
                        }
                    is OnboardingAction.Remind -> ui.fields["onboard-remind"] = action.enabled.toString()
                    is OnboardingAction.ToggleTemplate -> Unit
                }
                Unit
            },
        templateRow = { index ->
            val template = byCategory[index]
            val id = template.id
            TemplateRow(template, id in chosen, onboarding = true) {
                if (dispatch != null) {
                    dispatch(OnboardingAction.ToggleTemplate(id))
                } else if (!ui.preview.onboarding.toggleTemplate(id)) {
                    ui.message = AppText.text(TextKey.CopyBanDaChon5ViecBoChonMotViecDeDoi95f1ae)
                }
            }
        },
    )
}
