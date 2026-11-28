package com.mamxanh.app.feature.settings.presentation.ui.backup

import com.mamxanh.app.core.presentation.UiState
import com.mamxanh.app.feature.settings.presentation.BackupImportSummary

data class BackupState(
    val pending: BackupOperation? = null,
    val display: BackupDisplay = BackupDisplay(0, false),
    val importSummary: BackupImportSummary? = null,
    val awaitingDocument: Boolean = false,
) : UiState {
    val working: Boolean get() = pending != null && !awaitingDocument
}
