package com.mamxanh.app.presentation.bio

import com.mamxanh.app.core.domain.RepositoryResult
import com.mamxanh.app.data.BackupService
import com.mamxanh.app.feature.garden.domain.GardenRepository
import com.mamxanh.app.feature.garden.domain.eligiblePlantingPlot
import com.mamxanh.app.feature.garden.domain.plantedTreeId
import com.mamxanh.app.feature.inventory.domain.InventoryRepository
import com.mamxanh.app.feature.settings.domain.ReminderSettings
import com.mamxanh.app.feature.settings.domain.SaveReminderSettingsUseCase
import com.mamxanh.app.feature.shop.domain.ShopRepository
import com.mamxanh.app.feature.stats.domain.JournalNote
import com.mamxanh.app.feature.stats.domain.SaveJournalNoteUseCase
import com.mamxanh.app.feature.tasks.domain.FocusCommandResult
import com.mamxanh.app.feature.tasks.domain.FocusSessionCommands
import com.mamxanh.app.feature.tasks.domain.TaskRepository
import com.mamxanh.app.feature.tree.domain.TreeRepository
import com.mamxanh.shared.data.GameStorage
import com.mamxanh.shared.data.sql.GameBackup
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.model.ActionResult
import com.mamxanh.shared.model.EngagementAction
import com.mamxanh.shared.model.GardenPlacement
import com.mamxanh.shared.model.LivingGardenAction
import com.mamxanh.shared.model.ReminderPreference
import com.mamxanh.shared.model.TaskConfig
import com.mamxanh.shared.model.TreeWateringResult
import com.mamxanh.shared.model.gardenCosmetics
import java.util.UUID

