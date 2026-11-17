package com.mamxanh.app.presentation.bio

import com.mamxanh.app.presentation.ui.design.AppColors
import com.mamxanh.app.presentation.ui.design.AppDimensions
import com.mamxanh.app.presentation.ui.design.AppIcons
import com.mamxanh.app.presentation.ui.design.AppOpacity
import com.mamxanh.app.presentation.ui.design.AppRadii
import com.mamxanh.app.presentation.ui.design.AppSpacing
import com.mamxanh.app.presentation.ui.design.AppTypography

import com.mamxanh.shared.localization.TextKey

import com.mamxanh.shared.localization.AppText

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.*
import org.json.JSONObject

internal fun treeState(t:JSONObject)=when(t.s("state")){"dead"->AppText.text(TextKey.CopyDaKhoHeoA9c1b9);"mature"->AppText.text(TextKey.CopyTruongThanh8c14d7);else->AppText.text(TextKey.CopyDangLon40daab)}
internal fun taskReward(t:JSONObject)=if(t.s("verification")=="TIMER")when{t.i("minutes")>=45->20;t.i("minutes")>=20->15;else->10}else 8
internal fun growthCaption(t:JSONObject)=when(t.s("state")){"dead"->AppText.text(TextKey.CopyMamXanhDaDungLai6dabc6);"mature"->AppText.text(TextKey.CopyDaTruongThanh54f165);else->AppText.text(TextKey.CopyDangLonCungBan6913c2)}
internal fun accessoryIcon(id:String)=when(id){"tree_ribbon"->"gift";"tree_lantern"->"sun";else->"spark"}

@Composable internal fun BioHero(world:BioWorld,ui:BioController,t:JSONObject,home:Boolean=false,short:Boolean=false){
    val p=bp;val narrow=LocalConfiguration.current.screenWidthDp<359;val shape=bioShape(AppRadii.Value26);val dead=t.s("state")=="dead"
    Column(Modifier.fillMaxWidth().shadow(AppDimensions.Dp5,shape,ambientColor=p.deep.copy(alpha=AppOpacity.Value_35),spotColor=p.deep.copy(alpha=AppOpacity.Value_30)).background(remember(p){bioHeroBrush(p)},shape).border(AppDimensions.Dp1,AppColors.White.copy(alpha=AppOpacity.Value_75),shape).clip(shape).drawBehind {
        val step=AppDimensions.Dp24.toPx();var x=0f;while(x<size.width){drawLine(AppColors.White.copy(alpha=AppOpacity.Value_08),Offset(x,0f),Offset(x,size.height),1f);x+=step};var y=0f;while(y<size.height){drawLine(AppColors.White.copy(alpha=AppOpacity.Value_08),Offset(0f,y),Offset(size.width,y),1f);y+=step}
        val right=size.width-AppDimensions.Dp12.toPx();val top=AppDimensions.Dp12.toPx();val len=AppDimensions.Dp18.toPx();drawLine(p.deep.copy(alpha=AppOpacity.Value_45),Offset(right-len,top),Offset(right,top),AppDimensions.Dp1.toPx());drawLine(p.deep.copy(alpha=AppOpacity.Value_45),Offset(right,top),Offset(right,top+len),AppDimensions.Dp1.toPx())
    }.padding(start=if(narrow)AppDimensions.Dp15 else AppDimensions.Dp19,end=if(narrow)AppDimensions.Dp15 else AppDimensions.Dp19,top=AppDimensions.Dp15,bottom=AppDimensions.Dp19)){
        Row(Modifier.fillMaxWidth().height(AppDimensions.Dp48),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.SpaceBetween){
            BioTag(treeState(t),dot=true)
            if(home)BioTextAction(AppText.text(TextKey.CopyDoiCayFada9c),{ui.show("tree-picker")},"next")
            else BioTextAction(if(ui.homeTree==t.s("id"))AppText.text(TextKey.CopyTrenTrangChuE7c2f5) else AppText.text(TextKey.CopyDatOTrangChuD8241c),{ui.act("bio-pin:"+t.s("id"))})
        }
        Spacer(Modifier.height(AppDimensions.Dp2))
        val landscape=LocalConfiguration.current.screenHeightDp<500
        val artHeight=if(landscape)100 else if(home){if(short)130 else 150}else if(short)160 else if(narrow)190 else 220
        Box(Modifier.fillMaxWidth().height(artHeight.dp).then(if(home)Modifier.clickable{ui.go("tree",t.s("id"))}else Modifier),contentAlignment=Alignment.Center){
            BioPlant(t.s("species"),Modifier.height((artHeight-if(home)0 else 12).dp).fillMaxWidth(),dead)
            val accessory=t.s("decor").takeIf{it.isNotBlank()&&world.owned(it)>0}
            if(accessory!=null&&!dead)Box(Modifier.align(Alignment.TopEnd).padding(top=AppDimensions.Dp55,end=AppDimensions.Dp14).size(AppDimensions.Dp32).background(AppColors.White.copy(alpha=AppOpacity.Value_6),RoundedCornerShape(50)).border(AppDimensions.Dp1,p.accent,RoundedCornerShape(50)),contentAlignment=Alignment.Center){Bi(accessoryIcon(accessory),size=AppIcons.Size23)}
            if(!home&&world.trees.size>1){BioIconButton("back",{ui.act("tree-prev")},Modifier.align(Alignment.CenterStart).offset(x=AppDimensions.DpMinus6),boxed=true,iconSize=AppIcons.Size18,tint=p.deep,label=AppText.text(TextKey.CopyCayTruoc6648b1));BioIconButton("next",{ui.act("tree-next")},Modifier.align(Alignment.CenterEnd).offset(x=AppDimensions.Dp6),boxed=true,iconSize=AppIcons.Size18,tint=p.deep,label=AppText.text(TextKey.CopyCayTiepTheo242116))}
        }
        Spacer(Modifier.height(AppDimensions.Dp10))
        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp8)){
            Column(Modifier.weight(1f)){Bt(t.s("name"),size=AppTypography.Size21,line=AppTypography.LineHeight28_35,bold=true,spacing=AppTypography.TrackingMinus_7);Bt(AppText.text(TextKey.Copy01GiaiDoan869247,t.i("stage",1),world.species(t.s("species")).i("stages",6)),size=AppTypography.Size12,tint=p.muted,line=AppTypography.LineHeight21_7)}
            BioTag(if(dead)AppText.text(TextKey.CopyKhoHeoBf546a) else if(t.b("watered"))AppText.text(TextKey.CopyDuNuocC17aa8) else AppText.text(TextKey.CopyCanTuoiA0957f),"water")
        }
        Spacer(Modifier.height(AppDimensions.Dp12));BioProgress(t.i("progress")/100f,white=true)
        Spacer(Modifier.height(AppDimensions.Dp6));Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Bt(growthCaption(t),size=AppTypography.Size10,tint=p.muted,line=AppTypography.LineHeight18_6);Bt("${t.i("progress")}%",size=AppTypography.Size11,tint=p.deep,mono=true,line=AppTypography.LineHeight18_6)}
        if(home){Spacer(Modifier.height(AppDimensions.Dp13));if(LocalDensity.current.fontScale>1.2f)Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp10)){
            BioButton(if(t.b("watered"))AppText.text(TextKey.CopyDaTuoiDe2042) else AppText.text(TextKey.CopyTuoiCay0990f6),{ui.send("water:"+t.s("id"))},Modifier.fillMaxWidth(),"water",enabled=!t.b("watered")&&!dead&&!ui.busy)
            BioButton(AppText.text(TextKey.CopyTrangTriD48231),{ui.act("bio-tree-decor:"+t.s("id"))},Modifier.fillMaxWidth(),"spark",secondary=true,enabled=!dead&&!ui.busy)
        }else Row(horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp10)){
            BioButton(if(t.b("watered"))AppText.text(TextKey.CopyDaTuoiDe2042) else AppText.text(TextKey.CopyTuoiCay0990f6),{ui.send("water:"+t.s("id"))},Modifier.weight(1f),"water",enabled=!t.b("watered")&&!dead&&!ui.busy,labelSize=if(narrow)12 else 14)
            BioButton(AppText.text(TextKey.CopyTrangTriD48231),{ui.act("bio-tree-decor:"+t.s("id"))},Modifier.weight(1f),"spark",secondary=true,enabled=!dead&&!ui.busy,labelSize=if(narrow)12 else 14)
        }}
    }
}

