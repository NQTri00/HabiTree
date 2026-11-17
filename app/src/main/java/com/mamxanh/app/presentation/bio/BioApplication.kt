package com.mamxanh.app.presentation.bio

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.*
import org.json.JSONObject

@Stable internal class BioController(initialRoute:String="home",initialParam:String="",initialModal:String="",var execute:(BioCommand)->Unit={}) {
    var route by mutableStateOf(initialRoute)
    var param by mutableStateOf(initialParam)
    var modal by mutableStateOf(initialModal)
    var modalId by mutableStateOf("")
    var homeTree by mutableStateOf("1")
    var message by mutableStateOf("")
    var modalMessage by mutableStateOf("")
    var busy by mutableStateOf(false)
    var quantity by mutableIntStateOf(1)
    var targetTree by mutableStateOf("")
    var plot by mutableStateOf("")
    var onboardingStep by mutableIntStateOf(0)
    var selectedSeed by mutableStateOf("succulent")
    var selectedCategory by mutableStateOf("Học tập")
    var selectedTemplates=mutableStateListOf("study25","water")
    var dirty by mutableStateOf(false)
    var pendingRoute by mutableStateOf("tasks")
    var pendingParam by mutableStateOf("")
    var pendingBack by mutableStateOf(false)
    var pendingRoot by mutableStateOf(false)
    var world:BioWorld?=null
    val tabs=mutableStateMapOf("shop" to "seeds","inventory" to "seeds","tree" to "care","taskFilter" to "today","gardenFilter" to "all","insightDays" to "7")
    val fields=mutableStateMapOf<String,String>()
    private val history=mutableStateListOf<Pair<String,String>>()
    private fun pauseFocus(){if(route=="focus"&&world?.state?.o("focus")?.s("state")=="RUNNING")send("focus-pause")}
    private fun saved():String=json(
        "route" to route,"param" to param,"modal" to modal,"modalId" to modalId,"homeTree" to homeTree,"quantity" to quantity,"targetTree" to targetTree,"plot" to plot,
        "step" to onboardingStep,"seed" to selectedSeed,"category" to selectedCategory,"templates" to org.json.JSONArray(selectedTemplates.toList()),
        "dirty" to dirty,"pendingRoute" to pendingRoute,"pendingParam" to pendingParam,"pendingBack" to pendingBack,"pendingRoot" to pendingRoot,
        "tabs" to JSONObject(tabs.toMap()),"fields" to JSONObject(fields.toMap()),
        "history" to org.json.JSONArray().apply{history.forEach{put(org.json.JSONArray(listOf(it.first,it.second)))}}
    ).toString()
    companion object {
        val Saver=Saver<BioController,String>(save={it.saved()},restore={raw->runCatching{
            val s=JSONObject(raw)
            BioController(s.s("route","home"),s.s("param"),s.s("modal").takeUnless{it=="confirm-import"}.orEmpty()).apply{
                modalId=s.s("modalId");homeTree=s.s("homeTree","0");quantity=s.i("quantity",1);targetTree=s.s("targetTree");plot=s.s("plot")
                onboardingStep=s.i("step");selectedSeed=s.s("seed","succulent");selectedCategory=s.s("category","Học tập")
                selectedTemplates.clear();selectedTemplates.addAll(s.a("templates").strings())
                dirty=s.b("dirty");pendingRoute=s.s("pendingRoute","tasks");pendingParam=s.s("pendingParam");pendingBack=s.b("pendingBack");pendingRoot=s.b("pendingRoot")
                listOf("tabs" to tabs,"fields" to fields).forEach{(key,destination)->val values=s.o(key);values.keys().forEach{destination[it]=values.s(it)}}
                val entries=s.a("history");repeat(entries.length()){val entry=entries.getJSONArray(it);history.add(entry.optString(0) to entry.optString(1))}
            }
        }.getOrElse{BioController()}})
    }
    fun go(next:String,id:String="",root:Boolean=false,treeTab:String?=null) {
        if(route=="task-new"&&dirty&&next!="task-new"){pendingRoute=next;pendingParam=id;pendingBack=false;pendingRoot=root;show("discard");return}
        if(next!=route)pauseFocus()
        if(next=="focus"&&id.isNotBlank()&&(route!="focus"||id!=param)){fields.remove("focus:task");fields.remove("focus:minutes")}
        if(root)history.clear() else if(next!=route||id!=param)history.add(route to param)
        route=next;param=id;modal="";message=""
        if(next=="tree")tabs["tree"]=treeTab?:"care"
        if(next=="task-new")fields.clear()
    }
    fun back(){if(busy)return;if(modal.isNotBlank()){modal="";return};if(route=="task-new"&&dirty){pendingRoute=history.lastOrNull()?.first?:"tasks";pendingParam=history.lastOrNull()?.second?:"";pendingBack=true;pendingRoot=false;show("discard");return};pauseFocus();val previous=history.lastOrNull();if(previous!=null){history.removeAt(history.lastIndex);route=previous.first;param=previous.second}else{route="home";param=""}}
    fun discard(){
        dirty=false;fields.clear();modal=""
        if(pendingBack){val previous=history.lastOrNull();if(previous!=null)history.removeAt(history.lastIndex);route=previous?.first?:pendingRoute;param=previous?.second?:pendingParam}
        else{
            if(!pendingRoot){val previous=history.lastOrNull();if(previous!=null)history.removeAt(history.lastIndex);route=previous?.first?:"home";param=previous?.second.orEmpty()}
            go(pendingRoute,pendingParam,root=pendingRoot)
        }
        pendingBack=false;pendingRoot=false
    }
    fun show(type:String,id:String=""){modal=type;modalId=id;modalMessage="";quantity=1;message=""}
    fun send(action:String,values:Map<String,String> = emptyMap()){if(!busy)execute(BioCommand(action,values))}
    fun result(result:BioResult){busy=false;if(result.success){modal="";if(result.route.isNotBlank()){if(route=="task-new"){dirty=false;fields.clear()};go(result.route,result.param,root=result.route in listOf("home","tasks","garden","shop","stats"))};if(result.message.isNotBlank())message=result.message}else if(modal.isNotBlank())modalMessage=result.message else message=result.message}
    fun act(action:String){
        val parts=action.split(':');val name=parts[0];val id=parts.getOrElse(1){""};val value=parts.getOrElse(2){""}
        when(name){
            "go"->go(id,value)
            "tab"->tabs[id]=value
            "back"->back()
            "bio-picker"->show("tree-picker")
            "bio-pin"->{homeTree=id;modal="";send("pin",mapOf("id" to id))}
            "bio-tree-decor"->go("tree",id,treeTab="decor")
            "bio-accessory-shop"->{tabs["shop"]="accessories";targetTree=id.ifBlank{homeTree};go("shop",targetTree)}
            "bio-seed-picker"->{plot=id;show("seed-picker")}
            "bio-shop-seeds"->{tabs["shop"]="seeds";go("shop")}
            "shop-care"->{tabs["shop"]="care";targetTree=param;go("shop",targetTree)}
            "purchase"->show("purchase",id)
            "accessory"->{targetTree=value.ifBlank{targetTree.ifBlank{homeTree}};show("accessory",id)}
            "plant"->{plot=value;show("plant",id)}
            "unlock","sell","remove","archive","water-all","ticket","adjust","permission","recover","clear","license","focus-end","fertilizer-help"->show(name,id)
            "tree-prev","tree-next"->{val trees=world?.trees.orEmpty();val i=trees.indexOfFirst{it.s("id")==param}.coerceAtLeast(0);if(trees.isNotEmpty())param=trees[(i+(if(name=="tree-next")1 else trees.size-1))%trees.size].s("id")}
            "focus-task"->go("focus",id)
            else->send(action)
        }
    }
}

