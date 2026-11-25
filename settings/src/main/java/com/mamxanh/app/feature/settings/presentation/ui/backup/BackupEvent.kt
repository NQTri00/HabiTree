package com.mamxanh.app.feature.settings.presentation.ui.backup

import com.mamxanh.app.core.presentation.UiEvent

sealed interface BackupEvent : UiEvent {
    data class Run(
        val operation: BackupOperation,
    ) : BackupEvent

    data class Completed(
        val operation: BackupOperation,
        val success: Boolean,
        val message: String,
    ) : BackupEvent

    data object ConfirmClear : BackupEvent

    data object ConfirmFinalClear : BackupEvent

    data object ConfirmRecover : BackupEvent

    data object ConfirmImport : BackupEvent

    data object ShowLicense : BackupEvent
}
