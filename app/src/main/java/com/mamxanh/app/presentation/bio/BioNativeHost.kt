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
import com.mamxanh.app.feature.inventory.domain.InventoryRepository
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
private fun clock(value:String):Int{if(value.isBlank())return -1;val m=Regex("^(\\d{1,2}):(\\d{2})$").matchEntire(value)?:return -2;val h=m.groupValues[1].toInt();val n=m.groupValues[2].toInt();return if(h in 0..23&&n in 0..59)h*60+n else -2}

private fun nativeWorld(storage:GameStorage,reference:BioWorld,service:BackupService):BioWorld {
    val data=JSONObject(reference.data.toString());val g=storage.garden();val tasks=storage.tasks();val e=storage.engagementSnapshot();val living=storage.livingGarden();val statistics=storage.statistics();val phase=storage.phaseSnapshot()
    val now=System.currentTimeMillis();val today=date(now)
    val trees=g.trees.map{t->val decoration=living.placements.firstOrNull{it.treeId==t.id&&gardenCosmetics.any{c->c.id==it.sku&&c.slot=="TREE"}}?.sku.orEmpty();json("id" to t.id,"species" to t.speciesId,"name" to AppText.plantName(t.speciesId,t.name),"plot" to (g.plots.firstOrNull{it.treeId==t.id}?.id?:0),"state" to t.status.state.name.lowercase(),"stage" to t.status.stageIndex+1,"progress" to t.status.percentage,"watered" to t.status.waterActive,"planted" to t.planted,"decor" to decoration,"canFertilize" to t.status.canFertilize,"fertilized" to t.status.fertilizerId)}
    val taskJson=tasks.map{t->json("id" to t.id,"title" to t.title,"kind" to t.kind,"category" to t.category,"verification" to t.config.verification,"minutes" to t.config.minutes,"frequency" to t.frequency,"description" to t.config.description,"days" to t.config.days,"due" to t.due,"start" to clockLabel(t.config.startMinute),"end" to clockLabel(t.config.endMinute),"reminder" to clockLabel(t.config.reminderMinute),"done" to t.doneToday,"finished" to t.finished,"lifecycle" to t.lifecycle,"scheduledToday" to t.scheduledToday,"available" to t.available,"availability" to t.availability,"streak" to t.streak,"lastDone" to t.lastDone,"history" to array(t.history.map{json("date" to it.date,"nominal" to it.nominal,"granted" to it.granted)}))}
    val focus=e.focus.firstOrNull{it.state in listOf("RUNNING","PAUSED","BREAK")}
    val reminders=JSONObject().apply{e.reminders.forEach{put(it.taskId.toString(),json("mode" to it.mode,"minute" to clockLabel(it.minute),"followUp" to it.followUp))}}
    val state=json("coins" to g.coins,"earned" to phase.earnedToday,"claimed" to storage.isDailyBonusClaimed(),"trees" to array(trees),"tasks" to array(taskJson),"inventory" to mapJson(g.inventory),"equipped" to g.equippedCan,"owned" to mapJson(living.owned),"unlocked" to g.plots.count{it.isUnlocked},"openPlots" to array(g.plots.filter{it.isUnlocked}.map{it.id}),"settings" to json("paused" to e.settings.paused,"treeReminders" to e.settings.treeReminders,"breakPrompts" to e.settings.breakPrompts,"reducedMotion" to e.settings.reducedMotion,"quietStart" to clockLabel(e.settings.quietStart),"quietEnd" to clockLabel(e.settings.quietEnd)),"reminders" to reminders,"templates" to array(e.templates),"tickets" to e.protectionTickets,"rest" to array(e.days.filter{it.state=="REST"}.map{it.date}),"protected" to array(e.days.filter{it.state=="PROTECTED"}.map{it.date}),"bytes" to service.sizeBytes(),"safety" to service.hasSafety(),"onboarded" to e.onboarded,"focusHistory" to array(e.focus.filter{it.state=="COMPLETE"}.map{json("title" to it.title,"date" to it.date)}),"memories" to array(e.memories.map{json("id" to it.id,"title" to AppText.history(it.title),"type" to it.type,"entity" to it.entity,"at" to it.at,"date" to java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.SHORT,java.text.DateFormat.SHORT,bioLocale()).format(Date(it.at)),"note" to it.note)}),"notes" to JSONObject())
    if(focus!=null)state.put("focus",json("id" to focus.id,"title" to focus.title,"state" to focus.state,"remaining" to focus.remaining,"break" to (focus.state=="BREAK")))
    data.put("state",state);data.put("date",today);data.put("tomorrow",e.tomorrow);data.put("phase",json("required" to phase.required,"completed" to phase.completed,"streak" to phase.streak,"canClaim" to storage.canClaimDailyBonus()))
    data.put("statistics",json("total" to statistics.total,"today" to statistics.today,"week" to statistics.week,"streak" to statistics.streak,"treeCount" to statistics.treeCount,"recentDailyCompletions" to array(statistics.recentDailyCompletions)))
    data.put("records",array(living.species.map{r->json("id" to r.id,"seeds" to r.ownedSeeds,"planted" to r.planted,"discovered" to (r.discoveredAt>0),"highest" to (r.highestStage+1).coerceAtLeast(0),"mature" to (r.firstMatured>0),"bloom" to r.flourished,"status" to r.status,"firstPlanted" to date(r.firstPlanted),"firstMatured" to date(r.firstMatured))}))
    data.put("goals",array(living.goals.map{g0->json("id" to g0.id,"title" to g0.title,"n" to g0.progress,"target" to g0.target,"reward" to g0.reward,"unlocked" to (g0.unlockedAt>0))}))
    data.put("achievements",array(e.achievements.map{a->json("id" to a.id,"title" to a.title,"criterion" to a.criterion,"progress" to a.progress,"target" to a.target,"unlockedAt" to a.unlockedAt)}))
    data.put("days",array(e.days.map{json("date" to it.date,"state" to it.state)}))
    data.put("insights",array(e.insights.map{i->json("taskId" to i.taskId,"longest" to i.longest,"rate" to i.rate,"focusMinutes" to i.focusMinutes,"commonHour" to i.commonHour,"suggestion" to i.suggestion,"days" to array(i.days.map{json("date" to it.date,"state" to it.state)}))}))
    data.put("review",e.review?.let{r->json("start" to r.start,"scheduled" to r.scheduled,"completed" to r.completed,"previousRate" to r.previousRate,"focusMinutes" to r.focusMinutes,"earned" to r.earned,"spent" to r.spent,"careDays" to r.careDays,"milestones" to r.milestones,"consistent" to r.consistent,"improve" to r.improve,"reflection" to r.reflection)}?:JSONObject())
    return BioWorld(data,native=true)
}

