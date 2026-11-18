package com.mamxanh.app.feature.home.presentation

import com.mamxanh.app.presentation.ui.design.*

import com.mamxanh.app.presentation.ui.design.AppColors
import com.mamxanh.app.presentation.ui.design.AppDimensions
import com.mamxanh.app.presentation.ui.design.AppRadii
import com.mamxanh.app.presentation.ui.design.AppSpacing
import com.mamxanh.app.presentation.ui.design.AppTypography

import com.mamxanh.shared.localization.TextKey

import com.mamxanh.shared.localization.AppText

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.tooling.preview.Preview

data class OnboardingDisplay(
    val step: Int,
    val seed: String,
    val category: String,
    val selectedCount: Int,
    val selectedTitles: List<String>,
    val seedNames: Map<String, String>,
    val templateCount: Int,
    val remind: Boolean,
)

sealed interface OnboardingAction {
    data object Back : OnboardingAction
    data object Next : OnboardingAction
    data object Skip : OnboardingAction
    data class Choose(val step: Int, val id: String) : OnboardingAction
    data class Remind(val enabled: Boolean) : OnboardingAction
}

@Composable private fun BioRadio(selected:Boolean){
    Box(Modifier.size(AppDimensions.Dp14).border(AppDimensions.Dp1,if(selected)bp.deep else bp.control,CircleShape),contentAlignment=Alignment.Center){if(selected)Box(Modifier.size(AppDimensions.Dp7).background(bp.deep,CircleShape))}
}

