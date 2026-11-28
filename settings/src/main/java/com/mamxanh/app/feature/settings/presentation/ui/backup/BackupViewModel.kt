package com.mamxanh.app.feature.settings.presentation.ui.backup

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mamxanh.app.core.presentation.BaseViewModel
import kotlinx.coroutines.launch

class BackupViewModel(
    private val savedState: SavedStateHandle,
) : BaseViewModel<BackupState, BackupEvent, BackupAction>(
        pickerOperations
            .firstOrNull { it.name == savedState.get<String>(PICKER_KEY) }
            ?.let { BackupState(pending = it, awaitingDocument = true) }
            ?: BackupState(),
    ) {
    override fun handleAction(action: BackupAction) {
        when (action) {
            is BackupAction.MetadataLoaded -> mutableState.value = state.value.copy(display = action.display)
            is BackupAction.Screen -> onScreen(action.action)
            BackupAction.ClearNext -> if (state.value.pending == null) send(BackupEvent.ConfirmFinalClear)
            is BackupAction.Confirm -> start(action.operation)
            BackupAction.Cancelled -> {
                savedState.remove<String>(PICKER_KEY)
                mutableState.value = state.value.copy(pending = null, importSummary = null, awaitingDocument = false)
            }
            is BackupAction.DocumentReceived -> {
                val current = state.value
                if (current.pending != action.operation || !current.awaitingDocument) return
                savedState.remove<String>(PICKER_KEY)
                mutableState.value = current.copy(awaitingDocument = false)
            }
            is BackupAction.ImportInspected -> {
                if (state.value.pending != BackupOperation.Import || !state.value.working) return
                mutableState.value = state.value.copy(pending = null, importSummary = action.summary)
                send(BackupEvent.ConfirmImport)
            }
            is BackupAction.Finished -> {
                if (state.value.pending != action.operation || !state.value.working) return
                mutableState.value =
                    if (action.operation == BackupOperation.Restore && !action.success) {
                        state.value.copy(pending = null)
                    } else {
                        state.value.copy(pending = null, importSummary = null)
                    }
                send(BackupEvent.Completed(action.operation, action.success, action.message))
            }
        }
    }

    private fun onScreen(action: BackupScreenAction) {
        if (state.value.pending != null) return
        when (action) {
            BackupScreenAction.Export -> start(BackupOperation.Export)
            BackupScreenAction.Import -> start(BackupOperation.Import)
            BackupScreenAction.Diagnostics -> start(BackupOperation.Diagnostics)
            BackupScreenAction.Clear -> send(BackupEvent.ConfirmClear)
            BackupScreenAction.Recover -> send(BackupEvent.ConfirmRecover)
            BackupScreenAction.License -> send(BackupEvent.ShowLicense)
        }
    }

    private fun start(operation: BackupOperation) {
        if (state.value.pending != null) return
        if (operation == BackupOperation.Restore && state.value.importSummary == null) return
        val awaitingDocument = operation in pickerOperations
        if (awaitingDocument) savedState[PICKER_KEY] = operation.name
        mutableState.value = state.value.copy(pending = operation, awaitingDocument = awaitingDocument)
        send(BackupEvent.Run(operation))
    }

    private companion object {
        const val PICKER_KEY = "backupDocumentPicker"
        val pickerOperations = setOf(BackupOperation.Export, BackupOperation.Import, BackupOperation.Diagnostics)
    }

    private fun send(event: BackupEvent) {
        viewModelScope.launch { emitEvent(event) }
    }
}
