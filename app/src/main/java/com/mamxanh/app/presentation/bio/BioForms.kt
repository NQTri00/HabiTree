package com.mamxanh.app.presentation.bio

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.selection.toggleable
import androidx.compose.ui.semantics.*
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.mamxanh.app.presentation.ui.GardenFontFamily
import org.json.JSONObject

@Composable internal fun BioField(label:String,value:String,onChange:(String)->Unit,modifier:Modifier=Modifier,multiline:Boolean=false,placeholder:String="",icon:String="",number:Boolean=false,centered:Boolean=false,error:String=""){
    Column(modifier.fillMaxWidth(),verticalArrangement=Arrangement.spacedBy(7.dp)){
        if(label.isNotBlank())Bt(label,size=12)
        val textColor=bp.ink;val muted=bp.muted
        BasicTextField(value,onChange,Modifier.fillMaxWidth().heightIn(min=if(multiline)90.dp else 48.dp).background(bp.surface,RoundedCornerShape(12.dp)).border(1.dp,if(error.isBlank())bp.control else Color(0xFFA63936),RoundedCornerShape(12.dp)).semantics{if(label.isNotBlank())contentDescription=label;if(error.isNotBlank())error(error)}.padding(12.dp),singleLine=!multiline,textStyle=TextStyle(textAlign=if(centered)TextAlign.Center else TextAlign.Start,color=textColor,fontSize=14.sp,lineHeight=21.sp,fontFamily=GardenFontFamily,platformStyle=androidx.compose.ui.text.PlatformTextStyle(includeFontPadding=false)),keyboardOptions=KeyboardOptions(keyboardType=if(number)KeyboardType.Number else KeyboardType.Text),cursorBrush=SolidColor(bp.deep),decorationBox={inner->Row(verticalAlignment=if(multiline)Alignment.Top else Alignment.CenterVertically){Box(Modifier.weight(1f)){if(value.isBlank()&&placeholder.isNotBlank())Bt(placeholder,size=14,tint=muted);inner()};if(icon.isNotBlank())Bi(icon,size=15,tint=textColor)}})
        BioError(error)
    }
}
@Composable internal fun BioSelect(label:String,options:List<Pair<String,String>>,value:String,modifier:Modifier=Modifier,onSelect:(String)->Unit){
    var open by remember{mutableStateOf(false)}
    Column(modifier.fillMaxWidth(),verticalArrangement=Arrangement.spacedBy(7.dp)){
        if(label.isNotBlank())Bt(label,size=12)
        Box{
            Row(Modifier.fillMaxWidth().heightIn(min=48.dp).background(bp.surface,RoundedCornerShape(12.dp)).border(1.dp,bp.control,RoundedCornerShape(12.dp)).semantics{if(label.isNotBlank())contentDescription=label;stateDescription=if(open)"Đã mở"else "Đã đóng"}.clickable(role=Role.DropdownList){open=true}.padding(12.dp),verticalAlignment=Alignment.CenterVertically){Bt(options.firstOrNull{it.first==value}?.second?:options.firstOrNull()?.second.orEmpty(),Modifier.weight(1f),size=14);Bi("next",Modifier.rotate(90f),tint=bp.ink,size=15)}
            DropdownMenu(open,{open=false},containerColor=bp.surface){options.forEach{(id,title)->DropdownMenuItem(text={Bt(title)},onClick={onSelect(id);open=false})}}
        }
    }
}
@Composable internal fun BioToggle(label:String,value:Boolean,onChange:(Boolean)->Unit){
    Row(Modifier.fillMaxWidth().heightIn(min=55.dp).padding(end=3.dp).toggleable(value=value,role=Role.Switch,onValueChange=onChange),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)){
        Bt(label,Modifier.weight(1f),size=13)
        Box(Modifier.size(42.dp,25.dp).background(if(value)bp.deep else Color(0xFFCFD6C8),RoundedCornerShape(20.dp)).padding(3.dp)){Box(Modifier.align(if(value)Alignment.CenterEnd else Alignment.CenterStart).size(19.dp).background(Color.White,RoundedCornerShape(50)))}
    }
}
@Composable internal fun BioError(text:String){if(text.isNotBlank())Bt(text,Modifier.semantics{liveRegion=LiveRegionMode.Polite},size=12,tint=Color(0xFFA63936))}

