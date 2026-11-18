package com.mamxanh.app.presentation.bio

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import com.mamxanh.app.core.presentation.navigation.HabiTreeNavigator
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import org.json.JSONObject

@Stable internal class BioController(initialRoute:String="home",initialParam:String="",initialModal:String="",var execute:(BioCommand)->Unit={}) {
    private val destinations = mutableStateListOf(BioDestination(initialRoute, initialParam))
    private val navigator = HabiTreeNavigator(destinations)
    val backStack: List<BioDestination> get() = navigator.backStack
    val route: String get() = navigator.current.route.id
    var param: String
        get() = navigator.current.argument
        private set(value) {
            navigator.replaceCurrent(navigator.current.copy(argument = value))
        }
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
    var selectedCategory by mutableStateOf(AppText.canonical(TextKey.CopyHocTapA51516))
    var selectedTemplates=mutableStateListOf("study25","water")
    var dirty by mutableStateOf(false)
    var pendingRoute by mutableStateOf("tasks")
    var pendingParam by mutableStateOf("")
    var pendingBack by mutableStateOf(false)
    var pendingRoot by mutableStateOf(false)
    var world:BioWorld?=null
    val tabs=mutableStateMapOf("shop" to "seeds","inventory" to "seeds","tree" to "care","taskFilter" to "today","gardenFilter" to "all","insightDays" to "7")
    val fields=mutableStateMapOf<String,String>()
    private fun pauseFocus(){if(route=="focus"&&world?.state?.o("focus")?.s("state")=="RUNNING")send("focus-pause")}
    private fun saved():String=json(
        "route" to route,"param" to param,"modal" to modal,"modalId" to modalId,"homeTree" to homeTree,"quantity" to quantity,"targetTree" to targetTree,"plot" to plot,
        "step" to onboardingStep,"seed" to selectedSeed,"category" to selectedCategory,"templates" to org.json.JSONArray(selectedTemplates.toList()),
        "dirty" to dirty,"pendingRoute" to pendingRoute,"pendingParam" to pendingParam,"pendingBack" to pendingBack,"pendingRoot" to pendingRoot,
        "tabs" to JSONObject(tabs.toMap()),"fields" to JSONObject(fields.toMap()),
        "history" to org.json.JSONArray().apply{destinations.dropLast(1).forEach{put(org.json.JSONArray(listOf(it.route.id,it.argument)))}}
    ).toString()
    companion object {
        val Saver=Saver<BioController,String>(save={it.saved()},restore={raw->runCatching{
            val s=JSONObject(raw)
            BioController(s.s("route","home"),s.s("param"),s.s("modal").takeUnless{it=="confirm-import"}.orEmpty()).apply{
                modalId=s.s("modalId");homeTree=s.s("homeTree","0");quantity=s.i("quantity",1);targetTree=s.s("targetTree");plot=s.s("plot")
                onboardingStep=s.i("step");selectedSeed=s.s("seed","succulent");selectedCategory=s.s("category",AppText.canonical(TextKey.CopyHocTapA51516))
                selectedTemplates.clear();selectedTemplates.addAll(s.a("templates").strings())
                dirty=s.b("dirty");pendingRoute=s.s("pendingRoute","tasks");pendingParam=s.s("pendingParam");pendingBack=s.b("pendingBack");pendingRoot=s.b("pendingRoot")
                listOf("tabs" to tabs,"fields" to fields).forEach{(key,destination)->val values=s.o(key);values.keys().forEach{destination[it]=values.s(it)}}
                val entries=s.a("history");repeat(entries.length()){val entry=entries.getJSONArray(it);destinations.add(it,BioDestination(entry.optString(0),entry.optString(1)))}
            }
        }.getOrElse{BioController()}})
    }
    fun go(next:String,id:String="",root:Boolean=false,treeTab:String?=null) {
        if(route=="task-new"&&dirty&&next!="task-new"){pendingRoute=next;pendingParam=id;pendingBack=false;pendingRoot=root;show("discard");return}
        if(next!=route)pauseFocus()
        if(next=="focus"&&id.isNotBlank()&&(route!="focus"||id!=param)){fields.remove("focus:task");fields.remove("focus:minutes")}
        navigator.goTo(BioDestination(next, id), replaceStack = root)
        modal="";message=""
        if(next=="tree")tabs["tree"]=treeTab?:"care"
        if(next=="task-new")fields.clear()
    }
    fun back() {
        if (busy) return
        if (modal.isNotBlank()) {
            modal = ""
            return
        }
        if (route == "task-new" && dirty) {
            val previous = navigator.previous
            pendingRoute = previous?.route?.id ?: "tasks"
            pendingParam = previous?.argument.orEmpty()
            pendingBack = true
            pendingRoot = false
            show("discard")
            return
        }
        pauseFocus()
        navigator.goBack(BioDestination(BioRoute.HOME))
    }

    fun discard() {
        dirty = false
        fields.clear()
        modal = ""
        if (pendingBack) {
            navigator.goBack(BioDestination(pendingRoute, pendingParam))
        } else {
            if (!pendingRoot) navigator.goBack(BioDestination(BioRoute.HOME))
            go(pendingRoute, pendingParam, root = pendingRoot)
        }
        pendingBack = false
        pendingRoot = false
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
