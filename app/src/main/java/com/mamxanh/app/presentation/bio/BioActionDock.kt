package com.mamxanh.app.presentation.bio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable internal fun BioActionDock(content:@Composable ()->Unit){
    Column(Modifier.fillMaxWidth().background(bp.bg)){
        HorizontalDivider(color=bp.line)
        Column(Modifier.fillMaxWidth().padding(horizontal=20.dp,vertical=12.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){content()}
    }
}

internal fun taskFormInitial(world:BioWorld,ui:BioController)=world.task(ui.param)?:json("title" to "","kind" to "DAILY","category" to "Học tập","frequency" to "EVERY_DAY","verification" to "CLICK","minutes" to 5)
internal fun taskFormValue(world:BioWorld,ui:BioController,key:String,default:String="")=ui.fields[key]?:taskFormInitial(world,ui).s(key,default)

@Composable internal fun BioTaskFormActions(world:BioWorld,ui:BioController){
    fun value(key:String,default:String="")=taskFormValue(world,ui,key,default)
    Row(horizontalArrangement=Arrangement.spacedBy(12.dp)){
        BioButton("Hủy",{ui.back()},Modifier.weight(1f),secondary=true,enabled=!ui.busy)
        BioButton("Lưu việc",{
            val error=when{
                value("title").isBlank()->"title"
                value("verification")=="TIMER"&&(value("minutes","5").toIntOrNull()?:0) !in 5..240->"minutes"
                value("kind")!="TODO"&&value("frequency")=="CUSTOM"&&value("days").isBlank()->"days"
                else->""
            }
            ui.fields["task-error"]=error
            ui.fields["task-error-attempt"]=((ui.fields["task-error-attempt"]?.toIntOrNull()?:0)+1).toString()
            if(error.isBlank())ui.send("save-task",listOf("title","kind","category","frequency","verification","minutes","description","days","due","start","end","reminder").associateWith{value(it)}+mapOf("id" to ui.param))
        },Modifier.weight(1f),enabled=!ui.busy)
    }
}

internal fun selectedBioTemplates(ui:BioController)=ui.fields.filter{it.key.startsWith("template:")&&it.value=="true"}.keys.map{it.removePrefix("template:")}
@Composable internal fun BioTemplateActions(ui:BioController){
    val chosen=selectedBioTemplates(ui)
    BioButton(if(chosen.isEmpty())"Chọn việc để thêm"else "Thêm ${chosen.size} việc",{ui.send("templates",mapOf("ids" to chosen.joinToString(",")))},Modifier.fillMaxWidth(),enabled=chosen.isNotEmpty()&&!ui.busy)
}

internal fun focusTask(ui:BioController)=ui.fields["focus:task"]?:ui.param.ifBlank{"0"}
internal fun focusMinutes(world:BioWorld,ui:BioController)=world.task(focusTask(ui))?.i("minutes")?.toString()?:ui.fields["focus:minutes"]?:"25"
@Composable internal fun BioFocusActions(world:BioWorld,ui:BioController){
    val focus=world.state.o("focus")
    if(focus.s("id").isNotBlank()){
        val running=focus.s("state")=="RUNNING"
        Row(horizontalArrangement=Arrangement.spacedBy(12.dp)){
            BioButton(if(running)"Tạm dừng"else "Tiếp tục",{ui.send("focus-toggle")},Modifier.weight(1f),if(running)"pause"else "play",enabled=!ui.busy)
            BioButton("Kết thúc",{ui.show("focus-end")},Modifier.weight(1f),secondary=true,enabled=!ui.busy)
        }
    }else{
        val task=world.task(focusTask(ui))
        BioButton("Bắt đầu",{ui.send("start-focus",mapOf("minutes" to if(task!=null)"25"else focusMinutes(world,ui),"task" to focusTask(ui)))},Modifier.fillMaxWidth(),"play",enabled=(if(task!=null)task.b("available",true)&&!task.b("done") else (focusMinutes(world,ui).toIntOrNull()?:0)in 5..120)&&!ui.busy)
    }
}