@Composable internal fun ColumnScope.BioHome(world:BioWorld,ui:BioController,short:Boolean){
    val narrow=LocalConfiguration.current.screenWidthDp<359;val tree=world.tree(ui.homeTree);val date=world.data.s("date").split('-').takeLast(2).reversed().joinToString("-")
    val landscape=LocalConfiguration.current.screenHeightDp<500
    Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.Top,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp14)){
        Column(Modifier.weight(1f)){if(!landscape){Bt(AppText.text(TextKey.CopyMotNhipXanh4e8208),size=AppTypography.Size10,tint=bp.muted,mono=true,spacing=AppTypography.Tracking1_8);Spacer(Modifier.height(AppDimensions.Dp7))};Bt(AppText.text(TextKey.CopyNgayMoiMamMoi236a07),size=if(narrow||landscape)22 else 25,line=if(narrow||landscape)28.6f else 32.5f,bold=true,spacing=AppTypography.TrackingMinus1)}
        Box(Modifier.padding(top=if(landscape)AppDimensions.Dp0 else AppDimensions.Dp19).background(AppColors.White.copy(alpha=AppOpacity.Value_25),RoundedCornerShape(AppDimensions.Dp8)).border(AppDimensions.Dp1,bp.line,RoundedCornerShape(AppDimensions.Dp8)).padding(horizontal=AppDimensions.Dp10,vertical=AppDimensions.Dp9)){Bt(date,size=AppTypography.Size11,tint=bp.muted,mono=true,line=AppTypography.LineHeight14_3)}
    }
    if(tree!=null)BioHero(world,ui,tree,true,short)else BioEmpty(AppText.text(TextKey.CopyBatDauBangMotMamXanhCd3b5c),AppText.text(TextKey.CopyMotHatNhoMotChutMoiNgayBcf756),action=AppText.text(TextKey.CopyGieoCayDauTienAfd3c1),onClick={ui.show("seed-picker")})
    BioSection(AppText.text(TextKey.CopyViecTiepTheoEa34c3),AppText.text(TextKey.CopyXemTatCa44aeb8)){ui.go("tasks")}
    val next=world.tasks.firstOrNull{!it.b("done")&&!it.b("finished")&&it.s("lifecycle","ACTIVE")=="ACTIVE"&&it.b("scheduledToday",true)}
    if(next!=null)BioTaskRow(next,ui)else BioLink(AppText.text(TextKey.CopyMotViecNhoHomNay3c953f),"plus",{ui.go("task-new")})
    val phase=world.data.o("phase")
    BioMetrics(listOf(AppText.text(TextKey.CopyViecHomNay22a0a4) to "${phase.i("completed")}/${phase.i("required")}",AppText.text(TextKey.CopyXuHomNay0c44ed) to "${world.state.i("earned")}/50",AppText.text(TextKey.CopyNgayDeuDan31b8f2) to phase.i("streak").toString()))
    if(phase.b("canClaim")&&!world.state.b("claimed"))BioReward(world,ui)
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp10)){
        listOf(Triple("plus",AppText.text(TextKey.CopyThemViecC371c2),"go:task-new"),Triple("garden",AppText.text(TextKey.CopyGieoCay31adde),"bio-seed-picker"),Triple("spark",AppText.text(TextKey.CopyPhuKien0852d7),"bio-accessory-shop")).forEach{(icon,title,action)->Column(Modifier.weight(1f).heightIn(min=AppDimensions.Dp76).background(AppColors.White.copy(alpha=AppOpacity.Value_25),bioShape(AppRadii.Value14)).border(AppDimensions.Dp1,bp.line,bioShape(AppRadii.Value14)).clickable{ui.act(action)}.padding(AppDimensions.Dp10),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp7)){Bi(icon);Bt(title,size=AppTypography.Size11,tint=bp.deep,align=TextAlign.Center)}}
    }
    BioLink(AppText.text(TextKey.CopyNhinLaiTuanFf3f0c),"calendar",{ui.go("review")},AppText.text(TextKey.CopyNhungDieuNhoDaLonLen882792))
}

