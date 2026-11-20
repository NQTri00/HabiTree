package com.mamxanh.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.SystemClock
import android.view.KeyEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.compose.runtime.saveable.SaverScope
import com.mamxanh.app.feature.tasks.presentation.ui.form.TaskFormField
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import com.mamxanh.app.data.local.GardenDatabase
import com.mamxanh.app.presentation.bio.BioController
import com.mamxanh.app.core.components.localization.AppLanguageController
import com.mamxanh.core.ui.R as CopyR
import com.mamxanh.shared.data.GameStorage
import com.mamxanh.shared.localization.AppLanguage
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.model.EngagementAction
import com.mamxanh.shared.model.TaskConfig
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test

class NavigationRestorationTest {
    @Test
    fun savedPlantingSelectionRestoresTheChosenPlot() {
        val controller = BioController("garden")
        val scope = object : SaverScope {
            override fun canBeSaved(value: Any): Boolean = true
        }
        assertEquals("2", controller.plantingSelection.initialDialogPlot(listOf(2, 5)))
        controller.act("bio-seed-picker:5")

        val saver = BioController.Saver
        val saved = checkNotNull(with(saver) { scope.save(controller) })
        val restored = checkNotNull(saver.restore(saved))

        assertEquals("seed-picker", restored.modal)
        assertEquals("5", restored.plantingSelection.initialDialogPlot(listOf(2, 5)))
        restored.plantingSelection.clear()
        assertEquals("2", restored.plantingSelection.initialDialogPlot(listOf(2, 5)))
    }

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context = instrumentation.targetContext

    @Test
    fun reminderLinksOpenTheirEntityAndRecreationKeepsTheSelectedTab() {
        check(context.packageName != "com.mamxanh.app") { "Use the isolated verification app." }
        val entities = GardenDatabase(context).use { database ->
            val storage = GameStorage(database)
            if (!storage.engagementSnapshot().onboarded) {
                assertTrue(storage.engage(EngagementAction.Onboard("succulent", "", emptyList(), "nav-onboard", skip = true)).success)
            }
            if (storage.tasks().none { it.title == TASK_TITLE }) {
                assertTrue(
                    storage.saveTask(
                        null,
                        TaskConfig(
                            title = TASK_TITLE,
                            kind = "DAILY",
                            category = AppText.canonical(TextKey.CopyHocTapA51516),
                            frequency = "EVERY_DAY",
                            verification = "CLICK",
                        ),
                    ).success,
                )
            }
            if (storage.garden().trees.none { it.name == TREE_NAME }) {
                assertTrue(storage.plant("succulent", TREE_NAME, plotId = 1))
            }
            storage.tasks().single { it.title == TASK_TITLE }.id to
                storage.garden().trees.single { it.name == TREE_NAME }.id
        }

        ActivityScenario.launch<MainActivity>(reminderIntent(entities.first)).use { scenario ->
            awaitLabel(AppText.text(TextKey.CopyNhipThoiQuen1b8324))
            awaitLabel(TASK_TITLE)
            clickLabel(AppText.text(TextKey.CopyVuon9e4c10))
            val garden = AppText.text(TextKey.CopyVuonCuaBanAd1c1b)
            awaitLabel(garden)
            scenario.recreate()
            awaitLabel(garden)

            clickLabel(AppText.text(TextKey.CopyGieoCay31adde))
            val picker = AppText.text(TextKey.CopyGieoMotMamMoi2a80ed)
            awaitLabel(picker)
            instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)
            awaitLabelGone(picker)
            awaitLabel(garden)
        }

