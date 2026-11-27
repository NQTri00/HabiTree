package com.mamxanh.app

import android.Manifest
import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.KeyEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.compose.runtime.saveable.SaverScope
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import com.mamxanh.app.core.components.localization.AppLanguageController
import com.mamxanh.app.data.local.GardenDatabase
import com.mamxanh.app.feature.tasks.presentation.ui.form.TaskFormField
import com.mamxanh.app.presentation.bio.BioController
import com.mamxanh.shared.data.GameStorage
import com.mamxanh.shared.data.sql.GameBackupCodec
import com.mamxanh.shared.localization.AppLanguage
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.model.EngagementAction
import com.mamxanh.shared.model.TaskConfig
import com.mamxanh.shared.model.Tree
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import java.io.File
import java.util.Locale
import com.mamxanh.core.ui.R as CopyR

class NavigationRestorationTest {
    @Test
    fun seedPickerRestoresTheOriginatingPlotArgument() {
        val controller = BioController("garden")
        val scope =
            object : SaverScope {
                override fun canBeSaved(value: Any): Boolean = true
            }
        controller.openSeedPicker(5)

        val saver = BioController.Saver
        val saved = checkNotNull(with(saver) { scope.save(controller) })
        val restored = checkNotNull(saver.restore(saved))

        assertEquals("seed-picker", restored.modal)
        assertEquals(5, restored.preferredPlantingPlot)
    }

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context = instrumentation.targetContext

    @Test
    fun onboardingResumesAtTheSavedStepAndContinuesToHome() {
        check(context.packageName != "com.mamxanh.app")
        GardenDatabase(context).use { GameStorage(it).engagementSnapshot() }
        editGameFixture("INSERT OR REPLACE INTO meta(k,v) VALUES('onboarded','0')")
        val launch = Intent(context, MainActivity::class.java)
        val next = AppText.text(TextKey.CopyTiepTuc87a7ae)
        val seedStep = AppText.text(TextKey.CopyHatDauTien8b8f9a)
        val reasonStep = AppText.text(TextKey.CopyDieuBanMuonCham4bb700)
        val templateStep = AppText.text(TextKey.CopyChon25ViecE8b193)
        val reviewStep = AppText.text(TextKey.CopySanSangNayMam8ad6e8)
        val home = AppText.text(TextKey.CopyHomNayE6747a)
        ActivityScenario.launch<MainActivity>(launch).use { scenario ->
            awaitLabel(AppText.text(TextKey.CopyMotViecNhoMotMamXanh225e73))
            clickLabel(next)
            awaitLabel(seedStep)
            clickLabel(next)
            awaitLabel(reasonStep)
            scenario.recreate()
            awaitLabel(reasonStep)
            clickLabel(next)
            awaitLabel(templateStep)
            clickLabel(next)
            awaitLabel(reviewStep)
            clickLabel(AppText.text(TextKey.CopyVaoVuonB6df0e))
            awaitLabel(home)
            awaitLabelGone(reviewStep)
        }
        GardenDatabase(context).use { assertTrue(GameStorage(it).engagementSnapshot().onboarded) }
        ActivityScenario.launch<MainActivity>(launch).use {
            awaitLabel(home)
            awaitLabelGone(AppText.text(TextKey.CopyMotViecNhoMotMamXanh225e73))
        }
    }

    @Test
    fun homeTreePickerChangesTheHeroAndPersistsAcrossActivityLaunches() {
        check(context.packageName != "com.mamxanh.app")
        val trees = prepareSelectableTrees().map { AppText.plantName(it.speciesId, it.name) }
        val launch = Intent(context, MainActivity::class.java)
        val change = AppText.text(TextKey.CopyDoiCayFada9c)
        val picker = AppText.text(TextKey.CopyCayTrenTrangChu76c8b5)
        val selectedName =
            ActivityScenario.launch<MainActivity>(launch).use {
                clickLabel(change)
                awaitLabel(picker)
                val first = trees.first()
                revealLabel(first)
                clickLabel(first)
                awaitLabelGone(picker)
                awaitLabel(first)

                clickLabel(change)
                awaitLabel(picker)
                val second = trees[1]
                revealLabel(second)
                clickLabel(second)
                awaitLabelGone(picker)
                awaitLabel(second)
                second
            }
        ActivityScenario.launch<MainActivity>(launch).use {
            awaitLabel(change)
            awaitLabel(selectedName)
        }
    }