@Composable internal fun BioTaskRow(task:JSONObject,ui:BioController){
    val done=task.b("done")||task.b("finished");val timer=task.s("verification")=="TIMER"
    BioCard(Modifier.fillMaxWidth(),padding=AppSpacing.Value14,radius=AppRadii.Value22,tint=if(done)lerp(AppColors.White,bp.soft,.7f)else bp.surface){
        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){
            Box(Modifier.size(AppDimensions.Dp38).background(bp.soft,bioShape(AppRadii.Value12)),contentAlignment=Alignment.Center){Bi(if(timer)"clock" else if(task.s("category")==AppText.canonical(TextKey.CopySucKhoe071df7))"water" else "book")}
            Column(Modifier.weight(1f).heightIn(min=AppDimensions.Dp48).clickable{ui.go("task",task.s("id"))},verticalArrangement=Arrangement.Center){
                if(done)Text(task.s("title"),color=bp.muted,fontFamily=com.mamxanh.app.presentation.ui.GardenFontFamily,fontSize=AppTypography.Sp13,fontWeight=androidx.compose.ui.text.font.FontWeight.SemiBold,textDecoration=androidx.compose.ui.text.style.TextDecoration.LineThrough)
                else Bt(task.s("title"),size=AppTypography.Size14,line=AppTypography.LineHeight21,semi=true)
                Bt(if(done)AppText.text(TextKey.CopyDaXong565e93) else AppText.category(task.s("category"))+(if(timer)AppText.text(TextKey.Copy0PhutC0bcb0,task.i("minutes"))else ""),size=AppTypography.Size11,tint=bp.muted)
            }
            Bt("+${taskReward(task)}",size=AppTypography.Size10,tint=AppColors.SwatchFF946A0E)
            Box(Modifier.size(AppDimensions.Dp48).background(if(done)bp.deep else bp.soft.copy(alpha=AppOpacity.Value_45),RoundedCornerShape(50)).border(AppDimensions.Dp1,if(done)bp.deep else bp.control,RoundedCornerShape(50)).semantics{contentDescription=if(done)AppText.text(TextKey.CopyDaHoanThanh1911fc)+task.s("title")else if(timer)AppText.text(TextKey.CopyTapTrung71ae4b)+task.s("title")else AppText.text(TextKey.CopyHoanThanhF183ae)+task.s("title")}.clickable(enabled=!done&&task.s("lifecycle","ACTIVE")=="ACTIVE"&&!ui.busy,role=Role.Button){if(timer)ui.go("focus",task.s("id"))else ui.send("complete:"+task.s("id"))},contentAlignment=Alignment.Center){Bi(if(timer&&!done)"play"else "check",tint=if(done)AppColors.White else bp.deep)}
        }
    }
}
@Composable internal fun BioReward(world:BioWorld,ui:BioController){BioCard(Modifier.fillMaxWidth(),AppColors.RewardSurface,padding=AppSpacing.Value14,radius=AppRadii.Value18){Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){Bi("gift",tint=bp.ink);Column(Modifier.weight(1f)){Bt(AppText.text(TextKey.CopyThuongHomNayC015c6),size=AppTypography.Size12,semi=true);Bt(AppText.text(TextKey.Copy8Xu3a7892),size=AppTypography.Size11)};BioButton(if(world.state.b("claimed"))AppText.text(TextKey.CopyDaNhan1d62ad) else AppText.text(TextKey.CopyNhan8518f2),{ui.send("claim")},enabled=world.data.o("phase").b("canClaim")&&!world.state.b("claimed"))}}}

