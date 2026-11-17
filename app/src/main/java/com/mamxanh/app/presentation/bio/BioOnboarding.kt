package com.mamxanh.app.presentation.bio

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
    Box(Modifier.size(14.dp).border(1.dp,if(selected)bp.deep else bp.control,CircleShape),contentAlignment=Alignment.Center){if(selected)Box(Modifier.size(7.dp).background(bp.deep,CircleShape))}
}

@Composable internal fun ColumnScope.BioOnboard(world:BioWorld,ui:BioController,gutter:Int){
    val step=ui.onboardingStep
    val footer:@Composable ()->Unit={
        if(step==3)Bt("Đã chọn ${ui.selectedTemplates.size}/5 · chọn ít nhất 2 việc",Modifier.fillMaxWidth().padding(bottom=8.dp),size=12,tint=bp.muted)
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp)){
            if(step>0)BioButton("Quay lại",{ui.onboardingStep=(step-1).coerceAtLeast(0)},Modifier.weight(1f),secondary=true)
            BioButton(if(step==4)"Vào vườn"else "Tiếp tục",{if(step<4)ui.onboardingStep++ else if(world.native&&world.state.b("onboarded"))ui.go("home",root=true)else ui.send("onboard",mapOf("seed" to ui.selectedSeed,"category" to ui.selectedCategory,"ids" to ui.selectedTemplates.joinToString(","),"remind" to (ui.fields["onboard-remind"]?:"false")))},Modifier.weight(1f),enabled=step!=3||ui.selectedTemplates.size in 2..5)
        }
    }
    val scroll=rememberScrollState()
    Box(Modifier.weight(1f).fillMaxWidth()){
        Column(Modifier.fillMaxSize().verticalScroll(scroll).padding(start=gutter.dp,end=gutter.dp,top=20.dp,bottom=if(step==3)0.dp else 24.dp),verticalArrangement=Arrangement.spacedBy(18.dp)){
            Row(horizontalArrangement=Arrangement.spacedBy(5.dp)){repeat(5){Box(Modifier.weight(1f).height(4.dp).background(if(it<=step)bp.deep else Color(0xFFD8DFCE)))}}
            Column(Modifier.fillMaxWidth(),horizontalAlignment=Alignment.CenterHorizontally){
                when(step){
                    0->{
                        BioPlant("succulent",Modifier.fillMaxWidth().height(220.dp))
                        Bt("Một việc nhỏ.\nMột mầm xanh.",Modifier.fillMaxWidth(),size=29,line=37.7f,bold=true,align=TextAlign.Center,spacing=-.8f)
                        Spacer(Modifier.height(12.dp));Bt("Chọn cây, chọn việc, lớn lên mỗi ngày.",size=12,tint=bp.muted,align=TextAlign.Center)
                    }
                    1,2->{
                        Bt(if(step==1)"Hạt đầu tiên"else "Điều bạn muốn chăm",Modifier.fillMaxWidth(),size=29,line=37.7f,bold=true,align=TextAlign.Center,spacing=-.8f)
                        Spacer(Modifier.height(12.dp));Bt(if(step==1)"Một hạt miễn phí, giữ trong túi."else "Bắt đầu từ điều quan trọng.",size=12,tint=bp.muted,align=TextAlign.Center)
                        Spacer(Modifier.height(24.dp))
                        Column(verticalArrangement=Arrangement.spacedBy(12.dp)){
                            val choices=if(step==1)listOf("succulent","monstera","snake")else listOf("Học tập","Sức khỏe","Nếp sống","Việc nhà")
                            choices.forEach{id->
                                val selected=if(step==1)ui.selectedSeed==id else ui.selectedCategory==id
                                BioCard(Modifier.fillMaxWidth().semantics{this.selected=selected;role=Role.RadioButton},if(selected)bp.soft else bp.surface,padding=12,radius=16,onClick={if(step==1)ui.selectedSeed=id else ui.selectedCategory=id},borderColor=if(selected)bp.deep else bp.line){
                                    Row(Modifier.fillMaxWidth().heightIn(min=38.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)){
                                        if(step==1)BioPlant(id,Modifier.size(54.dp))
                                        Bt(if(step==1)world.species(id).s("name")else id,Modifier.weight(1f),size=12,semi=true)
                                        BioRadio(selected)
                                    }
                                }
                            }
                        }
                    }
                    3->{
                        Bt("Chọn 2–5 việc",Modifier.fillMaxWidth(),size=29,line=37.7f,bold=true,align=TextAlign.Center)
                        Spacer(Modifier.height(12.dp));Bt("Vừa sức thôi, có thể sửa sau.",size=12,tint=bp.muted,align=TextAlign.Center)
                        Spacer(Modifier.height(24.dp))
                        Column(verticalArrangement=Arrangement.spacedBy(12.dp)){
                            world.catalog.a("templates").objects().sortedBy{it.s("category")!=ui.selectedCategory}.forEach{t->BioTemplateRow(t,ui.selectedTemplates.contains(t.s("id")),onboarding=true){if(!ui.selectedTemplates.remove(t.s("id"))){if(ui.selectedTemplates.size<5)ui.selectedTemplates.add(t.s("id"))else ui.message="Bạn đã chọn 5 việc. Bỏ chọn một việc để đổi."}}}
                        }
                    }
                    else->{
                        BioPlant(ui.selectedSeed,Modifier.fillMaxWidth().height(150.dp))
                        Bt("Sẵn sàng nảy mầm",Modifier.fillMaxWidth(),size=29,line=37.7f,bold=true,align=TextAlign.Center)
                        Spacer(Modifier.height(12.dp));Bt("${world.species(ui.selectedSeed).s("name")} · ${ui.selectedTemplates.size} việc · ${ui.selectedCategory}",Modifier.fillMaxWidth(),size=12,tint=bp.muted,align=TextAlign.Center)
                        Spacer(Modifier.height(18.dp))
                        world.catalog.a("templates").objects().filter{ui.selectedTemplates.contains(it.s("id"))}.forEach{Row(Modifier.fillMaxWidth().padding(vertical=11.dp)){Bt(it.s("title")+" · Mỗi ngày",size=12)};HorizontalDivider(color=bp.line)}
                        BioToggle("Nhắc nhẹ lúc 09:00",ui.fields["onboard-remind"]=="true"){ui.fields["onboard-remind"]=it.toString()}
                        Bt("Không cần tài khoản. Cấp quyền trong Tùy chọn để nhận thông báo; từ chối vẫn dùng việc và bộ đếm.",Modifier.fillMaxWidth(),size=12,tint=bp.muted,align=TextAlign.Center)
                    }
                }
            }
            if(step==3)Column{HorizontalDivider(color=bp.line);Spacer(Modifier.height(13.dp));footer();Spacer(Modifier.height(12.dp))}else footer()
            BioTextAction("Bỏ qua",{if(world.native&&world.state.b("onboarded"))ui.go("home",root=true)else ui.send("onboard",mapOf("skip" to "true"))},modifier=Modifier.fillMaxWidth())
        }
        if(step==3&&scroll.canScrollForward)Column(Modifier.align(Alignment.BottomCenter).fillMaxWidth().background(bp.bg).padding(horizontal=gutter.dp,vertical=12.dp)){HorizontalDivider(color=bp.line);Spacer(Modifier.height(12.dp));footer()}
    }
}
