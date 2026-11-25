package com.mamxanh.app.feature.settings.presentation.ui.backup

import com.mamxanh.app.core.presentation.UiAction
import com.mamxanh.app.feature.settings.presentation.BackupImportSummary

sealed interface BackupAction : UiAction {
    data class MetadataLoaded(
        val display: BackupDisplay,
    ) : BackupAction

    data class Screen(
        val action: BackupScreenAction,
    ) : BackupAction

    data class Finished(
        val operation: BackupOperation,
        val success: Boolean,
        val message: String,
    ) : BackupAction

    data class ImportInspected(
        val summary: BackupImportSummary,
    ) : BackupAction

    data object Cancelled : BackupAction

    data class DocumentReceived(
        val operation: BackupOperation,
    ) : BackupAction

    data object ClearNext : BackupAction

    data class Confirm(
        val operation: BackupOperation,
    ) : BackupAction
}
