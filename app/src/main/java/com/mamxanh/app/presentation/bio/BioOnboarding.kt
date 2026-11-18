package com.mamxanh.app.presentation.bio

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

@Composable private fun BioRadio(selected:Boolean){
    Box(Modifier.size(AppDimensions.Dp14).border(AppDimensions.Dp1,if(selected)bp.deep else bp.control,CircleShape),contentAlignment=Alignment.Center){if(selected)Box(Modifier.size(AppDimensions.Dp7).background(bp.deep,CircleShape))}
}

@Composable internal fun ColumnScope.BioOnboard(world:BioWorld,ui:BioController,gutter:Int){
    val step=ui.onboardingStep
    val footer:@Composable ()->Unit={
        if(step==3)Bt(AppText.text(TextKey.CopyDaChon05ChonItNhat2ViecE813b8,ui.selectedTemplates.size),Modifier.fillMaxWidth().padding(bottom=AppDimensions.Dp8),size=AppTypography.Size12,tint=bp.muted)
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp10)){
            if(step>0)BioButton(AppText.text(TextKey.CopyQuayLai6d7935),{ui.onboardingStep=(step-1).coerceAtLeast(0)},Modifier.weight(1f),secondary=true)
            BioButton(if(step==4)AppText.text(TextKey.CopyVaoVuonB6df0e)else AppText.text(TextKey.CopyTiepTuc87a7ae),{if(step<4)ui.onboardingStep++ else if(world.native&&world.state.b("onboarded"))ui.go("home",root=true)else ui.send("onboard",mapOf("seed" to ui.selectedSeed,"category" to ui.selectedCategory,"ids" to ui.selectedTemplates.joinToString(","),"remind" to (ui.fields["onboard-remind"]?:"false")))},Modifier.weight(1f),enabled=step!=3||ui.selectedTemplates.size in 2..5)
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
                                val selected=if(step==1)ui.selectedSeed==id else ui.selectedCategory==id
                                BioCard(Modifier.fillMaxWidth().semantics{this.selected=selected;role=Role.RadioButton},if(selected)bp.soft else bp.surface,padding=AppSpacing.Value12,radius=AppRadii.Value16,onClick={if(step==1)ui.selectedSeed=id else ui.selectedCategory=id},borderColor=if(selected)bp.deep else bp.line){
                                    Row(Modifier.fillMaxWidth().heightIn(min=AppDimensions.Dp38),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){
                                        if(step==1)BioPlant(id,Modifier.size(AppDimensions.Dp54))
                                        Bt(if(step==1)world.species(id).s("name")else AppText.category(id),Modifier.weight(1f),size=AppTypography.Size12,semi=true)
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
                            world.catalog.a("templates").objects().sortedBy{it.s("category")!=ui.selectedCategory}.forEach{t->BioTemplateRow(t,ui.selectedTemplates.contains(t.s("id")),onboarding=true){if(!ui.selectedTemplates.remove(t.s("id"))){if(ui.selectedTemplates.size<5)ui.selectedTemplates.add(t.s("id"))else ui.message=AppText.text(TextKey.CopyBanDaChon5ViecBoChonMotViecDeDoi95f1ae)}}}
                        }
                    }
                    else->{
                        BioPlant(ui.selectedSeed,Modifier.fillMaxWidth().height(AppDimensions.Dp150))
                        Bt(AppText.text(TextKey.CopySanSangNayMam8ad6e8),Modifier.fillMaxWidth(),size=AppTypography.Size29,line=AppTypography.LineHeight37_7,bold=true,align=TextAlign.Center)
                        Spacer(Modifier.height(AppDimensions.Dp12));Bt(AppText.text(TextKey.Copy01Viec2189a78,world.species(ui.selectedSeed).s("name"),ui.selectedTemplates.size,AppText.category(ui.selectedCategory)),Modifier.fillMaxWidth(),size=AppTypography.Size12,tint=bp.muted,align=TextAlign.Center)
                        Spacer(Modifier.height(AppDimensions.Dp18))
                        world.catalog.a("templates").objects().filter{ui.selectedTemplates.contains(it.s("id"))}.forEach{Row(Modifier.fillMaxWidth().padding(vertical=AppDimensions.Dp11)){Bt(it.s("title")+AppText.text(TextKey.CopyMoiNgay2c37dd),size=AppTypography.Size12)};HorizontalDivider(color=bp.line)}
                        BioToggle(AppText.text(TextKey.CopyNhacNheLuc0900Cda6e7),ui.fields["onboard-remind"]=="true"){ui.fields["onboard-remind"]=it.toString()}
                        Bt(AppText.text(TextKey.CopyKhongCanTaiKhoanCapQuyenTrongTuyChonDeNhanCc9847),Modifier.fillMaxWidth(),size=AppTypography.Size12,tint=bp.muted,align=TextAlign.Center)
                    }
                }
            }
            if(step==3)Column{HorizontalDivider(color=bp.line);Spacer(Modifier.height(AppDimensions.Dp13));footer();Spacer(Modifier.height(AppDimensions.Dp12))}else footer()
            BioTextAction(AppText.text(TextKey.CopyBoQuaC67ba2),{if(world.native&&world.state.b("onboarded"))ui.go("home",root=true)else ui.send("onboard",mapOf("skip" to "true"))},modifier=Modifier.fillMaxWidth())
        }
        if(step==3&&scroll.canScrollForward)Column(Modifier.align(Alignment.BottomCenter).fillMaxWidth().background(bp.bg).padding(horizontal=gutter.dp,vertical=AppDimensions.Dp12)){HorizontalDivider(color=bp.line);Spacer(Modifier.height(AppDimensions.Dp12));footer()}
    }
}
