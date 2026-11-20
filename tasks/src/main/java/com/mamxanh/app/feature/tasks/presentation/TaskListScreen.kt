package com.mamxanh.app.feature.tasks.presentation

import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.*
import com.mamxanh.app.presentation.ui.design.*
import com.mamxanh.core.ui.R
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

@Composable
fun TaskListScreen(state: TaskListState, onAction: (TaskListAction) -> Unit, reward: @Composable () -> Unit) {
    val filter = state.filter
    BioIntro(when(filter){"paused"->stringResource(R.string.copy_viec_tam_dung_b756a3);"archived"->stringResource(R.string.copy_viec_luu_tru_911d4f);else->stringResource(R.string.copy_viec_hom_nay_22a0a4)},stringResource(R.string.copy_tung_chut_mot_238966),action={BioIconButton("plus",{onAction(TaskListAction.Add)})})
    if (filter == "today") {
        BioCard(Modifier.fillMaxWidth(), bp.soft, padding = AppSpacing.Value18) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp12),
            ) {
                val progressLabel = pluralStringResource(
                    R.plurals.copy_0_1_viec_da_xong_cf7a5d,
                    state.required,
                    state.completed,
                    state.required,
                )
                Bt(progressLabel, Modifier.weight(1f), size = AppTypography.Size14, semi = true)
                BioTag(AppText.text(TextKey.Copy0Xu7c2391, state.earned), "spark")
            }
            Spacer(Modifier.height(AppDimensions.Dp12))
            BioProgress(if (state.required > 0) state.completed.toFloat() / state.required else 0f)
        }
    }
    Row(horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){BioButton(stringResource(R.string.copy_tap_trung_964002),{onAction(TaskListAction.Focus)},Modifier.weight(1f),"clock",true);BioButton(stringResource(R.string.copy_viec_mau_535dc1),{onAction(TaskListAction.Templates)},Modifier.weight(1f),"plus",true)}
    BioChips(listOf("today" to stringResource(R.string.copy_hom_nay_e6747a),"paused" to AppText.text(TextKey.TabPaused),"archived" to stringResource(R.string.copy_luu_tru_7e7c5f)),filter){onAction(TaskListAction.Filter(it))}
    val list = state.items
    Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){if(list.isEmpty())BioEmpty(when(filter){"paused"->stringResource(R.string.copy_khong_co_viec_tam_dung_074795);"archived"->stringResource(R.string.copy_chua_co_viec_luu_tru_3a7d32);else->stringResource(R.string.copy_chua_co_viec_e6f37e)},if(filter=="today")stringResource(R.string.copy_bat_dau_bang_mot_viec_vua_suc_a10a5a)else stringResource(R.string.copy_cac_viec_cua_ban_van_o_hom_nay_b04085),"tasks",if(filter=="today")stringResource(R.string.copy_them_viec_c371c2)else stringResource(R.string.copy_ve_hom_nay_08154e),{if(filter=="today")onAction(TaskListAction.Add)else onAction(TaskListAction.Filter("today"))})else list.forEach{TaskListRow(it, state.busy, { onAction(TaskListAction.Open(it.id)) }, { onAction(TaskListAction.Complete(it.id, it.timer)) })}}
    if(list.isNotEmpty()&&filter=="today"){reward();BioButton(stringResource(R.string.copy_them_viec_c371c2),{onAction(TaskListAction.Add)},Modifier.fillMaxWidth(),"plus")}
    BioLink(stringResource(R.string.copy_nhip_thoi_quen_1b8324),"chart",{onAction(TaskListAction.Insights)})
}

@Composable
fun TaskListRow(task: TaskListItem, busy: Boolean, onOpen: () -> Unit, onComplete: () -> Unit) {
    val done = task.done
    val timer = task.timer
    val completionLabel = when {
        done -> stringResource(R.string.copy_da_hoan_thanh_1911fc, task.title)
        timer -> stringResource(R.string.copy_tap_trung_71ae4b, task.title)
        else -> stringResource(R.string.copy_hoan_thanh_f183ae, task.title)
    }
    BioCard(Modifier.fillMaxWidth(),padding=AppSpacing.Value14,radius=AppRadii.Value22,tint=if(done)lerp(AppColors.White,bp.soft,.7f)else bp.surface){
        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){
            Box(Modifier.size(AppDimensions.Dp38).background(bp.soft,bioShape(AppRadii.Value12)),contentAlignment=Alignment.Center){Bi(if(timer)"clock" else if(task.category==AppText.canonical(TextKey.CopySucKhoe071df7))"water" else "book")}
            Column(Modifier.weight(1f).heightIn(min=AppDimensions.Dp48).clickable(onClick = onOpen),verticalArrangement=Arrangement.Center){
                if(done)Text(task.title,color=bp.muted,fontFamily=com.mamxanh.app.presentation.ui.GardenFontFamily,fontSize=AppTypography.Sp13,fontWeight=androidx.compose.ui.text.font.FontWeight.SemiBold,textDecoration=androidx.compose.ui.text.style.TextDecoration.LineThrough)
                else Bt(task.title,size=AppTypography.Size14,line=AppTypography.LineHeight21,semi=true)
                Bt(if(done)stringResource(R.string.copy_da_xong_565e93) else AppText.category(task.category)+(if(timer)stringResource(R.string.copy_0_phut_c0bcb0, task.minutes)else ""),size=AppTypography.Size11,tint=bp.muted)
            }
            Bt("+${task.reward}",size=AppTypography.Size10,tint=AppColors.SwatchFF946A0E)
            Box(
                Modifier.size(AppDimensions.Dp48)
                    .semantics { contentDescription = completionLabel }
                    .clickable(enabled = !done && task.active && !busy, role = Role.Button, onClick = onComplete),
                contentAlignment = Alignment.CenterEnd,
            ) {
                val shape = RoundedCornerShape(percent = 50)
                Box(
                    Modifier.width(AppDimensions.Dp44).fillMaxHeight()
                        .background(if (done) bp.deep else AppColors.Transparent, shape)
                        .border(AppDimensions.Dp1, if (done) bp.deep else bp.control, shape),
                    contentAlignment = Alignment.Center,
                ) {
                    if (done) Bi("check", tint = AppColors.White)
                    else if (timer) Bi("play", tint = bp.deep)
                }
            }
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