@Composable internal fun ColumnScope.BioTasks(world:BioWorld,ui:BioController){
    val filter=ui.tabs["taskFilter"]?:"today"
    BioIntro(when(filter){"paused"->AppText.text(TextKey.CopyViecTamDungB756a3);"archived"->AppText.text(TextKey.CopyViecLuuTru911d4f);else->AppText.text(TextKey.CopyViecHomNay22a0a4)},AppText.text(TextKey.CopyTungChutMot238966),action={BioIconButton("plus",{ui.go("task-new")})})
    val phase=world.data.o("phase")
    if(filter=="today")BioCard(Modifier.fillMaxWidth(),bp.soft,padding=AppSpacing.Value18){Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){Bt(AppText.text(TextKey.Copy01ViecDaXongCf7a5d,phase.i("completed"),phase.i("required")),Modifier.weight(1f),size=AppTypography.Size14,semi=true);BioTag(AppText.text(TextKey.Copy0Xu7c2391,world.state.i("earned")),"spark")};Spacer(Modifier.height(AppDimensions.Dp12));BioProgress(if(phase.i("required")>0)phase.i("completed").toFloat()/phase.i("required")else 0f)}
    Row(horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){BioButton(AppText.text(TextKey.CopyTapTrung964002),{ui.go("focus")},Modifier.weight(1f),"clock",true);BioButton(AppText.text(TextKey.CopyViecMau535dc1),{ui.go("templates")},Modifier.weight(1f),"plus",true)}
    BioChips(listOf("today" to AppText.text(TextKey.CopyHomNayE6747a),"paused" to AppText.text(TextKey.TabPaused),"archived" to AppText.text(TextKey.CopyLuuTru7e7c5f)),filter){ui.tabs["taskFilter"]=it}
    val list=world.tasks.filter{it.s("lifecycle","ACTIVE")==when(filter){"paused"->"PAUSED";"archived"->"ARCHIVED";else->"ACTIVE"}}
    Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){if(list.isEmpty())BioEmpty(when(filter){"paused"->AppText.text(TextKey.CopyKhongCoViecTamDung074795);"archived"->AppText.text(TextKey.CopyChuaCoViecLuuTru3a7d32);else->AppText.text(TextKey.CopyChuaCoViecE6f37e)},if(filter=="today")AppText.text(TextKey.CopyBatDauBangMotViecVuaSucA10a5a)else AppText.text(TextKey.CopyCacViecCuaBanVanOHomNayB04085),"tasks",if(filter=="today")AppText.text(TextKey.CopyThemViecC371c2)else AppText.text(TextKey.CopyVeHomNay08154e),{if(filter=="today")ui.go("task-new")else ui.tabs["taskFilter"]="today"})else list.forEach{BioTaskRow(it,ui)}}
    if(list.isNotEmpty()&&filter=="today"){BioReward(world,ui);BioButton(AppText.text(TextKey.CopyThemViecC371c2),{ui.go("task-new")},Modifier.fillMaxWidth(),"plus")}
    BioLink(AppText.text(TextKey.CopyNhipThoiQuen1b8324),"chart",{ui.go("insights")})
}