@Composable internal fun BioApplication(world:BioWorld,ui:BioController=remember{BioController()},modifier:Modifier=Modifier) {
    ui.world=world
    val holders=rememberSaveableStateHolder()
    val primary=ui.route in listOf("home","tasks","garden","shop","stats")
    val inspection=LocalInspectionMode.current
    if(!inspection)BackHandler(enabled=ui.modal.isNotBlank()||ui.route!="home"){ui.back()}
    LaunchedEffect(ui.message){if(ui.message.isNotBlank()){kotlinx.coroutines.delay(4000);ui.message=""}}
    BioTheme(world,ui.homeTree){
        CompositionLocalProvider(LocalBioExpanded provides (ui.tabs["expanded"]=="true")){
        BoxWithConstraints(modifier.fillMaxSize().background(bp.bg)){
            val gutter=if(maxWidth<359.dp)16 else if(maxWidth>=400.dp)22 else 20
            val compactScreen=maxHeight<600.dp
            val shortScreen=maxHeight<700.dp
            val topInset=if(maxHeight<600.dp)14 else if(maxWidth<359.dp)16 else 20
            Column(Modifier.fillMaxSize()){
                val title=if(ui.route=="task-new")if(ui.param.isBlank())"Thêm việc"else "Sửa việc"else bioRoutes[ui.route]?:"Mầm Xanh"
                BioHeader(title,world.coins,primary,{ui.back()},{ui.go("more")},showWallet=ui.route!="onboard")
                if(ui.busy)LinearProgressIndicator(Modifier.fillMaxWidth(),color=bp.deep,trackColor=bp.soft)
                holders.SaveableStateProvider(ui.route+":"+ui.param){
                    Column(Modifier.weight(1f).fillMaxWidth()){
                        if(ui.route=="onboard")BioOnboard(world,ui,gutter)
                        else Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState(if(ui.tabs["scrollBottom"]=="true")Int.MAX_VALUE else 0)).padding(start=gutter.dp,end=gutter.dp,top=topInset.dp,bottom=24.dp),verticalArrangement=Arrangement.spacedBy(18.dp)){
                            if(world.loading)LinearProgressIndicator(Modifier.fillMaxWidth(),color=bp.deep,trackColor=bp.soft)
                            when(ui.route){
                                "home"->BioHome(world,ui,shortScreen)
                                "tasks"->BioTasks(world,ui)
                                "garden"->BioGarden(world,ui)
                                "shop","inventory"->BioCatalog(world,ui,ui.route=="inventory")
                                "tree"->BioTree(world,ui,compactScreen)
                                "stats"->BioStats(world,ui)
                                "task"->BioTaskDetail(world,ui)
                                "task-new"->BioTaskForm(world,ui)
                                "focus"->BioFocus(world,ui)
                                "templates"->BioTemplates(world,ui)
                                "review"->BioReview(world,ui)
                                "rest"->BioRest(world,ui)
                                "insights"->BioInsights(world,ui)
                                "achievements"->BioAchievements(world,ui)
                                "timeline"->BioTimeline(world,ui)
                                "settings"->BioSettings(world,ui)
                                "data"->BioData(world,ui)
                                "encyclopedia"->BioEncyclopedia(world,ui)
                                "species"->BioSpecies(world,ui)
                                "more"->BioMore(ui)
                            }
                        }
                    }
                }
                when(ui.route){
                    "task-new"->BioActionDock{BioTaskFormActions(world,ui)}
                    "templates"->BioActionDock{BioTemplateActions(ui)}
                    "focus"->BioActionDock{BioFocusActions(world,ui)}
                    "onboard","data"->Unit
                    else->BioNav(ui.route){if(it=="shop"){ui.targetTree="";ui.plot=""};ui.go(it,root=true)}
                }
            }
            if(ui.message.isNotBlank()&&ui.modal.isBlank())BioToast(ui.message,{ui.message=""},Modifier.align(Alignment.BottomCenter).padding(start=15.dp,end=15.dp,bottom=if(ui.route in listOf("onboard","data"))18.dp else 90.dp))
            if(ui.modal.isNotBlank())CompositionLocalProvider(LocalBioButtonSize provides 13){BioModal(world,ui)}
        }
    }
    }
}

@Composable internal fun BioToast(message:String,onDismiss:()->Unit,modifier:Modifier=Modifier){BioCard(modifier.fillMaxWidth(),padding=14,radius=14){Row(verticalAlignment=Alignment.CenterVertically){Bt(message,Modifier.weight(1f),size=12);BioIconButton("close",onDismiss)}}}
