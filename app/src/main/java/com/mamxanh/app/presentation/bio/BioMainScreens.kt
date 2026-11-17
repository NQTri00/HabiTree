package com.mamxanh.app.presentation.bio

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

internal fun treeState(t:JSONObject)=when(t.s("state")){"dead"->"Đã khô héo";"mature"->"Trưởng thành";else->"Đang lớn"}
internal fun taskReward(t:JSONObject)=if(t.s("verification")=="TIMER")when{t.i("minutes")>=45->20;t.i("minutes")>=20->15;else->10}else 8
internal fun growthCaption(t:JSONObject)=when(t.s("state")){"dead"->"Mầm xanh đã dừng lại";"mature"->"Đã trưởng thành";else->"Đang lớn cùng bạn"}
internal fun accessoryIcon(id:String)=when(id){"tree_ribbon"->"gift";"tree_lantern"->"sun";else->"spark"}

@Composable internal fun BioHero(world:BioWorld,ui:BioController,t:JSONObject,home:Boolean=false,short:Boolean=false){
    val p=bp;val narrow=LocalConfiguration.current.screenWidthDp<359;val shape=bioShape(26);val dead=t.s("state")=="dead"
    Column(Modifier.fillMaxWidth().shadow(5.dp,shape,ambientColor=p.deep.copy(alpha=.35f),spotColor=p.deep.copy(alpha=.30f)).background(remember(p){bioHeroBrush(p)},shape).border(1.dp,Color.White.copy(alpha=.75f),shape).clip(shape).drawBehind {
        val step=24.dp.toPx();var x=0f;while(x<size.width){drawLine(Color.White.copy(alpha=.08f),Offset(x,0f),Offset(x,size.height),1f);x+=step};var y=0f;while(y<size.height){drawLine(Color.White.copy(alpha=.08f),Offset(0f,y),Offset(size.width,y),1f);y+=step}
        val right=size.width-12.dp.toPx();val top=12.dp.toPx();val len=18.dp.toPx();drawLine(p.deep.copy(alpha=.45f),Offset(right-len,top),Offset(right,top),1.dp.toPx());drawLine(p.deep.copy(alpha=.45f),Offset(right,top),Offset(right,top+len),1.dp.toPx())
    }.padding(start=if(narrow)15.dp else 19.dp,end=if(narrow)15.dp else 19.dp,top=15.dp,bottom=19.dp)){
        Row(Modifier.fillMaxWidth().height(48.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.SpaceBetween){
            BioTag(treeState(t),dot=true)
            if(home)BioTextAction("Đổi cây",{ui.show("tree-picker")},"next")
            else BioTextAction(if(ui.homeTree==t.s("id"))"✓ Trên trang chủ" else "Đặt ở trang chủ",{ui.act("bio-pin:"+t.s("id"))})
        }
        Spacer(Modifier.height(2.dp))
        val landscape=LocalConfiguration.current.screenHeightDp<500
        val artHeight=if(landscape)100 else if(home){if(short)130 else 150}else if(short)160 else if(narrow)190 else 220
        Box(Modifier.fillMaxWidth().height(artHeight.dp).then(if(home)Modifier.clickable{ui.go("tree",t.s("id"))}else Modifier),contentAlignment=Alignment.Center){
            BioPlant(t.s("species"),Modifier.height((artHeight-if(home)0 else 12).dp).fillMaxWidth(),dead)
            val accessory=t.s("decor").takeIf{it.isNotBlank()&&world.owned(it)>0}
            if(accessory!=null&&!dead)Box(Modifier.align(Alignment.TopEnd).padding(top=55.dp,end=14.dp).size(32.dp).background(Color.White.copy(alpha=.6f),RoundedCornerShape(50)).border(1.dp,p.accent,RoundedCornerShape(50)),contentAlignment=Alignment.Center){Bi(accessoryIcon(accessory),size=23)}
            if(!home&&world.trees.size>1){BioIconButton("back",{ui.act("tree-prev")},Modifier.align(Alignment.CenterStart).offset(x=(-6).dp),boxed=true,iconSize=18,tint=p.deep,label="Cây trước");BioIconButton("next",{ui.act("tree-next")},Modifier.align(Alignment.CenterEnd).offset(x=6.dp),boxed=true,iconSize=18,tint=p.deep,label="Cây tiếp theo")}
        }
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(8.dp)){
            Column(Modifier.weight(1f)){Bt(t.s("name"),size=21,line=28.35f,bold=true,spacing=-.7f);Bt("${t.i("stage",1)}/${world.species(t.s("species")).i("stages",6)} giai đoạn",size=12,tint=p.muted,line=21.7f)}
            BioTag(if(dead)"Khô héo" else if(t.b("watered"))"Đủ nước" else "Cần tưới","water")
        }
        Spacer(Modifier.height(12.dp));BioProgress(t.i("progress")/100f,white=true)
        Spacer(Modifier.height(6.dp));Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Bt(growthCaption(t),size=10,tint=p.muted,line=18.6f);Bt("${t.i("progress")}%",size=11,tint=p.deep,mono=true,line=18.6f)}
        if(home){Spacer(Modifier.height(13.dp));if(LocalDensity.current.fontScale>1.2f)Column(verticalArrangement=Arrangement.spacedBy(10.dp)){
            BioButton(if(t.b("watered"))"Đã tưới" else "Tưới cây",{ui.send("water:"+t.s("id"))},Modifier.fillMaxWidth(),"water",enabled=!t.b("watered")&&!dead&&!ui.busy)
            BioButton("Trang trí",{ui.act("bio-tree-decor:"+t.s("id"))},Modifier.fillMaxWidth(),"spark",secondary=true,enabled=!dead&&!ui.busy)
        }else Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){
            BioButton(if(t.b("watered"))"Đã tưới" else "Tưới cây",{ui.send("water:"+t.s("id"))},Modifier.weight(1f),"water",enabled=!t.b("watered")&&!dead&&!ui.busy,labelSize=if(narrow)12 else 14)
            BioButton("Trang trí",{ui.act("bio-tree-decor:"+t.s("id"))},Modifier.weight(1f),"spark",secondary=true,enabled=!dead&&!ui.busy,labelSize=if(narrow)12 else 14)
        }}
    }
}

