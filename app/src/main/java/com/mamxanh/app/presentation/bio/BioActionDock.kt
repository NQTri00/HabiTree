package com.mamxanh.app.presentation.bio

import com.mamxanh.app.presentation.ui.design.*

import com.mamxanh.app.presentation.ui.design.AppDimensions
import com.mamxanh.app.feature.tasks.presentation.TaskFormField

import com.mamxanh.shared.localization.TextKey

import com.mamxanh.shared.localization.AppText

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable internal fun BioActionDock(content:@Composable ()->Unit){
    Column(Modifier.fillMaxWidth().background(bp.bg)){
        HorizontalDivider(color=bp.line)
        Column(Modifier.fillMaxWidth().padding(horizontal=AppDimensions.Dp20,vertical=AppDimensions.Dp12),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp8)){content()}
    }
}

internal fun taskFormInitial(world:BioWorld,argument:String)=world.task(argument)?:json("title" to "","kind" to "DAILY","category" to AppText.canonical(TextKey.CopyHocTapA51516),"frequency" to "EVERY_DAY","verification" to "CLICK","minutes" to 5)
private fun taskFormValue(world: BioWorld, ui: BioController, field: TaskFormField, default: String = "") =
    ui.taskDraft[field] ?: taskFormInitial(world, ui.param).s(field.savedKey, default)

@Composable internal fun BioTaskFormActions(world:BioWorld,ui:BioController){
    fun value(field: TaskFormField) =
        taskFormValue(world, ui, field, if (field == TaskFormField.Minutes) "5" else "")
    Row(horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){
        BioButton(AppText.text(TextKey.CopyHuy74fcd3),{ui.back()},Modifier.weight(1f),secondary=true,enabled=!ui.busy)
        BioButton(AppText.text(TextKey.CopyLuuViec522a3d),{
            if (ui.taskDraft.validate(::value)) {
                val values = TaskFormField.entries.associate { field ->
                    field.savedKey to taskFormValue(world, ui, field)
                }
                ui.send("save-task", values + ("id" to ui.param))
            }
        },Modifier.weight(1f),enabled=!ui.busy)
    }
}

internal fun selectedBioTemplates(ui:BioController)=ui.taskSelection.selectedTemplates()
@Composable internal fun BioTemplateActions(ui:BioController){
    val chosen=selectedBioTemplates(ui)
    BioButton(if(chosen.isEmpty())AppText.text(TextKey.CopyChonViecDeThemA9f4fc)else AppText.text(TextKey.CopyThem0ViecD0390c,chosen.size),{ui.send("templates",mapOf("ids" to chosen.joinToString(",")))},Modifier.fillMaxWidth(),enabled=chosen.isNotEmpty()&&!ui.busy)
}

internal fun focusTask(ui:BioController,argument:String=ui.param)=ui.taskSelection.focusTask?:argument.ifBlank{"0"}
internal fun focusMinutes(world:BioWorld,ui:BioController,argument:String=ui.param)=world.task(focusTask(ui,argument))?.i("minutes")?.toString()?:ui.taskSelection.focusMinutes?:"25"
@Composable internal fun BioFocusActions(world:BioWorld,ui:BioController){
    val focus=world.state.o("focus")
    if(focus.s("id").isNotBlank()){
        val running=focus.s("state")=="RUNNING"
        Row(horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){
            BioButton(if(running)AppText.text(TextKey.CopyTamDung676358)else AppText.text(TextKey.CopyTiepTuc87a7ae),{ui.send("focus-toggle")},Modifier.weight(1f),if(running)"pause"else "play",enabled=!ui.busy)
            BioButton(AppText.text(TextKey.CopyKetThuc351849),{ui.show("focus-end")},Modifier.weight(1f),secondary=true,enabled=!ui.busy)
        }
    }else{
        val task=world.task(focusTask(ui))
        BioButton(AppText.text(TextKey.CopyBatDauB3cacc),{ui.send("start-focus",mapOf("minutes" to if(task!=null)"25"else focusMinutes(world,ui),"task" to focusTask(ui)))},Modifier.fillMaxWidth(),"play",enabled=(if(task!=null)task.b("available",true)&&!task.b("done") else (focusMinutes(world,ui).toIntOrNull()?:0)in 5..120)&&!ui.busy)
    }
}
