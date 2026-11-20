package com.mamxanh.app.feature.tasks.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.presentation.ui.design.*
import com.mamxanh.core.ui.R
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

@Composable fun TemplateRow(t: TaskTemplateDisplay,selected:Boolean,onboarding:Boolean=false,onChange:()->Unit){
    BioCard(Modifier.fillMaxWidth().semantics{toggleableState=if(selected)ToggleableState.On else ToggleableState.Off;role=Role.Checkbox},padding=if(onboarding)12 else 14,radius=AppRadii.Value18,tint=if(selected)bp.soft else bp.surface,onClick=onChange,borderColor=if(selected)bp.deep else bp.line){Row(Modifier.fillMaxWidth().heightIn(min=if(onboarding)AppDimensions.Dp65 else AppDimensions.Dp60),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp10)){
        if(!onboarding)Box(Modifier.width(AppDimensions.Dp20).height(AppDimensions.Dp28).background(bp.soft,bioShape(AppRadii.Value8)),contentAlignment=Alignment.Center){Bi(if(t.timer)"clock"else "leaf",size=AppIcons.Size20)}
        Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp4)){
            Bt(t.title,size=AppTypography.Size13,semi=true)
            Bt(AppText.category(t.category)+" · "+(if(t.timer)stringResource(R.string.copy_bo_dem_0_phut_a99be5, t.minutes)else AppText.text(TextKey.CopyChamF4623f)+t.effort),size=AppTypography.Size12,tint=bp.muted)
            Bt((if(t.weekends)stringResource(R.string.copy_cuoi_tuan_a0fb27)else stringResource(R.string.copy_moi_ngay_8270cb))+stringResource(R.string.copy_0_xu_9c90c1, t.reward),size=AppTypography.Size12,tint=bp.muted)
        }
        Box(Modifier.size(AppDimensions.Dp20).background(if(selected)bp.deep else AppColors.Transparent,RoundedCornerShape(AppDimensions.Dp3)).border(AppDimensions.Dp1,if(selected)bp.deep else bp.control,RoundedCornerShape(AppDimensions.Dp3)),contentAlignment=Alignment.Center){if(selected)Bi("check",size=AppIcons.Size14,tint=AppColors.White)}
    }}
}
@Composable
fun TaskTemplatesScreen(
    templates: List<TaskTemplateDisplay>,
    chosen: Set<String>,
    onToggle: (String) -> Unit,
    actions: (@Composable () -> Unit)? = null,
) {
    Column{Bt(stringResource(R.string.copy_chon_viec_vua_suc_895fff),size=AppTypography.Size27,line=AppTypography.LineHeight35_1,bold=true,spacing=AppTypography.TrackingMinus1);Bt(stringResource(R.string.copy_them_mot_lan_chinh_sua_bat_cu_luc_nao_55b241),size=AppTypography.Size12,tint=bp.muted)}
    Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){templates.forEach { template -> TemplateRow(template, template.id in chosen) { onToggle(template.id) } }}
    actions?.invoke()
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun TaskTemplatesScreenPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp18)) {
        TaskTemplatesScreen(listOf(TaskTemplateDisplay("reading", "Read ten pages", "Study", 0, "Easy", false, false, 8)), setOf("reading"), {})
    }
}
