package com.mamxanh.app.presentation.bio

import com.mamxanh.app.presentation.ui.design.bioLocale
import com.mamxanh.app.core.presentation.navigation.HabiTreeRouteRegistry
import org.koin.compose.koinInject

import com.mamxanh.shared.localization.TextKey

import com.mamxanh.shared.localization.AppText

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
import com.mamxanh.app.BuildConfig
import com.mamxanh.app.data.BackupService
import com.mamxanh.app.feature.garden.domain.GardenCollectionRepository
import com.mamxanh.app.feature.garden.domain.GardenRepository
import com.mamxanh.app.feature.inventory.domain.InventoryRepository
import com.mamxanh.app.feature.home.domain.HomeTreeSelection
import com.mamxanh.app.feature.tree.domain.TreeRepository
import com.mamxanh.app.feature.settings.domain.SaveReminderSettingsUseCase
import com.mamxanh.app.feature.shop.domain.ShopRepository
import com.mamxanh.app.feature.stats.domain.LoadGardenStatistics
import com.mamxanh.app.feature.stats.domain.SaveJournalNoteUseCase
import com.mamxanh.app.feature.tasks.domain.FocusSessionCommands
import com.mamxanh.app.feature.tasks.domain.TaskRepository
import com.mamxanh.app.feature.tasks.presentation.focusSessionTitle
import com.mamxanh.shared.data.GameStorage
import com.mamxanh.shared.data.sql.*
import com.mamxanh.shared.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import org.json.*
import java.text.SimpleDateFormat
import java.util.*

private fun array(items:Iterable<Any?>)=JSONArray().apply{items.forEach{put(it)}}
private fun mapJson(map:Map<String,*>)=JSONObject().apply{map.forEach{(k,v)->put(k,v)}}
private fun date(at:Long)=if(at<=0)AppText.text(TextKey.CopyChuaGhiNhanF7654d)else SimpleDateFormat("yyyy-MM-dd",Locale.US).format(Date(at))

