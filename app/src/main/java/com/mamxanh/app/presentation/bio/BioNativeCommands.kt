package com.mamxanh.app.presentation.bio

import com.mamxanh.app.core.domain.RepositoryResult
import com.mamxanh.app.data.BackupService
import com.mamxanh.app.feature.garden.domain.usecase.GardenRepository
import com.mamxanh.app.feature.garden.domain.usecase.eligiblePlantingPlot
import com.mamxanh.app.feature.garden.domain.usecase.plantedTreeId
import com.mamxanh.app.feature.home.domain.repository.OnboardingRepository
import com.mamxanh.app.feature.home.domain.repository.OnboardingRequest
import com.mamxanh.app.feature.inventory.domain.repository.InventoryRepository
import com.mamxanh.app.feature.settings.domain.model.ReminderSettings
import com.mamxanh.app.feature.settings.domain.model.TaskReminderSettings
import com.mamxanh.app.feature.settings.domain.usecase.SaveReminderSettingsUseCase
import com.mamxanh.app.feature.shop.domain.repository.ShopRepository
import com.mamxanh.app.feature.stats.domain.usecase.JournalNote
import com.mamxanh.app.feature.stats.domain.usecase.SaveJournalNoteUseCase
import com.mamxanh.app.feature.tasks.domain.repository.FocusCommandResult
import com.mamxanh.app.feature.tasks.domain.repository.FocusSessionCommands
import com.mamxanh.app.feature.tasks.domain.repository.TaskOperationResult
import com.mamxanh.app.feature.tasks.domain.repository.TaskRepository
import com.mamxanh.app.feature.tasks.domain.usecase.taskConfigFromDraft
import com.mamxanh.app.feature.tree.domain.repository.TreeCareReceipt
import com.mamxanh.app.feature.tree.domain.repository.TreeRepository
import com.mamxanh.app.feature.tree.domain.repository.WateringOutcome
import com.mamxanh.shared.data.GameStorage
import com.mamxanh.shared.data.sql.GameBackup
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.model.ActionResult
import com.mamxanh.shared.model.EngagementAction
import java.util.UUID

private fun clock(value: String): Int {
    if (value.isBlank())return -1
    val m =
        Regex("^(\\d{1,2}):(\\d{2})$").matchEntire(value) ?: return -2
    val h = m.groupValues[1].toInt()
    val n = m.groupValues[2].toInt()
    return if (h in
        0..23 &&
        n in 0..59
    ) {
        h * 60 + n
    } else {
        -2
    }
}