@Composable internal fun ColumnScope.BioTaskDetail(world:BioWorld,ui:BioController){
    val t=world.task(ui.param)
    if(t==null){BioEmpty("Việc không còn ở đây",icon="tasks",action="Về danh sách",onClick={ui.go("tasks")});return}
    Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.Top,horizontalArrangement=Arrangement.spacedBy(12.dp)){Column(Modifier.weight(1f)){Bt(t.s("category"),size=10,tint=bp.muted,mono=true,spacing=1.8f);Spacer(Modifier.height(7.dp));Bt(t.s("title"),size=27,line=35.1f,bold=true,spacing=-1f)};BioTag("+${taskReward(t)} xu")}
    val lifecycle=t.s("lifecycle","ACTIVE");val done=t.b("done")||t.b("finished");val timer=t.s("verification")=="TIMER"
    BioCard(Modifier.fillMaxWidth()){BioFacts(listOf("Loại" to when(t.s("kind")){"HABIT"->"Thói quen";"TODO"->"Một lần";else->"Hằng ngày"},"Lịch" to when(t.s("frequency")){"WEEKDAYS"->"Ngày thường";"WEEKENDS"->"Cuối tuần";"CUSTOM"->"Ngày "+t.s("days");"ONCE"->"Một lần";else->"Mỗi ngày"},"Xác nhận" to if(timer)"${t.i("minutes")} phút"else "Chạm để hoàn thành","Trạng thái" to if(done)"Đã xong"else if(lifecycle=="PAUSED")"Tạm dừng"else if(lifecycle=="ARCHIVED")"Lưu trữ"else "Đang hoạt động"));if(t.s("description").isNotBlank())Bt(t.s("description"),size=12,tint=bp.muted)}
    BioDetails("Lịch & lịch sử"){BioFacts(listOf("Khung giờ" to if(t.s("start").isBlank())"Cả ngày"else t.s("start")+"–"+t.s("end"),"Hạn chót" to t.s("due","Không có"),"Chuỗi ngày" to t.i("streak").toString(),"Lần cuối" to t.s("lastDone").ifBlank{if(done)"Hôm nay"else "Chưa ghi nhận"}));Bt("7 ngày gần đây",size=12);if(t.a("history").length()==0)Bt("Chưa có lần hoàn thành",size=12,tint=bp.muted)else BioFacts(t.a("history").objects().map{it.s("date") to "${it.i("granted")}/${it.i("nominal")} xu"})}
    BioButton(if(timer)"Tập trung"else if(done)"Đã hoàn thành"else "Hoàn thành",{if(timer)ui.go("focus",t.s("id"))else ui.send("complete:"+t.s("id"))},Modifier.fillMaxWidth(),if(timer)"clock"else "check",enabled=!done&&lifecycle=="ACTIVE")
    BioButton("Sửa việc",{ui.go("task-new",t.s("id"))},Modifier.fillMaxWidth(),"edit",true)
    BioLink("Nhịp thói quen","chart",{ui.go("insights",t.s("id"))})
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){BioTextAction(if(lifecycle=="ACTIVE")"Tạm dừng"else "Tiếp tục",{ui.send("lifecycle:"+t.s("id")+":"+if(lifecycle=="ACTIVE")"PAUSED"else "ACTIVE")});BioTextAction("Lưu trữ",{ui.show("archive",t.s("id"))})}
}

