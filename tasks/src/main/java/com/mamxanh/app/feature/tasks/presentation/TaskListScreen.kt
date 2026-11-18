package com.mamxanh.app.feature.tasks.presentation

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.*
import com.mamxanh.app.presentation.ui.design.*
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

data class TaskListItem(
    val id: String,
    val title: String,
    val category: String,
    val minutes: Int,
    val reward: Int,
    val done: Boolean,
    val timer: Boolean,
    val active: Boolean,
)

data class TaskListState(
    val filter: String,
    val items: List<TaskListItem>,
    val completed: Int,
    val required: Int,
    val earned: Int,
    val busy: Boolean,
)

sealed interface TaskListAction {
    data object Add : TaskListAction
    data object Focus : TaskListAction
    data object Templates : TaskListAction
    data object Insights : TaskListAction
    data class Filter(val value: String) : TaskListAction
    data class Open(val id: String) : TaskListAction
    data class Complete(val id: String, val timer: Boolean) : TaskListAction
}

@Composable
fun TaskListScreen(state: TaskListState, onAction: (TaskListAction) -> Unit, reward: @Composable () -> Unit) {
    val filter = state.filter
    BioIntro(when(filter){"paused"->AppText.text(TextKey.CopyViecTamDungB756a3);"archived"->AppText.text(TextKey.CopyViecLuuTru911d4f);else->AppText.text(TextKey.CopyViecHomNay22a0a4)},AppText.text(TextKey.CopyTungChutMot238966),action={BioIconButton("plus",{onAction(TaskListAction.Add)})})
    if(filter=="today")BioCard(Modifier.fillMaxWidth(),bp.soft,padding=AppSpacing.Value18){Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){Bt(AppText.text(TextKey.Copy01ViecDaXongCf7a5d,state.completed,state.required),Modifier.weight(1f),size=AppTypography.Size14,semi=true);BioTag(AppText.text(TextKey.Copy0Xu7c2391,state.earned),"spark")};Spacer(Modifier.height(AppDimensions.Dp12));BioProgress(if(state.required>0)state.completed.toFloat()/state.required else 0f)}
    Row(horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){BioButton(AppText.text(TextKey.CopyTapTrung964002),{onAction(TaskListAction.Focus)},Modifier.weight(1f),"clock",true);BioButton(AppText.text(TextKey.CopyViecMau535dc1),{onAction(TaskListAction.Templates)},Modifier.weight(1f),"plus",true)}
    BioChips(listOf("today" to AppText.text(TextKey.CopyHomNayE6747a),"paused" to AppText.text(TextKey.TabPaused),"archived" to AppText.text(TextKey.CopyLuuTru7e7c5f)),filter){onAction(TaskListAction.Filter(it))}
    val list = state.items
    Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){if(list.isEmpty())BioEmpty(when(filter){"paused"->AppText.text(TextKey.CopyKhongCoViecTamDung074795);"archived"->AppText.text(TextKey.CopyChuaCoViecLuuTru3a7d32);else->AppText.text(TextKey.CopyChuaCoViecE6f37e)},if(filter=="today")AppText.text(TextKey.CopyBatDauBangMotViecVuaSucA10a5a)else AppText.text(TextKey.CopyCacViecCuaBanVanOHomNayB04085),"tasks",if(filter=="today")AppText.text(TextKey.CopyThemViecC371c2)else AppText.text(TextKey.CopyVeHomNay08154e),{if(filter=="today")onAction(TaskListAction.Add)else onAction(TaskListAction.Filter("today"))})else list.forEach{TaskListRow(it, state.busy, { onAction(TaskListAction.Open(it.id)) }, { onAction(TaskListAction.Complete(it.id, it.timer)) })}}
    if(list.isNotEmpty()&&filter=="today"){reward();BioButton(AppText.text(TextKey.CopyThemViecC371c2),{onAction(TaskListAction.Add)},Modifier.fillMaxWidth(),"plus")}
    BioLink(AppText.text(TextKey.CopyNhipThoiQuen1b8324),"chart",{onAction(TaskListAction.Insights)})
}

@Composable
fun TaskListRow(task: TaskListItem, busy: Boolean, onOpen: () -> Unit, onComplete: () -> Unit) {
    val done = task.done
    val timer = task.timer
    BioCard(Modifier.fillMaxWidth(),padding=AppSpacing.Value14,radius=AppRadii.Value22,tint=if(done)lerp(AppColors.White,bp.soft,.7f)else bp.surface){
        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){
            Box(Modifier.size(AppDimensions.Dp38).background(bp.soft,bioShape(AppRadii.Value12)),contentAlignment=Alignment.Center){Bi(if(timer)"clock" else if(task.category==AppText.canonical(TextKey.CopySucKhoe071df7))"water" else "book")}
            Column(Modifier.weight(1f).heightIn(min=AppDimensions.Dp48).clickable(onClick = onOpen),verticalArrangement=Arrangement.Center){
                if(done)Text(task.title,color=bp.muted,fontFamily=com.mamxanh.app.presentation.ui.GardenFontFamily,fontSize=AppTypography.Sp13,fontWeight=androidx.compose.ui.text.font.FontWeight.SemiBold,textDecoration=androidx.compose.ui.text.style.TextDecoration.LineThrough)
                else Bt(task.title,size=AppTypography.Size14,line=AppTypography.LineHeight21,semi=true)
                Bt(if(done)AppText.text(TextKey.CopyDaXong565e93) else AppText.category(task.category)+(if(timer)AppText.text(TextKey.Copy0PhutC0bcb0,task.minutes)else ""),size=AppTypography.Size11,tint=bp.muted)
            }
            Bt("+${task.reward}",size=AppTypography.Size10,tint=AppColors.SwatchFF946A0E)
            Box(Modifier.size(AppDimensions.Dp48).background(if(done)bp.deep else bp.soft.copy(alpha=AppOpacity.Value_45),RoundedCornerShape(50)).border(AppDimensions.Dp1,if(done)bp.deep else bp.control,RoundedCornerShape(50)).semantics{contentDescription=if(done)AppText.text(TextKey.CopyDaHoanThanh1911fc)+task.title else if(timer)AppText.text(TextKey.CopyTapTrung71ae4b)+task.title else AppText.text(TextKey.CopyHoanThanhF183ae)+task.title}.clickable(enabled=!done&&task.active&&!busy,role=Role.Button,onClick=onComplete),contentAlignment=Alignment.Center){Bi(if(timer&&!done)"play"else "check",tint=if(done)AppColors.White else bp.deep)}
        }
    }
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun TaskListScreenPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp18)) {
        TaskListScreen(TaskListState("today", emptyList(), 0, 4, 0, false), {}, {})
    }
}
