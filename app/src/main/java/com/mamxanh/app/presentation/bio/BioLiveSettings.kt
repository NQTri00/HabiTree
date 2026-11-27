package com.mamxanh.app.presentation.bio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.mamxanh.app.feature.settings.presentation.navigation.SettingsRouteContent
import com.mamxanh.app.feature.settings.presentation.ui.backup.BackupViewModel
import com.mamxanh.app.feature.settings.presentation.ui.settings.SettingsAction
import com.mamxanh.app.feature.settings.presentation.ui.settings.SettingsEvent
import com.mamxanh.app.feature.settings.presentation.ui.settings.SettingsLink
import com.mamxanh.app.feature.settings.presentation.ui.settings.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun settingsViewModel(): SettingsViewModel = koinViewModel()

@Composable
internal fun backupViewModel(): BackupViewModel = koinViewModel()

@Composable
internal fun liveSettingsRouteContent(ui: BioController): SettingsRouteContent {
    val model = settingsViewModel()
    val state by model.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(model, lifecycle, ui) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            model.events.collect { event ->
                if (ui.route != "settings") return@collect
                when (event) {
                    is SettingsEvent.ChangeLanguage -> ui.selectLanguage(event.tag)
                    SettingsEvent.RequestPermission -> ui.requestNotificationPermission()
                    SettingsEvent.OpenPermissionSettings -> ui.openNotificationSettings()
                    SettingsEvent.ShowPermissionSettings -> ui.show("permission-settings")
                    is SettingsEvent.Feedback -> ui.message = event.message
                    is SettingsEvent.Open ->
                        when (event.link) {
                            SettingsLink.Permission -> ui.show("permission")
                            SettingsLink.Insights -> ui.go("insights")
                            SettingsLink.Data -> ui.go("data")
                            SettingsLink.Onboarding -> ui.go("onboard")
                        }
                }
            }
        }
    }
    val display = state.display()
    return SettingsRouteContent(
        state = display,
        onLanguage = { model.dispatch(SettingsAction.ChangeLanguage(it)) },
        onToggle = { key, value -> model.dispatch(SettingsAction.Toggle(key, value)) },
        onSave = { _, _ -> model.dispatch(SettingsAction.Save) },
        onLink = { model.dispatch(SettingsAction.Open(it)) },
        editQuietHours = { start, end -> model.dispatch(SettingsAction.EditQuietHours(start, end)) },
        busy = state.busy || state.persisted == null,
    )
}