@Composable internal fun ColumnScope.BioHome(world:BioWorld,ui:BioController,short:Boolean){
    val narrow=LocalConfiguration.current.screenWidthDp<359;val tree=world.tree(ui.homeTree);val date=world.data.s("date").split('-').takeLast(2).reversed().joinToString("-")
    val landscape=LocalConfiguration.current.screenHeightDp<500
    Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.Top,horizontalArrangement=Arrangement.spacedBy(14.dp)){
        Column(Modifier.weight(1f)){if(!landscape){Bt("MỘT NHỊP XANH",size=10,tint=bp.muted,mono=true,spacing=1.8f);Spacer(Modifier.height(7.dp))};Bt("Ngày mới, mầm mới.",size=if(narrow||landscape)22 else 25,line=if(narrow||landscape)28.6f else 32.5f,bold=true,spacing=-1f)}
        Box(Modifier.padding(top=if(landscape)0.dp else 19.dp).background(Color.White.copy(alpha=.25f),RoundedCornerShape(8.dp)).border(1.dp,bp.line,RoundedCornerShape(8.dp)).padding(horizontal=10.dp,vertical=9.dp)){Bt(date,size=11,tint=bp.muted,mono=true,line=14.3f)}
    }
    if(tree!=null)BioHero(world,ui,tree,true,short)else BioEmpty("Bắt đầu bằng một mầm xanh","Một hạt nhỏ, một chút mỗi ngày.",action="Gieo cây đầu tiên",onClick={ui.show("seed-picker")})
    BioSection("Việc tiếp theo","Xem tất cả"){ui.go("tasks")}
    val next=world.tasks.firstOrNull{!it.b("done")&&!it.b("finished")&&it.s("lifecycle","ACTIVE")=="ACTIVE"&&it.b("scheduledToday",true)}
    if(next!=null)BioTaskRow(next,ui)else BioLink("Một việc nhỏ hôm nay","plus",{ui.go("task-new")})
    val phase=world.data.o("phase")
    BioMetrics(listOf("Việc hôm nay" to "${phase.i("completed")}/${phase.i("required")}","Xu hôm nay" to "${world.state.i("earned")}/50","Ngày đều đặn" to phase.i("streak").toString()))
    if(phase.b("canClaim")&&!world.state.b("claimed"))BioReward(world,ui)
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp)){
        listOf(Triple("plus","Thêm việc","go:task-new"),Triple("garden","Gieo cây","bio-seed-picker"),Triple("spark","Phụ kiện","bio-accessory-shop")).forEach{(icon,title,action)->Column(Modifier.weight(1f).heightIn(min=76.dp).background(Color.White.copy(alpha=.25f),bioShape(14)).border(1.dp,bp.line,bioShape(14)).clickable{ui.act(action)}.padding(10.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(7.dp)){Bi(icon);Bt(title,size=11,tint=bp.deep,align=TextAlign.Center)}}
    }
    BioLink("Nhìn lại tuần","calendar",{ui.go("review")},"Những điều nhỏ đã lớn lên")
}