@Composable internal fun ColumnScope.BioGarden(world:BioWorld,ui:BioController){
    val open=world.state.i("unlocked",5);BioIntro(AppText.text(TextKey.CopyVuonCuaBanAd1c1b),AppText.text(TextKey.CopyTungMamDangLon4a30e9),AppText.text(TextKey.Copy0Cay1OTrong964022,world.trees.size,world.emptyPlots().size),{BioIconButton("bag",{ui.go("inventory")})})
    Row(horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp10)){BioButton(AppText.text(TextKey.CopyTuoiTatCa3d814a),{ui.show("water-all")},Modifier.weight(1f),"water",enabled=world.trees.any{!it.b("watered")&&it.s("state")!="dead"});BioButton(AppText.text(TextKey.CopyGieoCay31adde),{ui.show("seed-picker")},Modifier.weight(1f),"plus",true)}
    val filter=ui.tabs["gardenFilter"]?:"all";BioChips(listOf("all" to AppText.text(TextKey.CopyTatCa49c73a),"thirsty" to AppText.text(TextKey.CopyCanTuoiA0957f),"growing" to AppText.text(TextKey.CopyDangLon40daab),"mature" to AppText.text(TextKey.CopyTruongThanh8c14d7)),filter){ui.tabs["gardenFilter"]=it}
    val plots=(1..open).filter{p->val t=world.trees.firstOrNull{it.i("plot")==p};filter=="all"||t!=null&&(filter==t.s("state")||filter=="thirsty"&&!t.b("watered")&&t.s("state")!="dead")}.toMutableList();if(filter=="all"&&open<24)plots.add(open+1)
    val plotLine=bp.line
    BioGrid(plots.size){index,modifier->val plot=plots[index];val t=world.trees.firstOrNull{it.i("plot")==plot};val locked=plot>open
        BioCard(modifier,padding=AppSpacing.Value12,onClick={if(t!=null)ui.go("tree",t.s("id"))else if(locked)ui.show("unlock",plot.toString())else{ui.plot=plot.toString();ui.show("seed-picker")}}){
            Box(Modifier.fillMaxWidth().height(if(t==null&&!locked)AppDimensions.Dp110 else AppDimensions.Dp105).background(if(locked)AppColors.Transparent else bp.soft,bioShape(AppRadii.Value14)),contentAlignment=Alignment.Center){
                if(t!=null)BioPlant(t.s("species"),Modifier.fillMaxSize(),t.s("state")=="dead")else Bi("plus",size=AppIcons.Size28)
                if(!locked)Bt(String.format("%02d",plot),Modifier.align(Alignment.TopStart).padding(AppDimensions.Dp6),size=AppTypography.Size10,tint=bp.muted,mono=true)
                if(t==null&&!locked)Canvas(Modifier.fillMaxSize()){drawRoundRect(plotLine,cornerRadius=androidx.compose.ui.geometry.CornerRadius(AppDimensions.Dp14.toPx()),style=androidx.compose.ui.graphics.drawscope.Stroke(AppDimensions.Dp1.toPx(),pathEffect=PathEffect.dashPathEffect(floatArrayOf(AppDimensions.Dp3.toPx(),AppDimensions.Dp3.toPx()))))}
            }
            Spacer(Modifier.height(AppDimensions.Dp25));Bt(t?.s("name")?:if(locked)AppText.text(TextKey.CopyThemOTrong9979ac) else AppText.text(TextKey.CopyGieoCay31adde),size=AppTypography.Size13,semi=true)
            if(t!=null){Spacer(Modifier.height(AppDimensions.Dp6));BioProgress(t.i("progress")/100f);Bt("${t.i("progress")}% · ${treeState(t)}",size=AppTypography.Size10,tint=bp.muted);Spacer(Modifier.height(AppDimensions.Dp16));Bt(if(t.s("state")=="dead")AppText.text(TextKey.CopyKhoHeoBf546a)else if(t.b("watered"))AppText.text(TextKey.CopyDaTuoiDe2042)else AppText.text(TextKey.CopyCanTuoiA0957f),size=AppTypography.Size10,tint=bp.muted)}else Bt(if(locked)AppText.text(TextKey.Copy0Xu251e68,400+(open-5).coerceAtLeast(0)*200)else AppText.text(TextKey.CopyMotKhoiDauMoi280995),size=AppTypography.Size10,tint=bp.muted)
        }
    }
    if(plots.isEmpty())BioEmpty(AppText.text(TextKey.CopyChuaCoCayODay1f9b1f),AppText.text(TextKey.CopyChonBoLocKhacDeXem6bf49e))
    BioLink(AppText.text(TextKey.CopyBachThaoF2ed64),"book",{ui.go("encyclopedia")},AppText.text(TextKey.Copy0LoaiCay921e87,world.species.size))
}