private fun clock(value:String):Int{if(value.isBlank())return -1;val m=Regex("^(\\d{1,2}):(\\d{2})$").matchEntire(value)?:return -2;val h=m.groupValues[1].toInt();val n=m.groupValues[2].toInt();return if(h in 0..23&&n in 0..59)h*60+n else -2}

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
    backup: BackupService,
    pending: GameBackup?,
    command: BioCommand,
): BioResult {
    val parts=command.action.split(':');val action=parts[0];val id=parts.getOrElse(1){command.values["id"].orEmpty()};val v=command.values;val key=UUID.randomUUID().toString()
    fun result(r:ActionResult,route:String="",param:String="")=BioResult(r.success,r.message,if(r.success)route else "",param)
    fun engage(a:EngagementAction,route:String="")=result(storage.engage(a),route)
    fun result(saved: RepositoryResult<String>, route: String = ""): BioResult = when (saved) {
        is RepositoryResult.Success -> BioResult(true, saved.value, route)
        is RepositoryResult.Error -> BioResult(false, saved.message)
    }
    fun focusResult(outcome: FocusCommandResult): BioResult = when (outcome) {
        FocusCommandResult.NoActiveSession -> BioResult(false, AppText.text(TextKey.CopyChuaCoPhienTapTrung80d4ae))
        is FocusCommandResult.Handled -> result(outcome.result)
    }
    when(action){
        "refresh"->return BioResult(true,AppText.text(TextKey.CopyDaLamMoiThongKeA8b494))
        "complete" -> return result(taskRepository.completeResult(id.toLong()))
        "claim" -> return BioResult(taskRepository.claimDailyBonus(), AppText.text(TextKey.CopyDaNhanThuongHomNayC5cad9))
        "water" -> {
            val outcome = treeRepository.water(id.toLong())
            val message = when (outcome) {
                TreeWateringResult.WATERED -> TextKey.CopyDaTuoiCayMienPhiC84ab7
                TreeWateringResult.ALREADY_WATERED -> TextKey.CopyCayDaDuNuoc66849f
                TreeWateringResult.DEAD -> TextKey.CopyCayDaKhoHeo9c0717
                else -> TextKey.CopyChuaTheTuoiCayB8c05f
            }
            return BioResult(outcome == TreeWateringResult.WATERED, AppText.text(message))
        }
        "fertilize" -> return result(treeRepository.applyFertilizer(id.toLong(), parts.getOrElse(2) { "fertilizer" }))
        "equip"->return inventory.equipWateringCan(id).let { BioResult(it.success, it.message) }
        "buy", "buy-equip", "buy-plant" -> {
            val item = v["id"] ?: id
            val quantity = v["quantity"]?.toIntOrNull()?.coerceIn(1, 99) ?: 1
            val plot = if (action == "buy-plant") garden.loadGarden().eligiblePlantingPlot(v["plot"]?.toIntOrNull()) else null
            if (action == "buy-plant" && plot == null) {
                return BioResult(false, AppText.text(TextKey.CopyVuonDaDayBanVanCoTheMuaHatVaoTuiE14bdf))
            }
            val bought = shop.purchase(item, quantity, key)
            if (!bought.success) return result(bought)
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
        "confirm-water-all" -> return result(garden.waterAll(key))
        "confirm-unlock" -> return BioResult(garden.unlockPlot(id.toInt()), AppText.text(TextKey.CopyDaMoOTrong37fc9b))
        "confirm-remove", "confirm-sell" ->
            return result(treeRepository.dispose(id.toLong(), action == "confirm-sell", key), "garden")
        "confirm-archive" -> return result(taskRepository.lifecycle(id.toLong(), "ARCHIVED"), "tasks")
        "lifecycle" -> return result(taskRepository.lifecycle(id.toLong(), parts.getOrElse(2) { "ACTIVE" }))
        "save-task" -> {
            val kind = v["kind"].orEmpty()
            val config = TaskConfig(
                title = v["title"].orEmpty(),
                kind = kind,
                category = v["category"].orEmpty(),
                description = v["description"].orEmpty(),
                frequency = if (kind == "TODO") "ONCE" else v["frequency"].orEmpty(),
                days = v["days"].orEmpty(),
                due = v["due"].orEmpty(),
                verification = v["verification"].orEmpty(),
                minutes = v["minutes"]?.toIntOrNull() ?: 5,
                startMinute = clock(v["start"].orEmpty()),
                endMinute = clock(v["end"].orEmpty()),
                reminderMinute = clock(v["reminder"].orEmpty()),
            )
            return result(taskRepository.save(v["id"]?.toLongOrNull(), config), "tasks")
        }
        "start-focus" -> return result(focusCommands.start(v["task"]?.toLongOrNull() ?: 0, v["minutes"]?.toIntOrNull() ?: 25, key), "focus")
        "focus-pause" -> return result(focusCommands.pauseIfRunning())
        "focus-toggle" -> return focusResult(focusCommands.toggle())
        "confirm-focus-end" -> return focusResult(focusCommands.end())
        "templates"->return engage(EngagementAction.Templates(v["ids"].orEmpty().split(',').filter{it.isNotBlank()},key))
        "reflection" -> return result(saveJournalNote(JournalNote.WeeklyReflection(v["week"].orEmpty(), v["note"].orEmpty())))
        "rest"->return engage(EngagementAction.Rest(v["date"].orEmpty()))
        "protect"->return engage(EngagementAction.Protect(v["date"].orEmpty(),key))
        "confirm-ticket"->return engage(EngagementAction.BuyProtection(key))
        "memory-note" -> return result(saveJournalNote(JournalNote.TimelineMemory(v["id"].orEmpty(), v["note"].orEmpty())))
        "reminder"->return engage(EngagementAction.Reminder(ReminderPreference(v["id"]?.toLongOrNull()?:0,v["mode"]?:"OFF",clock(v["minute"].orEmpty()),v["followUp"]=="true")))
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
            val task = taskRepository.observeSnapshot().firstOrNull { it.id == id.toLong() }
                ?: return BioResult(false, AppText.text(TextKey.CopyKhongTimThayViec07c69c))
            return result(taskRepository.save(task.id, task.config.copy(frequency = "WEEKDAYS", days = "")))
        }
        "onboard"->return engage(EngagementAction.Onboard(v["seed"]?:"succulent",v["category"]?:AppText.canonical(TextKey.CopyHocTapA51516),v["ids"].orEmpty().split(',').filter{it.isNotBlank()},key,v["skip"]=="true",v["remind"]=="true"),"home")
        "accessory-buy","accessory-use","unequip"->{
            val sku=v["id"]?:id;var living=storage.livingGarden()
            if(action!="unequip"&&(living.owned[sku]?:0)==0){val bought=storage.designGarden(LivingGardenAction.Buy(sku,key));if(!bought.success)return result(bought);living=storage.livingGarden()}
            if(action=="accessory-buy")return BioResult(true,AppText.text(TextKey.CopyDaThemPhuKienVaoTui40b7d7))
            val treeId=if(action=="unequip")id.toLong()else v["tree"]?.toLongOrNull()?:return BioResult(false,AppText.text(TextKey.CopyChonMotCayTruocKhiDeoPhuKien4f5562))
            val g=storage.garden();val tree=g.trees.firstOrNull{it.id==treeId&&it.status.alive}?:return BioResult(false,AppText.text(TextKey.CopyChonMotCayConSong6d5572));val plot=g.plots.firstOrNull{it.treeId==tree.id}?:return BioResult(false,AppText.text(TextKey.CopyKhongTimThayOTrong4ab339))
            var placements=living.placements.filterNot{it.treeId==treeId&&gardenCosmetics.any{c->c.id==it.sku&&c.slot=="TREE"}}
            if(action!="unequip"){if(placements.count{it.sku==sku}>=(living.owned[sku]?:0))placements=placements.filterNot{it.id==placements.firstOrNull{p->p.sku==sku}?.id};placements=placements+GardenPlacement(UUID.randomUUID().toString(),sku,plot.id,treeId)}
            return result(storage.designGarden(LivingGardenAction.Layout(living.version,placements,UUID.randomUUID().toString())))
        }
        "clear-data"->{backup.clear();return BioResult(true,AppText.text(TextKey.CopyDaXoaDuLieu1841a6),"onboard")}
        "recover-data"->{backup.recover();return BioResult(true,AppText.text(TextKey.CopyDaKhoiPhucDuLieuB0b459),"home")}
        "confirm-import"->{if(pending==null)return BioResult(false,AppText.text(TextKey.CopyChonTepSaoLuuTruoc71112e));backup.restore(pending);return BioResult(true,AppText.text(TextKey.CopyDaKhoiPhucDuLieuB0b459),"home")}
    }
    return BioResult(false,AppText.text(TextKey.CopyChuaTheCapNhatVuiLongThuLai42c8ac))
}
