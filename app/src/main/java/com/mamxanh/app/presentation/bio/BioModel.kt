package com.mamxanh.app.presentation.bio

import com.mamxanh.shared.localization.TextKey

import com.mamxanh.shared.localization.AppText

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

internal fun JSONObject.s(key:String, fallback:String="")=optString(key,fallback).takeUnless{it=="null"}?:fallback
internal fun JSONObject.i(key:String, fallback:Int=0)=optInt(key,fallback)
internal fun JSONObject.l(key:String, fallback:Long=0)=optLong(key,fallback)
internal fun JSONObject.b(key:String, fallback:Boolean=false)=optBoolean(key,fallback)
internal fun JSONObject.o(key:String)=optJSONObject(key)?:JSONObject()
internal fun JSONObject.a(key:String)=optJSONArray(key)?:JSONArray()
internal fun JSONArray.objects()=(0 until length()).mapNotNull{optJSONObject(it)}
internal fun JSONArray.strings()=(0 until length()).map{optString(it)}
internal fun bioTier(value:String)=when(value.lowercase()){"common"->AppText.text(TextKey.CopyPhoBien961e77);"rare"->AppText.text(TextKey.CopyHiem82557a);"legendary"->AppText.text(TextKey.CopyHuyenThoai538bf1);else->value}
internal fun json(vararg pairs:Pair<String,Any?>)=JSONObject().apply{pairs.forEach{(k,v)->put(k,v)}}

internal data class BioWorld(
    val data: JSONObject,
    val loading: Boolean = false,
    val native: Boolean = false,
    val inventoryQuantities: Map<String, Int>? = null,
) {
    val state get()=data.o("state")
    val catalog get()=data.o("catalog")
    val trees get()=state.a("trees").objects()
    val tasks get()=state.a("tasks").objects()
    val species get()=catalog.a("species").objects()
    val products get()=data.a("products").objects()
    val accessories get()=catalog.a("cosmetics").objects().filter{it.s("category")=="TREE"}
    val records get()=data.a("records").objects()
    val coins get()=state.i("coins")
    val settings get()=state.o("settings")
    fun species(id:String)=species.firstOrNull{it.s("id")==id}?:species.first()
    fun product(id:String)=(species+products+accessories).firstOrNull{it.s("id")==id}?:JSONObject()
    fun tree(id:String)=trees.firstOrNull{it.s("id")==id}
    fun task(id:String)=tasks.firstOrNull{it.s("id")==id}
    fun count(id:String)=state.o("inventory").i(id)
    fun owned(id:String)=state.o("owned").i(id)
    fun inventoryCount(id: String, accessory: Boolean): Int =
        if (accessory) owned(id) else inventoryQuantities?.getOrDefault(id, 0) ?: count(id)
    fun emptyPlots():List<Int> {
        val all=state.optJSONArray("openPlots")?.let{a->(0 until a.length()).map{a.optInt(it)}}?:(1..state.i("unlocked",5)).toList()
        return all.filter{p->trees.none{it.i("plot")==p}}
    }
    fun record(id:String)=records.firstOrNull{it.s("id")==id}?:JSONObject()
    companion object {
        fun reference(context:Context):BioWorld {
            val data=JSONObject(context.assets.open("bio_reference.json").bufferedReader().use{it.readText()})
            fun owned(items:List<JSONObject>,vararg fields:String){items.forEach{item->fields.forEach{field->if(item.has(field))item.put(field,AppText.builtIn(item.s(field)))}}}
            val catalog=data.o("catalog")
            owned(catalog.a("species").objects(),"name","description")
            owned(catalog.a("cosmetics").objects(),"name","description","footprint")
            owned(catalog.a("templates").objects(),"title","effort")
            owned(data.a("products").objects(),"name","description")
            owned(data.a("records").objects(),"status","firstPlanted","firstMatured")
            owned(data.a("goals").objects(),"title")
            owned(data.a("achievements").objects(),"title","criterion")
            owned(data.a("insights").objects(),"suggestion")
            owned(listOf(data.o("review")),"consistent","improve")
            val palettes=data.o("palettes");palettes.keys().forEach{key->owned(listOf(palettes.o(key)),"name")}
            owned(data.o("state").a("memories").objects(),"title")
            data.o("state").a("trees").objects().forEach { tree ->
                // Reference trees are app-provided sample defaults, not persisted user nicknames.
                val species=catalog.a("species").objects().firstOrNull { it.s("id")==tree.s("species") }
                tree.put("name",species?.s("name")?:AppText.plantName(tree.s("species"),tree.s("name")))
            }
            return BioWorld(data)
        }
    }
}

internal val bioRoutes get()=linkedMapOf("home" to AppText.text(TextKey.CopyHomNayE6747a),"tasks" to AppText.text(TextKey.CopyViec818768),"garden" to AppText.text(TextKey.CopyVuon9e4c10),"shop" to AppText.text(TextKey.CopyTiemF1ee00),"stats" to AppText.text(TextKey.CopyNhatKy1e6c3c),"task" to AppText.text(TextKey.CopyChiTietViecD9965a),"task-new" to AppText.text(TextKey.CopyThemSuaViec90d0ff),"inventory" to AppText.text(TextKey.CopyTuiDo6a8e55),"tree" to AppText.text(TextKey.CopyCayCuaBan64f9d7),"focus" to AppText.text(TextKey.CopyTapTrung964002),"templates" to AppText.text(TextKey.CopyViecMau535dc1),"review" to AppText.text(TextKey.CopyNhinLaiTuanFf3f0c),"rest" to AppText.text(TextKey.CopyLichNghi588c3f),"insights" to AppText.text(TextKey.CopyNhipThoiQuen1b8324),"achievements" to AppText.text(TextKey.CopyDauMocDffd66),"timeline" to AppText.text(TextKey.CopyKyNiemE3e113),"settings" to AppText.text(TextKey.CopyTuyChon598c1b),"data" to AppText.text(TextKey.CopyDuLieuUngDung9843b7),"encyclopedia" to AppText.text(TextKey.CopyBachThaoF2ed64),"species" to AppText.text(TextKey.CopyHoSoLoaiCfff22),"onboard" to AppText.text(TextKey.CopyBatDauB3cacc),"more" to AppText.text(TextKey.RouteMore))

internal data class BioCommand(val action:String,val values:Map<String,String> = emptyMap())
internal data class BioResult(val success:Boolean,val message:String="",val route:String="",val param:String="")