@Composable internal fun BioTaskRow(task:JSONObject,ui:BioController){
    val done=task.b("done")||task.b("finished");val timer=task.s("verification")=="TIMER"
    BioCard(Modifier.fillMaxWidth(),padding=14,radius=22,tint=if(done)lerp(Color.White,bp.soft,.7f)else bp.surface){
        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)){
            Box(Modifier.size(38.dp).background(bp.soft,bioShape(12)),contentAlignment=Alignment.Center){Bi(if(timer)"clock" else if(task.s("category")=="Sức khỏe")"water" else "book")}
            Column(Modifier.weight(1f).heightIn(min=48.dp).clickable{ui.go("task",task.s("id"))},verticalArrangement=Arrangement.Center){
                if(done)Text(task.s("title"),color=bp.muted,fontFamily=com.mamxanh.app.presentation.ui.GardenFontFamily,fontSize=13.sp,fontWeight=androidx.compose.ui.text.font.FontWeight.SemiBold,textDecoration=androidx.compose.ui.text.style.TextDecoration.LineThrough)
                else Bt(task.s("title"),size=14,line=21f,semi=true)
                Bt(if(done)"Đã xong" else task.s("category")+(if(timer)" · ${task.i("minutes")} phút"else ""),size=11,tint=bp.muted)
            }
            Bt("+${taskReward(task)}",size=10,tint=Color(0xFF946A0E))
            Box(Modifier.size(48.dp).background(if(done)bp.deep else bp.soft.copy(alpha=.45f),RoundedCornerShape(50)).border(1.dp,if(done)bp.deep else bp.control,RoundedCornerShape(50)).semantics{contentDescription=if(done)"Đã hoàn thành: "+task.s("title")else if(timer)"Tập trung: "+task.s("title")else "Hoàn thành: "+task.s("title")}.clickable(enabled=!done&&task.s("lifecycle","ACTIVE")=="ACTIVE"&&!ui.busy,role=Role.Button){if(timer)ui.go("focus",task.s("id"))else ui.send("complete:"+task.s("id"))},contentAlignment=Alignment.Center){Bi(if(timer&&!done)"play"else "check",tint=if(done)Color.White else bp.deep)}
        }
    }
}
@Composable internal fun BioReward(world:BioWorld,ui:BioController){BioCard(Modifier.fillMaxWidth(),Color(0xFFF3E8C9),padding=14,radius=18){Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)){Bi("gift",tint=bp.ink);Column(Modifier.weight(1f)){Bt("Thưởng hôm nay",size=12,semi=true);Bt("+8 xu",size=11)};BioButton(if(world.state.b("claimed"))"Đã nhận" else "Nhận",{ui.send("claim")},enabled=world.data.o("phase").b("canClaim")&&!world.state.b("claimed"))}}}

