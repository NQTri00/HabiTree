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
import com.mamxanh.app.feature.catalog.domain.repository.GardenCollectionRepository
import com.mamxanh.app.feature.garden.domain.usecase.GardenRepository
import com.mamxanh.app.feature.home.domain.repository.OnboardingRepository
import com.mamxanh.app.feature.home.domain.usecase.HomeTreeSelection
import com.mamxanh.app.feature.inventory.domain.repository.InventoryRepository
import com.mamxanh.app.feature.settings.domain.usecase.SaveReminderSettingsUseCase
import com.mamxanh.app.feature.settings.presentation.BackupImportSummary
import com.mamxanh.app.feature.settings.presentation.NotificationPermissionResult
import com.mamxanh.app.feature.shop.domain.repository.ShopRepository
import com.mamxanh.app.feature.stats.domain.usecase.LoadGardenStatistics
import com.mamxanh.app.feature.stats.domain.usecase.SaveJournalNoteUseCase
import com.mamxanh.app.feature.tasks.domain.repository.FocusSessionCommands
import com.mamxanh.app.feature.tasks.domain.repository.TaskRepository
import com.mamxanh.app.feature.tree.domain.repository.TreeRepository
import com.mamxanh.shared.data.GameStorage
import com.mamxanh.shared.data.sql.*
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.json.*
import org.koin.compose.koinInject
import java.util.*

