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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.mamxanh.app.BuildConfig
import com.mamxanh.app.core.presentation.navigation.HabiTreeRouteRegistry
import com.mamxanh.app.data.BackupService
import com.mamxanh.app.data.TaskReminders
import com.mamxanh.app.feature.garden.domain.usecase.GardenRepository
import com.mamxanh.app.feature.home.domain.usecase.HomeTreeSelection
import com.mamxanh.app.feature.settings.presentation.NotificationPermissionResult
import com.mamxanh.app.feature.settings.presentation.ui.backup.BackupAction
import com.mamxanh.app.feature.settings.presentation.ui.backup.BackupDisplay
import com.mamxanh.shared.data.GameStorage
import com.mamxanh.shared.data.sql.*
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
    val backupState by backupModel.state.collectAsStateWithLifecycle()
    ui.backupWorking = { backupState.working }
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
    LaunchedEffect(storage, lifecycleOwner, context) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            storage.changes.collect {
                try {
                    withContext(Dispatchers.IO) {
                        TaskReminders.schedule(context.applicationContext, onlyIfChanged = true)
                    }
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (failure: Exception) {
                    android.util.Log.w("BioNativeHost", "Reminder scheduling failed", failure)
                }
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

    BindAndroidBackup(ui, service, backupModel, ::refresh)
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