@Composable internal fun ColumnScope.BioTasks(world:BioWorld,ui:BioController){
    val filter=ui.tabs["taskFilter"]?:"today"
    BioIntro(when(filter){"paused"->"Việc tạm dừng";"archived"->"Việc lưu trữ";else->"Việc hôm nay"},"TỪNG CHÚT MỘT",action={BioIconButton("plus",{ui.go("task-new")})})
    val phase=world.data.o("phase")
    if(filter=="today")BioCard(Modifier.fillMaxWidth(),bp.soft,padding=18){Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)){Bt("${phase.i("completed")}/${phase.i("required")} việc đã xong",Modifier.weight(1f),size=14,semi=true);BioTag("+${world.state.i("earned")} xu","spark")};Spacer(Modifier.height(12.dp));BioProgress(if(phase.i("required")>0)phase.i("completed").toFloat()/phase.i("required")else 0f)}
    Row(horizontalArrangement=Arrangement.spacedBy(12.dp)){BioButton("Tập trung",{ui.go("focus")},Modifier.weight(1f),"clock",true);BioButton("Việc mẫu",{ui.go("templates")},Modifier.weight(1f),"plus",true)}
    BioChips(listOf("today" to "Hôm nay","paused" to "Tạm dừng","archived" to "Lưu trữ"),filter){ui.tabs["taskFilter"]=it}
    val list=world.tasks.filter{it.s("lifecycle","ACTIVE")==when(filter){"paused"->"PAUSED";"archived"->"ARCHIVED";else->"ACTIVE"}}
    Column(verticalArrangement=Arrangement.spacedBy(12.dp)){if(list.isEmpty())BioEmpty(when(filter){"paused"->"Không có việc tạm dừng";"archived"->"Chưa có việc lưu trữ";else->"Chưa có việc"},if(filter=="today")"Bắt đầu bằng một việc vừa sức."else "Các việc của bạn vẫn ở Hôm nay.","tasks",if(filter=="today")"Thêm việc"else "Về hôm nay",{if(filter=="today")ui.go("task-new")else ui.tabs["taskFilter"]="today"})else list.forEach{BioTaskRow(it,ui)}}
    if(list.isNotEmpty()&&filter=="today"){BioReward(world,ui);BioButton("Thêm việc",{ui.go("task-new")},Modifier.fillMaxWidth(),"plus")}
    BioLink("Nhịp thói quen","chart",{ui.go("insights")})
}