    @Test
    fun shopBuysAndEquipsTheAccessoryOnTheSelectedTreeAfterRecreation() {
        check(context.packageName != "com.mamxanh.app")
        val trees = prepareSelectableTrees()
        val sku = "tree_ribbon"
        val openingBalance = 1_000
        editGameFixture("UPDATE meta SET v=? WHERE k='coins'", arrayOf(openingBalance.toString()))
        val product = AppText.text(TextKey.CopyNoDiuDangE01645)
        val accessories = AppText.text(TextKey.CopyPhuKien0852d7)
        val chooseTree = AppText.text(TextKey.CopyDeoChoCayAfc821)
        val equip = AppText.text(TextKey.CopyMuaDeoD9c74d)
        val equipped = AppText.text(TextKey.CopyDangDung275ca2)
        val target =
            ActivityScenario.launch<MainActivity>(Intent(context, MainActivity::class.java)).use { scenario ->
                clickLabel(AppText.text(TextKey.CopyTiemF1ee00))
                clickLabel(accessories)
                revealLabel(product)
                clickLabel(product)
                revealLabel(chooseTree)
                val initiallyShown = awaitLabel(chooseTree, clickable = true)
                val selected =
                    trees.first { tree ->
                        val name = AppText.plantName(tree.speciesId, tree.name)
                        initiallyShown.findLabel(name) == null
                    }
                val name = AppText.plantName(selected.speciesId, selected.name)
                clickLabel(chooseTree)
                clickLabel(name)
                awaitLabel(chooseTree, clickable = true)
                awaitLabel(name)
                scenario.recreate()
                revealLabel(chooseTree)
                awaitLabel(name)
                revealLabel(equip)
                clickLabel(equip)
                awaitLabelGone(equip)
                revealLabel(product)
                awaitLabel(equipped)
                selected
            }
        GardenDatabase(context).use { database ->
            val stored = GameStorage(database).livingGarden()
            assertEquals(1, stored.owned[sku])
            assertEquals(target.id, stored.placements.single { it.sku == sku }.treeId)
            assertEquals(openingBalance - 150, stored.coins)
        }
    }

    @Test
    fun treeAccessoryShopKeepsTheOriginatingTreeInTheBannerAndPicker() {
        check(context.packageName != "com.mamxanh.app")
        val tree = prepareSelectableTrees().last()
        val treeName = AppText.plantName(tree.speciesId, tree.name)
        val intent = Intent(context, MainActivity::class.java).putExtra("engagement_task", -tree.id)
        val decoration = context.getString(CopyR.string.copy_trang_tri_d48231)
        val browse = AppText.text(TextKey.CopyTiemPhuKienE4d7d0)
        val backToTree = AppText.text(TextKey.CopyVeCayB033ce)
        val product = AppText.text(TextKey.CopyNoDiuDangE01645)
        val chooseTree = AppText.text(TextKey.CopyDeoChoCayAfc821)
        ActivityScenario.launch<MainActivity>(intent).use {
            revealLabel(decoration)
            clickLabel(decoration)
            revealLabel(browse)
            clickLabel(browse)
            awaitLabel(backToTree)
            awaitLabel(treeName)
            revealLabel(product)
            clickLabel(product)
            revealLabel(chooseTree)
            awaitLabel(chooseTree, clickable = true, descendant = treeName)
        }
    }

    private fun prepareSelectableTrees(): List<Tree> =
        GardenDatabase(context).use { database ->
            val storage = GameStorage(database)
            if (!storage.engagementSnapshot().onboarded) {
                assertTrue(
                    storage.engage(EngagementAction.Onboard("succulent", "", emptyList(), "picker-onboard", skip = true)).success,
                )
            }
            val initial = storage.garden()
            val livingNames =
                initial.trees
                    .filter { it.status.alive }
                    .map { AppText.plantName(it.speciesId, it.name) }
                    .toSet()
            val emptyPlots = initial.plots.filter { it.isUnlocked && it.treeId == null }.asReversed()
            emptyPlots.take((2 - livingNames.size).coerceAtLeast(0)).forEach { plot ->
                ensureFixtureSeed(storage)
                assertTrue(storage.plant("succulent", "Picker fixture tree ${plot.id}", plot.id))
            }
            storage
                .garden()
                .trees
                .filter { it.status.alive }
                .distinctBy { AppText.plantName(it.speciesId, it.name) }
                .also { check(it.size >= 2) { "Tree pickers need two distinguishable living trees" } }
        }

