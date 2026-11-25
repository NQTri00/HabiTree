package com.mamxanh.app.presentation.bio

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.mamxanh.app.core.components.design.bioLocale
import com.mamxanh.app.data.BackupService
import com.mamxanh.app.feature.settings.presentation.BackupImportSummary
import com.mamxanh.app.feature.settings.presentation.ui.backup.BackupAction
import com.mamxanh.app.feature.settings.presentation.ui.backup.BackupEvent
import com.mamxanh.app.feature.settings.presentation.ui.backup.BackupOperation
import com.mamxanh.app.feature.settings.presentation.ui.backup.BackupViewModel
import com.mamxanh.shared.data.sql.*
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*

@Composable
internal fun BindAndroidBackup(
    ui: BioController,
    service: BackupService,
    backupModel: BackupViewModel,
    refresh: suspend () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentRefresh by rememberUpdatedState(refresh)
    var pending by remember { mutableStateOf<GameBackup?>(null) }
    LaunchedEffect(backupModel) {
        if (pending == null && backupModel.state.value.importSummary != null) {
            backupModel.dispatch(BackupAction.Cancelled)
        }
        backupModel.state.collect { state ->
            if (state.importSummary == null && state.pending != BackupOperation.Import) pending = null
        }
    }

    fun fileOperation(
        operation: BackupOperation,
        action: () -> Unit,
    ) {
        if (ui.busy)return
        ui.busy = true
        scope.launch {
            try {
                withContext(Dispatchers.IO) { action() }
                try {
                    currentRefresh()
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (failure: Exception) {
                    android.util.Log.w("BioNativeHost", "Backup completed before display refresh failed", failure)
                }
                val label =
                    when (operation) {
                        BackupOperation.Clear -> TextKey.CopyDaXoaDuLieu1841a6
                        BackupOperation.Recover, BackupOperation.Restore -> TextKey.CopyDaKhoiPhucDuLieuB0b459
                        else -> TextKey.CopyDaLuuDuLieuD8a848
                    }
                backupModel.dispatch(BackupAction.Finished(operation, true, AppText.text(label)))
            } catch (cancelled: CancellationException) {
                backupModel.dispatch(BackupAction.Cancelled)
                throw cancelled
            } catch (_: Exception) {
                backupModel.dispatch(
                    BackupAction.Finished(
                        operation,
                        false,
                        AppText.text(TextKey.CopyKhongTheXuLyTepDuLieuHienTaiGiuNguyenAa333d),
                    ),
                )
            } finally {
                ui.busy = false
            }
        }
    }
    val export =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/octet-stream")) { uri ->
            if (uri !=
                null
            ) {
                backupModel.dispatch(BackupAction.DocumentReceived(BackupOperation.Export))
                fileOperation(BackupOperation.Export) { service.export(uri) }
            } else {
                backupModel.dispatch(BackupAction.Cancelled)
            }
        }
    val diagnostics =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/plain")) { uri ->
            if (uri !=
                null
            ) {
                backupModel.dispatch(BackupAction.DocumentReceived(BackupOperation.Diagnostics))
                fileOperation(BackupOperation.Diagnostics) { service.diagnostics(uri) }
            } else {
                backupModel.dispatch(BackupAction.Cancelled)
            }
        }
    val import =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri == null) backupModel.dispatch(BackupAction.Cancelled)
            if (uri != null && !ui.busy) {
                backupModel.dispatch(BackupAction.DocumentReceived(BackupOperation.Import))
                pending = null
                ui.busy = true
                scope.launch {
                    try {
                        val backup = withContext(Dispatchers.IO) { service.inspect(uri) }
                        pending = backup
                        val created =
                            if (backup.createdAt > 0) {
                                java.text.DateFormat
                                    .getDateTimeInstance(
                                        java.text.DateFormat.SHORT,
                                        java.text.DateFormat.SHORT,
                                        bioLocale(),
                                    ).format(Date(backup.createdAt))
                            } else {
                                AppText.text(TextKey.CopyKhongRo8af7da)
                            }
                        val summary =
                            BackupImportSummary(
                                tasks = backup.count("tasks").toString(),
                                trees = backup.count("trees").toString(),
                                occurrences = backup.count("occurrences").toString(),
                                created = created,
                                version = backup.schema.toString(),
                            )
                        ui.busy = false
                        backupModel.dispatch(BackupAction.ImportInspected(summary))
                    } catch (cancelled: CancellationException) {
                        backupModel.dispatch(BackupAction.Cancelled)
                        throw cancelled
                    } catch (_: Exception) {
                        backupModel.dispatch(
                            BackupAction.Finished(
                                BackupOperation.Import,
                                false,
                                AppText.text(TextKey.CopyKhongTheXuLyTepDuLieuHienTaiGiuNguyenAa333d),
                            ),
                        )
                    } finally {
                        ui.busy = false
                    }
                }
            }
        }
    LaunchedEffect(backupModel, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            backupModel.events.collect { event ->
                when (event) {
                    BackupEvent.ConfirmClear -> ui.show("clear")
                    BackupEvent.ConfirmFinalClear -> ui.show("clear-final")
                    BackupEvent.ConfirmRecover -> ui.show("recover")
                    BackupEvent.ConfirmImport -> {
                        if (pending != null && backupModel.state.value.importSummary != null) ui.show("confirm-import")
                    }
                    BackupEvent.ShowLicense -> ui.show("license")
                    is BackupEvent.Run ->
                        when (event.operation) {
                            BackupOperation.Export -> export.launch("MamXanh-" + System.currentTimeMillis() + ".mamxanh")
                            BackupOperation.Diagnostics -> diagnostics.launch("MamXanh-diagnostics.txt")
                            BackupOperation.Import ->
                                import.launch(
                                    arrayOf("application/octet-stream", "text/plain", "application/zip", "application/json"),
                                )
                            BackupOperation.Clear -> fileOperation(event.operation) { service.clear() }
                            BackupOperation.Recover -> fileOperation(event.operation) { service.recover() }
                            BackupOperation.Restore -> {
                                val backup = pending
                                if (backup != null) {
                                    fileOperation(event.operation) { service.restore(backup) }
                                } else {
                                    backupModel.dispatch(
                                        BackupAction.Finished(
                                            BackupOperation.Restore,
                                            false,
                                            AppText.text(TextKey.CopyChonTepSaoLuuTruoc71112e),
                                        ),
                                    )
                                }
                            }
                        }
                    is BackupEvent.Completed -> {
                        if (event.operation != BackupOperation.Restore || event.success) pending = null
                        val route =
                            if (!event.success) {
                                ""
                            } else {
                                when (event.operation) {
                                    BackupOperation.Clear -> "onboard"
                                    BackupOperation.Recover, BackupOperation.Restore -> "home"
                                    else -> ""
                                }
                            }
                        ui.result(BioResult(event.success, event.message, route))
                    }
                }
            }
        }
    }
}