@Composable fun BioNativeHost(
    storage: GameStorage,
    externalTree: Long? = null,
    externalRoute: String = "",
    externalTask: Long = 0,
    onExternalHandled: () -> Unit = {},
) {
    val inventory = koinInject<InventoryRepository>()
    val garden = koinInject<GardenRepository>()
    val collectionRepository = koinInject<GardenCollectionRepository>()
    val homeSelection = koinInject<HomeTreeSelection>()
    val onboarding = koinInject<OnboardingRepository>()
    val treeRepository = koinInject<TreeRepository>()
    val shop = koinInject<ShopRepository>()
    val taskRepository = koinInject<TaskRepository>()
    val focusCommands = koinInject<FocusSessionCommands>()
    val loadStatistics = koinInject<LoadGardenStatistics>()
    val saveJournalNote = koinInject<SaveJournalNoteUseCase>()
    val saveReminderSettings = koinInject<SaveReminderSettingsUseCase>()
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
        mutableStateOf(
            BioWorld(
                JSONObject(reference.data.toString()).apply {
                    put(
                        "state",
                        json(
                            "coins" to 0,
                            "trees" to JSONArray(),
                            "tasks" to JSONArray(),
                            "inventory" to JSONObject(),
                            "owned" to JSONObject(),
                            "settings" to JSONObject(),
                        ),
                    )
                },
                loading = true,
                native = true,
            ),
        )
    }
    var pending by remember { mutableStateOf<GameBackup?>(null) }
    val homeSpecies = world.tree(ui.homeTreeState.selectedId)?.s("species").orEmpty()
    LaunchedEffect(homeSpecies, world.loading) {
        if (!world.loading)withContext(Dispatchers.IO) { HabiTreeLauncherIcon.update(context.applicationContext, homeSpecies) }
    }

    suspend fun refresh() {
        world =
            withContext(Dispatchers.IO) {
                nativeWorld(storage, reference, service, inventory, garden, collectionRepository, taskRepository, loadStatistics)
            }
        ui.homeTreeState.reconcile(homeSelection, world.trees.map { it.s("id") })
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
                    refresh()
                    if (!world.state.b("onboarded", true) && ui.route == "home")ui.go("onboard", root = true)
                }
        }
    }
    // Schedule from the displayed snapshot, without loading unused screen ViewModels.
    LaunchedEffect(
        world.loading,
        world.trees.map { listOf(it.s("id"), it.s("state"), it.b("watered").toString()) },
        world.tasks.map { it.toString() },
        world.state.o("settings").toString(),
        world.state.o("reminders").toString(),
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

    fun fileOperation(action: () -> Unit) {
        if (ui.busy)return
        ui.busy = true
        scope.launch {
            try {
                withContext(Dispatchers.IO) { action() }
                refresh()
                ui.result(BioResult(true, AppText.text(TextKey.CopyDaLuuDuLieuD8a848)))
            } catch (_: Exception) {
                ui.result(BioResult(false, AppText.text(TextKey.CopyKhongTheXuLyTepDuLieuHienTaiGiuNguyenAa333d)))
            }
        }
    }
    val export =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/octet-stream")) { uri ->
            if (uri !=
                null
            ) {
                fileOperation { service.export(uri) }
            }
        }
    val diagnostics =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/plain")) { uri ->
            if (uri !=
                null
            ) {
                fileOperation { service.diagnostics(uri) }
            }
        }
    val import =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null && !ui.busy) {
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
                        ui.settingsRequests.backupImport =
                            BackupImportSummary(
                                tasks = backup.count("tasks").toString(),
                                trees = backup.count("trees").toString(),
                                occurrences = backup.count("occurrences").toString(),
                                created = created,
                            )
                        ui.show("confirm-import")
                        ui.busy = false
                    } catch (_: Exception) {
                        ui.result(BioResult(false, AppText.text(TextKey.CopyKhongTheXuLyTepDuLieuHienTaiGiuNguyenAa333d)))
                    }
                }
            }
        }
    val permission =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { allowed ->
            val result = if (allowed) NotificationPermissionResult.Granted else NotificationPermissionResult.Denied
            ui.message = result.message()
        }
    ui.execute = { command ->
        when (command.action) {
            "language" ->
                com.mamxanh.app.core.components.localization.AppLanguageController.select(
                    context,
                    command.values["tag"].orEmpty(),
                )
            "pin" -> homeSelection.save(command.values["id"].orEmpty())
            "export" -> export.launch("MamXanh-${System.currentTimeMillis()}.mamxanh")
            "diagnostics" -> diagnostics.launch("MamXanh-diagnostics.txt")
            "import" -> import.launch(arrayOf("application/octet-stream", "text/plain", "application/zip", "application/json"))
            "permission-allow" -> {
                val granted =
                    Build.VERSION.SDK_INT < 33 ||
                        context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
                val rationale =
                    Build.VERSION.SDK_INT >= 33 &&
                        (context as? android.app.Activity)?.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) ==
                        true
                if (ui.modal == "permission-settings") {
                    ui.modal = ""
                    context.startActivity(
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null)),
                    )
                } else if (granted || permissionPrefs.getBoolean("requested", false) && !rationale) {
                    ui.show("permission-settings")
                } else {
                    permissionPrefs.edit().putBoolean("requested", true).apply()
                    ui.modal = ""
                    if (Build.VERSION.SDK_INT >=
                        33
                    ) {
                        permission.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }
            "permission-settings" -> {
                ui.modal = ""
                context.startActivity(
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null)),
                )
            }
            else ->
                if (!ui.busy) {
                    ui.busy = true
                    scope.launch {
                        try {
                            val result =
                                withContext(Dispatchers.IO) {
                                    executeNative(
                                        storage,
                                        inventory,
                                        garden,
                                        treeRepository,
                                        shop,
                                        taskRepository,
                                        focusCommands,
                                        saveJournalNote,
                                        saveReminderSettings,
                                        onboarding,
                                        service,
                                        pending,
                                        command,
                                    )
                                }
                            refresh()
                            ui.result(result)
                        } catch (_: Exception) {
                            ui.result(BioResult(false, AppText.text(TextKey.CopyChuaTheCapNhatVuiLongThuLai42c8ac)))
                        }
                    }
                }
        }
    }
    BioApplication(world, ui, Modifier.fillMaxSize().safeDrawingPadding().imePadding(), koinInject<HabiTreeRouteRegistry>())
}
