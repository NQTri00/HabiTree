package com.mamxanh.app.presentation.bio

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.mamxanh.app.BuildConfig
import com.mamxanh.app.core.components.design.bioLocale
import com.mamxanh.app.core.presentation.navigation.HabiTreeRouteRegistry
import com.mamxanh.app.data.BackupService
import com.mamxanh.app.feature.garden.domain.usecase.GardenRepository
import com.mamxanh.app.feature.home.domain.usecase.HomeTreeSelection
import com.mamxanh.app.feature.settings.presentation.BackupImportSummary
import com.mamxanh.app.feature.settings.presentation.NotificationPermissionResult
import com.mamxanh.app.feature.settings.presentation.ui.backup.BackupAction
import com.mamxanh.app.feature.settings.presentation.ui.backup.BackupDisplay
import com.mamxanh.app.feature.settings.presentation.ui.backup.BackupEvent
import com.mamxanh.app.feature.settings.presentation.ui.backup.BackupOperation
import com.mamxanh.shared.data.GameStorage
import com.mamxanh.shared.data.sql.*
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.compose.koinInject
import java.util.*

@Composable fun BioNativeHost(
    storage: GameStorage,
    externalTree: Long? = null,
    externalRoute: String = "",
    externalTask: Long = 0,
    onExternalHandled: () -> Unit = {},
) {
    val garden = koinInject<GardenRepository>()
    val homeSelection = koinInject<HomeTreeSelection>()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val reference = remember { BioWorld.reference(context) }
    val permissionPrefs = remember { context.getSharedPreferences("engagement_permissions", Context.MODE_PRIVATE) }
    val service = remember { BackupService(context.applicationContext, storage, BuildConfig.VERSION_NAME) }
    val ui =
        rememberSaveable(saver = BioController.Saver) {
            BioController().apply { homeTreeState.selectedId = homeSelection.savedId() }
        }
    var world by remember {
        mutableStateOf(loadingNativeWorld(reference))
    }
    val backupModel = backupViewModel()
    var pending by remember { mutableStateOf<GameBackup?>(null) }
    LaunchedEffect(backupModel) {
        if (pending == null && backupModel.state.value.importSummary != null) {
            backupModel.dispatch(BackupAction.Cancelled)
        }
        backupModel.state.collect { state ->
            if (state.importSummary == null && state.pending != BackupOperation.Import) pending = null
        }
    }
    val homeSpecies = world.treeSpecies(ui.homeTreeState.selectedId).orEmpty()
    LaunchedEffect(homeSpecies, world.loading) {
        if (!world.loading)withContext(Dispatchers.IO) { HabiTreeLauncherIcon.update(context.applicationContext, homeSpecies) }
    }

    suspend fun refresh() {
        val (snapshot, backup) =
            withContext(Dispatchers.IO) {
                nativeWorld(storage, reference, garden) to
                    BackupDisplay((service.sizeBytes() / 1024).coerceAtMost(Int.MAX_VALUE.toLong()).toInt(), service.hasSafety())
            }
        world = snapshot
        backupModel.dispatch(BackupAction.MetadataLoaded(backup))
        ui.homeTreeState.reconcile(homeSelection, world.treeIds)
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(storage, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            merge(
                storage.changes.map { Unit },
                flow {
                    while (currentCoroutineContext().isActive) {
                        delay(15_000)
                        emit(Unit)
                    }
                },
            ).conflate()
                .collect {
                    try {
                        refresh()
                        if (!world.onboarded && ui.route == "home") ui.go("onboard", root = true)
                    } catch (cancelled: CancellationException) {
                        throw cancelled
                    } catch (failure: Exception) {
                        android.util.Log.w("BioNativeHost", "Display refresh failed", failure)
                    }
                }
        }
    }
    // Refresh Android alarms when scheduling inputs change.
    LaunchedEffect(
        world.loading,
        world.liveTrees,
        world.reminderScheduleInput,
    ) {
        if (!world.loading) {
            withContext(
                Dispatchers.IO,
            ) {
                com.mamxanh.app.data.TaskReminders
                    .schedule(context.applicationContext, emptyList())
            }
        }
    }
    LaunchedEffect(externalTree) {
        externalTree?.let {
            ui.go("tree", it.toString())
            onExternalHandled()
        }
    }
    LaunchedEffect(externalRoute, externalTask) {
        if (externalTask > 0 &&
            externalRoute in listOf("FOCUS", "INSIGHTS")
        ) {
            ui.go(externalRoute.lowercase(), externalTask.toString())
            onExternalHandled()
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
                    refresh()
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
    val settingsModel = settingsViewModel()
    val permission =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { allowed ->
            val result = if (allowed) NotificationPermissionResult.Granted else NotificationPermissionResult.Denied
            settingsModel.dispatch(
                com.mamxanh.app.feature.settings.presentation.ui.settings.SettingsAction
                    .PermissionFinished(result),
            )
        }
    ui.selectLanguage = { tag ->
        com.mamxanh.app.core.components.localization.AppLanguageController
            .select(context, tag)
    }
    ui.openNotificationSettings = {
        ui.modal = ""
        context.startActivity(
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null)),
        )
    }
    ui.requestNotificationPermission = {
        val granted =
            Build.VERSION.SDK_INT < 33 ||
                context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        val rationale =
            Build.VERSION.SDK_INT >= 33 &&
                (context as? android.app.Activity)?.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) == true
        when {
            ui.modal == "permission-settings" -> ui.openNotificationSettings()
            granted || permissionPrefs.getBoolean("requested", false) && !rationale -> {
                settingsModel.dispatch(com.mamxanh.app.feature.settings.presentation.ui.settings.SettingsAction.PermissionRequiresSettings)
            }
            else -> {
                permissionPrefs.edit().putBoolean("requested", true).apply()
                ui.modal = ""
                if (Build.VERSION.SDK_INT >= 33) permission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    BioApplication(
        world,
        ui,
        Modifier.fillMaxSize().safeDrawingPadding().imePadding(),
        koinInject<HabiTreeRouteRegistry>(),
        refreshWorld = ::refresh,
    )
}
