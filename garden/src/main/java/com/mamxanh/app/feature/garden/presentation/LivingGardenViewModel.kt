package com.mamxanh.app.feature.garden.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mamxanh.shared.data.GameStorage
import com.mamxanh.shared.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class LivingGardenUiState(val snapshot:LivingGardenSnapshot=LivingGardenSnapshot(),val garden:Garden=Garden(),
    val route:String="",val selected:String="",val draft:List<GardenPlacement>?=null,val draftVersion:Long=0,
    val busy:Boolean=false,val message:String="",val loading:Boolean=true,val undoAvailable:Boolean=false)
class LivingGardenViewModel(private val storage:GameStorage,private val saved:SavedStateHandle):ViewModel() {
    private val restored=saved.get<ArrayList<String>>("layout_draft")?.mapNotNull {
        val p=it.split('|');if(p.size==4)GardenPlacement(p[0],p[1],p[2].toInt(),p[3].toLong())else null
    }
    private val _state=MutableStateFlow(LivingGardenUiState(route=saved["living_route"] ?: "",
        selected=saved["living_selected"] ?: "",draft=restored,draftVersion=saved["layout_version"] ?: 0L))
    val state=_state.asStateFlow()
    private val history=mutableListOf<List<GardenPlacement>>()
    init {viewModelScope.launch{storage.changes.collect{if(_state.value.route.isNotBlank() || _state.value.loading)refresh()}}}
    fun discover(ids:List<String>){viewModelScope.launch{withContext(Dispatchers.IO){storage.designGarden(LivingGardenAction.Discover(ids))}}}
    fun refreshVisible(){if(_state.value.route.isNotBlank() && !_state.value.busy)viewModelScope.launch{refresh()}}
    private suspend fun refresh() {
        val pair=withContext(Dispatchers.IO){storage.garden() to storage.livingGarden()}
        _state.value=_state.value.copy(garden=pair.first,snapshot=pair.second,loading=false)
    }
    fun open(route:String,selected:String="") {
        val current=_state.value
        val stack=saved.get<ArrayList<String>>("living_backstack") ?: arrayListOf()
        if(route.isBlank())stack.clear()
        else if(current.route.isNotBlank() && (current.route!=route || current.selected!=selected)) {
            stack.add(current.route+"|"+current.selected)
        }
        saved["living_backstack"]=stack
        show(route,selected)
    }
    fun back() {
        val stack=saved.get<ArrayList<String>>("living_backstack") ?: arrayListOf()
        val previous=if(stack.isEmpty())null else stack.removeAt(stack.lastIndex).split("|",limit=2)
        saved["living_backstack"]=stack
        show(previous?.get(0) ?: "",previous?.getOrNull(1) ?: "")
    }
    private fun show(route:String,selected:String="") {
        saved["living_route"]=route;saved["living_selected"]=selected
        _state.value=_state.value.copy(route=route,selected=selected,message="")
        if(route.isNotBlank()) {
            _state.value=_state.value.copy(loading=true)
            viewModelScope.launch {
                refresh()
                if(_state.value.route=="ARRANGE" && _state.value.draft==null) {
                    history.clear()
                    _state.value=_state.value.copy(draft=_state.value.snapshot.placements,draftVersion=_state.value.snapshot.version,undoAvailable=false)
                    persist()
                }
            }
        }
    }
    fun resetAfterRestore() {
        history.clear();saved.remove<ArrayList<String>>("layout_draft");saved.remove<Long>("layout_version")
        _state.value=_state.value.copy(draft=null,draftVersion=0,undoAvailable=false,selected="")
        open("")
    }
    fun cancel(){history.clear();saved.remove<ArrayList<String>>("layout_draft");_state.value=_state.value.copy(draft=null,undoAvailable=false);back()}
    private fun persist() {
        saved["layout_draft"]=_state.value.draft?.map{listOf(it.id,it.sku,it.plotId,it.treeId).joinToString("|")}?.let{ArrayList(it)}
        saved["layout_version"]=_state.value.draftVersion
    }
    private fun change(next:List<GardenPlacement>) {
        history.add(_state.value.draft ?: return)
        _state.value=_state.value.copy(draft=next,undoAvailable=true,message="");persist()
    }
    fun undo(){if(history.isNotEmpty()){_state.value=_state.value.copy(draft=history.removeAt(history.lastIndex),undoAvailable=history.isNotEmpty());persist()}}
    fun select(sku:String){saved["living_selected"]=sku;_state.value=_state.value.copy(selected=sku,message="")}
    fun place(plot:Int,moving:String?=null) {
        val state=_state.value
        val draft=state.draft ?: return
        val existing=draft.firstOrNull{it.id==moving}
        val sku=existing?.sku ?: state.selected
        val item=gardenCosmetics.firstOrNull{it.id==sku} ?: return
        val target=state.garden.plots.firstOrNull{it.id==plot}
        val tree=state.garden.trees.firstOrNull{it.id==target?.treeId}
        val base=draft.filterNot{it.id==moving}
        val error=when {
            item.slot=="THEME"->"Chọn chủ đề tại Diện mạo"
            item.slot!="AMBIENT" && target?.isUnlocked!=true->"Ô đang khóa"
            item.slot=="TREE" && tree?.status?.alive!=true->"Chọn ô có cây còn sống"
            base.count{it.sku==sku} >= (state.snapshot.owned[sku] ?: 0)->"Vật phẩm đã được đặt hết"
            base.any{it.plotId==(if(item.slot=="AMBIENT")0 else plot) && gardenCosmetics.firstOrNull{c->c.id==it.sku}?.slot==item.slot}->"Ô này đã có vật phẩm cùng loại"
            else->null
        }
        if(error!=null){_state.value=state.copy(message=error);return}
        change(base+GardenPlacement(existing?.id ?: java.util.UUID.randomUUID().toString(),sku,
            if(item.slot=="AMBIENT")0 else plot,if(item.slot=="TREE")tree!!.id else 0))
    }
    fun remove(id:String){change((_state.value.draft ?: return).filterNot{it.id==id})}
    fun save(){val s=_state.value;act(LivingGardenAction.Layout(s.draftVersion,s.draft ?: return,java.util.UUID.randomUUID().toString()))}
    fun act(action:LivingGardenAction) {
        if(_state.value.busy || _state.value.loading && action !is LivingGardenAction.Discover)return
        _state.value=_state.value.copy(busy=true)
        viewModelScope.launch {
            try {
                val result=withContext(Dispatchers.IO){storage.designGarden(action)}
                refresh()
                if(result.success && action is LivingGardenAction.Layout){cancel()}
                _state.value=_state.value.copy(message=result.message)
            } finally {_state.value=_state.value.copy(busy=false)}
        }
    }
}