@Composable internal fun ColumnScope.BioGarden(world:BioWorld,ui:BioController){
    val open=world.state.i("unlocked",5);BioIntro("Vườn của bạn","TỪNG MẦM ĐANG LỚN","${world.trees.size} cây · ${world.emptyPlots().size} ô trống",{BioIconButton("bag",{ui.go("inventory")})})
    Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){BioButton("Tưới tất cả",{ui.show("water-all")},Modifier.weight(1f),"water",enabled=world.trees.any{!it.b("watered")&&it.s("state")!="dead"});BioButton("Gieo cây",{ui.show("seed-picker")},Modifier.weight(1f),"plus",true)}
    val filter=ui.tabs["gardenFilter"]?:"all";BioChips(listOf("all" to "Tất cả","thirsty" to "Cần tưới","growing" to "Đang lớn","mature" to "Trưởng thành"),filter){ui.tabs["gardenFilter"]=it}
    val plots=(1..open).filter{p->val t=world.trees.firstOrNull{it.i("plot")==p};filter=="all"||t!=null&&(filter==t.s("state")||filter=="thirsty"&&!t.b("watered")&&t.s("state")!="dead")}.toMutableList();if(filter=="all"&&open<24)plots.add(open+1)
    val plotLine=bp.line
    BioGrid(plots.size){index,modifier->val plot=plots[index];val t=world.trees.firstOrNull{it.i("plot")==plot};val locked=plot>open
        BioCard(modifier,padding=12,onClick={if(t!=null)ui.go("tree",t.s("id"))else if(locked)ui.show("unlock",plot.toString())else{ui.plot=plot.toString();ui.show("seed-picker")}}){
            Box(Modifier.fillMaxWidth().height(if(t==null&&!locked)110.dp else 105.dp).background(if(locked)Color.Transparent else bp.soft,bioShape(14)),contentAlignment=Alignment.Center){
                if(t!=null)BioPlant(t.s("species"),Modifier.fillMaxSize(),t.s("state")=="dead")else Bi("plus",size=28)
                if(!locked)Bt(String.format("%02d",plot),Modifier.align(Alignment.TopStart).padding(6.dp),size=10,tint=bp.muted,mono=true)
                if(t==null&&!locked)Canvas(Modifier.fillMaxSize()){drawRoundRect(plotLine,cornerRadius=androidx.compose.ui.geometry.CornerRadius(14.dp.toPx()),style=androidx.compose.ui.graphics.drawscope.Stroke(1.dp.toPx(),pathEffect=PathEffect.dashPathEffect(floatArrayOf(3.dp.toPx(),3.dp.toPx()))))}
            }
            Spacer(Modifier.height(25.dp));Bt(t?.s("name")?:if(locked)"Thêm ô trồng" else "Gieo cây",size=13,semi=true)
            if(t!=null){Spacer(Modifier.height(6.dp));BioProgress(t.i("progress")/100f);Bt("${t.i("progress")}% · ${treeState(t)}",size=10,tint=bp.muted);Spacer(Modifier.height(16.dp));Bt(if(t.s("state")=="dead")"Khô héo"else if(t.b("watered"))"Đã tưới"else "Cần tưới",size=10,tint=bp.muted)}else Bt(if(locked)"${400+(open-5).coerceAtLeast(0)*200} xu"else "Một khởi đầu mới",size=10,tint=bp.muted)
        }
    }
    if(plots.isEmpty())BioEmpty("Chưa có cây ở đây","Chọn bộ lọc khác để xem.")
    BioLink("Bách thảo","book",{ui.go("encyclopedia")},"${world.species.size} loài cây")
}