@Composable internal fun ColumnScope.BioTaskForm(world:BioWorld,ui:BioController){
    val initial=taskFormInitial(world,ui)
    fun value(key:String,default:String="")=ui.fields[key]?:initial.s(key,default)
    fun change(key:String,v:String){ui.fields[key]=v;ui.dirty=true;if(ui.fields["task-error"]==key)ui.fields["task-error"]=""}
    val error=ui.fields["task-error"].orEmpty()
    val titleLocation=remember{BringIntoViewRequester()};val minutesLocation=remember{BringIntoViewRequester()};val daysLocation=remember{BringIntoViewRequester()}
    LaunchedEffect(error,ui.fields["task-error-attempt"]){when(error){"title"->titleLocation.bringIntoView();"minutes"->minutesLocation.bringIntoView();"days"->daysLocation.bringIntoView()}}
    Column(Modifier.fillMaxWidth().padding(top=16.dp),verticalArrangement=Arrangement.spacedBy(16.dp)){
    BioField("Tên việc",value("title"),{change("title",it.take(100))},Modifier.bringIntoViewRequester(titleLocation),placeholder="Ví dụ: Đọc 10 trang sách",error=if(error=="title")"Nhập tên việc (1–100 ký tự)."else "")
    BioSelect("Loại",listOf("HABIT" to "Thói quen","DAILY" to "Hằng ngày","TODO" to "Một lần"),value("kind")){change("kind",it)}
    BioSelect("Nhóm",listOf("Học tập","Sức khỏe","Nếp sống","Việc nhà","Cá nhân").map{it to it},value("category")){change("category",it)}
    if(value("kind")=="TODO")BioDateField("Hạn chót · tùy chọn",value("due"),{change("due",it)})else{
        BioSelect("Lịch",listOf("EVERY_DAY" to "Mỗi ngày","WEEKDAYS" to "Ngày thường","WEEKENDS" to "Cuối tuần","CUSTOM" to "Tự chọn"),value("frequency")){change("frequency",it)}
        if(value("frequency")=="CUSTOM"){Column(Modifier.bringIntoViewRequester(daysLocation)){Bt("Chọn ngày",size=12,tint=bp.muted);BioError(if(error=="days")"Chọn ít nhất một ngày."else "")};BioGrid(7,4){i,m->val selected=value("days").contains(i.toString());Row(m.heightIn(min=48.dp).border(1.dp,bp.line,bioShape(13)).background(if(selected)bp.accent else Color.White.copy(alpha=.55f),bioShape(13)).clickable{change("days",if(selected)value("days").replace(i.toString(),"")else value("days")+i)},verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.Center){Bt(if(selected)"☑"else "□",size=14);Spacer(Modifier.width(5.dp));Bt(listOf("CN","T2","T3","T4","T5","T6","T7")[i],size=11)}}}
    }
    BioSelect("Hoàn thành bằng",listOf("CLICK" to "Một lần chạm","TIMER" to "Bộ đếm thời gian"),value("verification")){change("verification",it)}
    if(value("verification")=="TIMER")BioField("Thời gian (5–240 phút)",value("minutes","5"),{change("minutes",it)},Modifier.bringIntoViewRequester(minutesLocation),number=true,error=if(error=="minutes")"Nhập số nguyên từ 5–240 phút."else "")
    BioDetails("Thêm chi tiết"){
        BioField("Mô tả",value("description"),{change("description",it.take(500))},multiline=true)
        Row(horizontalArrangement=Arrangement.spacedBy(12.dp)){BioTimeField("Từ giờ",value("start"),{change("start",it)},Modifier.weight(1f));BioTimeField("Đến giờ",value("end"),{change("end",it)},Modifier.weight(1f))}
        BioTimeField("Nhắc lúc",value("reminder"),{change("reminder",it)})
        Bt("Lịch mới áp dụng từ ngày mai. Hôm nay là tùy chọn. Khung giờ có thể qua đêm. Nhắc việc cần quyền thông báo; từ chối vẫn lưu được việc.",size=12,tint=bp.muted)
    }
    val minutes=value("minutes","5").toIntOrNull()?:5
    BioCard(Modifier.fillMaxWidth(),bp.soft){Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Bt("Thưởng mỗi lần",size=12);Bt("${if(value("verification")=="TIMER")if(minutes>=45)20 else if(minutes>=20)15 else 10 else 8} xu",semi=true)};Bt("Tối đa 50 xu/ngày",size=12,tint=bp.muted)}
    }
}

