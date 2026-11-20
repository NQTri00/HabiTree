package com.mamxanh.app.presentation.bio

import androidx.compose.ui.res.stringResource
import com.mamxanh.core.ui.R
import com.mamxanh.app.presentation.ui.design.*

import com.mamxanh.app.presentation.ui.design.AppDimensions
import com.mamxanh.app.feature.tasks.presentation.FocusDock
import com.mamxanh.app.feature.tasks.presentation.FocusDockAction
import com.mamxanh.app.feature.tasks.presentation.FocusDockState
import com.mamxanh.app.feature.tasks.presentation.TaskFormField

import com.mamxanh.shared.localization.TextKey

import com.mamxanh.shared.localization.AppText

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

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
        BioButton(stringResource(R.string.copy_huy_74fcd3),{ui.back()},Modifier.weight(1f),secondary=true,enabled=!ui.busy)
        BioButton(stringResource(R.string.copy_luu_viec_522a3d),{
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
    BioButton(if(chosen.isEmpty())stringResource(R.string.copy_chon_viec_de_them_a9f4fc)else stringResource(R.string.copy_them_0_viec_d0390c, chosen.size),{ui.send("templates",mapOf("ids" to chosen.joinToString(",")))},Modifier.fillMaxWidth(),enabled=chosen.isNotEmpty()&&!ui.busy)
}

internal fun focusTask(ui:BioController,argument:String=ui.param)=ui.taskSelection.focusTask?:argument.ifBlank{"0"}
internal fun focusMinutes(world:BioWorld,ui:BioController,argument:String=ui.param)=world.task(focusTask(ui,argument))?.i("minutes")?.toString()?:ui.taskSelection.focusMinutes?:"25"
@Composable internal fun BioFocusActions(world:BioWorld,ui:BioController){
    val focus = world.state.o("focus")
    val taskId = focusTask(ui)
    val task = world.task(taskId)
    val minutes = focusMinutes(world, ui)
    FocusDock(
        FocusDockState(
            hasSession = focus.s("id").isNotBlank(),
            running = focus.s("state") == "RUNNING",
            canStart = task?.let { it.b("available", true) && !it.b("done") }
                ?: ((minutes.toIntOrNull() ?: 0) in 5..120),
            busy = ui.busy,
        ),
    ) { action ->
        when (action) {
            FocusDockAction.Start -> ui.send(
                "start-focus", mapOf("minutes" to (if (task != null) "25" else minutes), "task" to taskId),
            )
            FocusDockAction.Toggle -> ui.send("focus-toggle")
            FocusDockAction.End -> ui.show("focus-end")
        }
    }
}

@Preview(name = "Focus action dock", widthDp = 375, showBackground = true)
@Composable
private fun BioActionDockPreview() {
    BioActionDock {
        FocusDock(FocusDockState(hasSession = false, running = false, canStart = true, busy = false)) {}
    }
}
