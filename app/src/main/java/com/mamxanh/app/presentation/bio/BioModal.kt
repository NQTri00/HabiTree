package com.mamxanh.app.presentation.bio

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*

@Composable internal fun BioModal(world:BioWorld,ui:BioController){
    val type=ui.modal;val id=ui.modalId;val item=world.product(id);val tree=world.tree(id);val task=world.task(id)
    val seed=world.species.any{it.s("id")==id};val permanent=item.b("permanent");val owned=if(type=="accessory")world.owned(id)>0 else permanent&&world.count(id)>0
    val total=if(owned)0 else item.i("price")*if(permanent)1 else ui.quantity
    val living=world.trees.filter{it.s("state")!="dead"}
    val target=living.firstOrNull{it.s("id")==ui.targetTree}?:living.firstOrNull{it.s("id")==ui.homeTree}?:living.firstOrNull()
    val full=world.emptyPlots().isEmpty()
    val title=when(type){"tree-picker"->"Cây trên trang chủ";"seed-picker"->"Gieo một mầm mới";"purchase"->if(permanent)"Dụng cụ chăm sóc"else "Thêm vào túi";"accessory"->item.s("name");"plant"->if(full)"Chưa có ô trống"else "Gieo một mầm xanh";"water-all"->"Tưới cả vườn";"unlock"->"Mở ô $id";"fertilizer-help"->"Dùng phân bón";"remove"->"Dọn cây";"sell"->"Bán cây";"archive"->"Lưu trữ việc?";"discard"->"Bỏ thay đổi?";"ticket"->"Mua vé bảo vệ?";"adjust"->"Điều chỉnh lịch?";"permission"->"Nhắc việc tùy chọn";"permission-choice"->"Cho phép thông báo?";"permission-settings"->"Cài đặt thông báo";"clear"->"Xóa dữ liệu?";"clear-final"->"Xác nhận lần cuối";"recover"->"Khôi phục bản an toàn?";"confirm-import"->"Khôi phục dữ liệu?";"license"->"Giấy phép nguồn mở";"focus-end"->"Kết thúc phiên?";else->"Xác nhận"}
    var plantName by rememberSaveable(type,id){mutableStateOf("")};var selectedPlot by rememberSaveable(type,id){mutableStateOf(ui.plot.ifBlank{world.emptyPlots().firstOrNull()?.toString().orEmpty()})}
    val dismiss={if(!ui.busy){ui.modal="";ui.modalMessage=""}}
    val hasFooter=type !in listOf("tree-picker","seed-picker","accessory","plant","permission-choice","permission-settings","license")||type=="plant"&&full
    BoxWithConstraints(Modifier.fillMaxSize()){
        Box(Modifier.fillMaxSize().background(Color(0x60252A2E)).clickable(onClick=dismiss))
        val limit=maxHeight-32.dp
        Column(Modifier.align(Alignment.Center).widthIn(max=432.dp).fillMaxWidth().padding(horizontal=16.dp).heightIn(max=limit).shadow(12.dp,bioShape(28)).background(bp.bg,bioShape(28)).border(1.dp,Color.White.copy(alpha=.6f),bioShape(28)).padding(1.dp).clip(bioShape(28)).clickable(enabled=false){}){
            Row(Modifier.fillMaxWidth().padding(start=16.dp,end=8.dp,top=8.dp,bottom=4.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(8.dp)){Bt(title,Modifier.weight(1f),size=22,line=30.8f,bold=true,spacing=-.7f);BioIconButton("close",dismiss,enabled=!ui.busy,iconSize=22,tint=bp.muted)}
            if(ui.busy)LinearProgressIndicator(Modifier.fillMaxWidth(),color=bp.deep,trackColor=bp.soft)
            Column(Modifier.weight(1f,fill=false).fillMaxWidth().verticalScroll(rememberScrollState(if(ui.tabs["sheetBottom"]=="true")Int.MAX_VALUE else 0)).padding(start=16.dp,end=16.dp,top=12.dp,bottom=18.dp),verticalArrangement=Arrangement.spacedBy(18.dp)){
                if(ui.modalMessage.isNotBlank())BioCard(Modifier.fillMaxWidth(),bp.soft,padding=12,radius=14){Bt(ui.modalMessage,size=12)}
                when(type){
                    "tree-picker"->{Bt("Chọn cây. Sắc màu sẽ đi cùng bạn.",size=12,tint=bp.muted);Column(verticalArrangement=Arrangement.spacedBy(9.dp)){world.trees.forEach{t->BioChoice(t.s("name"),world.data.o("palettes").o(t.s("species")).s("name"),t.s("species"),ui.homeTree==t.s("id"),{ui.act("bio-pin:"+t.s("id"))})}};if(world.trees.isEmpty())BioButton("Gieo cây đầu tiên",{ui.show("seed-picker")},Modifier.fillMaxWidth(),"plus")}
                    "seed-picker"->{Bt("Hạt đang có · chọn để gieo.",size=12,tint=bp.muted);Column(verticalArrangement=Arrangement.spacedBy(9.dp)){world.species.filter{world.count(it.s("id"))>0}.forEach{s->BioChoice(s.s("name"),"Còn ${world.count(s.s("id"))} hạt",s.s("id"),false,{ui.show("plant",s.s("id"))},"plus")}};if(world.species.none{world.count(it.s("id"))>0})Bt("Chưa có hạt trong túi.",size=12,tint=bp.muted);BioButton("Chọn hạt ở tiệm",{ui.tabs["shop"]="seeds";ui.go("shop")},Modifier.fillMaxWidth(),"shop",true)}
                    "purchase"->{
                        Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(16.dp)){
                            if(seed)BioPlant(id,Modifier.size(90.dp))else Box(Modifier.size(48.dp),contentAlignment=Alignment.Center){Bi(if(permanent)"water"else "fertilizer",size=32)}
                            Column(Modifier.weight(1f)){Bt(item.s("name"),size=17,bold=true);Bt("${item.i("price")} xu/món",size=12,tint=bp.muted);Bt("Trong túi: ${world.count(id)}",size=12,tint=bp.muted)}
                        }
                        Bt(item.s("description").ifBlank{if(permanent)"Tưới trong ${item.i("hours")} giờ. Dùng lâu dài."else "${item.s("rate")}× trong ${item.i("hours")} giờ. Dùng khi cây đã tưới, một lần mỗi 24 giờ."},size=12,tint=bp.muted)
                        if(seed)BioTag(bioTier(item.s("tier"))+" · ${item.i("stages")} giai đoạn",modifier=Modifier.fillMaxWidth())
                        if(!permanent)Row(Modifier.fillMaxWidth().heightIn(min=52.dp),horizontalArrangement=Arrangement.Center,verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(48.dp).alpha(if(ui.quantity>1&&!ui.busy)1f else .45f).background(bp.surface,bioShape(12)).border(1.dp,bp.line,bioShape(12)).clickable(enabled=ui.quantity>1&&!ui.busy){ui.quantity=(ui.quantity-1).coerceAtLeast(1)},contentAlignment=Alignment.Center){Bt("\u2212",size=22,tint=bp.deep)};Spacer(Modifier.width(25.dp));BioField("",ui.quantity.toString(),{ui.quantity=(it.toIntOrNull()?:1).coerceIn(1,99)},Modifier.width(65.dp),number=true,centered=true);Spacer(Modifier.width(25.dp));BioIconButton("plus",{ui.quantity=(ui.quantity+1).coerceAtMost(99)},boxed=true,enabled=ui.quantity<99&&!ui.busy)}
                        BioMetrics(listOf("Tổng" to total.toString(),"Ví" to world.coins.toString(),(if(world.coins<total)"Cần thêm"else "Còn lại") to kotlin.math.abs(world.coins-total).toString()))
                        if(world.coins<total)Bt("Khoảng ${kotlin.math.ceil((total-world.coins)/40.0).toInt()} ngày · ước tính 40 xu/ngày",size=12,tint=bp.muted)
                        if(seed&&full)Bt("Vườn đã đầy. Bạn vẫn có thể mua hạt vào túi.",size=12,tint=bp.muted)
                    }
                    "accessory"->{
                        Box(Modifier.fillMaxWidth().height(135.dp).background(Brush.linearGradient(listOf(Color.White.copy(alpha=.6f),bp.soft)),bioShape(20)).border(1.dp,Color.White.copy(alpha=.6f),bioShape(20)),contentAlignment=Alignment.Center){Bi(accessoryIcon(id),size=56);BioTag(if(owned)"Đã sở hữu"else "${item.i("price")} xu",modifier=Modifier.align(Alignment.BottomEnd).padding(12.dp))}
                        if(living.isNotEmpty())BioSelect("Đeo cho cây",living.map{it.s("id") to it.s("name")},target?.s("id").orEmpty(),onSelect={ui.targetTree=it})else Bt("Gieo một cây trước khi đeo phụ kiện.",size=12,tint=bp.muted)
                        val other=world.trees.firstOrNull{it.s("decor")==id&&it.s("id")!=target?.s("id")}
                        if(other!=null&&owned)Bt("Chuyển từ ${other.s("name")}. Không mất thêm xu.",size=12,tint=bp.muted)
                        Bt("Chỉ trang trí. Không thay đổi tốc độ lớn.",size=12,tint=bp.muted)
                        if(!owned&&world.coins<item.i("price"))BioError("Cần thêm ${item.i("price")-world.coins} xu.")
                        val using=target?.s("decor")==id
                        Column(verticalArrangement=Arrangement.spacedBy(10.dp)){
                            BioButton(if(using)"Đang dùng"else if(owned)"Đeo cho cây"else "Mua & đeo",{ui.send("accessory-use",mapOf("id" to id,"tree" to target?.s("id").orEmpty()))},Modifier.fillMaxWidth(),enabled=target!=null&&!using&&(owned||world.coins>=item.i("price"))&&!ui.busy)
                            if(!owned)BioButton("Chỉ mua vào túi",{ui.send("accessory-buy",mapOf("id" to id))},Modifier.fillMaxWidth(),secondary=true,enabled=world.coins>=item.i("price")&&!ui.busy)
                        }
                    }
                    "plant"->{if(full)Bt("Hạt vẫn ở trong túi. Mở thêm ô để gieo.",size=12,tint=bp.muted)else{
                        Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(16.dp)){BioPlant(id,Modifier.size(85.dp));Column{Bt(item.s("name"),size=17,bold=true);Bt("Trong túi: ${world.count(id)} hạt",size=12,tint=bp.muted)}}
                        BioField("Tên cây · tùy chọn",plantName,{plantName=it.take(100)},placeholder="Mầm bình yên")
                        BioSelect("Ô trồng",world.emptyPlots().map{it.toString() to "Ô "+it.toString().padStart(2,'0')},selectedPlot,onSelect={selectedPlot=it})
                        Bt("Dùng 1 hạt. Để trống tên để dùng tên loài.",size=12,tint=bp.muted)
                        BioButton("Gieo hạt",{ui.send("plant",mapOf("id" to id,"plot" to selectedPlot,"name" to plantName))},Modifier.fillMaxWidth(),enabled=world.count(id)>0&&!ui.busy)
                    }}
                    "water-all"->{BioTag("Miễn phí",modifier=Modifier.fillMaxWidth());BioFacts(listOf("Cần tưới" to world.trees.count{!it.b("watered")&&it.s("state")!="dead"}.toString(),"Đã tưới" to world.trees.count{it.b("watered")}.toString(),"Khô héo" to world.trees.count{it.s("state")=="dead"}.toString()));Bt("Chỉ tưới cây sống cần nước. Thời gian không cộng dồn.",size=12,tint=bp.muted)}
                    "unlock"->{val cost=400+(world.state.i("unlocked",5)-5).coerceAtLeast(0)*200;BioFacts(listOf("Giá mở ô" to "$cost xu","Ví hiện tại" to "${world.coins} xu"));if(world.coins<cost)Bt("Cần thêm ${cost-world.coins} xu.",size=12,tint=bp.muted)}
                    "fertilizer-help"->Bt("Bón phân tại cây đang lớn đã được tưới.",size=12,tint=bp.muted)
                    "remove","sell"->{Bt(tree?.s("name").orEmpty(),size=17,bold=true);Bt("Cây rời ô vĩnh viễn. Không hoàn lại hạt hoặc tiến trình.",size=12,tint=bp.muted);if(type=="sell"){val sale=world.species(tree?.s("species")?:"succulent").i("sale");BioFacts(listOf("Giá bán" to "$sale xu","Nhận hôm nay" to "${minOf(sale,(50-world.state.i("earned")).coerceAtLeast(0))} xu"));Bt("Phần vượt 50 xu/ngày sẽ mất, không để dành.",size=12,tint=Color(0xFFA63936))}else BioTag("Không nhận xu hoặc hạt",modifier=Modifier.fillMaxWidth())}
                    "archive"->Bt("Lưu trữ việc; giữ nguyên lịch sử.",size=12,tint=bp.muted)
                    "discard"->Bt("Nội dung chưa lưu sẽ mất.",size=12,tint=bp.muted)
                    "ticket"->Bt("250 xu · bảo vệ một ngày bỏ lỡ.",size=12,tint=bp.muted)
                    "adjust"->Bt("Chuyển sang ngày thường từ ngày mai. Giữ lịch sử và kế hoạch hôm nay.",size=12,tint=bp.muted)
                    "permission"->Bt("Chỉ nhắc những việc đã chọn, tôn trọng giờ yên lặng. Từ chối vẫn dùng được mọi chức năng.",size=12,tint=bp.muted)
                    "permission-choice","permission-settings"->{BioButton("Cho phép",{ui.send("permission-allow")},Modifier.fillMaxWidth());BioButton(if(type=="permission-choice")"Để sau"else "Tắt thông báo",{if(type=="permission-choice")dismiss()else ui.send("permission-settings")},Modifier.fillMaxWidth(),secondary=true)}
                    "clear"->Bt("Bạn có thể xuất bản sao trước khi tiếp tục.",size=12,tint=bp.muted)
                    "clear-final"->Bt("Xóa dữ liệu và bắt đầu lại?",size=12,tint=bp.muted)
                    "recover"->Bt("Thay dữ liệu hiện tại trên thiết bị, không gộp. Xuất bản sao hiện tại nếu muốn giữ cả hai.",size=12,tint=bp.muted)
                    "confirm-import"->{BioFacts(listOf("Việc" to (ui.fields["backupTasks"]?:"0"),"Cây" to (ui.fields["backupTrees"]?:"0"),"Lượt hoàn thành" to (ui.fields["backupOccurrences"]?:"0"),"Tạo lúc" to (ui.fields["backupCreated"]?.takeIf{it.isNotBlank()}?:"Không rõ")));Bt("Không gộp. Một bản an toàn được lưu trước khi thay. Cây quay về trạng thái tại thời điểm bản sao.",size=12,tint=bp.muted)}
                    "license"->{val context=LocalContext.current;val license=remember{context.resources.openRawResource(com.mamxanh.app.R.raw.apache_license).bufferedReader().use{it.readText()}.replace("\r\n","\n").replace(Regex("(?m)^[ \\t]+"),"").replace(Regex("(?<!\\n)\\n(?!\\n)")," ").replace(Regex("[ \\t]{2,}")," ").trim()};Bt(license,size=13,line=21.45f)}
                    "focus-end"->Bt("Phần đã làm sẽ được lưu.",size=12,tint=bp.muted)
                }
            }
            if(hasFooter){HorizontalDivider(color=bp.line);Row(Modifier.fillMaxWidth().padding(start=16.dp,end=16.dp,top=12.dp,bottom=16.dp),horizontalArrangement=Arrangement.spacedBy(10.dp)){
                if(type=="purchase"&&seed){
                    BioButton("Mua vào túi",{ui.send("buy",mapOf("id" to id,"quantity" to ui.quantity.toString()))},Modifier.weight(1f),secondary=true,enabled=world.coins>=total&&!ui.busy)
                    BioButton(if(ui.quantity>1)"Mua & gieo 1"else "Mua & gieo",{ui.send("buy-plant",mapOf("id" to id,"quantity" to ui.quantity.toString(),"plot" to ui.plot))},Modifier.weight(1f),enabled=!full&&world.coins>=total&&!ui.busy)
                }else{
                    BioButton(if(type=="purchase"&&permanent&&!owned)"Để sau"else "Hủy",dismiss,Modifier.weight(1f),secondary=true,enabled=!ui.busy)
                    val label=when(type){"purchase"->if(permanent)if(owned)if(world.state.s("equipped")==id)"Đang dùng"else "Trang bị"else "Mua & trang bị"else "Mua";"plant"->"Xem ô khóa";"water-all"->"Tưới cây";"unlock"->"Mở ô";"fertilizer-help"->"Chọn cây";"remove"->"Dọn cây";"sell"->"Bán cây";"archive"->"Lưu trữ";"discard"->"Bỏ thay đổi";"ticket"->"Mua vé";"adjust"->"Áp dụng";"permission","clear"->"Tiếp tục";"clear-final"->"Xóa dữ liệu";"recover"->"Khôi phục";"confirm-import"->"Thay thế";"focus-end"->"Kết thúc";else->"Xác nhận"}
                    val enabled=!ui.busy&&when(type){"purchase"->world.coins>=total&&!(owned&&world.state.s("equipped")==id);"unlock"->world.coins>=400+(world.state.i("unlocked",5)-5).coerceAtLeast(0)*200;"ticket"->world.coins>=250;else->true}
                    BioButton(label,{
                        when(type){
                            "purchase"->ui.send(if(owned)"equip:$id"else if(permanent)"buy-equip"else "buy",mapOf("id" to id,"quantity" to ui.quantity.toString()))
                            "plant","fertilizer-help"->ui.go("garden")
                            "discard"->ui.discard()
                            "permission"->if(world.native)ui.send("permission-allow")else ui.show("permission-choice")
                            "clear"->ui.show("clear-final")
                            "clear-final"->ui.send("clear-data")
                            "recover"->ui.send("recover-data")
                            "confirm-import"->ui.send("confirm-import")
                            else->ui.send("confirm-$type",mapOf("id" to id))
                        }
                    },Modifier.weight(1f),enabled=enabled,danger=type in listOf("remove","sell","clear-final"))
                }
            }}
        }
    }
}
@Composable internal fun BioChoice(title:String,subtitle:String,species:String,selected:Boolean,onClick:()->Unit,icon:String="next"){
    Row(Modifier.fillMaxWidth().heightIn(min=76.dp).background(if(selected)bp.soft else bp.surface,bioShape(16)).border(1.dp,if(selected)bp.deep else bp.line,bioShape(16)).clickable(onClick=onClick).padding(12.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)){
        BioPlant(species,Modifier.size(50.dp));Column(Modifier.weight(1f)){Bt(title,size=13,semi=true);Spacer(Modifier.height(3.dp));Bt(subtitle,size=11,tint=bp.muted)};Bi(if(selected)"check"else icon,size=18)
    }
}