@Composable internal fun BioProduct(world:BioWorld,ui:BioController,item:JSONObject,modifier:Modifier=Modifier,inventory:Boolean=false,target:String="",encyclopedia:Boolean=false){
    val id=item.s("id");val seed=world.species.any{it.s("id")==id};val accessory=item.s("category")=="TREE";val owned=world.owned(id)>0;val equipped=world.trees.any{it.s("decor")==id};val known=world.record(id).b("discovered")||world.record(id).i("planted")>0||world.count(id)>0
    val action={if(encyclopedia)ui.go("species",id)else if(accessory)ui.act("accessory:$id:$target")else if(inventory){if(seed)ui.act("plant:$id:${ui.plot}")else if(item.b("permanent"))ui.send("equip:$id")else ui.show("fertilizer-help")}else ui.show("purchase",id)}
    BioCard(modifier,padding=12,onClick=action){
        val backdrop=Brush.linearGradient(if(seed&&(!encyclopedia||known))listOf(Color(0xFFFAFAF8),bp.soft)else listOf(bp.soft,bp.soft))
        Box(Modifier.fillMaxWidth().height(if(LocalConfiguration.current.screenWidthDp<359)95.dp else 112.dp).background(backdrop,bioShape(15)),contentAlignment=Alignment.Center){if(seed&&(!encyclopedia||known))BioPlant(id,Modifier.fillMaxSize())else Bi(if(seed)"leaf"else if(accessory)accessoryIcon(id)else if(item.b("permanent"))"water"else "fertilizer",size=if(accessory)32 else 43)}
        Spacer(Modifier.height(10.dp));Bt(item.s("name"),Modifier.heightIn(min=36.dp),size=13,line=18.85f,semi=true);Spacer(Modifier.height(10.dp))
        if(encyclopedia)Bt(world.record(id).s("status","Chưa khám phá"),size=11,tint=bp.muted,line=15.4f)
        else if(accessory){Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){Bt(if(equipped)"Đang dùng"else if(owned)"Đã sở hữu"else "${item.i("price")} xu",Modifier.weight(1f),size=13,tint=bp.deep);Box(Modifier.size(27.dp).border(1.dp,bp.line,RoundedCornerShape(8.dp)),contentAlignment=Alignment.Center){Bi(if(equipped)"check"else if(owned)"next"else "plus",size=14)}}}
        else{Bt(if(inventory)if(item.b("permanent"))if(world.state.s("equipped")==id)"Đang dùng"else "Trang bị"else "×${world.count(id)}"else if(item.b("permanent")&&world.count(id)>0)if(world.state.s("equipped")==id)"Đang dùng"else "Đã sở hữu"else "${item.i("price")} xu",size=13,tint=bp.deep);if(seed&&!inventory)Bt(bioTier(item.s("tier")),size=12,line=17f,tint=bp.muted)}
    }
}
@Composable internal fun ColumnScope.BioCatalog(world:BioWorld,ui:BioController,inventory:Boolean){
    val family=if(inventory)"inventory"else "shop";val tab=ui.tabs[family]?:"seeds"
    if(!inventory)BioIntro("Tiệm nhỏ","MỘT CHÚT CHO MẦM XANH",action={BioIconButton("bag",{ui.go("inventory")})})
    BioTabs(listOf("seeds" to "Hạt giống","care" to if(inventory)"Dụng cụ"else "Chăm sóc","accessories" to "Phụ kiện"),tab){ui.tabs[family]=it}
    val shoppingTree=if(!inventory&&ui.param.isNotBlank())world.trees.firstOrNull{it.s("id")==ui.param}else null
    if(shoppingTree!=null)Row(Modifier.fillMaxWidth().background(Color.White.copy(alpha=.44f),RoundedCornerShape(15.dp)).border(1.dp,Color.White.copy(alpha=.6f),RoundedCornerShape(15.dp)).padding(horizontal=11.dp,vertical=9.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(10.dp)){
        BioPlant(shoppingTree.s("species"),Modifier.size(38.dp))
        Column(Modifier.weight(1f)){Bt("Đang chọn cho",size=10,tint=bp.muted);Bt(shoppingTree.s("name"),size=12,semi=true)}
        BioTextAction("Về cây",{ui.go("tree",shoppingTree.s("id"))},"next")
    }
    if(tab=="accessories"&&!inventory)Bt("Chọn một món, đeo ngay cho cây.",size=12,tint=bp.muted)
    val items=(when(tab){"care"->world.products;"accessories"->world.accessories;else->world.species}).filter{!inventory||if(tab=="accessories")world.owned(it.s("id"))>0 else world.count(it.s("id"))>0}
    if(items.isNotEmpty())BioGrid(items.size){i,m->BioProduct(world,ui,items[i],m,inventory,ui.targetTree.ifBlank{ui.homeTree})}else BioEmpty("Túi đang trống","Bạn có thể mua và dùng ngay trong tiệm.","bag","Ghé tiệm",{ui.go("shop")})
    if(inventory&&items.isNotEmpty())BioButton(if(tab=="accessories")"Mua phụ kiện"else "Ghé tiệm",{if(tab=="accessories")ui.tabs["shop"]="accessories";ui.go("shop")},Modifier.fillMaxWidth(),"shop",true)
    if(!inventory&&tab=="care")BioDetails("Dùng vật phẩm"){Bt("Bình tưới dùng lâu dài. Phân bón dùng trực tiếp tại cây.",size=12,tint=bp.muted)}
    if(!inventory&&tab=="seeds")Bt("Chọn hạt → Mua & gieo → Chăm cây",Modifier.fillMaxWidth(),size=11,tint=bp.muted,align=TextAlign.Center)
}

@Composable internal fun ColumnScope.BioTree(world:BioWorld,ui:BioController,compact:Boolean=false){
    val t=world.tree(ui.param)
    if(t==null){BioEmpty("Cây đã rời vườn","Bạn vẫn có thể xem lịch sử trong Bách thảo.",action="Bách thảo",onClick={ui.go("encyclopedia")});return}
    BioHero(world,ui,t,short=compact)
    val tab=ui.tabs["tree"]?:"care";BioTabs(listOf("care" to "Chăm sóc","decor" to "Trang trí","info" to "Thông tin"),tab){ui.tabs["tree"]=it}
    when(tab){
        "care"->{Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Bt(if(t.s("state")=="dead")"Cây đã khô héo"else "Một chút nước nhé?",size=17,bold=true);Bt(if(t.b("watered"))"Đã tưới"else "Cần tưới",size=12,tint=bp.muted);Bt(if(t.s("state")=="dead")"Không thể hồi phục."else "Tưới miễn phí, không cộng dồn.",size=11,tint=bp.muted)};BioButton("Tưới",{ui.send("water:"+t.s("id"))},icon="water",enabled=!t.b("watered")&&t.s("state")!="dead")}
            if(t.s("state")!="dead"){Bt("Dinh dưỡng",size=17,bold=true);Bt(if(t.s("state")=="mature")"Cây đã trưởng thành, không cần bón thêm."else if(!t.b("watered"))"Tưới cây trước khi bón phân."else if(!t.b("canFertilize",true))"Cây đang được bón. Chờ hết thời gian trước khi dùng tiếp."else "Chọn phân đang có để bón cho cây.",size=12,tint=bp.muted);Column(verticalArrangement=Arrangement.spacedBy(12.dp)){world.products.filter{!it.b("permanent")}.forEach{p->BioCard(Modifier.fillMaxWidth(),padding=12,radius=18){Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)){Box(Modifier.size(32.dp).background(bp.soft,bioShape(10)),contentAlignment=Alignment.Center){Bi("fertilizer",size=19)};Column(Modifier.weight(1f)){Bt(p.s("name"),size=12,semi=true);Bt("${p.s("rate")}× · ${p.i("hours")} giờ · Còn ${world.count(p.s("id"))}",size=11,tint=bp.muted)};BioButton(if(world.count(p.s("id"))==0)"Hết"else "Bón",{ui.send("fertilize:"+t.s("id")+":"+p.s("id"))},enabled=t.b("canFertilize",t.b("watered"))&&world.count(p.s("id"))>0&&t.s("state")=="growing"&&!ui.busy)}}}};BioTextAction("Bổ sung vật phẩm",{ui.act("shop-care")},"next",modifier=Modifier.fillMaxWidth())}}
        "decor"->{if(t.s("state")=="dead")Bt("Cây đã khô héo không thể đeo phụ kiện.",tint=bp.muted)else{Row(Modifier.fillMaxWidth().height(if(t.s("decor").isNotBlank())32.dp else 26.dp),verticalAlignment=Alignment.CenterVertically){Bt("Điểm tô cho cây",Modifier.weight(1f),size=16,bold=true,spacing=-.35f);if(t.s("decor").isNotBlank())BioTextAction("Tháo",{ui.send("unequip:"+t.s("id"))},modifier=Modifier.requiredHeight(48.dp))};if(t.s("decor").isNotBlank())Bt("Đang dùng "+world.product(t.s("decor")).s("name").lowercase(),size=12,tint=bp.muted);BioGrid(world.accessories.size){i,m->BioProduct(world,ui,world.accessories[i],m,target=t.s("id"))};BioButton("Tiệm phụ kiện",{ui.act("bio-accessory-shop:"+t.s("id"))},Modifier.fillMaxWidth(),"shop",true)}}
        else->{val s=world.species(t.s("species"));BioCard(Modifier.fillMaxWidth()){BioFacts(listOf("Loài" to s.s("name"),"Giai đoạn" to "${t.i("stage")} / ${s.i("stages")}","Phát triển" to "${t.i("progress")}%","Ngày trồng" to t.s("planted")));Bt(s.s("description"),size=12,tint=bp.muted)};BioLink("Hồ sơ loài","book",{ui.go("species",t.s("species"))})}
    }
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){if(t.s("state")=="mature")BioTextAction("Bán cây",{ui.show("sell",t.s("id"))});BioTextAction("Dọn cây",{ui.show("remove",t.s("id"))},"trash",Color(0xFFA63936),iconFirst=true)}
}

