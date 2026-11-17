package com.mamxanh.app.feature.tasks.presentation

import com.mamxanh.app.presentation.ui.design.AppDimensions
import com.mamxanh.app.presentation.ui.design.AppTypography

import com.mamxanh.shared.localization.TextKey

import com.mamxanh.shared.localization.AppText

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.presentation.ui.*
import com.mamxanh.app.presentation.ui.components.*
import com.mamxanh.shared.model.*

@Composable
internal fun OnboardingScreen(state:EngagementUiState,onAct:(EngagementAction)->Unit) {
    var step by rememberSaveable{mutableIntStateOf(0)}
    var seed by rememberSaveable{mutableStateOf("succulent")}
    var focus by rememberSaveable{mutableStateOf(AppText.canonical(TextKey.CopyHocTapA51516))}
    var chosen by rememberSaveable{mutableStateOf(listOf("study25","water"))}
    var remind by rememberSaveable{mutableStateOf(false)}
    val key=rememberSaveable{java.util.UUID.randomUUID().toString()}
    Column(Modifier.fillMaxSize().padding(AppDimensions.Dp24),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp16)) {
        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
            Text(AppText.text(TextKey.CopyBatDauNheNhangE790f8),Modifier.weight(1f),color=Deep,fontSize=AppTypography.Sp16,lineHeight=AppTypography.Sp21,fontWeight=FontWeight.Bold)
            TextButton(onClick={onAct(EngagementAction.Onboard(seed,focus,emptyList(),key,skip=true))},enabled=!state.busy) {
                Text(AppText.text(TextKey.CopyBoQuaC67ba2),fontSize=AppTypography.Sp11)
            }
        }
        MintProgress((step+1)/5f)
        val scroll=key(step){rememberScrollState()}
        Column(Modifier.weight(1f,fill=false).fillMaxWidth().verticalScroll(scroll),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp16)) {
            when(step) {
                0-> {
                    GardenPlantPortrait("succulent","NONE",Modifier.size(AppDimensions.Dp280,AppDimensions.Dp220).align(Alignment.CenterHorizontally))
                    Text(AppText.text(TextKey.CopyMotViecNhoMotMamXanh225e73),Modifier.fillMaxWidth(),color=Deep,fontSize=AppTypography.Sp28,lineHeight=AppTypography.Sp36,fontWeight=FontWeight.Bold,textAlign=androidx.compose.ui.text.style.TextAlign.Center)
                    Text(AppText.text(TextKey.CopyChonCayChonViecLonLenMoiNgayB1308a),Modifier.fillMaxWidth(),textAlign=androidx.compose.ui.text.style.TextAlign.Center,color=Muted,fontSize=AppTypography.Sp12,lineHeight=AppTypography.Sp18)
                }
                1-> {
                    Text(AppText.text(TextKey.CopyMotHatGiongCommonMienPhi3d5e43),color=Deep,fontSize=AppTypography.Sp22,lineHeight=AppTypography.Sp29,fontWeight=FontWeight.Bold)
                    Text(AppText.text(TextKey.CopyChonMotTrongBaLoaiHatDuocGiuTrongTuiDeBan9340b2),color=Muted,fontSize=AppTypography.Sp11,lineHeight=AppTypography.Sp16)
                    listOf("succulent","monstera","snake").forEach{id->
                        GardenBotanicalFrame(Modifier.fillMaxWidth().heightIn(min=AppDimensions.Dp64).clickable{seed=id},
                            skin=if(seed==id)GardenPanelSkin.HINT else GardenPanelSkin.BADGE) {
                            Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp10)) {
                                PaintedGardenPlant(id,Modifier.size(AppDimensions.Dp36))
                                Text(catalog.first{it.id==id}.name,Modifier.weight(1f),color=Deep,fontSize=AppTypography.Sp12)
                                Text(if(seed==id)"●" else "○",color=Green)
                            }
                        }
                    }
                }
                2-> {
                    Text(AppText.text(TextKey.CopyBanMuonDanhThoiGianChoDieuGi5a5a7f),color=Deep,fontSize=AppTypography.Sp22,lineHeight=AppTypography.Sp29,fontWeight=FontWeight.Bold)
                    listOf(AppText.canonical(TextKey.CopyHocTapA51516),AppText.canonical(TextKey.CopySucKhoe071df7),AppText.canonical(TextKey.CopyNepSong8d1a98),AppText.canonical(TextKey.CopyViecNha126ef3)).forEach{category->
                        GardenChoiceChip(category,focus==category,{focus=category},Modifier.fillMaxWidth())
                    }
                }
                3-> {
                    Text(AppText.text(TextKey.CopyChon25ViecVuaSuc781582),color=Deep,fontSize=AppTypography.Sp22,lineHeight=AppTypography.Sp29,fontWeight=FontWeight.Bold)
                    Text(AppText.text(TextKey.CopyLichVaKhungGioDuocGhiTrenMoiMauCoTheChinh999368),color=Muted,fontSize=AppTypography.Sp11,lineHeight=AppTypography.Sp16)
                    habitTemplates.sortedBy{it.config.category!=focus}.forEach{t->
                        GardenBotanicalFrame(Modifier.fillMaxWidth().heightIn(min=AppDimensions.Dp48).clickable {
                            chosen=if(t.id in chosen)chosen-t.id else if(chosen.size<5)chosen+t.id else chosen
                        },skin=if(t.id in chosen)GardenPanelSkin.HINT else GardenPanelSkin.BADGE) {
                            Column {
                                Text((if(t.id in chosen)"✓ " else "○ ")+t.config.title,color=Deep,fontSize=AppTypography.Sp12,fontWeight=FontWeight.Bold)
                                Text(AppText.text(TextKey.Copy0123Xu98b335,AppText.category(t.config.category),if(t.config.verification=="TIMER")AppText.text(TextKey.CopyBoDemC8def0)else AppText.text(TextKey.CopyCham36a8bd),t.effort,t.config.reward),
                                    color=Muted,fontSize=AppTypography.Sp12,lineHeight=AppTypography.Sp17)
                                Text(if(t.config.frequency=="WEEKENDS")AppText.text(TextKey.CopyCuoiTuanA0fb27) else AppText.text(TextKey.CopyMoiNgay8270cb),color=Muted,fontSize=AppTypography.Sp10)
                                if(t.config.startMinute>=0)Text(AppText.text(TextKey.CopyKhungGio01C4abe6,clockLabel(t.config.startMinute),clockLabel(t.config.endMinute)),color=Muted,fontSize=AppTypography.Sp12,lineHeight=AppTypography.Sp17)
                            }
                        }
                    }
                }
                else-> {
                    GardenPlantPortrait(seed,"NONE",Modifier.fillMaxWidth().height(AppDimensions.Dp150))
                    Text(AppText.text(TextKey.CopySanSangNayMam8ad6e8),color=Deep,fontSize=AppTypography.Sp22,lineHeight=AppTypography.Sp29,fontWeight=FontWeight.Bold)
                    Text(AppText.text(TextKey.Copy01Viec2189a78,catalog.first{it.id==seed}.name,chosen.size,focus),color=Deep,fontSize=AppTypography.Sp12)
                    chosen.mapNotNull{id->habitTemplates.firstOrNull{it.id==id}}.forEach{t->
                        Text("• ${t.config.title} · ${if(t.config.frequency=="WEEKENDS")AppText.text(TextKey.CopyCuoiTuanCbc24c) else AppText.text(TextKey.CopyMoiNgay4a1f92)}",color=Muted,fontSize=AppTypography.Sp11,lineHeight=AppTypography.Sp16)
                    }
                    Row(verticalAlignment=Alignment.CenterVertically) {
                        Text(AppText.text(TextKey.CopyNhacNheLuc0900TuyChon1a1d81),Modifier.weight(1f),color=Deep,fontSize=AppTypography.Sp12)
                        Switch(remind,{remind=it})
                    }
                    Text(AppText.text(TextKey.CopyThongBaoChiHoatDongKhiBanCapQuyenTrongTuy68f38a),
                        color=Muted,fontSize=AppTypography.Sp11,lineHeight=AppTypography.Sp16)
                }
            }
        }
        Row(horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp16)) {
            MintDialogAction(AppText.text(TextKey.CopyQuayLai6d7935),{step--},Modifier.weight(1f),primary=false,enabled=step>0 && !state.busy)
            MintDialogAction(if(step==4)AppText.text(TextKey.CopyVaoVuonB6df0e) else AppText.text(TextKey.CopyTiepTuc87a7ae),{
                if(step<4)step++ else onAct(EngagementAction.Onboard(seed,focus,chosen,key,reminders=remind))
            },Modifier.weight(1f),enabled=!state.busy && (step!=3 || chosen.size in 2..5))
        }
    }
}

