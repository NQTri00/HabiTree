package com.mamxanh.app.feature.settings.presentation.ui.settings

import com.mamxanh.app.core.presentation.UiAction
import com.mamxanh.app.feature.settings.presentation.NotificationPermissionResult

sealed interface SettingsAction : UiAction {
    data class ChangeLanguage(
        val tag: String,
    ) : SettingsAction

    data class Toggle(
        val key: String,
        val enabled: Boolean,
    ) : SettingsAction

    data object Save : SettingsAction

    data class Open(
        val link: SettingsLink,
    ) : SettingsAction

    data class EditQuietHours(
        val start: String,
        val end: String,
    ) : SettingsAction

    data object RequestPermission : SettingsAction

    data object OpenPermissionSettings : SettingsAction

    data object PermissionRequiresSettings : SettingsAction

    data class PermissionFinished(
        val result: NotificationPermissionResult,
    ) : SettingsAction
}
