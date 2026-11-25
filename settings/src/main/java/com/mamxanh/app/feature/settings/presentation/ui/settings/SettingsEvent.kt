package com.mamxanh.app.feature.settings.presentation.ui.settings

import com.mamxanh.app.core.presentation.UiEvent

sealed interface SettingsEvent : UiEvent {
    data class ChangeLanguage(
        val tag: String,
    ) : SettingsEvent

    data class Open(
        val link: SettingsLink,
    ) : SettingsEvent

    data class Feedback(
        val message: String,
    ) : SettingsEvent

    data object RequestPermission : SettingsEvent

    data object OpenPermissionSettings : SettingsEvent

    data object ShowPermissionSettings : SettingsEvent
}