@Composable internal fun ColumnScope.BioFocus(world:BioWorld,ui:BioController){
    val focus=world.state.o("focus");val running=focus.s("state")=="RUNNING";val present=focus.s("id").isNotBlank();val minutes=focusMinutes(world,ui);val task=focusTask(ui)
    val landscape=LocalConfiguration.current.screenHeightDp<500
    if(present){if(!landscape)Bt("KHOẢNG THỜI GIAN CỦA BẠN",size=10,mono=true,tint=bp.muted,spacing=1.8f);Bt(focus.s("title","Một khoảng riêng mình"),Modifier.fillMaxWidth(),size=if(landscape)22 else 27,bold=true,align=TextAlign.Center)}else if(landscape)Bt("Tập trung",size=22,bold=true)else BioIntro("Tập trung","CHỈ MỘT VIỆC THÔI","Dành một khoảng cho điều quan trọng.",{Bi("sun")})
    val remaining=if(present)focus.i("remaining",1500)else (minutes.toIntOrNull()?:0).coerceIn(0,240)*60
    val p=bp
    Box(Modifier.align(Alignment.CenterHorizontally).size(if(landscape)140.dp else 200.dp).drawBehind{drawCircle(p.soft,radius=size.minDimension/2-4.dp.toPx(),style=androidx.compose.ui.graphics.drawscope.Stroke(8.dp.toPx()))},contentAlignment=Alignment.Center){Column(horizontalAlignment=Alignment.CenterHorizontally){Bt("${remaining/60}:${(remaining%60).toString().padStart(2,'0')}",size=if(landscape)36 else 46,line=if(landscape)46f else 60f,mono=true,spacing=-2f);if(!landscape)Bt(if(present)if(running)"Đang tập trung"else if(focus.b("break"))"Nghỉ một chút"else "Đã tạm dừng"else "Bắt đầu khi bạn sẵn sàng",size=12,tint=bp.muted)}}
    if(present){BioDetails("Phiên đang lưu"){Bt(bioDisplayDate(world.data.s("date"))+" · Rời trang sẽ tạm dừng. Phần đã làm luôn được giữ lại. Phiên tự do không thưởng xu.",size=12,tint=bp.muted)}}
    else{
        BioSelect("Dành cho",listOf("0" to "Tự do · không thưởng xu")+world.tasks.filter{it.s("verification")=="TIMER"&&!it.b("done")&&it.b("available",true)&&it.s("lifecycle","ACTIVE")=="ACTIVE"}.map{it.s("id") to "${it.s("title")} · ${it.i("minutes")} phút"},task){ui.fields["focus:task"]=it}
        if(task=="0"){
            BioChips(listOf(15,25,45,60).map{it.toString() to "$it phút"},minutes){ui.fields["focus:minutes"]=it}
            BioField("Phút tập trung · 5–120",minutes,{ui.fields["focus:minutes"]=it},number=true,error=if((minutes.toIntOrNull()?:0) !in 5..120)"Nhập số phút từ 5–120."else "")
        }else BioCard(Modifier.fillMaxWidth(),bp.soft,padding=12){Bt("${minutes} phút · theo thời gian của việc",size=13,semi=true);Bt("Sửa thời gian trong chi tiết việc.",size=12,tint=bp.muted)}
    }
    BioDetails("Các phiên đã lưu · ${world.state.a("focusHistory").length()} hoàn thành"){if(world.state.a("focusHistory").length()==0)Bt("Chưa có phiên hoàn thành.",size=12,tint=bp.muted);world.state.a("focusHistory").objects().forEach{Bt(it.s("title"),size=13)}}
}

@Composable internal fun BioTemplateRow(t:JSONObject,selected:Boolean,onboarding:Boolean=false,onChange:()->Unit){
    BioCard(Modifier.fillMaxWidth().semantics{toggleableState=if(selected)ToggleableState.On else ToggleableState.Off;role=Role.Checkbox},padding=if(onboarding)12 else 14,radius=18,tint=if(selected)bp.soft else bp.surface,onClick=onChange,borderColor=if(selected)bp.deep else bp.line){Row(Modifier.fillMaxWidth().heightIn(min=if(onboarding)65.dp else 60.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(10.dp)){
        if(!onboarding)Box(Modifier.width(20.dp).height(28.dp).background(bp.soft,bioShape(8)),contentAlignment=Alignment.Center){Bi(if(t.s("verification")=="TIMER")"clock"else "leaf",size=20)}
        Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(4.dp)){
            Bt(t.s("title"),size=13,semi=true)
            Bt(t.s("category")+" · "+(if(t.s("verification")=="TIMER")"Bộ đếm · ${t.i("minutes")} phút"else "Chạm · "+t.s("effort")),size=12,tint=bp.muted)
            Bt((if(t.s("frequency")=="WEEKENDS")"Cuối tuần"else "Mỗi ngày")+" · +${taskReward(t)} xu",size=12,tint=bp.muted)
        }
        Box(Modifier.size(20.dp).background(if(selected)bp.deep else Color.Transparent,RoundedCornerShape(3.dp)).border(1.dp,if(selected)bp.deep else bp.control,RoundedCornerShape(3.dp)),contentAlignment=Alignment.Center){if(selected)Bi("check",size=14,tint=Color.White)}
    }}
}
@Composable internal fun ColumnScope.BioTemplates(world:BioWorld,ui:BioController){
    val chosen=selectedBioTemplates(ui);Column{Bt("Chọn việc vừa sức",size=27,line=35.1f,bold=true,spacing=-1f);Bt("Thêm một lần, chỉnh sửa bất cứ lúc nào.",size=12,tint=bp.muted)}
    Column(verticalArrangement=Arrangement.spacedBy(12.dp)){world.catalog.a("templates").objects().forEach{t->BioTemplateRow(t,chosen.contains(t.s("id"))){ui.fields["template:"+t.s("id")]=(!chosen.contains(t.s("id"))).toString()}}}
}