    private fun ensureFixtureSeed(storage: GameStorage) {
        if (storage.garden().inventory.getOrDefault("succulent", 0) == 0) {
            editGameFixture("INSERT OR REPLACE INTO inventory(item,quantity) VALUES('succulent',1)")
        }
    }

    private fun editGameFixture(
        sql: String,
        args: Array<String> = emptyArray(),
    ) {
        SQLiteDatabase
            .openDatabase(context.getDatabasePath("mam_xanh.db").path, null, SQLiteDatabase.OPEN_READWRITE)
            .use { database -> database.execSQL(sql, args) }
    }

    @Test
    fun reminderLinksOpenTheirEntityAndRecreationKeepsTheSelectedTab() {
        check(context.packageName != "com.mamxanh.app") { "Use the isolated verification app." }
        val entities =
            GardenDatabase(context).use { database ->
                val storage = GameStorage(database)
                if (!storage.engagementSnapshot().onboarded) {
                    assertTrue(storage.engage(EngagementAction.Onboard("succulent", "", emptyList(), "nav-onboard", skip = true)).success)
                }
                if (storage.tasks().none { it.title == TASK_TITLE }) {
                    assertTrue(
                        storage
                            .saveTask(
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
                    ensureFixtureSeed(storage)
                    assertTrue("Reminder fixture requires an empty unlocked plot", storage.plant("succulent", TREE_NAME))
                }
                storage.tasks().single { it.title == TASK_TITLE }.id to
                    storage
                        .garden()
                        .trees
                        .single { it.name == TREE_NAME }
                        .id
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

        ActivityScenario.launch<MainActivity>(reminderIntent(-entities.second)).use { scenario ->
            awaitLabel(AppText.text(TextKey.CopyCayCuaBan64f9d7))
            awaitLabel(TREE_NAME)
            val info = AppText.text(TextKey.CopyThongTin4aa16c)
            val species = AppText.text(TextKey.CopyHoSoLoaiCfff22)
            revealLabel(info)
            clickLabel(info)
            revealLabel(species)
            scenario.recreate()
            awaitLabel(AppText.text(TextKey.CopyCayCuaBan64f9d7))
            revealLabel(species)
        }
    }

    @Test
    fun timelineOpensTheTreeStoredInTheMemory() {
        check(context.packageName != "com.mamxanh.app")
        val (memoryTitle, treeName) =
            GardenDatabase(context).use { database ->
                val storage = GameStorage(database)
                if (!storage.engagementSnapshot().onboarded) {
                    assertTrue(
                        storage.engage(EngagementAction.Onboard("succulent", "", emptyList(), "timeline-onboard", skip = true)).success,
                    )
                }
                if (storage.garden().trees.isEmpty()) {
                    assertTrue(storage.plant("succulent", "Timeline first tree", plotId = 1))
                }
                val memory = storage.engagementSnapshot().memories.single { it.id == "first-tree" }
                AppText.history(memory.title) to
                    storage
                        .garden()
                        .trees
                        .single { it.id == memory.entity }
                        .name
            }
        ActivityScenario.launch<MainActivity>(Intent(context, MainActivity::class.java)).use {
            clickLabel(AppText.text(TextKey.CopyNhatKy1e6c3c))
            val timeline = AppText.text(TextKey.CopyKyNiemE3e113)
            revealLabel(timeline)
            clickLabel(timeline)
            awaitLabel(AppText.text(TextKey.CopyDieuMinhMuonNho665bc8))
            val label = AppText.text(TextKey.CopyXemLienQuan2167e0)
            val deadline = SystemClock.uptimeMillis() + 20_000
            var related: AccessibilityNodeInfo? = null
            while (related == null && SystemClock.uptimeMillis() < deadline) {
                val root = appRoot()
                related = root?.findLabel(memoryTitle)?.parent?.findLabel(label, clickable = true)
                if (related == null) {
                    root
                        ?.findAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
                        ?.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
                    SystemClock.sleep(250)
                }
            }
            assertTrue(
                checkNotNull(related) { "First-tree memory action was not visible" }.performAction(AccessibilityNodeInfo.ACTION_CLICK),
            )
            awaitLabel(AppText.text(TextKey.CopyCayCuaBan64f9d7))
            awaitLabel(treeName)
        }
    }

    @Test
    fun lockedPlotConfirmationRestoresAndCancelPreservesTheGarden() {
        check(context.packageName != "com.mamxanh.app")
        val before =
            GardenDatabase(context).use { database ->
                val storage = GameStorage(database)
                if (!storage.engagementSnapshot().onboarded) {
                    assertTrue(
                        storage.engage(EngagementAction.Onboard("succulent", "", emptyList(), "locked-plot-onboard", skip = true)).success,
                    )
                }
                storage.garden()
            }
        val lockedPlot = before.plots.first { !it.isUnlocked }
        val addPlot = AppText.text(TextKey.CopyThemOTrong9979ac)
        val confirmation = AppText.text(TextKey.CopyMoO001668e, lockedPlot.id.toString())
        ActivityScenario.launch<MainActivity>(Intent(context, MainActivity::class.java)).use { scenario ->
            clickLabel(AppText.text(TextKey.CopyVuon9e4c10))
            awaitLabel(AppText.text(TextKey.CopyVuonCuaBanAd1c1b))
            revealLabel(addPlot)
            clickLabel(addPlot)
            awaitLabel(confirmation)
            scenario.recreate()
            awaitLabel(confirmation)
            clickLabel(AppText.text(TextKey.CopyHuy74fcd3))
            awaitLabelGone(confirmation)
        }
        GardenDatabase(context).use { database ->
            val after = GameStorage(database).garden()
            assertEquals(before.plots, after.plots)
            assertEquals(before.coins, after.coins)
        }
    }

    @Test
    fun restoresDiscardConfirmationAndEntityStack() {
        val controller = BioController()
        controller.go("tree", "14")
        controller.go("tree", "31")
        controller.go("task-new", "2")
        controller.preview.taskDraft.update(TaskFormField.Title, "Draft title")
        controller.back()

        val saver = BioController.Saver
        val scope =
            object : SaverScope {
                override fun canBeSaved(value: Any): Boolean = true
            }
        val serialized = checkNotNull(with(saver) { scope.save(controller) })
        val restored = checkNotNull(saver.restore(serialized))
        assertEquals(listOf("", "14", "31", "2"), restored.backStack.map { it.argument })
        assertEquals("discard", restored.modal)
        assertEquals("task-new", restored.route)
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
    fun taskDraftSurvivesActivityRecreationAndCanBeDiscarded() {
        check(context.packageName != "com.mamxanh.app") { "Use the isolated verification app." }
        GardenDatabase(context).use { database ->
            val storage = GameStorage(database)
            if (!storage.engagementSnapshot().onboarded) {
                val request = EngagementAction.Onboard("succulent", "", emptyList(), "task-draft-onboard", skip = true)
                assertTrue(storage.engage(request).success)
            }
        }

        val addTask = AppText.text(TextKey.CopyThemViecC371c2)
        val titleLabel = AppText.text(TextKey.CopyTenViec672c8c)
        val draft = "Recreated task draft"
        ActivityScenario.launch<MainActivity>(Intent(context, MainActivity::class.java)).use { scenario ->
            clickLabel(AppText.text(TextKey.CopyViec818768))
            clickLabel(addTask)
            awaitLabel(titleLabel)
            val saveTask = AppText.text(TextKey.CopyLuuViec522a3d)
            revealLabel(saveTask)
            clickLabel(saveTask)
            awaitLabel(AppText.text(TextKey.CopyNhapTenViec1100KyTu712679))
            val setText = AccessibilityNodeInfo.ACTION_SET_TEXT
            val editor = checkNotNull(appRoot()?.findAction(setText))
            assertTrue(
                editor.performAction(
                    setText,
                    Bundle().apply {
                        putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, draft)
                    },
                ),
            )
            awaitLabel(draft)

            scenario.recreate()
            awaitLabel(draft)
            scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
            awaitLabel(AppText.text(TextKey.CopyBoThayDoiB07820))
            clickLabel(AppText.text(TextKey.CopyBoThayDoiE01800))
            awaitLabel(addTask, clickable = true)

            clickLabel(addTask)
            awaitLabel(titleLabel)
            awaitLabelGone(draft)
        }
    }

    @Test
    fun archiveConfirmationPersistsTheTaskLifecycle() {
        check(context.packageName != "com.mamxanh.app")
        val title = "MVI archive test task"
        val taskId =
            GardenDatabase(context).use { database ->
                val storage = GameStorage(database)
                if (!storage.engagementSnapshot().onboarded) {
                    assertTrue(
                        storage.engage(EngagementAction.Onboard("succulent", "", emptyList(), "archive-onboard", skip = true)).success,
                    )
                }
                assertTrue(storage.saveTask(null, TaskConfig(title = title)).success)
                storage.tasks().first { it.title == title }.id
            }
        val archive = AppText.text(TextKey.ActionArchive)
        val confirmation = AppText.text(TextKey.CopyLuuTruViec50123f)
        ActivityScenario.launch<MainActivity>(Intent(context, MainActivity::class.java)).use { scenario ->
            clickLabel(AppText.text(TextKey.CopyViec818768))
            revealLabel(title)
            clickLabel(title)
            awaitLabel(AppText.text(TextKey.CopyLichLichSu7153b3))
            awaitLabel(title)
            revealLabel(archive)
            clickLabel(archive)
            awaitLabel(confirmation)
            val node = checkNotNull(appRoot()?.findLabel(archive, clickable = true, last = true))
            assertTrue(node.performAction(AccessibilityNodeInfo.ACTION_CLICK))
            awaitLabelGone(confirmation)
            GardenDatabase(context).use { database ->
                assertEquals("ARCHIVED", GameStorage(database).tasks().single { it.id == taskId }.lifecycle)
            }
            scenario.recreate()
            val archived = AppText.text(TextKey.CopyLuuTru7e7c5f)
            revealLabel(archived, AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD)
            clickLabel(archived)
            revealLabel(title)
        }
    }

    @Test
    fun focusPresetUpdatesTheTimerAndSurvivesActivityRecreation() {
        check(context.packageName != "com.mamxanh.app") { "Use the isolated verification app." }
        GardenDatabase(context).use { database ->
            val storage = GameStorage(database)
            if (!storage.engagementSnapshot().onboarded) {
                assertTrue(
                    storage
                        .engage(
                            EngagementAction.Onboard("succulent", "", emptyList(), "focus-preset-onboard", skip = true),
                        ).success,
                )
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

            val durationField = AppText.text(TextKey.CopyPhutTapTrung5120Ca7d24)

            fun setDuration(
                before: String,
                minutes: String,
            ) {
                revealLabel(durationField, clickable = false)
                val setText = AccessibilityNodeInfo.ACTION_SET_TEXT
                val editor = checkNotNull(appRoot()?.findAction(setText))
                assertTrue(editor.isEditable)
                assertEquals(before, editor.text?.toString())
                assertTrue(
                    editor.performAction(
                        setText,
                        Bundle().apply { putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, minutes) },
                    ),
                )
            }

            setDuration(before = "15", minutes = "2")
            val rangeError = AppText.text(TextKey.CopyNhapSoPhutTu51202b514d)
            revealLabel(rangeError, clickable = false)
            revealLabel("15:00", AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD, clickable = false)
            scenario.recreate()
            awaitLabel("15:00")
            revealLabel(rangeError, clickable = false)
            setDuration(before = "2", minutes = "15")
            val start = AppText.text(TextKey.CopyBatDauB3cacc)
            val end = AppText.text(TextKey.CopyKetThuc351849)
            val confirmation = AppText.text(TextKey.CopyKetThucPhien7ea116)
            revealLabel(start)
            clickLabel(start)
            revealLabel(end)
            clickLabel(end)
            awaitLabel(confirmation)
            clickLabel(AppText.text(TextKey.CopyHuy74fcd3))
            awaitLabelGone(confirmation)
            revealLabel(end)
            clickLabel(end)
            awaitLabel(confirmation)
            val confirm = checkNotNull(appRoot()?.findLabel(end, clickable = true, last = true))
            assertTrue(confirm.performAction(AccessibilityNodeInfo.ACTION_CLICK))
            awaitLabelGone(confirmation)
            revealLabel(start)
        }
    }

    @Test
    fun languageSelectionRefreshesCatalogNamesAndSurvivesRecreation() {
        check(context.packageName != "com.mamxanh.app") { "Use the isolated verification app." }
        GardenDatabase(context).use { database ->
            val storage = GameStorage(database)
            if (!storage.engagementSnapshot().onboarded) {
                assertTrue(
                    storage
                        .engage(
                            EngagementAction.Onboard("succulent", "", emptyList(), "locale-onboard", skip = true),
                        ).success,
                )
            }
        }

        val original = AppLanguageController.current(context)
        val target = AppLanguage.entries.single { it != original }
        val configuration =
            Configuration(context.resources.configuration).apply {
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
                assertTrue(
                    storage
                        .engage(
                            EngagementAction.Onboard("succulent", "", emptyList(), "permission-onboard", skip = true),
                        ).success,
                )
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
                assertTrue(
                    storage
                        .engage(
                            EngagementAction.Onboard("succulent", "", emptyList(), "clear-onboard", skip = true),
                        ).success,
                )
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

    private fun reminderIntent(entity: Long) = Intent(context, MainActivity::class.java).putExtra("engagement_task", entity)

    @Test
    fun encyclopediaKeepsTheCollectedSpeciesTreeAfterRecreation() {
        check(context.packageName != "com.mamxanh.app")
        val tree = prepareSelectableTrees().first()
        val species = AppText.plantName(tree.speciesId, "")
        val treeName = AppText.plantName(tree.speciesId, tree.name)
        ActivityScenario.launch<MainActivity>(Intent(context, MainActivity::class.java)).use { scenario ->
            clickLabel(AppText.text(TextKey.CopyVuon9e4c10))
            val encyclopedia = AppText.text(TextKey.CopyBachThaoF2ed64)
            revealLabel(encyclopedia)
            clickLabel(encyclopedia)
            revealLabel(species)
            clickLabel(species)
            revealLabel(treeName)
            scenario.recreate()
            revealLabel(treeName)
            clickLabel(treeName)
            awaitLabel(AppText.text(TextKey.CopyCayCuaBan64f9d7))
            awaitLabel(treeName)
        }
    }

    @Test
    fun backupImportRejectsInvalidBytesAndRestoresAConfirmedSnapshot() {
        check(context.packageName != "com.mamxanh.app")
        prepareSelectableTrees()
        val (balance, bytes) =
            GardenDatabase(context).use { database ->
                val storage = GameStorage(database)
                storage.garden().coins to GameBackupCodec.encode(storage.backup("verification", System.currentTimeMillis()))
            }
        val invalid = File.createTempFile("invalid-backup", ".mamxanh", context.cacheDir)
        val valid = File.createTempFile("valid-backup", ".mamxanh", context.cacheDir)
        try {
            invalid.writeText("Invalid backup fixture")
            valid.writeBytes(bytes)
            val import = AppText.text(TextKey.CopyKhoiPhucC652d9)
            val confirmation = AppText.text(TextKey.CopyKhoiPhucDuLieuFeaa4a)
            val changedBalance = balance + 71
            editGameFixture("UPDATE meta SET v=? WHERE k='coins'", arrayOf(changedBalance.toString()))
            ActivityScenario.launch<MainActivity>(Intent(context, MainActivity::class.java)).use { scenario ->
                var versionLabel = ""
                scenario.onActivity { versionLabel = it.getString(CopyR.string.backup_version) }
                clickLabel(AppText.text(TextKey.CopyNhatKy1e6c3c))
                clickLabel(AppText.text(TextKey.CopyTuyChon598c1b))
                val data = AppText.text(TextKey.CopyDuLieuUngDung9843b7)
                revealLabel(data)
                clickLabel(data)
                withBackupPickerResult(invalid) {
                    clickLabel(import)
                    awaitLabel(AppText.text(TextKey.CopyKhongTheXuLyTepDuLieuHienTaiGiuNguyenAa333d))
                }
                GardenDatabase(context).use { assertEquals(changedBalance, GameStorage(it).garden().coins) }
                withBackupPickerResult(valid) {
                    clickLabel(import)
                    awaitLabel(confirmation)
                    awaitLabel(versionLabel)
                }
                GardenDatabase(context).use { assertEquals(changedBalance, GameStorage(it).garden().coins) }
                clickLabel(AppText.text(TextKey.CopyThayTheE4d19c))
                awaitLabel(AppText.text(TextKey.CopyDaKhoiPhucDuLieuB0b459))
                awaitLabelGone(confirmation)
            }
            GardenDatabase(context).use { assertEquals(balance, GameStorage(it).garden().coins) }
        } finally {
            invalid.delete()
            valid.delete()
        }
    }

    private fun withBackupPickerResult(
        file: File,
        action: () -> Unit,
    ) {
        val filter =
            IntentFilter(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                addDataType("*/*")
            }
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, Intent().setData(Uri.fromFile(file)))
        val monitor = instrumentation.addMonitor(filter, result, true)
        try {
            action()
            assertEquals(1, monitor.hits)
        } finally {
            instrumentation.removeMonitor(monitor)
        }
    }

    private fun awaitLabel(
        label: String,
        clickable: Boolean = false,
        descendant: String? = null,
    ): AccessibilityNodeInfo {
        val deadline = SystemClock.uptimeMillis() + 20_000
        while (SystemClock.uptimeMillis() < deadline) {
            val node = appRoot()?.findLabel(label, clickable)
            if (node != null && (descendant == null || node.findLabel(descendant) != null)) return node
            SystemClock.sleep(100)
        }
        error("Timed out waiting for Android accessibility label: $label ${descendant.orEmpty()}")
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

    private fun appRoot(): AccessibilityNodeInfo? =
        instrumentation.uiAutomation.rootInActiveWindow
            ?.takeIf { it.packageName?.toString() == context.packageName }

    private fun clickLabel(label: String) {
        repeat(3) {
            val target = awaitLabel(label, clickable = true)
            if (target.performAction(AccessibilityNodeInfo.ACTION_CLICK)) return
            instrumentation.waitForIdleSync()
            SystemClock.sleep(100)
        }
        error("Android rejected click for $label after refreshing the accessibility node")
    }

    private fun clickSystemPermissionButton(id: String) {
        val permissionControllers = setOf("com.android.permissioncontroller", "com.google.android.permissioncontroller")
        val automation = instrumentation.uiAutomation
        automation.serviceInfo =
            automation.serviceInfo.apply {
                flags = flags or android.accessibilityservice.AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
            }
        val deadline = SystemClock.uptimeMillis() + 20_000
        while (SystemClock.uptimeMillis() < deadline) {
            val root = automation.rootInActiveWindow
            val controller = root?.packageName?.toString()
            if (root != null && controller != null && controller in permissionControllers) {
                val button =
                    root
                        .findAccessibilityNodeInfosByViewId("$controller:id/$id")
                        .firstOrNull { it.isVisibleToUser && it.isEnabled }
                if (button?.performAction(AccessibilityNodeInfo.ACTION_CLICK) == true) return
            }
            SystemClock.sleep(100)
        }
        error("Timed out waiting for system permission button: $id")
    }

    private fun revealLabel(
        label: String,
        action: Int = AccessibilityNodeInfo.ACTION_SCROLL_FORWARD,
        clickable: Boolean = true,
    ) {
        val deadline = SystemClock.uptimeMillis() + 20_000
        while (SystemClock.uptimeMillis() < deadline) {
            val root = appRoot()
            if (root?.findLabel(label, clickable = clickable) != null) return
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

    private fun AccessibilityNodeInfo.findLabel(
        label: String,
        clickable: Boolean = false,
        last: Boolean = false,
    ): AccessibilityNodeInfo? {
        if (isVisibleToUser && (text?.toString() == label || contentDescription?.toString() == label)) {
            if (!clickable) return this
            var target: AccessibilityNodeInfo? = this
            while (target != null && !target.isClickable) target = target.parent
            if (target != null && target.isEnabled) return target
        }
        val children = 0 until childCount
        for (index in if (last) children.reversed() else children) {
            getChild(index)?.findLabel(label, clickable, last)?.let { return it }
        }
        return null
    }

    private companion object {
        const val TASK_TITLE = "Navigation reminder task"
        const val TREE_NAME = "Navigation reminder tree"
    }
}