@Composable fun ColumnScope.OnboardingScreen(
    state: OnboardingDisplay,
    gutter: Int,
    onAction: (OnboardingAction) -> Unit,
    templateRow: @Composable (Int) -> Unit,
){
    val step = state.step
    val footer:@Composable ()->Unit={
        if(step==3)Bt(AppText.text(TextKey.CopyDaChon05ChonItNhat2ViecE813b8,state.selectedCount),Modifier.fillMaxWidth().padding(bottom=AppDimensions.Dp8),size=AppTypography.Size12,tint=bp.muted)
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp10)){
            if(step>0)BioButton(AppText.text(TextKey.CopyQuayLai6d7935),{ onAction(OnboardingAction.Back) },Modifier.weight(1f),secondary=true)
            BioButton(if(step==4)AppText.text(TextKey.CopyVaoVuonB6df0e)else AppText.text(TextKey.CopyTiepTuc87a7ae),{ onAction(OnboardingAction.Next) },Modifier.weight(1f),enabled=step!=3||state.selectedCount in 2..5)
        }
    }
    val scroll=rememberScrollState()
    Box(Modifier.weight(1f).fillMaxWidth()){
        Column(Modifier.fillMaxSize().verticalScroll(scroll).padding(start=gutter.dp,end=gutter.dp,top=AppDimensions.Dp20,bottom=if(step==3)AppDimensions.Dp0 else AppDimensions.Dp24),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp18)){
            Row(horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp5)){repeat(5){Box(Modifier.weight(1f).height(AppDimensions.Dp4).background(if(it<=step)bp.deep else AppColors.SwatchFFD8DFCE))}}
            Column(Modifier.fillMaxWidth(),horizontalAlignment=Alignment.CenterHorizontally){
                when(step){
                    0->{
                        BioPlant("succulent",Modifier.fillMaxWidth().height(AppDimensions.Dp220))
                        Bt(AppText.text(TextKey.CopyMotViecNhoMotMamXanh225e73),Modifier.fillMaxWidth(),size=AppTypography.Size29,line=AppTypography.LineHeight37_7,bold=true,align=TextAlign.Center,spacing=AppTypography.TrackingMinus_8)
                        Spacer(Modifier.height(AppDimensions.Dp12));Bt(AppText.text(TextKey.CopyChonCayChonViecLonLenMoiNgayB1308a),size=AppTypography.Size12,tint=bp.muted,align=TextAlign.Center)
                    }
                    1,2->{
                        Bt(if(step==1)AppText.text(TextKey.CopyHatDauTien8b8f9a)else AppText.text(TextKey.CopyDieuBanMuonCham4bb700),Modifier.fillMaxWidth(),size=AppTypography.Size29,line=AppTypography.LineHeight37_7,bold=true,align=TextAlign.Center,spacing=AppTypography.TrackingMinus_8)
                        Spacer(Modifier.height(AppDimensions.Dp12));Bt(if(step==1)AppText.text(TextKey.CopyMotHatMienPhiGiuTrongTuiF10b37)else AppText.text(TextKey.CopyBatDauTuDieuQuanTrongB1a63b),size=AppTypography.Size12,tint=bp.muted,align=TextAlign.Center)
                        Spacer(Modifier.height(AppDimensions.Dp24))
                        Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){
                            val choices=if(step==1)listOf("succulent","monstera","snake")else listOf(AppText.canonical(TextKey.CopyHocTapA51516),AppText.canonical(TextKey.CopySucKhoe071df7),AppText.canonical(TextKey.CopyNepSong8d1a98),AppText.canonical(TextKey.CopyViecNha126ef3))
                            choices.forEach{id->
                                val selected=if(step==1)state.seed == id else state.category == id
                                BioCard(Modifier.fillMaxWidth().semantics{this.selected=selected;role=Role.RadioButton},if(selected)bp.soft else bp.surface,padding=AppSpacing.Value12,radius=AppRadii.Value16,onClick={ onAction(OnboardingAction.Choose(step, id)) },borderColor=if(selected)bp.deep else bp.line){
                                    Row(Modifier.fillMaxWidth().heightIn(min=AppDimensions.Dp38),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){
                                        if(step==1)BioPlant(id,Modifier.size(AppDimensions.Dp54))
                                        Bt(if(step==1)state.seedNames[id].orEmpty()else AppText.category(id),Modifier.weight(1f),size=AppTypography.Size12,semi=true)
                                        BioRadio(selected)
                                    }
                                }
                            }
                        }
                    }
                    3->{
                        Bt(AppText.text(TextKey.CopyChon25ViecE8b193),Modifier.fillMaxWidth(),size=AppTypography.Size29,line=AppTypography.LineHeight37_7,bold=true,align=TextAlign.Center)
                        Spacer(Modifier.height(AppDimensions.Dp12));Bt(AppText.text(TextKey.CopyVuaSucThoiCoTheSuaSauC7359e),size=AppTypography.Size12,tint=bp.muted,align=TextAlign.Center)
                        Spacer(Modifier.height(AppDimensions.Dp24))
                        Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){
                            repeat(state.templateCount) { templateRow(it) }
                        }
                    }
                    else->{
                        BioPlant(state.seed,Modifier.fillMaxWidth().height(AppDimensions.Dp150))
                        Bt(AppText.text(TextKey.CopySanSangNayMam8ad6e8),Modifier.fillMaxWidth(),size=AppTypography.Size29,line=AppTypography.LineHeight37_7,bold=true,align=TextAlign.Center)
                        Spacer(Modifier.height(AppDimensions.Dp12));Bt(AppText.text(TextKey.Copy01Viec2189a78,state.seedNames[state.seed].orEmpty(),state.selectedCount,AppText.category(state.category)),Modifier.fillMaxWidth(),size=AppTypography.Size12,tint=bp.muted,align=TextAlign.Center)
                        Spacer(Modifier.height(AppDimensions.Dp18))
                        state.selectedTitles.forEach{Row(Modifier.fillMaxWidth().padding(vertical=AppDimensions.Dp11)){Bt(it+AppText.text(TextKey.CopyMoiNgay2c37dd),size=AppTypography.Size12)};HorizontalDivider(color=bp.line)}
                        BioToggle(AppText.text(TextKey.CopyNhacNheLuc0900Cda6e7),state.remind) { onAction(OnboardingAction.Remind(it)) }
                        Bt(AppText.text(TextKey.CopyKhongCanTaiKhoanCapQuyenTrongTuyChonDeNhanCc9847),Modifier.fillMaxWidth(),size=AppTypography.Size12,tint=bp.muted,align=TextAlign.Center)
                    }
                }
            }
            if(step==3)Column{HorizontalDivider(color=bp.line);Spacer(Modifier.height(AppDimensions.Dp13));footer();Spacer(Modifier.height(AppDimensions.Dp12))}else footer()
            BioTextAction(AppText.text(TextKey.CopyBoQuaC67ba2),{ onAction(OnboardingAction.Skip) },modifier=Modifier.fillMaxWidth())
        }
        if(step==3&&scroll.canScrollForward)Column(Modifier.align(Alignment.BottomCenter).fillMaxWidth().background(bp.bg).padding(horizontal=gutter.dp,vertical=AppDimensions.Dp12)){HorizontalDivider(color=bp.line);Spacer(Modifier.height(AppDimensions.Dp12));footer()}
    }
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun OnboardingScreenPreview() {
    Column(Modifier.fillMaxSize()) {
        OnboardingScreen(OnboardingDisplay(0, "succulent", "", 0, emptyList(), emptyMap(), 0, false), 20, {}, {})
    }
}