/** Native database/OS adapter. The presentation used here is also the preview target. */
@Composable fun BioNativeHost(storage:GameStorage,externalTree:Long?=null,externalRoute:String="",externalTask:Long=0,onExternalHandled:()->Unit={}) {
    val inventory = koinInject<InventoryRepository>()
    val context=LocalContext.current;val scope=rememberCoroutineScope();val reference=remember{BioWorld.reference(context)}
    val prefs=remember{context.getSharedPreferences("bio_ui",Context.MODE_PRIVATE)}
    val permissionPrefs=remember{context.getSharedPreferences("engagement_permissions",Context.MODE_PRIVATE)}
    val service=remember{BackupService(context.applicationContext,storage,BuildConfig.VERSION_NAME)}
    val ui=rememberSaveable(saver=BioController.Saver){BioController().apply{homeTree=prefs.getLong("home_tree_id",0).toString()}}
    var world by remember{mutableStateOf(BioWorld(JSONObject(reference.data.toString()).apply{put("state",json("coins" to 0,"trees" to JSONArray(),"tasks" to JSONArray(),"inventory" to JSONObject(),"owned" to JSONObject(),"settings" to JSONObject()))},loading=true,native=true))}
    var pending by remember{mutableStateOf<GameBackup?>(null)}
    val homeSpecies=world.tree(ui.homeTree)?.s("species").orEmpty()
    LaunchedEffect(homeSpecies,world.loading){
        if(!world.loading)withContext(Dispatchers.IO){HabiTreeLauncherIcon.update(context.applicationContext,homeSpecies)}
    }
    suspend fun refresh(){world=withContext(Dispatchers.IO){nativeWorld(storage,reference,service)};if(world.trees.none{it.s("id")==ui.homeTree})ui.homeTree=world.trees.firstOrNull()?.s("id")?:"0"}
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
            "pin"->{val id=command.values["id"]?.toLongOrNull()?:0;prefs.edit().putLong("home_tree_id",id).apply()}
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
            else->if(!ui.busy){ui.busy=true;scope.launch{try{val result=withContext(Dispatchers.IO){executeNative(storage,inventory,service,pending,command)};refresh();ui.result(result)}catch(_:Exception){ui.result(BioResult(false,AppText.text(TextKey.CopyChuaTheCapNhatVuiLongThuLai42c8ac)))}}}
        }
    }
    BioApplication(world,ui,Modifier.fillMaxSize().safeDrawingPadding().imePadding(),koinInject<HabiTreeRouteRegistry>())
}