@Composable internal fun BioProduct(world:BioWorld,ui:BioController,item:JSONObject,modifier:Modifier=Modifier,inventory:Boolean=false,target:String="",encyclopedia:Boolean=false){
    val id=item.s("id");val seed=world.species.any{it.s("id")==id};val accessory=item.s("category")=="TREE";val owned=world.owned(id)>0;val equipped=world.trees.any{it.s("decor")==id};val known=world.record(id).b("discovered")||world.record(id).i("planted")>0||world.count(id)>0
    val action={if(encyclopedia)ui.go("species",id)else if(accessory)ui.act("accessory:$id:$target")else if(inventory){if(seed)ui.act("plant:$id:${ui.plot}")else if(item.b("permanent"))ui.send("equip:$id")else ui.show("fertilizer-help")}else ui.show("purchase",id)}
    BioCard(modifier,padding=AppSpacing.Value12,onClick=action){
        val backdrop=Brush.linearGradient(if(seed&&(!encyclopedia||known))listOf(AppColors.SwatchFFFAFAF8,bp.soft)else listOf(bp.soft,bp.soft))
        Box(Modifier.fillMaxWidth().height(if(LocalConfiguration.current.screenWidthDp<359)AppDimensions.Dp95 else AppDimensions.Dp112).background(backdrop,bioShape(AppRadii.Value15)),contentAlignment=Alignment.Center){if(seed&&(!encyclopedia||known))BioPlant(id,Modifier.fillMaxSize())else Bi(if(seed)"leaf"else if(accessory)accessoryIcon(id)else if(item.b("permanent"))"water"else "fertilizer",size=if(accessory)32 else 43)}
        Spacer(Modifier.height(AppDimensions.Dp10));Bt(item.s("name"),Modifier.heightIn(min=AppDimensions.Dp36),size=AppTypography.Size13,line=AppTypography.LineHeight18_85,semi=true);Spacer(Modifier.height(AppDimensions.Dp10))
        if(encyclopedia)Bt(world.record(id).s("status",AppText.text(TextKey.CopyChuaKhamPhaC0b0cb)),size=AppTypography.Size11,tint=bp.muted,line=AppTypography.LineHeight15_4)
        else if(accessory){Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){Bt(if(equipped)AppText.text(TextKey.CopyDangDung275ca2)else if(owned)AppText.text(TextKey.CopyDaSoHuu822625)else AppText.text(TextKey.Copy0Xu251e68,item.i("price")),Modifier.weight(1f),size=AppTypography.Size13,tint=bp.deep);Box(Modifier.size(AppDimensions.Dp27).border(AppDimensions.Dp1,bp.line,RoundedCornerShape(AppDimensions.Dp8)),contentAlignment=Alignment.Center){Bi(if(equipped)"check"else if(owned)"next"else "plus",size=AppIcons.Size14)}}}
        else{Bt(if(inventory)if(item.b("permanent"))if(world.state.s("equipped")==id)AppText.text(TextKey.CopyDangDung275ca2)else AppText.text(TextKey.CopyTrangBi9e8e12)else "×${world.count(id)}"else if(item.b("permanent")&&world.count(id)>0)if(world.state.s("equipped")==id)AppText.text(TextKey.CopyDangDung275ca2)else AppText.text(TextKey.CopyDaSoHuu822625)else AppText.text(TextKey.Copy0Xu251e68,item.i("price")),size=AppTypography.Size13,tint=bp.deep);if(seed&&!inventory)Bt(bioTier(item.s("tier")),size=AppTypography.Size12,line=AppTypography.LineHeight17,tint=bp.muted)}
    }
}
@Composable internal fun ColumnScope.BioCatalog(world:BioWorld,ui:BioController,inventory:Boolean){
    val family=if(inventory)"inventory"else "shop";val tab=ui.tabs[family]?:"seeds"
    if(!inventory)BioIntro(AppText.text(TextKey.CopyTiemNho87d6e3),AppText.text(TextKey.CopyMotChutChoMamXanh34d909),action={BioIconButton("bag",{ui.go("inventory")})})
    BioTabs(listOf("seeds" to AppText.text(TextKey.CopyHatGiongF02257),"care" to if(inventory)AppText.text(TextKey.CopyDungCu913b1a)else AppText.text(TextKey.CopyChamSoc7b7df6),"accessories" to AppText.text(TextKey.CopyPhuKien0852d7)),tab){ui.tabs[family]=it}
    val shoppingTree=if(!inventory&&ui.param.isNotBlank())world.trees.firstOrNull{it.s("id")==ui.param}else null
    if(shoppingTree!=null)Row(Modifier.fillMaxWidth().background(AppColors.White.copy(alpha=AppOpacity.Value_44),RoundedCornerShape(AppDimensions.Dp15)).border(AppDimensions.Dp1,AppColors.White.copy(alpha=AppOpacity.Value_6),RoundedCornerShape(AppDimensions.Dp15)).padding(horizontal=AppDimensions.Dp11,vertical=AppDimensions.Dp9),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp10)){
        BioPlant(shoppingTree.s("species"),Modifier.size(AppDimensions.Dp38))
        Column(Modifier.weight(1f)){Bt(AppText.text(TextKey.CopyDangChonCho3a6c0f),size=AppTypography.Size10,tint=bp.muted);Bt(shoppingTree.s("name"),size=AppTypography.Size12,semi=true)}
        BioTextAction(AppText.text(TextKey.CopyVeCayB033ce),{ui.go("tree",shoppingTree.s("id"))},"next")
    }
    if(tab=="accessories"&&!inventory)Bt(AppText.text(TextKey.CopyChonMotMonDeoNgayChoCay32c723),size=AppTypography.Size12,tint=bp.muted)
    val items=(when(tab){"care"->world.products;"accessories"->world.accessories;else->world.species}).filter{!inventory||if(tab=="accessories")world.owned(it.s("id"))>0 else world.count(it.s("id"))>0}
    if(items.isNotEmpty())BioGrid(items.size){i,m->BioProduct(world,ui,items[i],m,inventory,ui.targetTree.ifBlank{ui.homeTree})}else BioEmpty(AppText.text(TextKey.CopyTuiDangTrong37bf7b),AppText.text(TextKey.CopyBanCoTheMuaVaDungNgayTrongTiem784a6a),"bag",AppText.text(TextKey.CopyGheTiemCb74e9),{ui.go("shop")})
    if(inventory&&items.isNotEmpty())BioButton(if(tab=="accessories")AppText.text(TextKey.CopyMuaPhuKien7055c4)else AppText.text(TextKey.CopyGheTiemCb74e9),{if(tab=="accessories")ui.tabs["shop"]="accessories";ui.go("shop")},Modifier.fillMaxWidth(),"shop",true)
    if(!inventory&&tab=="care")BioDetails(AppText.text(TextKey.CopyDungVatPham1cb958)){Bt(AppText.text(TextKey.CopyBinhTuoiDungLauDaiPhanBonDungTrucTiepTaiC180e06),size=AppTypography.Size12,tint=bp.muted)}
    if(!inventory&&tab=="seeds")Bt(AppText.text(TextKey.CopyChonHatMuaGieoChamCayAd0e4c),Modifier.fillMaxWidth(),size=AppTypography.Size11,tint=bp.muted,align=TextAlign.Center)
}

