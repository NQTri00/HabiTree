package com.mamxanh.app.presentation.bio

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import org.json.JSONObject
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*

internal fun bioDates(date:String,count:Int):List<String>{val format=SimpleDateFormat("yyyy-MM-dd",Locale.US);val c=Calendar.getInstance();try{c.time=format.parse(date)!!}catch(_:Exception){};c.add(Calendar.DATE,1-count);return (0 until count).map{format.format(c.time).also{c.add(Calendar.DATE,1)}}}
@Composable internal fun BioCalendar(world:BioWorld,count:Int=7,recordOverride:JSONArray?=null){
    val explicit=recordOverride?:world.data.optJSONArray("days")
    val dates=bioDates(world.data.s("date"),count)
    val values=dates.mapIndexed{i,date->val explicitRecord=explicit?.objects()?.firstOrNull{it.s("date")==date};val state=explicitRecord?.s("state")?:if(explicit!=null||world.native)"NONE"else if(i%7==3)"REST"else if(i%9==5)"PROTECTED"else if(i%3!=1)"SUCCESS"else "MISSED";date to state}
    Column(verticalArrangement=Arrangement.spacedBy(8.dp)){
        Bt(bioDisplayDate(dates.first())+" – "+bioDisplayDate(dates.last()),size=12,tint=bp.muted)
        Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){dates.take(7).forEach{date->val calendar=Calendar.getInstance().apply{time=SimpleDateFormat("yyyy-MM-dd",Locale.US).parse(date)!!};val day=calendar.get(Calendar.DAY_OF_WEEK);Bt(if(day==1)"CN"else "T$day",Modifier.weight(1f),size=11,tint=bp.muted,align=TextAlign.Center)}}
        values.chunked(7).forEach{row->Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){repeat(7){i->if(i<row.size){val(date,state)=row[i];val fill=when(state){"SUCCESS"->bp.deep;"REST"->Color(0xFFE7E1F5);"PROTECTED"->Color(0xFFF3E8C9);else->Color(0xFFF0F2EB)};Column(Modifier.weight(1f).heightIn(min=44.dp).background(fill,RoundedCornerShape(10.dp)).padding(vertical=4.dp),horizontalAlignment=Alignment.CenterHorizontally){Bt(date.takeLast(2),size=12,tint=if(state=="SUCCESS")Color.White else bp.ink,line=16f);Bt(when(state){"SUCCESS"->"✓";"REST"->"N";"PROTECTED"->"◇";"MISSED"->"×";else->"—"},size=10,tint=if(state=="SUCCESS")Color.White else bp.muted,line=14f)}}else Spacer(Modifier.weight(1f))}}}
        Bt("✓ Xong   N Nghỉ   ◇ Bảo vệ",size=11,tint=bp.muted)
        Bt("× Bỏ lỡ   — Chưa ghi nhận",size=11,tint=bp.muted)
    }
}
@Composable internal fun ColumnScope.BioReview(world:BioWorld,ui:BioController){
    val review=world.data.o("review")
    if(world.native&&review.length()==0){BioEmpty("Chưa có tổng kết","Số liệu sẽ xuất hiện khi có hoạt động.","calendar","Xem việc",{ui.go("tasks")});return}
    var note by rememberSaveable{mutableStateOf(review.s("reflection",world.state.s("reflection")))}
    Bt("TUẦN TỪ "+bioDisplayDate(review.s("start","2026-11-16")),size=11,mono=true,tint=bp.muted,spacing=1.2f);Bt("Tuần của bạn",size=27,bold=true,spacing=-1f)
    BioMetrics(listOf("Hoàn thành" to "${review.i("completed")}/${review.i("scheduled")}","Tỷ lệ" to if(review.i("scheduled")>0)"${review.i("completed")*100/review.i("scheduled")}%"else "—","Tập trung" to "${review.i("focusMinutes")}′"))
    BioCard(Modifier.fillMaxWidth()){Bt("7 ngày gần nhất",size=13,semi=true);Spacer(Modifier.height(8.dp));BioBars(world);Spacer(Modifier.height(14.dp));Bt(if(review.has("previousRate"))"Tuần trước ${review.i("previousRate")}% · Số liệu đến hôm nay."else "Chưa đủ dữ liệu so sánh.",size=12,tint=bp.muted)}
    BioDetails("Những điều đã làm"){BioFacts(listOf("Xu nhận / chi" to "${review.i("earned")} / ${review.i("spent")}","Chăm cây" to "${review.i("careDays")} ngày","Mốc trưởng thành" to review.i("milestones").toString(),"Đều đặn nhất" to review.s("consistent","Chưa đủ dữ liệu"),"Có thể điều chỉnh" to review.s("improve","Chưa đủ dữ liệu")))}
    BioField("Một điều muốn nhớ",note,{note=it.take(2000)},multiline=true,placeholder="Tuần này, mình…")
    BioButton("Lưu suy ngẫm",{ui.send("reflection",mapOf("note" to note,"week" to review.s("start")))},Modifier.fillMaxWidth())
    BioLink("Lịch nghỉ & bảo vệ","moon",{ui.go("rest")},"${world.state.i("tickets")} vé bảo vệ")
}
@Composable internal fun ColumnScope.BioRest(world:BioWorld,ui:BioController){
    var restDate by rememberSaveable{mutableStateOf(world.data.s("tomorrow","2026-11-18"))};var protectDate by rememberSaveable{mutableStateOf("")}
    Bt("Nghỉ cũng là một nhịp.",size=27,bold=true,spacing=-1f);BioCard(Modifier.fillMaxWidth()){BioCalendar(world,30)}
    BioDateField("Ngày nghỉ sắp tới",restDate,{restDate=it},futureOnly=true);Bt("Tối đa 2 ngày/tháng. Đặt trước ngày nghỉ; không thưởng xu.",size=12,tint=bp.muted)
    BioButton("Lên lịch nghỉ",{ui.send("rest",mapOf("date" to restDate))},Modifier.fillMaxWidth())
    val rest=world.state.a("rest").strings();if(rest.isNotEmpty())BioCard(Modifier.fillMaxWidth()){BioFacts(rest.map{"Nghỉ" to it})}
    BioSection("Vé bảo vệ");Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.SpaceBetween){BioTag("${world.state.i("tickets")} vé","award");BioButton("Mua · 250 xu",{ui.show("ticket")},secondary=true,enabled=world.coins>=250)}
    BioDateField("Ngày đã bỏ lỡ",protectDate,{protectDate=it});BioButton("Dùng vé",{ui.send("protect",mapOf("date" to protectDate))},Modifier.fillMaxWidth(),enabled=world.state.i("tickets")>0&&protectDate.isNotBlank())
}
@Composable internal fun ColumnScope.BioInsights(world:BioWorld,ui:BioController){
    val task=world.task(ui.param)
    if(task==null){Bt("Nhịp thói quen",size=27,bold=true,spacing=-1f);if(world.tasks.isEmpty())BioEmpty("Chưa có nhịp thói quen","Thêm việc để bắt đầu.","chart","Thêm việc",{ui.go("task-new")})else Column(verticalArrangement=Arrangement.spacedBy(12.dp)){world.tasks.forEach{BioLink(it.s("title"),"chart",{ui.go("insights",it.s("id"))})}};return}
    val supplied=world.data.a("insights").objects().firstOrNull{it.s("taskId")==task.s("id")};val insight=supplied?:json("longest" to 7,"rate" to 72,"focusMinutes" to 125,"commonHour" to "19:00","suggestion" to "Điều chỉnh sang ngày thường")
    Bt(task.s("title"),size=27,bold=true,spacing=-1f);val period=ui.tabs["insightDays"]?:"7";BioTabs(listOf("7" to "7 ngày","30" to "30 ngày"),period){ui.tabs["insightDays"]=it}
    BioCard(Modifier.fillMaxWidth()){BioCalendar(world,period.toInt(),insight.optJSONArray("days"))}
    BioMetrics(listOf("Chuỗi hiện tại" to task.i("streak").toString(),"Dài nhất" to insight.i("longest").toString(),"Tỷ lệ 30 ngày" to if(insight.has("rate"))"${insight.i("rate")}%"else "—"))
    BioDetails("Tập trung & gợi ý"){BioFacts(listOf("Tập trung" to "${insight.i("focusMinutes")} phút","Giờ thường xong" to insight.s("commonHour","Chưa đủ dữ liệu")));if(insight.s("suggestion").isNotBlank())BioButton(insight.s("suggestion"),{ui.show("adjust",task.s("id"))},secondary=true)}
    val reminder=world.state.o("reminders").o(task.s("id"));var minute by rememberSaveable(task.s("id")){mutableStateOf(reminder.s("minute","09:00"))};var mode by rememberSaveable(task.s("id")){mutableStateOf(reminder.s("mode","OFF"))};var follow by rememberSaveable(task.s("id")){mutableStateOf(reminder.b("followUp"))}
    Bt("Nhắc việc",size=17,bold=true);BioTimeField("Giờ nhắc",minute,{minute=it});BioSelect("Khi nào",listOf("OFF" to "Tắt","AT" to "Theo giờ","BEFORE" to "Trước khung giờ"),mode,onSelect={mode=it});BioToggle("Nhắc nhẹ thêm một lần",follow){follow=it};BioButton("Lưu nhắc việc",{ui.send("reminder",mapOf("id" to task.s("id"),"minute" to minute,"mode" to mode,"followUp" to follow.toString()))},Modifier.fillMaxWidth());BioLink("Quyền & nhắc toàn cục","bell",{ui.go("settings")})
}
@Composable internal fun ColumnScope.BioAchievements(world:BioWorld,ui:BioController){
    Bt("Từng dấu mốc nhỏ",size=27,bold=true,spacing=-1f);Bt("Ghi nhận hành trình · Không thưởng xu",size=12,tint=bp.muted)
    val defaults=listOf(Triple("Bước đầu tiên","Hoàn thành một việc hợp lệ",1 to 1),Triple("Ba ngày tốt lành","3 ngày có lịch hoàn thành",3 to 3),Triple("Một tuần đều đặn","7 ngày có lịch hoàn thành",5 to 7),Triple("Khởi đầu tập trung","Hoàn thành một phiên ít nhất 25 phút",0 to 1),Triple("Nhịp học tập","10 lần hoàn thành việc học",6 to 10),Triple("Bàn tay xanh","Tưới cây trong 10 ngày khác nhau",3 to 10),Triple("Bông hoa đầu tiên","Một loài hoa đạt giai đoạn cuối",0 to 1),Triple("Cây trưởng thành đầu tiên","Một loài không hoa đạt giai đoạn cuối",1 to 1),Triple("Góc vườn đa dạng","Đang sở hữu 3 loài khác nhau",3 to 3),Triple("Tháng đều đặn","20 ngày có lịch hoàn thành",5 to 20))
    val supplied=world.data.a("achievements").objects();val entries=if(world.native)supplied else defaults.map{(t,c,v)->json("title" to t,"criterion" to c,"progress" to v.first,"target" to v.second)}
    Column(verticalArrangement=Arrangement.spacedBy(12.dp)){entries.forEach{item->val n=item.i("progress");val max=item.i("target",1);val unlocked=item.l("unlockedAt")>0||n>=max
        BioCard(Modifier.fillMaxWidth(),padding=18){Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)){Box(Modifier.size(48.dp).background(if(unlocked)Color(0xFFF3E8C9)else Color(0xFFF0F0F0),RoundedCornerShape(50)),contentAlignment=Alignment.Center){Bi(if(unlocked)"award"else "lock",size=22,tint=if(unlocked)Color(0xFF947A30)else bp.muted)};Column(Modifier.weight(1f)){Bt(item.s("title"),size=14,semi=true);Bt(item.s("criterion"),size=10,tint=bp.muted);Spacer(Modifier.height(6.dp));Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(10.dp)){BioProgress(n.toFloat()/max,Modifier.weight(1f));Bt("${minOf(n,max)}/$max"+if(unlocked)" · Đã ghi nhận"else "",size=11,tint=bp.muted)}}}}
    }}
}
@Composable internal fun ColumnScope.BioTimeline(world:BioWorld,ui:BioController){
    Bt("Điều mình muốn nhớ",size=27,bold=true,spacing=-1f)
    val memories=world.state.a("memories").objects()
    val timelineLine=bp.line;val timelineDot=bp.deep
    if(memories.isEmpty())BioEmpty("Chưa có kỷ niệm","Những việc bạn làm sẽ xuất hiện ở đây.","book")else Column(Modifier.fillMaxWidth().padding(start=8.dp).drawBehind{drawLine(timelineLine,Offset.Zero,Offset(0f,size.height),1.dp.toPx())}.padding(start=20.dp),verticalArrangement=Arrangement.spacedBy(28.dp)){
        memories.forEach{m->var note by rememberSaveable(m.s("id")){mutableStateOf(world.state.o("notes").s(m.s("id"),m.s("note")))}
            Column(Modifier.drawBehind{drawCircle(timelineDot,4.5.dp.toPx(),Offset(-20.dp.toPx(),8.dp.toPx()))},verticalArrangement=Arrangement.spacedBy(0.dp)){Bt(m.s("date"),size=10,tint=bp.muted);Spacer(Modifier.height(4.dp));Bt(m.s("title"),size=12,semi=true);if(m.l("entity")>0)BioTextAction("Xem liên quan",{if(m.s("type").startsWith("TREE"))ui.go("tree",m.s("entity"))else ui.go("insights",m.s("entity"))},"next");BioField("",note,{note=it.take(1000)},multiline=true,placeholder="Một điều muốn nhớ…");Spacer(Modifier.height(8.dp));BioTextAction("Lưu ghi chú",{ui.send("memory-note",mapOf("id" to m.s("id"),"note" to note))},modifier=Modifier.fillMaxWidth())}
        }
    }
}
@Composable internal fun ColumnScope.BioEncyclopedia(world:BioWorld,ui:BioController){
    Bt("Mỗi loài, một câu chuyện.",size=27,bold=true,spacing=-1f)
    val n=world.records.count{it.b("discovered")};BioCard(Modifier.fillMaxWidth(),bp.soft,padding=18){Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Bt("Bộ sưu tầm",semi=true);Bt("$n/${world.species.size} · ${n*100/world.species.size}%",semi=true)};BioProgress(n.toFloat()/world.species.size);Bt("Giữ lịch sử khi dọn hoặc bán cây.",size=12,tint=bp.muted)}
    BioGrid(world.species.size,2){i,m->BioProduct(world,ui,world.species[i],m,encyclopedia=true)}
    BioSection("Dấu mốc sưu tầm")
    Column(verticalArrangement=Arrangement.spacedBy(12.dp)){
        world.data.a("goals").objects().filter{it.s("id") !in listOf("expansion","section")}.forEach{g->
            BioCard(Modifier.fillMaxWidth()){
                Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){
                    Bt(g.s("title"),Modifier.weight(1f),size=14,bold=true)
                    Bt("${g.i("n")}/${g.i("target",1)}",size=12)
                    if(g.i("n")>=g.i("target",1)){Spacer(Modifier.width(18.dp));Bi("check")}
                }
                Bt(when(g.s("reward")){"reward_sign"->"Biển nhà vườn";"reward_petals"->"Cánh hoa";else->"Huy hiệu bộ sưu tầm"},size=12,tint=bp.muted)
                Spacer(Modifier.height(8.dp));BioProgress(g.i("n").toFloat()/g.i("target",1))
            }
        }
    }
}
@Composable internal fun ColumnScope.BioSpecies(world:BioWorld,ui:BioController){
    val s=world.species(ui.param);val id=s.s("id");val record=world.record(id);val known=record.b("discovered");val highest=record.i("highest");val trees=world.trees.filter{it.s("species")==id}
    BioCard(Modifier.fillMaxWidth(),bp.soft,padding=18,radius=26){Column(Modifier.fillMaxWidth(),horizontalAlignment=Alignment.CenterHorizontally){BioTag(bioTier(s.s("tier"))+" · "+record.s("status","Chưa khám phá"));if(known){Spacer(Modifier.height(2.dp));BioPlant(id,Modifier.fillMaxWidth().height(220.dp))}else Box(Modifier.fillMaxWidth().height(140.dp),contentAlignment=Alignment.Center){Bi("leaf",size=36)};Bt(s.s("name"),size=27,bold=true,spacing=-1f,align=TextAlign.Center);Bt(s.s("description"),size=12,tint=bp.muted,align=TextAlign.Center)}}
    BioSection("Các giai đoạn · $highest/${s.i("stages")}")
    BioGrid(s.i("stages"),if(LocalDensity.current.fontScale>1.2f)2 else 3){i,m->val reached=i<highest;Column(m.background(if(reached)bp.soft else bp.surface,RoundedCornerShape(12.dp)).padding(10.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(5.dp)){Bt("${i+1}",size=22,mono=true,tint=if(reached)bp.deep else bp.muted);Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(4.dp)){Bi(if(reached)"check"else "lock",size=12,tint=bp.muted);Bt(if(reached)"Đã đạt"else "Chưa đạt",size=11,tint=bp.muted)}}}
    BioCard(Modifier.fillMaxWidth()){BioFacts(listOf("Đã trồng" to record.i("planted").toString(),"Trong túi" to world.count(id).toString(),"Đang sống" to trees.count{it.s("state")!="dead"}.toString(),"Trồng đầu tiên" to bioDisplayDate(record.s("firstPlanted","Chưa ghi nhận")),"Trưởng thành đầu" to bioDisplayDate(record.s("firstMatured","Chưa ghi nhận")),"Cây cao nhất đang sống" to if(trees.isEmpty())"Chưa có"else "${trees.maxOf{it.i("stage")}}/${s.i("stages")}"))}
    BioDetails("Chăm sóc loài"){Bt("Bình tưới: 8 / 12 / 18 / 24 giờ. Bón phân khi cây đang lớn và đã tưới; không cộng dồn.",size=12,tint=bp.muted)}
    trees.forEach{BioLink(it.s("name"),"garden",{ui.go("tree",it.s("id"))})}
    BioButton("Tìm hạt",{ui.tabs["shop"]="seeds";ui.go("shop")},Modifier.fillMaxWidth(),"shop")
}