private fun nativeWorld(
    storage: GameStorage,
    reference: BioWorld,
    service: BackupService,
    inventory: InventoryRepository,
    garden: GardenRepository,
    collectionRepository: GardenCollectionRepository,
    taskRepository: TaskRepository,
    loadStatistics: LoadGardenStatistics,
): BioWorld {
    val data = JSONObject(reference.data.toString())
    val g = garden.loadGarden()
    val tasks = taskRepository.observeSnapshot()
    val e = storage.engagementSnapshot()
    val collection = collectionRepository.load()
    val statistics = loadStatistics()
    val phase = taskRepository.snapshot()
    val now=System.currentTimeMillis();val today=date(now)
    val trees=g.trees.map{t->val decoration=collection.decorationsByTree[t.id].orEmpty();json("id" to t.id,"species" to t.speciesId,"name" to AppText.plantName(t.speciesId,t.name),"plot" to (g.plots.firstOrNull{it.treeId==t.id}?.id?:0),"state" to t.status.state.name.lowercase(),"stage" to t.status.stageIndex+1,"progress" to t.status.percentage,"watered" to t.status.waterActive,"planted" to t.planted,"decor" to decoration,"canFertilize" to t.status.canFertilize,"fertilized" to t.status.fertilizerId)}
    val taskJson=tasks.map{t->json("id" to t.id,"title" to t.title,"kind" to t.kind,"category" to t.category,"verification" to t.config.verification,"minutes" to t.config.minutes,"frequency" to t.frequency,"description" to t.config.description,"days" to t.config.days,"due" to t.due,"start" to clockLabel(t.config.startMinute),"end" to clockLabel(t.config.endMinute),"reminder" to clockLabel(t.config.reminderMinute),"done" to t.doneToday,"finished" to t.finished,"lifecycle" to t.lifecycle,"scheduledToday" to t.scheduledToday,"available" to t.available,"availability" to t.availability,"streak" to t.streak,"lastDone" to t.lastDone,"history" to array(t.history.map{json("date" to it.date,"nominal" to it.nominal,"granted" to it.granted)}))}
    val focus=e.focus.firstOrNull{it.state in listOf("RUNNING","PAUSED","BREAK")}
    val reminders=JSONObject().apply{e.reminders.forEach{put(it.taskId.toString(),json("mode" to it.mode,"minute" to clockLabel(it.minute),"followUp" to it.followUp))}}
    val state=json("coins" to g.coins,"earned" to phase.earnedToday,"claimed" to taskRepository.isDailyBonusClaimed(),"trees" to array(trees),"tasks" to array(taskJson),"inventory" to mapJson(g.inventory),"equipped" to g.equippedCan,"owned" to mapJson(collection.ownedAccessories),"unlocked" to g.plots.count{it.isUnlocked},"openPlots" to array(g.plots.filter{it.isUnlocked}.map{it.id}),"settings" to json("paused" to e.settings.paused,"treeReminders" to e.settings.treeReminders,"breakPrompts" to e.settings.breakPrompts,"reducedMotion" to e.settings.reducedMotion,"quietStart" to clockLabel(e.settings.quietStart),"quietEnd" to clockLabel(e.settings.quietEnd)),"reminders" to reminders,"templates" to array(e.templates),"tickets" to e.protectionTickets,"rest" to array(e.days.filter{it.state=="REST"}.map{it.date}),"protected" to array(e.days.filter{it.state=="PROTECTED"}.map{it.date}),"bytes" to service.sizeBytes(),"safety" to service.hasSafety(),"onboarded" to e.onboarded,"focusHistory" to array(e.focus.filter{it.state=="COMPLETE"}.map{json("title" to focusSessionTitle(it.taskId,it.title),"date" to it.date)}),"memories" to array(e.memories.map{json("id" to it.id,"title" to AppText.history(it.title),"type" to it.type,"entity" to it.entity,"at" to it.at,"date" to java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.SHORT,java.text.DateFormat.SHORT,bioLocale()).format(Date(it.at)),"note" to it.note)}),"notes" to JSONObject())
    if(focus!=null)state.put("focus",json("id" to focus.id,"title" to focusSessionTitle(focus.taskId,focus.title),"state" to focus.state,"remaining" to focus.remaining,"break" to (focus.state=="BREAK")))
    data.put("state",state);data.put("date",today);data.put("tomorrow",e.tomorrow);data.put("phase",json("required" to phase.required,"completed" to phase.completed,"streak" to phase.streak,"canClaim" to taskRepository.canClaimDailyBonus()))
    data.put("statistics",json("total" to statistics.total,"today" to statistics.today,"week" to statistics.week,"streak" to statistics.streak,"treeCount" to statistics.treeCount,"recentDailyCompletions" to array(statistics.recentDailyCompletions)))
    data.put("records",array(collection.species.map{r->json("id" to r.id,"seeds" to r.ownedSeeds,"planted" to r.planted,"discovered" to r.discovered,"highest" to r.highestStage,"mature" to (r.firstMatured>0),"bloom" to r.flourished,"status" to r.status,"firstPlanted" to date(r.firstPlanted),"firstMatured" to date(r.firstMatured))}))
    data.put("goals",array(collection.milestones.map{g0->json("id" to g0.id,"title" to g0.title,"n" to g0.progress,"target" to g0.target,"reward" to g0.reward,"unlocked" to g0.unlocked)}))
    data.put("achievements",array(e.achievements.map{a->json("id" to a.id,"title" to a.title,"criterion" to a.criterion,"progress" to a.progress,"target" to a.target,"unlockedAt" to a.unlockedAt)}))
    data.put("days",array(e.days.map{json("date" to it.date,"state" to it.state)}))
    data.put("insights",array(e.insights.map{i->json("taskId" to i.taskId,"longest" to i.longest,"rate" to i.rate,"focusMinutes" to i.focusMinutes,"commonHour" to i.commonHour,"suggestion" to i.suggestion,"days" to array(i.days.map{json("date" to it.date,"state" to it.state)}))}))
    data.put("review",e.review?.let{r->json("start" to r.start,"scheduled" to r.scheduled,"completed" to r.completed,"previousRate" to r.previousRate,"focusMinutes" to r.focusMinutes,"earned" to r.earned,"spent" to r.spent,"careDays" to r.careDays,"milestones" to r.milestones,"consistent" to r.consistent,"improve" to r.improve,"reflection" to r.reflection)}?:JSONObject())
    return BioWorld(data,native=true,inventoryQuantities=inventory.quantities())
}