@Composable internal fun ColumnScope.BioSettings(world:BioWorld,ui:BioController){
    val settings=world.settings
    fun toggle(key:String,default:Boolean=false)=(ui.fields["settings:$key"]?:settings.b(key,default).toString()).toBoolean()
    var start by rememberSaveable{mutableStateOf(settings.s("quietStart","22:00"))};var end by rememberSaveable{mutableStateOf(settings.s("quietEnd","07:00"))}
    Bt("Theo nhịp của bạn",size=27,bold=true,spacing=-1f);Bt("Nhắc việc tùy chọn; có thể trễ do tiết kiệm pin. Không gửi dồn dập.",size=12,tint=bp.muted)
    BioCard(Modifier.fillMaxWidth()){
        listOf("paused" to "Tạm dừng mọi nhắc việc","treeReminders" to "Nhắc khi cây cần tưới","breakPrompts" to "Gợi ý nghỉ sau 25 phút","reducedMotion" to "Giảm chuyển động").forEachIndexed{i,(key,label)->if(i>0)HorizontalDivider(color=bp.line);BioToggle(label,toggle(key)){ui.fields["settings:$key"]=it.toString()}}
    }
    BioDetails("Giờ yên lặng"){Row(horizontalArrangement=Arrangement.spacedBy(12.dp)){BioTimeField("Bắt đầu",start,{start=it},Modifier.weight(1f));BioTimeField("Kết thúc",end,{end=it},Modifier.weight(1f))}}
    BioButton("Lưu tùy chọn",{ui.send("settings",listOf("paused","treeReminders","breakPrompts","reducedMotion").associateWith{toggle(it).toString()}+mapOf("quietStart" to start,"quietEnd" to end))},Modifier.fillMaxWidth())
    BioButton("Quyền thông báo",{ui.show("permission")},Modifier.fillMaxWidth(),"bell",true)
    BioLink("Nhắc riêng từng việc","clock",{ui.go("insights")});BioLink("Dữ liệu & ứng dụng","download",{ui.go("data")});BioLink("Xem lại bắt đầu","leaf",{ui.go("onboard")})
}

@Composable internal fun ColumnScope.BioData(world:BioWorld,ui:BioController){
    BioCard(Modifier.fillMaxWidth()){
        Bt("Sao lưu của bạn",size=17,bold=true);Bt("Lưu trên thiết bị · ${world.state.i("bytes",3072)/1024} KB",size=12,tint=bp.muted);Spacer(Modifier.height(18.dp));Row(horizontalArrangement=Arrangement.spacedBy(12.dp)){BioButton("Xuất tệp",{ui.send("export")},Modifier.weight(1f),"download");BioButton("Khôi phục",{ui.send("import")},Modifier.weight(1f),"upload",true)}
        Spacer(Modifier.height(16.dp));BioDetails("Quyền riêng tư của bản sao"){Bt("Bản sao có thể chứa lịch sử và ghi chú riêng tư. Chỉ chia sẻ với người tin cậy.",size=12,tint=bp.muted)}
    }
    Column(verticalArrangement=Arrangement.spacedBy(6.dp)){BioButton("Bản an toàn gần nhất",{ui.show("recover")},Modifier.fillMaxWidth(),secondary=true,enabled=world.state.b("safety"));if(!world.state.b("safety"))Bt("Chưa có bản an toàn. Bản này được tạo trước khi khôi phục dữ liệu.",size=12,tint=bp.muted)}
    BioButton("Xóa dữ liệu",{ui.show("clear")},Modifier.fillMaxWidth(),danger=true)
    BioDetails("Ứng dụng & riêng tư"){Bt("Không cần tài khoản. Dữ liệu được lưu trên thiết bị.",size=12,tint=bp.muted);BioTextAction("Giấy phép nguồn mở",{ui.show("license")})}
    BioDetails("Chẩn đoán"){Bt("Xuất thông tin dung lượng và số lượng mục để hỗ trợ kiểm tra sự cố.",size=12,tint=bp.muted);BioButton("Xuất chẩn đoán",{ui.send("diagnostics")},Modifier.fillMaxWidth(),secondary=true)}
}