@Composable internal fun BioBars(world:BioWorld){
    val values=world.data.o("statistics").a("recentDailyCompletions").let{a->(0 until 7).map{a.optInt(it).coerceAtLeast(0)}};val max=maxOf(1,values.maxOrNull()?:0)
    val labels=bioDates(world.data.s("date"),7).map{date->val calendar=java.util.Calendar.getInstance().apply{time=java.text.SimpleDateFormat("yyyy-MM-dd",java.util.Locale.US).parse(date)!!};val day=calendar.get(java.util.Calendar.DAY_OF_WEEK);if(day==1)"CN"else "T$day"}
    Row(Modifier.fillMaxWidth().height(112.dp),horizontalArrangement=Arrangement.spacedBy(10.dp),verticalAlignment=Alignment.Bottom){values.forEachIndexed{i,value->Column(Modifier.weight(1f).semantics(mergeDescendants=true){contentDescription="${labels[i]}: $value việc hoàn thành"},horizontalAlignment=Alignment.CenterHorizontally){Bt(value.toString(),size=12,tint=bp.muted);Spacer(Modifier.height(4.dp));Box(Modifier.width(24.dp).height((value.toFloat()/max*66).dp).background(if(i==6)bp.deep else bp.accent,RoundedCornerShape(topStart=6.dp,topEnd=6.dp)));HorizontalDivider(color=bp.line,thickness=1.dp);Spacer(Modifier.height(5.dp));Bt(labels[i],size=12,tint=bp.muted)}}}
}
@Composable internal fun ColumnScope.BioStats(world:BioWorld,ui:BioController){
    val stats=world.data.o("statistics")
    BioIntro("Nhật ký xanh","NHỮNG ĐIỀU ĐÃ LỚN LÊN","Mỗi bước nhỏ đều được ghi nhận.",{BioIconButton("settings",{ui.go("settings")})})
    BioMetrics(listOf("Việc hoàn thành" to stats.i("total").toString(),"Cây trong vườn" to world.trees.size.toString()))
    BioCard(Modifier.fillMaxWidth()){Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){Bt("7 ngày vừa qua",Modifier.weight(1f),size=17,bold=true);BioIconButton("refresh",{ui.send("refresh")})};BioBars(world)}
    BioCard(Modifier.fillMaxWidth(),bp.soft,padding=18){BioMetrics(listOf("Tuần này" to stats.i("week").toString(),"Hôm nay" to stats.i("today").toString(),"Chuỗi ngày" to stats.i("streak").toString()))}
    Column(verticalArrangement=Arrangement.spacedBy(12.dp)){listOf(Triple("review","calendar","Nhìn lại tuần"),Triple("achievements","award","Dấu mốc"),Triple("timeline","book","Kỷ niệm"),Triple("focus","clock","Tập trung"),Triple("settings","settings","Tùy chọn"),Triple("more","grid","Hành trình")).forEach{(r,i,t)->BioLink(t,i,{ui.go(r)})}}
    Bt("Lưu trên thiết bị · Không cần tài khoản",Modifier.fillMaxWidth(),size=11,tint=bp.muted,align=TextAlign.Center)
}
@Composable internal fun ColumnScope.BioMore(ui:BioController){BioIntro("Thêm một chút","THEO NHỊP CỦA BẠN");Column(verticalArrangement=Arrangement.spacedBy(12.dp)){listOf(Triple("focus","clock","Tập trung"),Triple("templates","plus","Việc mẫu"),Triple("review","calendar","Nhìn lại tuần"),Triple("achievements","award","Dấu mốc"),Triple("timeline","book","Kỷ niệm"),Triple("insights","chart","Nhịp thói quen"),Triple("encyclopedia","book","Bách thảo"),Triple("settings","settings","Tùy chọn & nhắc việc"),Triple("data","download","Dữ liệu & ứng dụng")).forEach{(r,i,t)->BioLink(t,i,{ui.go(r)})}}}