private fun executeNative(storage:GameStorage,inventory:InventoryRepository,backup:BackupService,pending:GameBackup?,command:BioCommand):BioResult {
    val parts=command.action.split(':');val action=parts[0];val id=parts.getOrElse(1){command.values["id"].orEmpty()};val v=command.values;val key=UUID.randomUUID().toString()
    fun result(r:ActionResult,route:String="",param:String="")=BioResult(r.success,r.message,if(r.success)route else "",param)
    fun engage(a:EngagementAction,route:String="")=result(storage.engage(a),route)
    when(action){
        "refresh"->return BioResult(true,AppText.text(TextKey.CopyDaLamMoiThongKeA8b494))
        "complete"->return result(storage.completeTask(id.toLong()))
        "claim"->return BioResult(storage.dailyBonus(),AppText.text(TextKey.CopyDaNhanThuongHomNayC5cad9))
        "water"->{val r=storage.water(id.toLong());return BioResult(r==TreeWateringResult.WATERED,when(r){TreeWateringResult.WATERED->AppText.text(TextKey.CopyDaTuoiCayMienPhiC84ab7);TreeWateringResult.ALREADY_WATERED->AppText.text(TextKey.CopyCayDaDuNuoc66849f);TreeWateringResult.DEAD->AppText.text(TextKey.CopyCayDaKhoHeo9c0717);else->AppText.text(TextKey.CopyChuaTheTuoiCayB8c05f)})}
        "fertilize"->return result(storage.applyFertilizer(id.toLong(),parts.getOrElse(2){"fertilizer"}))
        "equip"->return inventory.equipWateringCan(id).let { BioResult(it.success, it.message) }
        "buy","buy-equip","buy-plant"->{
            val item=v["id"]?:id;val quantity=v["quantity"]?.toIntOrNull()?.coerceIn(1,99)?:1
            val g=storage.garden();val plot=g.plots.firstOrNull{it.isUnlocked&&it.treeId==null&&it.id==(v["plot"]?.toIntOrNull()?:it.id)}?:g.plots.firstOrNull{it.isUnlocked&&it.treeId==null}
            if(action=="buy-plant"&&plot==null)return BioResult(false,AppText.text(TextKey.CopyVuonDaDayBanVanCoTheMuaHatVaoTuiE14bdf))
            val bought=storage.purchase(item,quantity,key);if(!bought.success)return result(bought)
            if(action=="buy-equip")return inventory.equipWateringCan(item).let { BioResult(it.success, it.message) }
            if(action=="buy-plant"){val planted=inventory.plantSeed(item,"",plot!!.id);val tree=storage.garden().trees.firstOrNull{t->storage.garden().plots.any{it.id==plot.id&&it.treeId==t.id}};return BioResult(planted,if(planted)AppText.text(TextKey.CopyDaGieoMotMamXanhB29310)else AppText.text(TextKey.CopyHatDaOTrongTuiChonOTrongDeGieo3a2bef),if(planted)"tree"else "",tree?.id?.toString().orEmpty())}
            return result(bought)
        }
        "plant"->{val item=v["id"].orEmpty();val plot=v["plot"]?.toIntOrNull();val planted=inventory.plantSeed(item,v["name"].orEmpty(),plot);val g=storage.garden();val tree=g.trees.firstOrNull{t->g.plots.any{it.id==plot&&it.treeId==t.id}};return BioResult(planted,if(planted)AppText.text(TextKey.CopyMotMamXanhMoi63e3dc)else AppText.text(TextKey.CopyKiemTraLaiHatVaOTrong36d949),if(planted)"tree"else "",tree?.id?.toString().orEmpty())}
        "confirm-water-all"->return result(storage.waterAll(key))
        "confirm-unlock"->return BioResult(storage.unlockPlot(id.toInt()),AppText.text(TextKey.CopyDaMoOTrong37fc9b))
        "confirm-remove","confirm-sell"->return result(storage.disposeTree(id.toLong(),action=="confirm-sell",key),"garden")
        "confirm-archive"->return result(storage.taskLifecycle(id.toLong(),"ARCHIVED"),"tasks")
        "lifecycle"->return result(storage.taskLifecycle(id.toLong(),parts.getOrElse(2){"ACTIVE"}))
        "save-task"->{val kind=v["kind"].orEmpty();val config=TaskConfig(title=v["title"].orEmpty(),kind=kind,category=v["category"].orEmpty(),description=v["description"].orEmpty(),frequency=if(kind=="TODO")"ONCE"else v["frequency"].orEmpty(),days=v["days"].orEmpty(),due=v["due"].orEmpty(),verification=v["verification"].orEmpty(),minutes=v["minutes"]?.toIntOrNull()?:5,startMinute=clock(v["start"].orEmpty()),endMinute=clock(v["end"].orEmpty()),reminderMinute=clock(v["reminder"].orEmpty()));return result(storage.saveTask(v["id"]?.toLongOrNull(),config),"tasks")}
        "start-focus"->return engage(EngagementAction.StartFocus(v["task"]?.toLongOrNull()?:0,v["minutes"]?.toIntOrNull()?:25,key),"focus")
        "focus-pause"->{val f=storage.engagementSnapshot().focus.firstOrNull{it.state=="RUNNING"}?:return BioResult(true);val r=storage.engage(EngagementAction.Focus(f.id,"PAUSE"));return BioResult(r.success,if(r.success)""else r.message)}
        "focus-toggle"->{val f=storage.engagementSnapshot().focus.firstOrNull{it.state in listOf("RUNNING","PAUSED","BREAK")}?:return BioResult(false,AppText.text(TextKey.CopyChuaCoPhienTapTrung80d4ae));return engage(EngagementAction.Focus(f.id,if(f.state=="RUNNING")"PAUSE"else "RESUME"))}
        "confirm-focus-end"->{val f=storage.engagementSnapshot().focus.firstOrNull{it.state in listOf("RUNNING","PAUSED","BREAK")}?:return BioResult(false,AppText.text(TextKey.CopyChuaCoPhienTapTrung80d4ae));return engage(EngagementAction.Focus(f.id,"END"))}
        "templates"->return engage(EngagementAction.Templates(v["ids"].orEmpty().split(',').filter{it.isNotBlank()},key))
        "reflection"->return engage(EngagementAction.Reflection(v["week"].orEmpty(),v["note"].orEmpty()))
        "rest"->return engage(EngagementAction.Rest(v["date"].orEmpty()))
        "protect"->return engage(EngagementAction.Protect(v["date"].orEmpty(),key))
        "confirm-ticket"->return engage(EngagementAction.BuyProtection(key))
        "memory-note"->return engage(EngagementAction.MemoryNote(v["id"].orEmpty(),v["note"].orEmpty()))
        "reminder"->return engage(EngagementAction.Reminder(ReminderPreference(v["id"]?.toLongOrNull()?:0,v["mode"]?:"OFF",clock(v["minute"].orEmpty()),v["followUp"]=="true")))
        "settings"->return engage(EngagementAction.Settings(EngagementSettings(paused=v["paused"]=="true",quietStart=clock(v["quietStart"].orEmpty()),quietEnd=clock(v["quietEnd"].orEmpty()),treeReminders=v["treeReminders"]=="true",reducedMotion=v["reducedMotion"]=="true",breakPrompts=v["breakPrompts"]=="true")))
        "confirm-adjust"->{val task=storage.tasks().firstOrNull{it.id==id.toLong()}?:return BioResult(false,AppText.text(TextKey.CopyKhongTimThayViec07c69c));return result(storage.saveTask(task.id,task.config.copy(frequency="WEEKDAYS",days="")))}
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