@Composable internal fun ColumnScope.BioTree(world:BioWorld,ui:BioController,compact:Boolean=false){
    val t=world.tree(ui.param)
    if(t==null){BioEmpty(AppText.text(TextKey.CopyCayDaRoiVuonA1f818),AppText.text(TextKey.CopyBanVanCoTheXemLichSuTrongBachThao92d81b),action=AppText.text(TextKey.CopyBachThaoF2ed64),onClick={ui.go("encyclopedia")});return}
    BioHero(world,ui,t,short=compact)
    val tab=ui.tabs["tree"]?:"care";BioTabs(listOf("care" to AppText.text(TextKey.CopyChamSoc7b7df6),"decor" to AppText.text(TextKey.CopyTrangTriD48231),"info" to AppText.text(TextKey.CopyThongTin4aa16c)),tab){ui.tabs["tree"]=it}
    when(tab){
        "care"->{Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Bt(if(t.s("state")=="dead")AppText.text(TextKey.CopyCayDaKhoHeo9c0717)else AppText.text(TextKey.CopyMotChutNuocNhe30fe89),size=AppTypography.Size17,bold=true);Bt(if(t.b("watered"))AppText.text(TextKey.CopyDaTuoiDe2042)else AppText.text(TextKey.CopyCanTuoiA0957f),size=AppTypography.Size12,tint=bp.muted);Bt(if(t.s("state")=="dead")AppText.text(TextKey.CopyKhongTheHoiPhuc086975)else AppText.text(TextKey.CopyTuoiMienPhiKhongCongDonDe3e6a),size=AppTypography.Size11,tint=bp.muted)};BioButton(AppText.text(TextKey.CopyTuoi617613),{ui.send("water:"+t.s("id"))},icon="water",enabled=!t.b("watered")&&t.s("state")!="dead")}
            if(t.s("state")!="dead"){Bt(AppText.text(TextKey.CopyDinhDuong3c8978),size=AppTypography.Size17,bold=true);Bt(if(t.s("state")=="mature")AppText.text(TextKey.CopyCayDaTruongThanhKhongCanBonThemEeed14)else if(!t.b("watered"))AppText.text(TextKey.CopyTuoiCayTruocKhiBonPhanCfd0cb)else if(!t.b("canFertilize",true))AppText.text(TextKey.CopyCayDangDuocBonChoHetThoiGianTruocKhiDungT72bbf2)else AppText.text(TextKey.CopyChonPhanDangCoDeBonChoCay540e72),size=AppTypography.Size12,tint=bp.muted);Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){world.products.filter{!it.b("permanent")}.forEach{p->BioCard(Modifier.fillMaxWidth(),padding=AppSpacing.Value12,radius=AppRadii.Value18){Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){Box(Modifier.size(AppDimensions.Dp32).background(bp.soft,bioShape(AppRadii.Value10)),contentAlignment=Alignment.Center){Bi("fertilizer",size=AppIcons.Size19)};Column(Modifier.weight(1f)){Bt(p.s("name"),size=AppTypography.Size12,semi=true);Bt(AppText.text(TextKey.Copy01GioCon252cf45,p.s("rate"),p.i("hours"),world.count(p.s("id"))),size=AppTypography.Size11,tint=bp.muted)};BioButton(if(world.count(p.s("id"))==0)AppText.text(TextKey.CopyHetA89ea0)else AppText.text(TextKey.CopyBon4fd741),{ui.send("fertilize:"+t.s("id")+":"+p.s("id"))},enabled=t.b("canFertilize",t.b("watered"))&&world.count(p.s("id"))>0&&t.s("state")=="growing"&&!ui.busy)}}}};BioTextAction(AppText.text(TextKey.CopyBoSungVatPhamA4c18c),{ui.act("shop-care")},"next",modifier=Modifier.fillMaxWidth())}}
        "decor"->{if(t.s("state")=="dead")Bt(AppText.text(TextKey.CopyCayDaKhoHeoKhongTheDeoPhuKien7658f1),tint=bp.muted)else{Row(Modifier.fillMaxWidth().height(if(t.s("decor").isNotBlank())AppDimensions.Dp32 else AppDimensions.Dp26),verticalAlignment=Alignment.CenterVertically){Bt(AppText.text(TextKey.CopyDiemToChoCay027c8d),Modifier.weight(1f),size=AppTypography.Size16,bold=true,spacing=AppTypography.TrackingMinus_35);if(t.s("decor").isNotBlank())BioTextAction(AppText.text(TextKey.CopyThaoD39d4a),{ui.send("unequip:"+t.s("id"))},modifier=Modifier.requiredHeight(AppDimensions.Dp48))};if(t.s("decor").isNotBlank())Bt(AppText.text(TextKey.CopyDangDungB6dcf3)+world.product(t.s("decor")).s("name").lowercase(),size=AppTypography.Size12,tint=bp.muted);BioGrid(world.accessories.size){i,m->BioProduct(world,ui,world.accessories[i],m,target=t.s("id"))};BioButton(AppText.text(TextKey.CopyTiemPhuKienE4d7d0),{ui.act("bio-accessory-shop:"+t.s("id"))},Modifier.fillMaxWidth(),"shop",true)}}
        else->{val s=world.species(t.s("species"));BioCard(Modifier.fillMaxWidth()){BioFacts(listOf(AppText.text(TextKey.CopyLoaiD0aa6d) to s.s("name"),AppText.text(TextKey.CopyGiaiDoanAa70a1) to "${t.i("stage")} / ${s.i("stages")}",AppText.text(TextKey.CopyPhatTrien2d33cb) to "${t.i("progress")}%",AppText.text(TextKey.CopyNgayTrong2eb870) to t.s("planted")));Bt(s.s("description"),size=AppTypography.Size12,tint=bp.muted)};BioLink(AppText.text(TextKey.CopyHoSoLoaiCfff22),"book",{ui.go("species",t.s("species"))})}
    }
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){if(t.s("state")=="mature")BioTextAction(AppText.text(TextKey.CopyBanCay5210c7),{ui.show("sell",t.s("id"))});BioTextAction(AppText.text(TextKey.CopyDonCayE3d410),{ui.show("remove",t.s("id"))},"trash",AppColors.Danger,iconFirst=true)}
}