        ActivityScenario.launch<MainActivity>(reminderIntent(-entities.second)).use {
            awaitLabel(AppText.text(TextKey.CopyCayCuaBan64f9d7))
            awaitLabel(TREE_NAME)
        }
    }

    @Test
    fun restoresDraftPopupAndEntityStack() {
        val controller = BioController()
        controller.go("tree", "14")
        controller.go("tree", "31")
        controller.go("task-new", "2")
        controller.taskDraft.update(TaskFormField.Title, "Draft title")
        controller.taskSelection.filter = "paused"
        controller.taskSelection.focusTask = "2"
        controller.taskSelection.focusMinutes = "30"
        controller.taskSelection.toggleTemplate("study25")
        controller.back()

        val saver = BioController.Saver
        val scope = object : SaverScope {
            override fun canBeSaved(value: Any): Boolean = true
        }
        val serialized = checkNotNull(with(saver) { scope.save(controller) })
        val restored = checkNotNull(saver.restore(serialized))
        assertEquals(listOf("", "14", "31", "2"), restored.backStack.map { it.argument })
        assertEquals("Draft title", restored.taskDraft[TaskFormField.Title])
        assertTrue(restored.taskDraft.isDirty)
        assertEquals("paused", restored.taskSelection.filter)
        assertEquals("2", restored.taskSelection.focusTask)
        assertEquals("30", restored.taskSelection.focusMinutes)
        assertEquals(listOf("study25"), restored.taskSelection.selectedTemplates())
        assertEquals("discard", restored.modal)
        restored.back()
        assertEquals("", restored.modal)
        assertEquals("task-new", restored.route)
        restored.back()
        restored.discard()
        assertEquals("tree", restored.route)
        assertEquals("31", restored.param)
    }

    @Test
    fun reminderSettingPersistsAfterActivityRecreation() {
        check(context.packageName != "com.mamxanh.app") { "Use the isolated verification app." }
        GardenDatabase(context).use { database ->
            val storage = GameStorage(database)
            if (!storage.engagementSnapshot().onboarded) {
                assertTrue(storage.engage(EngagementAction.Onboard("succulent", "", emptyList(), "settings-onboard", skip = true)).success)
            }
            val previous = storage.engagementSnapshot().settings
            try {
                ActivityScenario.launch<MainActivity>(Intent(context, MainActivity::class.java)).use { scenario ->
                    clickLabel(AppText.text(TextKey.CopyNhatKy1e6c3c))
                    clickLabel(AppText.text(TextKey.CopyTuyChon598c1b))
                    val option = AppText.text(TextKey.CopyGoiYNghiSau25Phut6e7319)
                    clickLabel(option)
                    val save = AppText.text(TextKey.CopyLuuTuyChon3847ca)
                    revealLabel(save)
                    clickLabel(save)
                    awaitLabel(AppText.text(TextKey.CopyDaLuuTuyChon6ceb29))

                    scenario.recreate()
                    awaitLabel(AppText.text(TextKey.CopyTuyChon598c1b))
                    assertEquals(previous.copy(breakPrompts = !previous.breakPrompts), storage.engagementSnapshot().settings)
                }
            } finally {
                storage.engage(EngagementAction.Settings(previous))
            }
        }
    }

    @Test
    fun focusPresetUpdatesTheTimerAndSurvivesActivityRecreation() {
        check(context.packageName != "com.mamxanh.app") { "Use the isolated verification app." }
        GardenDatabase(context).use { database ->
            val storage = GameStorage(database)
            if (!storage.engagementSnapshot().onboarded) {
                assertTrue(storage.engage(
                    EngagementAction.Onboard("succulent", "", emptyList(), "focus-preset-onboard", skip = true),
                ).success)
            }
        }

        val intent = Intent(context, MainActivity::class.java)
        ActivityScenario.launch<MainActivity>(intent).use { scenario ->
            clickLabel(AppText.text(TextKey.CopyViec818768))
            clickLabel(AppText.text(TextKey.CopyTapTrung964002))
            clickLabel(AppText.text(TextKey.Copy0Phut2596d2, 15))
            awaitLabel("15:00")

            scenario.recreate()
            awaitLabel("15:00")
            awaitLabel(AppText.text(TextKey.CopyBatDauB3cacc))
        }
    }

    @Test
    fun languageSelectionRefreshesCatalogNamesAndSurvivesRecreation() {
        check(context.packageName != "com.mamxanh.app") { "Use the isolated verification app." }
        GardenDatabase(context).use { database ->
            val storage = GameStorage(database)
            if (!storage.engagementSnapshot().onboarded) {
                assertTrue(storage.engage(
                    EngagementAction.Onboard("succulent", "", emptyList(), "locale-onboard", skip = true),
                ).success)
            }
        }

        val original = AppLanguageController.current(context)
        val target = AppLanguage.entries.single { it != original }
        val configuration = Configuration(context.resources.configuration).apply {
            setLocale(Locale.forLanguageTag(original.tag))
        }
        val originalResources = context.createConfigurationContext(configuration).resources
        val option = if (target == AppLanguage.English) CopyR.string.app_language_en else CopyR.string.app_language_vi
        val settingsTitle = AppText.forLanguage(TextKey.CopyTheoNhipCuaBan8e2e15, target)
        val plantName = AppText.forLanguage(TextKey.CopySenDaNgocAba46f, target)
        try {
            ActivityScenario.launch<MainActivity>(Intent(context, MainActivity::class.java)).use { scenario ->
                clickLabel(AppText.forLanguage(TextKey.CopyNhatKy1e6c3c, original))
                clickLabel(AppText.forLanguage(TextKey.CopyTuyChon598c1b, original))
                clickLabel(originalResources.getString(CopyR.string.app_language_title))
                clickLabel(originalResources.getString(option))
                awaitLabel(settingsTitle)
                scenario.recreate()
                awaitLabel(settingsTitle)
                assertEquals(target, AppLanguageController.current(context))

                clickLabel(AppText.forLanguage(TextKey.CopyTiemF1ee00, target))
                awaitLabel(plantName)
                awaitLabel("HabiTree")
            }
        } finally {
            instrumentation.runOnMainSync { AppLanguageController.select(context, original.tag) }
        }
    }

    @Test
    fun requestingNotificationsReportsDenialAndThenGrant() {
        assumeTrue(Build.VERSION.SDK_INT >= 33)
        check(context.packageName != "com.mamxanh.app") { "Use the isolated verification app." }
        val permission = Manifest.permission.POST_NOTIFICATIONS
        assertEquals(PackageManager.PERMISSION_DENIED, context.checkSelfPermission(permission))
        GardenDatabase(context).use { database ->
            val storage = GameStorage(database)
            if (!storage.engagementSnapshot().onboarded) {
                assertTrue(storage.engage(
                    EngagementAction.Onboard("succulent", "", emptyList(), "permission-onboard", skip = true),
                ).success)
            }
        }
        ActivityScenario.launch<MainActivity>(Intent(context, MainActivity::class.java)).use {
            clickLabel(AppText.text(TextKey.CopyNhatKy1e6c3c))
            clickLabel(AppText.text(TextKey.CopyTuyChon598c1b))
            val request = AppText.text(TextKey.CopyQuyenThongBao58e97d)
            val proceed = AppText.text(TextKey.CopyTiepTuc87a7ae)
            revealLabel(request)
            clickLabel(request)
            clickLabel(proceed)
            clickSystemPermissionButton("permission_deny_button")
            awaitLabel(AppText.text(TextKey.CopyThongBaoTatViecVaBoDemVanDungDuoc70a974))
            assertEquals(PackageManager.PERMISSION_DENIED, context.checkSelfPermission(permission))

            revealLabel(request)
            clickLabel(request)
            clickLabel(proceed)
            clickSystemPermissionButton("permission_allow_button")
            awaitLabel(AppText.text(TextKey.CopyDaBatThongBao5f71c6))
            assertEquals(PackageManager.PERMISSION_GRANTED, context.checkSelfPermission(permission))
        }
    }

    @Test
    fun cancellingFinalClearConfirmationPreservesSavedData() {
        check(context.packageName != "com.mamxanh.app") { "Use the isolated verification app." }
        GardenDatabase(context).use { database ->
            val storage = GameStorage(database)
            if (!storage.engagementSnapshot().onboarded) {
                assertTrue(storage.engage(
                    EngagementAction.Onboard("succulent", "", emptyList(), "clear-onboard", skip = true),
                ).success)
            }
            val taskIds = storage.tasks().map { it.id }
            val treeIds = storage.garden().trees.map { it.id }

            ActivityScenario.launch<MainActivity>(Intent(context, MainActivity::class.java)).use {
                clickLabel(AppText.text(TextKey.CopyNhatKy1e6c3c))
                clickLabel(AppText.text(TextKey.CopyTuyChon598c1b))
                val dataLink = AppText.text(TextKey.CopyDuLieuUngDung9843b7)
                revealLabel(dataLink)
                clickLabel(dataLink)
                clickLabel(AppText.text(TextKey.CopyXoaDuLieu398085))
                awaitLabel(AppText.text(TextKey.CopyBanCoTheXuatBanSaoTruocKhiTiepTuc60f8b1))
                clickLabel(AppText.text(TextKey.CopyTiepTuc87a7ae))
                val finalConfirmation = AppText.text(TextKey.CopyXacNhanLanCuoi8421a1)
                awaitLabel(finalConfirmation)
                clickLabel(AppText.text(TextKey.CopyHuy74fcd3))
                awaitLabelGone(finalConfirmation)
            }

            assertTrue(storage.engagementSnapshot().onboarded)
            assertEquals(taskIds, storage.tasks().map { it.id })
            assertEquals(treeIds, storage.garden().trees.map { it.id })
        }
    }

    private fun reminderIntent(entity: Long) =
        Intent(context, MainActivity::class.java).putExtra("engagement_task", entity)

    private fun awaitLabel(label: String, clickable: Boolean = false): AccessibilityNodeInfo {
        val deadline = SystemClock.uptimeMillis() + 20_000
        while (SystemClock.uptimeMillis() < deadline) {
            appRoot()?.findLabel(label, clickable)?.let { return it }
            SystemClock.sleep(100)
        }
        error("Timed out waiting for Android accessibility label: $label")
    }

    private fun awaitLabelGone(label: String) {
        val deadline = SystemClock.uptimeMillis() + 20_000
        while (SystemClock.uptimeMillis() < deadline) {
            val root = appRoot()
            if (root != null && root.findLabel(label) == null) return
            SystemClock.sleep(100)
        }
        error("Android accessibility label remained visible: $label")
    }

    private fun appRoot(): AccessibilityNodeInfo? = instrumentation.uiAutomation.rootInActiveWindow
        ?.takeIf { it.packageName?.toString() == context.packageName }

    private fun clickLabel(label: String) {
        val target = awaitLabel(label, clickable = true)
        assertTrue("Android rejected click for $label", target.performAction(AccessibilityNodeInfo.ACTION_CLICK))
    }

    private fun clickSystemPermissionButton(id: String) {
        val permissionControllers = setOf("com.android.permissioncontroller", "com.google.android.permissioncontroller")
        val automation = instrumentation.uiAutomation
        automation.serviceInfo = automation.serviceInfo.apply {
            flags = flags or android.accessibilityservice.AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
        }
        val deadline = SystemClock.uptimeMillis() + 20_000
        while (SystemClock.uptimeMillis() < deadline) {
            val root = automation.rootInActiveWindow
            val controller = root?.packageName?.toString()
            if (root != null && controller != null && controller in permissionControllers) {
                val button = root.findAccessibilityNodeInfosByViewId("$controller:id/$id")
                    .firstOrNull { it.isVisibleToUser && it.isEnabled }
                if (button?.performAction(AccessibilityNodeInfo.ACTION_CLICK) == true) return
            }
            SystemClock.sleep(100)
        }
        error("Timed out waiting for system permission button: $id")
    }

    private fun revealLabel(label: String) {
        val action = AccessibilityNodeInfo.ACTION_SCROLL_FORWARD
        val deadline = SystemClock.uptimeMillis() + 20_000
        while (SystemClock.uptimeMillis() < deadline) {
            val root = appRoot()
            if (root?.findLabel(label, clickable = true) != null) return
            if (root?.findAction(action)?.performAction(action) == true) {
                instrumentation.waitForIdleSync()
            }
            SystemClock.sleep(250)
        }
        error("Timed out scrolling to Android accessibility label: $label")
    }

    private fun AccessibilityNodeInfo.findAction(action: Int): AccessibilityNodeInfo? {
        if (actionList.any { it.id == action }) return this
        for (index in 0 until childCount) {
            getChild(index)?.findAction(action)?.let { return it }
        }
        return null
    }

    private fun AccessibilityNodeInfo.findLabel(label: String, clickable: Boolean = false): AccessibilityNodeInfo? {
        if (isVisibleToUser && (text?.toString() == label || contentDescription?.toString() == label)) {
            if (!clickable) return this
            var target: AccessibilityNodeInfo? = this
            while (target != null && !target.isClickable) target = target.parent
            if (target != null && target.isEnabled) return target
        }
        for (index in 0 until childCount) {
            getChild(index)?.findLabel(label, clickable)?.let { return it }
        }
        return null
    }

    private companion object {
        const val TASK_TITLE = "Navigation reminder task"
        const val TREE_NAME = "Navigation reminder tree"
    }
}