@Composable
internal fun SettingsContent(state:EngagementUiState,onAct:(EngagementAction)->Unit,onPermission:()->Unit,onData:()->Unit={},onTaskReminders:()->Unit={}) {
    val settings=state.snapshot.settings
    var start by rememberSaveable(settings.quietStart){mutableStateOf(clockLabel(settings.quietStart))}
    var end by rememberSaveable(settings.quietEnd){mutableStateOf(clockLabel(settings.quietEnd))}
    GardenScreenIntro(AppText.text(TextKey.CopyTheoNhipCuaBan8e2e15))
    Text(AppText.text(TextKey.CopyNhacViecTuyChonCoTheTreDoTietKiemPinKhongCf13dd),color=Muted,fontSize=AppTypography.Sp12,lineHeight=AppTypography.Sp18)
    GardenBotanicalFrame(Modifier.fillMaxWidth()){
        Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp16)){
            SettingToggle(AppText.text(TextKey.CopyTamDungMoiNhacViec0fae21),settings.paused,!state.busy){onAct(EngagementAction.Settings(settings.copy(paused=it)))}
            SettingToggle(AppText.text(TextKey.CopyNhacKhiCayCanTuoi246e3f),settings.treeReminders,!state.busy){onAct(EngagementAction.Settings(settings.copy(treeReminders=it)))}
            SettingToggle(AppText.text(TextKey.CopyGoiYNghiSau25Phut6e7319),settings.breakPrompts,!state.busy){onAct(EngagementAction.Settings(settings.copy(breakPrompts=it)))}
            SettingToggle(AppText.text(TextKey.CopyGiamChuyenDongFf3295),settings.reducedMotion,!state.busy){onAct(EngagementAction.Settings(settings.copy(reducedMotion=it)))}
        }
    }
    GardenExpandableSection(AppText.text(TextKey.CopyGioYenLang112f45)){
        Row(horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){
            Column(Modifier.weight(1f)){Text(AppText.text(TextKey.CopyBatDauB3cacc),color=Muted,fontSize=AppTypography.Sp12);GardenTextField(start,{start=it},AppText.text(TextKey.CopyBatDauHhMmD6f81d))}
            Column(Modifier.weight(1f)){Text(AppText.text(TextKey.CopyKetThuc351849),color=Muted,fontSize=AppTypography.Sp12);GardenTextField(end,{end=it},AppText.text(TextKey.CopyKetThucHhMm80ed38))}
        }
        GardenPaintedButton(AppText.text(TextKey.CopyLuuGioYenLang9ba68f),{onAct(EngagementAction.Settings(settings.copy(quietStart=parseMinute(start)!!,quietEnd=parseMinute(end)!!)))},Modifier.fillMaxWidth(),enabled=!state.busy&&parseMinute(start)!=null&&parseMinute(end)!=null)
    }
    GardenPaintedButton(AppText.text(TextKey.CopyQuyenThongBao58e97d),onPermission,Modifier.fillMaxWidth(),primary=false)
    GardenNavigationCard(AppText.text(TextKey.CopyNhacRiengTungViecDf60c6),onTaskReminders,art=GardenArt.CALENDAR)
    GardenNavigationCard(AppText.text(TextKey.CopyDuLieuUngDung9843b7),onData,art=GardenArt.CATEGORY)

}
@Composable
private fun SettingToggle(label:String,value:Boolean,enabled:Boolean,onChange:(Boolean)->Unit) {
    Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp16)) {
        Text(label,Modifier.weight(1f),color=Deep,fontSize=AppTypography.Sp15,lineHeight=AppTypography.Sp21)
        Switch(value,onChange,enabled=enabled)
    }
}

@Preview(name="Settings component · complete",widthDp=360,heightDp=720,
    device="spec:width=360dp,height=720dp,dpi=160")
@Composable
fun EngagementSettingsComponentPreview() {
    MamXanhPreviewTheme {
        ProvideTextStyle(MaterialTheme.typography.bodySmall.copy(fontSize=AppTypography.Sp15,lineHeight=AppTypography.Sp21)) {
            Column(Modifier.fillMaxWidth().padding(AppDimensions.Dp12),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp16)) {
                SettingsContent(EngagementUiState(),{},{})
            }
        }
    }
}

@Preview(widthDp=414,heightDp=1000,device="spec:width=414dp,height=1000dp,dpi=160")
@Composable
fun SettingsWebDesignPreview(){com.mamxanh.app.presentation.ui.MamXanhPreviewTheme{MintBackdrop{Column(Modifier.padding(AppDimensions.Dp24),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp18)){SettingsContent(EngagementUiState(loading=false),{},{})}}}}


@Preview(widthDp=414,heightDp=1400,device="spec:width=414dp,height=1400dp,dpi=160")
@Composable fun OnboardWebDesignPreview(){MamXanhPreviewTheme{MintBackdrop{OnboardingScreen(EngagementUiState(loading=false),{})}}}
