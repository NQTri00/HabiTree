package com.mamxanh.app.feature.tasks.presentation

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
    var focus by rememberSaveable{mutableStateOf("Học tập")}
    var chosen by rememberSaveable{mutableStateOf(listOf("study25","water"))}
    var remind by rememberSaveable{mutableStateOf(false)}
    val key=rememberSaveable{java.util.UUID.randomUUID().toString()}
    Column(Modifier.fillMaxSize().padding(24.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
            Text("Bắt đầu nhẹ nhàng",Modifier.weight(1f),color=Deep,fontSize=16.sp,lineHeight=21.sp,fontWeight=FontWeight.Bold)
            TextButton(onClick={onAct(EngagementAction.Onboard(seed,focus,emptyList(),key,skip=true))},enabled=!state.busy) {
                Text("Bỏ qua",fontSize=11.sp)
            }
        }
        MintProgress((step+1)/5f)
        val scroll=key(step){rememberScrollState()}
        Column(Modifier.weight(1f,fill=false).fillMaxWidth().verticalScroll(scroll),verticalArrangement=Arrangement.spacedBy(16.dp)) {
            when(step) {
                0-> {
                    GardenPlantPortrait("succulent","NONE",Modifier.size(280.dp,220.dp).align(Alignment.CenterHorizontally))
                    Text("Một việc nhỏ.\nMột mầm xanh.",Modifier.fillMaxWidth(),color=Deep,fontSize=28.sp,lineHeight=36.sp,fontWeight=FontWeight.Bold,textAlign=androidx.compose.ui.text.style.TextAlign.Center)
                    Text("Chọn cây, chọn việc, lớn lên mỗi ngày.",Modifier.fillMaxWidth(),textAlign=androidx.compose.ui.text.style.TextAlign.Center,color=Muted,fontSize=12.sp,lineHeight=18.sp)
                }
                1-> {
                    Text("Một hạt giống Common miễn phí",color=Deep,fontSize=22.sp,lineHeight=29.sp,fontWeight=FontWeight.Bold)
                    Text("Chọn một trong ba loài. Hạt được giữ trong túi để bạn tự gieo.",color=Muted,fontSize=11.sp,lineHeight=16.sp)
                    listOf("succulent","monstera","snake").forEach{id->
                        GardenBotanicalFrame(Modifier.fillMaxWidth().heightIn(min=64.dp).clickable{seed=id},
                            skin=if(seed==id)GardenPanelSkin.HINT else GardenPanelSkin.BADGE) {
                            Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                                PaintedGardenPlant(id,Modifier.size(36.dp))
                                Text(catalog.first{it.id==id}.name,Modifier.weight(1f),color=Deep,fontSize=12.sp)
                                Text(if(seed==id)"●" else "○",color=Green)
                            }
                        }
                    }
                }
                2-> {
                    Text("Bạn muốn dành thời gian cho điều gì?",color=Deep,fontSize=22.sp,lineHeight=29.sp,fontWeight=FontWeight.Bold)
                    listOf("Học tập","Sức khỏe","Nếp sống","Việc nhà").forEach{category->
                        GardenChoiceChip(category,focus==category,{focus=category},Modifier.fillMaxWidth())
                    }
                }
                3-> {
                    Text("Chọn 2–5 việc vừa sức",color=Deep,fontSize=22.sp,lineHeight=29.sp,fontWeight=FontWeight.Bold)
                    Text("Lịch và khung giờ được ghi trên mỗi mẫu; có thể chỉnh về sau.",color=Muted,fontSize=11.sp,lineHeight=16.sp)
                    habitTemplates.sortedBy{it.config.category!=focus}.forEach{t->
                        GardenBotanicalFrame(Modifier.fillMaxWidth().heightIn(min=48.dp).clickable {
                            chosen=if(t.id in chosen)chosen-t.id else if(chosen.size<5)chosen+t.id else chosen
                        },skin=if(t.id in chosen)GardenPanelSkin.HINT else GardenPanelSkin.BADGE) {
                            Column {
                                Text((if(t.id in chosen)"✓ " else "○ ")+t.config.title,color=Deep,fontSize=12.sp,fontWeight=FontWeight.Bold)
                                Text("${t.config.category} · ${t.config.verification} · ${t.effort} · +${t.config.reward} xu",
                                    color=Muted,fontSize=12.sp,lineHeight=17.sp)
                                Text(if(t.config.frequency=="WEEKENDS")"Cuối tuần" else "Mỗi ngày",color=Muted,fontSize=10.sp)
                                if(t.config.startMinute>=0)Text("Khung giờ ${clockLabel(t.config.startMinute)}–${clockLabel(t.config.endMinute)}",color=Muted,fontSize=12.sp,lineHeight=17.sp)
                            }
                        }
                    }
                }
                else-> {
                    GardenPlantPortrait(seed,"NONE",Modifier.fillMaxWidth().height(150.dp))
                    Text("Sẵn sàng nảy mầm",color=Deep,fontSize=22.sp,lineHeight=29.sp,fontWeight=FontWeight.Bold)
                    Text("${catalog.first{it.id==seed}.name} · ${chosen.size} việc · $focus",color=Deep,fontSize=12.sp)
                    chosen.mapNotNull{id->habitTemplates.firstOrNull{it.id==id}}.forEach{t->
                        Text("• ${t.config.title} · ${if(t.config.frequency=="WEEKENDS")"cuối tuần" else "mỗi ngày"}",color=Muted,fontSize=11.sp,lineHeight=16.sp)
                    }
                    Row(verticalAlignment=Alignment.CenterVertically) {
                        Text("Nhắc nhẹ lúc 09:00 (tùy chọn)",Modifier.weight(1f),color=Deep,fontSize=12.sp)
                        Switch(remind,{remind=it})
                    }
                    Text("Thông báo chỉ hoạt động khi bạn cấp quyền trong Tùy chọn. Từ chối quyền không ảnh hưởng việc và bộ đếm.",
                        color=Muted,fontSize=11.sp,lineHeight=16.sp)
                }
            }
        }
        Row(horizontalArrangement=Arrangement.spacedBy(16.dp)) {
            MintDialogAction("Quay lại",{step--},Modifier.weight(1f),primary=false,enabled=step>0 && !state.busy)
            MintDialogAction(if(step==4)"Vào vườn" else "Tiếp tục",{
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
    GardenScreenIntro("Theo nhịp của bạn")
    Text("Nhắc việc tùy chọn; có thể trễ do tiết kiệm pin. Không gửi dồn dập.",color=Muted,fontSize=12.sp,lineHeight=18.sp)
    GardenBotanicalFrame(Modifier.fillMaxWidth()){
        Column(verticalArrangement=Arrangement.spacedBy(16.dp)){
            SettingToggle("Tạm dừng mọi nhắc việc",settings.paused,!state.busy){onAct(EngagementAction.Settings(settings.copy(paused=it)))}
            SettingToggle("Nhắc khi cây cần tưới",settings.treeReminders,!state.busy){onAct(EngagementAction.Settings(settings.copy(treeReminders=it)))}
            SettingToggle("Gợi ý nghỉ sau 25 phút",settings.breakPrompts,!state.busy){onAct(EngagementAction.Settings(settings.copy(breakPrompts=it)))}
            SettingToggle("Giảm chuyển động",settings.reducedMotion,!state.busy){onAct(EngagementAction.Settings(settings.copy(reducedMotion=it)))}
        }
    }
    GardenExpandableSection("Giờ yên lặng"){
        Row(horizontalArrangement=Arrangement.spacedBy(12.dp)){
            Column(Modifier.weight(1f)){Text("Bắt đầu",color=Muted,fontSize=12.sp);GardenTextField(start,{start=it},"Bắt đầu HH:mm")}
            Column(Modifier.weight(1f)){Text("Kết thúc",color=Muted,fontSize=12.sp);GardenTextField(end,{end=it},"Kết thúc HH:mm")}
        }
        GardenPaintedButton("Lưu giờ yên lặng",{onAct(EngagementAction.Settings(settings.copy(quietStart=parseMinute(start)!!,quietEnd=parseMinute(end)!!)))},Modifier.fillMaxWidth(),enabled=!state.busy&&parseMinute(start)!=null&&parseMinute(end)!=null)
    }
    GardenPaintedButton("Quyền thông báo",onPermission,Modifier.fillMaxWidth(),primary=false)
    GardenNavigationCard("Nhắc riêng từng việc",onTaskReminders,art=GardenArt.CALENDAR)
    GardenNavigationCard("Dữ liệu & ứng dụng",onData,art=GardenArt.CATEGORY)

}
@Composable
private fun SettingToggle(label:String,value:Boolean,enabled:Boolean,onChange:(Boolean)->Unit) {
    Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(16.dp)) {
        Text(label,Modifier.weight(1f),color=Deep,fontSize=15.sp,lineHeight=21.sp)
        Switch(value,onChange,enabled=enabled)
    }
}

@Preview(name="Settings component · complete",widthDp=360,heightDp=720,
    device="spec:width=360dp,height=720dp,dpi=160")
@Composable
fun EngagementSettingsComponentPreview() {
    MamXanhPreviewTheme {
        ProvideTextStyle(MaterialTheme.typography.bodySmall.copy(fontSize=15.sp,lineHeight=21.sp)) {
            Column(Modifier.fillMaxWidth().padding(12.dp),verticalArrangement=Arrangement.spacedBy(16.dp)) {
                SettingsContent(EngagementUiState(),{},{})
            }
        }
    }
}

@Preview(widthDp=414,heightDp=1000,device="spec:width=414dp,height=1000dp,dpi=160")
@Composable
fun SettingsWebDesignPreview(){com.mamxanh.app.presentation.ui.MamXanhPreviewTheme{MintBackdrop{Column(Modifier.padding(24.dp),verticalArrangement=Arrangement.spacedBy(18.dp)){SettingsContent(EngagementUiState(loading=false),{},{})}}}}


@Preview(widthDp=414,heightDp=1400,device="spec:width=414dp,height=1400dp,dpi=160")
@Composable fun OnboardWebDesignPreview(){MamXanhPreviewTheme{MintBackdrop{OnboardingScreen(EngagementUiState(loading=false),{})}}}