internal fun executeNative(
    storage: GameStorage,
    inventory: InventoryRepository,
    garden: GardenRepository,
    treeRepository: TreeRepository,
    shop: ShopRepository,
    taskRepository: TaskRepository,
    focusCommands: FocusSessionCommands,
    saveJournalNote: SaveJournalNoteUseCase,
    saveReminderSettings: SaveReminderSettingsUseCase,
    onboarding: OnboardingRepository,
    backup: BackupService,
    pending: GameBackup?,
    command: BioCommand,
): BioResult {
    val parts = command.action.split(':')
    val action = parts[0]
    val id = parts.getOrElse(1) { command.values["id"].orEmpty() }
    val v = command.values
    val key = UUID.randomUUID().toString()

    fun result(
        r: ActionResult,
        route: String = "",
        param: String = "",
    ) = BioResult(r.success, r.message, if (r.success)route else "", param)

    fun result(
        result: TaskOperationResult,
        route: String = "",
    ) = BioResult(result.success, result.message, if (result.success) route else "")

    fun engage(
        a: EngagementAction,
        route: String = "",
    ) = result(storage.engage(a), route)

    fun result(
        saved: RepositoryResult<String>,
        route: String = "",
    ): BioResult =
        when (saved) {
            is RepositoryResult.Success -> BioResult(true, saved.value, route)
            is RepositoryResult.Error -> BioResult(false, saved.message)
        }

    fun treeResult(
        receipt: RepositoryResult<TreeCareReceipt>,
        route: String = "",
    ): BioResult =
        when (receipt) {
            is RepositoryResult.Success -> BioResult(true, receipt.value.message, route)
            is RepositoryResult.Error -> BioResult(false, receipt.message)
        }

    fun focusResult(outcome: FocusCommandResult): BioResult =
        when (outcome) {
            FocusCommandResult.NoActiveSession -> BioResult(false, AppText.text(TextKey.CopyChuaCoPhienTapTrung80d4ae))
            is FocusCommandResult.Handled -> result(outcome.result)
        }
    when (action) {
        "refresh" -> return BioResult(true, AppText.text(TextKey.CopyDaLamMoiThongKeA8b494))
        "complete" -> return result(taskRepository.completeResult(id.toLong()))
        "claim" -> return BioResult(taskRepository.claimDailyBonus(), AppText.text(TextKey.CopyDaNhanThuongHomNayC5cad9))
        "water" -> {
            val outcome = treeRepository.water(id.toLong())
            val message =
                when (outcome) {
                    WateringOutcome.Watered -> TextKey.CopyDaTuoiCayMienPhiC84ab7
                    WateringOutcome.AlreadyWatered -> TextKey.CopyCayDaDuNuoc66849f
                    WateringOutcome.Dead -> TextKey.CopyCayDaKhoHeo9c0717
                    WateringOutcome.ClockChanged, WateringOutcome.MissingTree -> TextKey.CopyChuaTheTuoiCayB8c05f
                }
            return BioResult(outcome == WateringOutcome.Watered, AppText.text(message))
        }
        "fertilize" -> return treeResult(treeRepository.applyFertilizer(id.toLong(), parts.getOrElse(2) { "fertilizer" }))
        "equip" -> return inventory.equipWateringCan(id).let { BioResult(it.success, it.message) }
        "buy", "buy-equip", "buy-plant" -> {
            val item = v["id"] ?: id
            val quantity = v["quantity"]?.toIntOrNull()?.coerceIn(1, 99) ?: 1
            val plot = if (action == "buy-plant") garden.loadGarden().eligiblePlantingPlot(v["plot"]?.toIntOrNull()) else null
            if (action == "buy-plant" && plot == null) {
                return BioResult(false, AppText.text(TextKey.CopyVuonDaDayBanVanCoTheMuaHatVaoTuiE14bdf))
            }
            val bought = shop.purchase(item, quantity, key)
            if (bought is RepositoryResult.Error) return result(bought)
            if (action == "buy-equip") {
                val equipped = inventory.equipWateringCan(item)
                return BioResult(equipped.success, equipped.message)
            }
            if (plot != null) {
                val planted = inventory.plantSeed(item, "", plot)
                val treeId = garden.loadGarden().plantedTreeId(plot)
                val message = if (planted) TextKey.CopyDaGieoMotMamXanhB29310 else TextKey.CopyHatDaOTrongTuiChonOTrongDeGieo3a2bef
                return BioResult(planted, AppText.text(message), if (planted) "tree" else "", treeId?.toString().orEmpty())
            }
            return result(bought)
        }
        "plant" -> {
            val plot = v["plot"]?.toIntOrNull()
            val planted = inventory.plantSeed(v["id"].orEmpty(), v["name"].orEmpty(), plot)
            val treeId = garden.loadGarden().plantedTreeId(plot)
            val message = if (planted) TextKey.CopyMotMamXanhMoi63e3dc else TextKey.CopyKiemTraLaiHatVaOTrong36d949
            return BioResult(planted, AppText.text(message), if (planted) "tree" else "", treeId?.toString().orEmpty())
        }
        "confirm-water-all" -> return garden.waterAll(key).let { BioResult(it.success, it.message) }
        "confirm-unlock" -> return BioResult(garden.unlockPlot(id.toInt()), AppText.text(TextKey.CopyDaMoOTrong37fc9b))
        "confirm-remove", "confirm-sell" ->
            return treeResult(treeRepository.dispose(id.toLong(), action == "confirm-sell", key), "garden")
        "confirm-archive" -> return result(taskRepository.lifecycle(id.toLong(), "ARCHIVED"), "tasks")
        "lifecycle" -> return result(taskRepository.lifecycle(id.toLong(), parts.getOrElse(2) { "ACTIVE" }))
        "save-task" -> return result(
            taskRepository.save(v["id"]?.toLongOrNull(), taskConfigFromDraft(v, ::clock)),
            "tasks",
        )
        "start-focus" -> return result(focusCommands.start(v["task"]?.toLongOrNull() ?: 0, v["minutes"]?.toIntOrNull() ?: 25, key), "focus")
        "focus-pause" -> return result(focusCommands.pauseIfRunning())
        "focus-toggle" -> return focusResult(focusCommands.toggle())
        "confirm-focus-end" -> return focusResult(focusCommands.end())
        "templates" -> return result(
            taskRepository.addTemplates(v["ids"].orEmpty().split(',').filter { it.isNotBlank() }, key),
        )
        "reflection" -> return result(saveJournalNote(JournalNote.WeeklyReflection(v["week"].orEmpty(), v["note"].orEmpty())))
        "rest" -> return engage(EngagementAction.Rest(v["date"].orEmpty()))
        "protect" -> return engage(EngagementAction.Protect(v["date"].orEmpty(), key))
        "confirm-ticket" -> return engage(EngagementAction.BuyProtection(key))
        "memory-note" -> return result(saveJournalNote(JournalNote.TimelineMemory(v["id"].orEmpty(), v["note"].orEmpty())))
        "reminder" -> return result(
            saveReminderSettings(
                TaskReminderSettings(
                    taskId = v["id"]?.toLongOrNull() ?: 0,
                    mode = v["mode"] ?: "OFF",
                    minute = clock(v["minute"].orEmpty()),
                    followUp = v["followUp"] == "true",
                ),
            ),
        )
        "settings" -> return result(
            saveReminderSettings(
                ReminderSettings(
                    paused = v["paused"] == "true",
                    quietStartMinute = clock(v["quietStart"].orEmpty()),
                    quietEndMinute = clock(v["quietEnd"].orEmpty()),
                    treeReminders = v["treeReminders"] == "true",
                    reducedMotion = v["reducedMotion"] == "true",
                    breakPrompts = v["breakPrompts"] == "true",
                ),
            ),
        )
        "confirm-adjust" -> {
            val task =
                taskRepository.observeSnapshot().firstOrNull { it.id == id.toLong() }
                    ?: return BioResult(false, AppText.text(TextKey.CopyKhongTimThayViec07c69c))
            return result(taskRepository.save(task.id, task.config.copy(frequency = "WEEKDAYS", days = "")))
        }
        "onboard" -> return result(
            onboarding.finish(
                OnboardingRequest(
                    seed = v["seed"] ?: "succulent",
                    category = v["category"] ?: AppText.canonical(TextKey.CopyHocTapA51516),
                    templateIds = v["ids"].orEmpty().split(',').filter { it.isNotBlank() },
                    operationKey = key,
                    skip = v["skip"] == "true",
                    reminders = v["remind"] == "true",
                ),
            ),
            "home",
        )
        "accessory-buy" -> return result(treeRepository.buyAccessory(v["id"] ?: id, key))
        "accessory-use" -> return result(treeRepository.equipAccessory(v["id"] ?: id, v["tree"]?.toLongOrNull(), key))
        "unequip" -> return result(treeRepository.unequipAccessory(id.toLong()))
        "clear-data" -> {
            backup.clear()
            return BioResult(true, AppText.text(TextKey.CopyDaXoaDuLieu1841a6), "onboard")
        }
        "recover-data" -> {
            backup.recover()
            return BioResult(true, AppText.text(TextKey.CopyDaKhoiPhucDuLieuB0b459), "home")
        }
        "confirm-import" -> {
            if (pending ==
                null
            ) {
                return BioResult(false, AppText.text(TextKey.CopyChonTepSaoLuuTruoc71112e))
            }
            backup.restore(pending)
            return BioResult(true, AppText.text(TextKey.CopyDaKhoiPhucDuLieuB0b459), "home")
        }
    }
    return BioResult(false, AppText.text(TextKey.CopyChuaTheCapNhatVuiLongThuLai42c8ac))
}