/** Native database/OS adapter. The presentation used here is also the preview target. */
@Composable fun BioNativeHost(storage:GameStorage,externalTree:Long?=null,externalRoute:String="",externalTask:Long=0,onExternalHandled:()->Unit={}) {
    val inventory = koinInject<InventoryRepository>()
    val garden = koinInject<GardenRepository>()
    val collectionRepository = koinInject<GardenCollectionRepository>()
    val homeSelection = koinInject<HomeTreeSelection>()
    val treeRepository = koinInject<TreeRepository>()
    val shop = koinInject<ShopRepository>()
    val taskRepository = koinInject<TaskRepository>()
    val focusCommands = koinInject<FocusSessionCommands>()
    val loadStatistics = koinInject<LoadGardenStatistics>()
    val saveJournalNote = koinInject<SaveJournalNoteUseCase>()
    val saveReminderSettings = koinInject<SaveReminderSettingsUseCase>()
    val context=LocalContext.current;val scope=rememberCoroutineScope();val reference=remember{BioWorld.reference(context)}
    val permissionPrefs=remember{context.getSharedPreferences("engagement_permissions",Context.MODE_PRIVATE)}
    val service=remember{BackupService(context.applicationContext,storage,BuildConfig.VERSION_NAME)}
    val ui = rememberSaveable(saver = BioController.Saver) {
        BioController().apply { homeTree = homeSelection.savedId() }
    }
    var world by remember{mutableStateOf(BioWorld(JSONObject(reference.data.toString()).apply{put("state",json("coins" to 0,"trees" to JSONArray(),"tasks" to JSONArray(),"inventory" to JSONObject(),"owned" to JSONObject(),"settings" to JSONObject()))},loading=true,native=true))}
    var pending by remember{mutableStateOf<GameBackup?>(null)}
    val homeSpecies=world.tree(ui.homeTree)?.s("species").orEmpty()
    LaunchedEffect(homeSpecies,world.loading){
        if(!world.loading)withContext(Dispatchers.IO){HabiTreeLauncherIcon.update(context.applicationContext,homeSpecies)}
    }
    suspend fun refresh() {
        world = withContext(Dispatchers.IO) {
            nativeWorld(storage, reference, service, inventory, garden, collectionRepository, taskRepository, loadStatistics)
        }
        ui.homeTree = homeSelection.resolve(ui.homeTree, world.trees.map { it.s("id") })
    }
    val lifecycleOwner=LocalLifecycleOwner.current
    LaunchedEffect(storage,lifecycleOwner){
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
            merge(storage.changes.map{Unit},flow{while(currentCoroutineContext().isActive){delay(15_000);emit(Unit)}})
                .conflate().collect{refresh();if(!world.state.b("onboarded",true)&&ui.route=="home")ui.go("onboard",root=true)}
        }
    }
    // Schedule from the displayed snapshot, without loading unused screen ViewModels.
    LaunchedEffect(world.loading,world.trees.map{listOf(it.s("id"),it.s("state"),it.b("watered").toString())},
        world.tasks.map{it.toString()},world.state.o("settings").toString(),world.state.o("reminders").toString()){
        if(!world.loading)withContext(Dispatchers.IO){com.mamxanh.app.data.TaskReminders.schedule(context.applicationContext,emptyList())}
    }
    LaunchedEffect(externalTree){externalTree?.let{ui.go("tree",it.toString());onExternalHandled()}}
    LaunchedEffect(externalRoute,externalTask){if(externalTask>0&&externalRoute in listOf("FOCUS","INSIGHTS")){ui.go(externalRoute.lowercase(),externalTask.toString());onExternalHandled()}}
    fun fileOperation(action:()->Unit){if(ui.busy)return;ui.busy=true;scope.launch{try{withContext(Dispatchers.IO){action()};refresh();ui.result(BioResult(true,AppText.text(TextKey.CopyDaLuuDuLieuD8a848)))}catch(_:Exception){ui.result(BioResult(false,AppText.text(TextKey.CopyKhongTheXuLyTepDuLieuHienTaiGiuNguyenAa333d)))}}}
    val export=rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/octet-stream")){uri->if(uri!=null)fileOperation{service.export(uri)}}
    val diagnostics=rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/plain")){uri->if(uri!=null)fileOperation{service.diagnostics(uri)}}
    val import=rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()){uri->if(uri!=null&&!ui.busy){ui.busy=true;scope.launch{try{val backup=withContext(Dispatchers.IO){service.inspect(uri)};pending=backup;ui.fields["backupTasks"]=backup.count("tasks").toString();ui.fields["backupTrees"]=backup.count("trees").toString();ui.fields["backupOccurrences"]=backup.count("occurrences").toString();ui.fields["backupCreated"]=if(backup.createdAt>0)java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.SHORT,java.text.DateFormat.SHORT,bioLocale()).format(Date(backup.createdAt))else AppText.text(TextKey.CopyKhongRo8af7da);ui.show("confirm-import");ui.busy=false}catch(_:Exception){ui.result(BioResult(false,AppText.text(TextKey.CopyKhongTheXuLyTepDuLieuHienTaiGiuNguyenAa333d)))}}}}
    val permission=rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()){allowed->ui.message=if(allowed)AppText.text(TextKey.CopyDaBatThongBao5f71c6)else AppText.text(TextKey.CopyThongBaoTatViecVaBoDemVanDungDuoc70a974)}
    ui.execute={command->
        when(command.action){
            "language"->com.mamxanh.app.presentation.ui.localization.AppLanguageController.select(context,command.values["tag"].orEmpty())
            "pin" -> homeSelection.save(command.values["id"].orEmpty())
            "export"->export.launch("MamXanh-${System.currentTimeMillis()}.mamxanh")
            "diagnostics"->diagnostics.launch("MamXanh-diagnostics.txt")
            "import"->import.launch(arrayOf("application/octet-stream","text/plain","application/zip","application/json"))
            "permission-allow"->{
                val granted=Build.VERSION.SDK_INT<33||context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)==PackageManager.PERMISSION_GRANTED
                val rationale=Build.VERSION.SDK_INT>=33&&(context as? android.app.Activity)?.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)==true
                if(ui.modal=="permission-settings"){
                    ui.modal="";context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,Uri.fromParts("package",context.packageName,null)))
                }else if(granted||permissionPrefs.getBoolean("requested",false)&&!rationale){ui.show("permission-settings")}
                else{permissionPrefs.edit().putBoolean("requested",true).apply();ui.modal="";if(Build.VERSION.SDK_INT>=33)permission.launch(Manifest.permission.POST_NOTIFICATIONS)}
            }
            "permission-settings"->{ui.modal="";context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,Uri.fromParts("package",context.packageName,null)))}
            else -> if (!ui.busy) {
                ui.busy = true
                scope.launch {
                    try {
                        val result = withContext(Dispatchers.IO) {
                            executeNative(
                                storage, inventory, garden, treeRepository, shop, taskRepository, focusCommands,
                                saveJournalNote, saveReminderSettings, service, pending, command,
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
    BioApplication(world,ui,Modifier.fillMaxSize().safeDrawingPadding().imePadding(),koinInject<HabiTreeRouteRegistry>())
}
