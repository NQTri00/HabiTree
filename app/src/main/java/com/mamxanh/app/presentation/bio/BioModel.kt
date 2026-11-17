package com.mamxanh.app.presentation.bio

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
internal fun bioTier(value:String)=when(value.lowercase()){"common"->"Phổ biến";"rare"->"Hiếm";"legendary"->"Huyền thoại";else->value}
internal fun bioDisplayDate(value:String):String {
    if(!Regex("\\d{4}-\\d{2}-\\d{2}").matches(value))return value
    return value.split('-').reversed().joinToString("/")
}
internal fun json(vararg pairs:Pair<String,Any?>)=JSONObject().apply{pairs.forEach{(k,v)->put(k,v)}}

internal data class BioWorld(val data:JSONObject, val loading:Boolean=false, val native:Boolean=false) {
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
    fun emptyPlots():List<Int> {
        val all=state.optJSONArray("openPlots")?.let{a->(0 until a.length()).map{a.optInt(it)}}?:(1..state.i("unlocked",5)).toList()
        return all.filter{p->trees.none{it.i("plot")==p}}
    }
    fun record(id:String)=records.firstOrNull{it.s("id")==id}?:JSONObject()
    companion object {
        fun reference(context:Context)=BioWorld(JSONObject(context.assets.open("bio_reference.json").bufferedReader().use{it.readText()}))
    }
}

internal val bioRoutes=linkedMapOf("home" to "Hôm nay","tasks" to "Việc","garden" to "Vườn","shop" to "Tiệm","stats" to "Nhật ký","task" to "Chi tiết việc","task-new" to "Thêm / sửa việc","inventory" to "Túi đồ","tree" to "Cây của bạn","focus" to "Tập trung","templates" to "Việc mẫu","review" to "Nhìn lại tuần","rest" to "Lịch nghỉ","insights" to "Nhịp thói quen","achievements" to "Dấu mốc","timeline" to "Kỷ niệm","settings" to "Tùy chọn","data" to "Dữ liệu & ứng dụng","encyclopedia" to "Bách thảo","species" to "Hồ sơ loài","onboard" to "Bắt đầu","more" to "Thêm")

internal data class BioCommand(val action:String,val values:Map<String,String> = emptyMap())
internal data class BioResult(val success:Boolean,val message:String="",val route:String="",val param:String="")