@Composable internal fun BioBars(world:BioWorld){
    val values=world.data.o("statistics").a("recentDailyCompletions").let{a->(0 until 7).map{a.optInt(it).coerceAtLeast(0)}};val max=maxOf(1,values.maxOrNull()?:0)
    val labels=bioDates(world.data.s("date"),7).map{date->val calendar=java.util.Calendar.getInstance().apply{time=java.text.SimpleDateFormat("yyyy-MM-dd",java.util.Locale.US).parse(date)!!};val day=calendar.get(java.util.Calendar.DAY_OF_WEEK);bioWeekday(day)}
    Row(Modifier.fillMaxWidth().height(AppDimensions.Dp112),horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp10),verticalAlignment=Alignment.Bottom){values.forEachIndexed{i,value->Column(Modifier.weight(1f).semantics(mergeDescendants=true){contentDescription=AppText.text(TextKey.Copy01ViecHoanThanh0bc363,labels[i],value)},horizontalAlignment=Alignment.CenterHorizontally){Bt(value.toString(),size=AppTypography.Size12,tint=bp.muted);Spacer(Modifier.height(AppDimensions.Dp4));Box(Modifier.width(AppDimensions.Dp24).height((value.toFloat()/max*66).dp).background(if(i==6)bp.deep else bp.accent,RoundedCornerShape(topStart=AppDimensions.Dp6,topEnd=AppDimensions.Dp6)));HorizontalDivider(color=bp.line,thickness=AppDimensions.Dp1);Spacer(Modifier.height(AppDimensions.Dp5));Bt(labels[i],size=AppTypography.Size12,tint=bp.muted)}}}
}
@Composable internal fun ColumnScope.BioStats(world:BioWorld,ui:BioController){
    val stats=world.data.o("statistics")
    BioIntro(AppText.text(TextKey.CopyNhatKyXanhAa1337),AppText.text(TextKey.CopyNhungDieuDaLonLen2c3892),AppText.text(TextKey.CopyMoiBuocNhoDeuDuocGhiNhanB0ce5b),{BioIconButton("settings",{ui.go("settings")})})
    BioMetrics(listOf(AppText.text(TextKey.CopyViecHoanThanhE0966b) to stats.i("total").toString(),AppText.text(TextKey.CopyCayTrongVuon6565bb) to world.trees.size.toString()))
    BioCard(Modifier.fillMaxWidth()){Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){Bt(AppText.text(TextKey.Copy7NgayVuaQua66a783),Modifier.weight(1f),size=AppTypography.Size17,bold=true);BioIconButton("refresh",{ui.send("refresh")})};BioBars(world)}
    BioCard(Modifier.fillMaxWidth(),bp.soft,padding=AppSpacing.Value18){BioMetrics(listOf(AppText.text(TextKey.CopyTuanNayCc1611) to stats.i("week").toString(),AppText.text(TextKey.CopyHomNayE6747a) to stats.i("today").toString(),AppText.text(TextKey.CopyChuoiNgay5ad2e6) to stats.i("streak").toString()))}
    Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){listOf(Triple("review","calendar",AppText.text(TextKey.CopyNhinLaiTuanFf3f0c)),Triple("achievements","award",AppText.text(TextKey.CopyDauMocDffd66)),Triple("timeline","book",AppText.text(TextKey.CopyKyNiemE3e113)),Triple("focus","clock",AppText.text(TextKey.CopyTapTrung964002)),Triple("settings","settings",AppText.text(TextKey.CopyTuyChon598c1b)),Triple("more","grid",AppText.text(TextKey.CopyHanhTrinh566b59))).forEach{(r,i,t)->BioLink(t,i,{ui.go(r)})}}
    Bt(AppText.text(TextKey.CopyLuuTrenThietBiKhongCanTaiKhoan3ade82),Modifier.fillMaxWidth(),size=AppTypography.Size11,tint=bp.muted,align=TextAlign.Center)
}
@Composable internal fun ColumnScope.BioMore(ui:BioController){BioIntro(AppText.text(TextKey.CopyThemMotChut006e8b),AppText.text(TextKey.CopyTheoNhipCuaBan5a2ada));Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){listOf(Triple("focus","clock",AppText.text(TextKey.CopyTapTrung964002)),Triple("templates","plus",AppText.text(TextKey.CopyViecMau535dc1)),Triple("review","calendar",AppText.text(TextKey.CopyNhinLaiTuanFf3f0c)),Triple("achievements","award",AppText.text(TextKey.CopyDauMocDffd66)),Triple("timeline","book",AppText.text(TextKey.CopyKyNiemE3e113)),Triple("insights","chart",AppText.text(TextKey.CopyNhipThoiQuen1b8324)),Triple("encyclopedia","book",AppText.text(TextKey.CopyBachThaoF2ed64)),Triple("settings","settings",AppText.text(TextKey.CopyTuyChonNhacViecA44d85)),Triple("data","download",AppText.text(TextKey.CopyDuLieuUngDung9843b7))).forEach{(r,i,t)->BioLink(t,i,{ui.go(r)})}}}
