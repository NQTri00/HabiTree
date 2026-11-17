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
    val title=when(type){"tree-picker"->AppText.text(TextKey.CopyCayTrenTrangChu76c8b5);"seed-picker"->AppText.text(TextKey.CopyGieoMotMamMoi2a80ed);"purchase"->if(permanent)AppText.text(TextKey.CopyDungCuChamSoc9f1ca9)else AppText.text(TextKey.CopyThemVaoTuiA0e7dc);"accessory"->item.s("name");"plant"->if(full)AppText.text(TextKey.CopyChuaCoOTrong6abd67)else AppText.text(TextKey.CopyGieoMotMamXanh776075);"water-all"->AppText.text(TextKey.CopyTuoiCaVuon20a14a);"unlock"->AppText.text(TextKey.CopyMoO001668e,id);"fertilizer-help"->AppText.text(TextKey.CopyDungPhanBon79fd5f);"remove"->AppText.text(TextKey.CopyDonCayE3d410);"sell"->AppText.text(TextKey.CopyBanCay5210c7);"archive"->AppText.text(TextKey.CopyLuuTruViec50123f);"discard"->AppText.text(TextKey.CopyBoThayDoiB07820);"ticket"->AppText.text(TextKey.CopyMuaVeBaoVeAcdfc5);"adjust"->AppText.text(TextKey.CopyDieuChinhLichBdb141);"permission"->AppText.text(TextKey.CopyNhacViecTuyChon907252);"permission-choice"->AppText.text(TextKey.CopyChoPhepThongBao6c0308);"permission-settings"->AppText.text(TextKey.CopyCaiDatThongBao765600);"clear"->AppText.text(TextKey.CopyXoaDuLieu8ce180);"clear-final"->AppText.text(TextKey.CopyXacNhanLanCuoi8421a1);"recover"->AppText.text(TextKey.CopyKhoiPhucBanAnToan9361dd);"confirm-import"->AppText.text(TextKey.CopyKhoiPhucDuLieuFeaa4a);"license"->AppText.text(TextKey.CopyGiayPhepNguonMo2ecf92);"focus-end"->AppText.text(TextKey.CopyKetThucPhien7ea116);else->AppText.text(TextKey.CopyXacNhan1503b0)}
    var plantName by rememberSaveable(type,id){mutableStateOf("")};var selectedPlot by rememberSaveable(type,id){mutableStateOf(ui.plot.ifBlank{world.emptyPlots().firstOrNull()?.toString().orEmpty()})}
    val dismiss={if(!ui.busy){ui.modal="";ui.modalMessage=""}}
    val hasFooter=type !in listOf("tree-picker","seed-picker","accessory","plant","permission-choice","permission-settings","license")||type=="plant"&&full
    BoxWithConstraints(Modifier.fillMaxSize()){
        Box(Modifier.fillMaxSize().background(AppColors.Swatch60252A2E).clickable(onClick=dismiss))
        val limit=maxHeight-AppDimensions.Dp32
        Column(Modifier.align(Alignment.Center).widthIn(max=AppDimensions.Dp432).fillMaxWidth().padding(horizontal=AppDimensions.Dp16).heightIn(max=limit).shadow(AppDimensions.Dp12,bioShape(AppRadii.Value28)).background(bp.bg,bioShape(AppRadii.Value28)).border(AppDimensions.Dp1,AppColors.White.copy(alpha=AppOpacity.Value_6),bioShape(AppRadii.Value28)).padding(AppDimensions.Dp1).clip(bioShape(AppRadii.Value28)).clickable(enabled=false){}){
            Row(Modifier.fillMaxWidth().padding(start=AppDimensions.Dp16,end=AppDimensions.Dp8,top=AppDimensions.Dp8,bottom=AppDimensions.Dp4),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp8)){Bt(title,Modifier.weight(1f),size=AppTypography.Size22,line=AppTypography.LineHeight30_8,bold=true,spacing=AppTypography.TrackingMinus_7);BioIconButton("close",dismiss,enabled=!ui.busy,iconSize=AppIcons.Size22,tint=bp.muted)}
            if(ui.busy)LinearProgressIndicator(Modifier.fillMaxWidth(),color=bp.deep,trackColor=bp.soft)
            Column(Modifier.weight(1f,fill=false).fillMaxWidth().verticalScroll(rememberScrollState(if(ui.tabs["sheetBottom"]=="true")Int.MAX_VALUE else 0)).padding(start=AppDimensions.Dp16,end=AppDimensions.Dp16,top=AppDimensions.Dp12,bottom=AppDimensions.Dp18),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp18)){
                if(ui.modalMessage.isNotBlank())BioCard(Modifier.fillMaxWidth(),bp.soft,padding=AppSpacing.Value12,radius=AppRadii.Value14){Bt(ui.modalMessage,size=AppTypography.Size12)}
                when(type){
                    "tree-picker"->{Bt(AppText.text(TextKey.CopyChonCaySacMauSeDiCungBan58b284),size=AppTypography.Size12,tint=bp.muted);Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp9)){world.trees.forEach{t->BioChoice(t.s("name"),world.data.o("palettes").o(t.s("species")).s("name"),t.s("species"),ui.homeTree==t.s("id"),{ui.act("bio-pin:"+t.s("id"))})}};if(world.trees.isEmpty())BioButton(AppText.text(TextKey.CopyGieoCayDauTienAfd3c1),{ui.show("seed-picker")},Modifier.fillMaxWidth(),"plus")}
                    "seed-picker"->{Bt(AppText.text(TextKey.CopyHatDangCoChonDeGieo237689),size=AppTypography.Size12,tint=bp.muted);Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp9)){world.species.filter{world.count(it.s("id"))>0}.forEach{s->BioChoice(s.s("name"),AppText.text(TextKey.CopyCon0Hat149c5e,world.count(s.s("id"))),s.s("id"),false,{ui.show("plant",s.s("id"))},"plus")}};if(world.species.none{world.count(it.s("id"))>0})Bt(AppText.text(TextKey.CopyChuaCoHatTrongTui095661),size=AppTypography.Size12,tint=bp.muted);BioButton(AppText.text(TextKey.CopyChonHatOTiemA4c334),{ui.tabs["shop"]="seeds";ui.go("shop")},Modifier.fillMaxWidth(),"shop",true)}
                    "purchase"->{
                        Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp16)){
                            if(seed)BioPlant(id,Modifier.size(AppDimensions.Dp90))else Box(Modifier.size(AppDimensions.Dp48),contentAlignment=Alignment.Center){Bi(if(permanent)"water"else "fertilizer",size=AppIcons.Size32)}
                            Column(Modifier.weight(1f)){Bt(item.s("name"),size=AppTypography.Size17,bold=true);Bt(AppText.text(TextKey.Copy0XuMon3dd1cb,item.i("price")),size=AppTypography.Size12,tint=bp.muted);Bt(AppText.text(TextKey.CopyTrongTui079a061,world.count(id)),size=AppTypography.Size12,tint=bp.muted)}
                        }
                        Bt(item.s("description").ifBlank{if(permanent)AppText.text(TextKey.CopyTuoiTrong0GioDungLauDai7c36d5,item.i("hours"))else AppText.text(TextKey.Copy0Trong1GioDungKhiCayDaTuoiMotLanMoi24GiAcf259,item.s("rate"),item.i("hours"))},size=AppTypography.Size12,tint=bp.muted)
                        if(seed)BioTag(bioTier(item.s("tier"))+AppText.text(TextKey.Copy0GiaiDoan26f2b4,item.i("stages")),modifier=Modifier.fillMaxWidth())
                        if(!permanent)Row(Modifier.fillMaxWidth().heightIn(min=AppDimensions.Dp52),horizontalArrangement=Arrangement.Center,verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(AppDimensions.Dp48).alpha(if(ui.quantity>1&&!ui.busy)AppOpacity.Value1 else AppOpacity.Value_45).background(bp.surface,bioShape(AppRadii.Value12)).border(AppDimensions.Dp1,bp.line,bioShape(AppRadii.Value12)).clickable(enabled=ui.quantity>1&&!ui.busy){ui.quantity=(ui.quantity-1).coerceAtLeast(1)},contentAlignment=Alignment.Center){Bt("\u2212",size=AppTypography.Size22,tint=bp.deep)};Spacer(Modifier.width(AppDimensions.Dp25));BioField("",ui.quantity.toString(),{ui.quantity=(it.toIntOrNull()?:1).coerceIn(1,99)},Modifier.width(AppDimensions.Dp65),number=true,centered=true);Spacer(Modifier.width(AppDimensions.Dp25));BioIconButton("plus",{ui.quantity=(ui.quantity+1).coerceAtMost(99)},boxed=true,enabled=ui.quantity<99&&!ui.busy)}
                        BioMetrics(listOf(AppText.text(TextKey.CopyTong13fdba) to total.toString(),AppText.text(TextKey.CopyVi39a29f) to world.coins.toString(),(if(world.coins<total)AppText.text(TextKey.CopyCanThemE011ae)else AppText.text(TextKey.CopyConLaiB3edb1)) to kotlin.math.abs(world.coins-total).toString()))
                        if(world.coins<total)Bt(AppText.text(TextKey.CopyKhoang0NgayUocTinh40XuNgay8be6c6,kotlin.math.ceil((total-world.coins)/40.0).toInt()),size=AppTypography.Size12,tint=bp.muted)
                        if(seed&&full)Bt(AppText.text(TextKey.CopyVuonDaDayBanVanCoTheMuaHatVaoTuiE14bdf),size=AppTypography.Size12,tint=bp.muted)
                    }
                    "accessory"->{
                        Box(Modifier.fillMaxWidth().height(AppDimensions.Dp135).background(Brush.linearGradient(listOf(AppColors.White.copy(alpha=AppOpacity.Value_6),bp.soft)),bioShape(AppRadii.Value20)).border(AppDimensions.Dp1,AppColors.White.copy(alpha=AppOpacity.Value_6),bioShape(AppRadii.Value20)),contentAlignment=Alignment.Center){Bi(accessoryIcon(id),size=AppIcons.Size56);BioTag(if(owned)AppText.text(TextKey.CopyDaSoHuu822625)else AppText.text(TextKey.Copy0Xu251e68,item.i("price")),modifier=Modifier.align(Alignment.BottomEnd).padding(AppDimensions.Dp12))}
                        if(living.isNotEmpty())BioSelect(AppText.text(TextKey.CopyDeoChoCayAfc821),living.map{it.s("id") to it.s("name")},target?.s("id").orEmpty(),onSelect={ui.targetTree=it})else Bt(AppText.text(TextKey.CopyGieoMotCayTruocKhiDeoPhuKienA3a9b9),size=AppTypography.Size12,tint=bp.muted)
                        val other=world.trees.firstOrNull{it.s("decor")==id&&it.s("id")!=target?.s("id")}
                        if(other!=null&&owned)Bt(AppText.text(TextKey.CopyChuyenTu0KhongMatThemXuC8b60f,other.s("name")),size=AppTypography.Size12,tint=bp.muted)
                        Bt(AppText.text(TextKey.CopyChiTrangTriKhongThayDoiTocDoLonC1bae1),size=AppTypography.Size12,tint=bp.muted)
                        if(!owned&&world.coins<item.i("price"))BioError(AppText.text(TextKey.CopyCanThem0XuD7d41b,item.i("price")-world.coins))
                        val using=target?.s("decor")==id
                        Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp10)){
                            BioButton(if(using)AppText.text(TextKey.CopyDangDung275ca2)else if(owned)AppText.text(TextKey.CopyDeoChoCayAfc821)else AppText.text(TextKey.CopyMuaDeoD9c74d),{ui.send("accessory-use",mapOf("id" to id,"tree" to target?.s("id").orEmpty()))},Modifier.fillMaxWidth(),enabled=target!=null&&!using&&(owned||world.coins>=item.i("price"))&&!ui.busy)
                            if(!owned)BioButton(AppText.text(TextKey.CopyChiMuaVaoTuiCbc02d),{ui.send("accessory-buy",mapOf("id" to id))},Modifier.fillMaxWidth(),secondary=true,enabled=world.coins>=item.i("price")&&!ui.busy)
                        }
                    }
                    "plant"->{if(full)Bt(AppText.text(TextKey.CopyHatVanOTrongTuiMoThemODeGieo17fb08),size=AppTypography.Size12,tint=bp.muted)else{
                        Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp16)){BioPlant(id,Modifier.size(AppDimensions.Dp85));Column{Bt(item.s("name"),size=AppTypography.Size17,bold=true);Bt(AppText.text(TextKey.CopyTrongTui0HatD56e0e,world.count(id)),size=AppTypography.Size12,tint=bp.muted)}}
                        BioField(AppText.text(TextKey.CopyTenCayTuyChon1af251),plantName,{plantName=it.take(100)},placeholder=AppText.text(TextKey.CopyMamBinhYenDf7062))
                        BioSelect(AppText.text(TextKey.CopyOTrong5da6bd),world.emptyPlots().map{it.toString() to AppText.text(TextKey.CopyO5b7240)+it.toString().padStart(2,'0')},selectedPlot,onSelect={selectedPlot=it})
                        Bt(AppText.text(TextKey.CopyDung1HatDeTrongTenDeDungTenLoai61bbff),size=AppTypography.Size12,tint=bp.muted)
                        BioButton(AppText.text(TextKey.CopyGieoHat6d82a9),{ui.send("plant",mapOf("id" to id,"plot" to selectedPlot,"name" to plantName))},Modifier.fillMaxWidth(),enabled=world.count(id)>0&&!ui.busy)
                    }}
                    "water-all"->{BioTag(AppText.text(TextKey.CopyMienPhiDfc4f7),modifier=Modifier.fillMaxWidth());BioFacts(listOf(AppText.text(TextKey.CopyCanTuoiA0957f) to world.trees.count{!it.b("watered")&&it.s("state")!="dead"}.toString(),AppText.text(TextKey.CopyDaTuoiDe2042) to world.trees.count{it.b("watered")}.toString(),AppText.text(TextKey.CopyKhoHeoBf546a) to world.trees.count{it.s("state")=="dead"}.toString()));Bt(AppText.text(TextKey.CopyChiTuoiCaySongCanNuocThoiGianKhongCongDon6737f7),size=AppTypography.Size12,tint=bp.muted)}
                    "unlock"->{val cost=400+(world.state.i("unlocked",5)-5).coerceAtLeast(0)*200;BioFacts(listOf(AppText.text(TextKey.CopyGiaMoO708ec3) to AppText.text(TextKey.Copy0Xu251e68,cost),AppText.text(TextKey.CopyViHienTai127da5) to AppText.text(TextKey.Copy0Xu251e68,world.coins)));if(world.coins<cost)Bt(AppText.text(TextKey.CopyCanThem0XuD7d41b,cost-world.coins),size=AppTypography.Size12,tint=bp.muted)}
                    "fertilizer-help"->Bt(AppText.text(TextKey.CopyBonPhanTaiCayDangLonDaDuocTuoiEe5c0a),size=AppTypography.Size12,tint=bp.muted)
                    "remove","sell"->{Bt(tree?.s("name").orEmpty(),size=AppTypography.Size17,bold=true);Bt(AppText.text(TextKey.CopyCayRoiOVinhVienKhongHoanLaiHatHoacTienTriAf5127),size=AppTypography.Size12,tint=bp.muted);if(type=="sell"){val sale=world.species(tree?.s("species")?:"succulent").i("sale");BioFacts(listOf(AppText.text(TextKey.CopyGiaBan358aea) to AppText.text(TextKey.Copy0Xu251e68,sale),AppText.text(TextKey.CopyNhanHomNay78a91a) to AppText.text(TextKey.Copy0Xu251e68,minOf(sale,(50-world.state.i("earned")).coerceAtLeast(0)))));Bt(AppText.text(TextKey.CopyPhanVuot50XuNgaySeMatKhongDeDanhF04e9d),size=AppTypography.Size12,tint=AppColors.Danger)}else BioTag(AppText.text(TextKey.CopyKhongNhanXuHoacHatA3884a),modifier=Modifier.fillMaxWidth())}
                    "archive"->Bt(AppText.text(TextKey.CopyLuuTruViecGiuNguyenLichSu630f63),size=AppTypography.Size12,tint=bp.muted)
                    "discard"->Bt(AppText.text(TextKey.CopyNoiDungChuaLuuSeMat20dc15),size=AppTypography.Size12,tint=bp.muted)
                    "ticket"->Bt(AppText.text(TextKey.Copy250XuBaoVeMotNgayBoLo6a13cf),size=AppTypography.Size12,tint=bp.muted)
                    "adjust"->Bt(AppText.text(TextKey.CopyChuyenSangNgayThuongTuNgayMaiGiuLichSuVaK02be72),size=AppTypography.Size12,tint=bp.muted)
                    "permission"->Bt(AppText.text(TextKey.CopyChiNhacNhungViecDaChonTonTrongGioYenLangTAe6dcc),size=AppTypography.Size12,tint=bp.muted)
                    "permission-choice","permission-settings"->{BioButton(AppText.text(TextKey.CopyChoPhepF1a437),{ui.send("permission-allow")},Modifier.fillMaxWidth());BioButton(if(type=="permission-choice")AppText.text(TextKey.CopyDeSau9be0a7)else AppText.text(TextKey.CopyTatThongBao9ba509),{if(type=="permission-choice")dismiss()else ui.send("permission-settings")},Modifier.fillMaxWidth(),secondary=true)}
                    "clear"->Bt(AppText.text(TextKey.CopyBanCoTheXuatBanSaoTruocKhiTiepTuc60f8b1),size=AppTypography.Size12,tint=bp.muted)
                    "clear-final"->Bt(AppText.text(TextKey.CopyXoaDuLieuVaBatDauLai60edd1),size=AppTypography.Size12,tint=bp.muted)
                    "recover"->Bt(AppText.text(TextKey.CopyThayDuLieuHienTaiTrenThietBiKhongGopXuatBB779a4),size=AppTypography.Size12,tint=bp.muted)
                    "confirm-import"->{BioFacts(listOf(AppText.text(TextKey.CopyViec818768) to (ui.fields["backupTasks"]?:"0"),AppText.text(TextKey.CopyCayC854d7) to (ui.fields["backupTrees"]?:"0"),AppText.text(TextKey.CopyLuotHoanThanh02de15) to (ui.fields["backupOccurrences"]?:"0"),AppText.text(TextKey.CopyTaoLucB1415f) to (ui.fields["backupCreated"]?.takeIf{it.isNotBlank()}?:AppText.text(TextKey.CopyKhongRo8af7da))));Bt(AppText.text(TextKey.CopyKhongGopMotBanAnToanDuocLuuTruocKhiThayCa11d8fa),size=AppTypography.Size12,tint=bp.muted)}
                    "license"->{val context=LocalContext.current;val license=remember{context.resources.openRawResource(com.mamxanh.app.R.raw.apache_license).bufferedReader().use{it.readText()}.replace("\r\n","\n").replace(Regex("(?m)^[ \\t]+"),"").replace(Regex("(?<!\\n)\\n(?!\\n)")," ").replace(Regex("[ \\t]{2,}")," ").trim()};Bt(license,size=AppTypography.Size13,line=AppTypography.LineHeight21_45)}
                    "focus-end"->Bt(AppText.text(TextKey.CopyPhanDaLamSeDuocLuu1e29e9),size=AppTypography.Size12,tint=bp.muted)
                }
            }
            if(hasFooter){HorizontalDivider(color=bp.line);Row(Modifier.fillMaxWidth().padding(start=AppDimensions.Dp16,end=AppDimensions.Dp16,top=AppDimensions.Dp12,bottom=AppDimensions.Dp16),horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp10)){
                if(type=="purchase"&&seed){
                    BioButton(AppText.text(TextKey.CopyMuaVaoTui4cd4d5),{ui.send("buy",mapOf("id" to id,"quantity" to ui.quantity.toString()))},Modifier.weight(1f),secondary=true,enabled=world.coins>=total&&!ui.busy)
                    BioButton(if(ui.quantity>1)AppText.text(TextKey.CopyMuaGieo1690d4f)else AppText.text(TextKey.CopyMuaGieo754323),{ui.send("buy-plant",mapOf("id" to id,"quantity" to ui.quantity.toString(),"plot" to ui.plot))},Modifier.weight(1f),enabled=!full&&world.coins>=total&&!ui.busy)
                }else{
                    BioButton(if(type=="purchase"&&permanent&&!owned)AppText.text(TextKey.CopyDeSau9be0a7)else AppText.text(TextKey.CopyHuy74fcd3),dismiss,Modifier.weight(1f),secondary=true,enabled=!ui.busy)
                    val label=when(type){"purchase"->if(permanent)if(owned)if(world.state.s("equipped")==id)AppText.text(TextKey.CopyDangDung275ca2)else AppText.text(TextKey.CopyTrangBi9e8e12)else AppText.text(TextKey.CopyMuaTrangBi33a520)else AppText.text(TextKey.CopyMuaC86bcd);"plant"->AppText.text(TextKey.CopyXemOKhoa2d0677);"water-all"->AppText.text(TextKey.CopyTuoiCay0990f6);"unlock"->AppText.text(TextKey.CopyMoO6d274c);"fertilizer-help"->AppText.text(TextKey.CopyChonCay61f0ec);"remove"->AppText.text(TextKey.CopyDonCayE3d410);"sell"->AppText.text(TextKey.CopyBanCay5210c7);"archive"->AppText.text(TextKey.ActionArchive);"discard"->AppText.text(TextKey.CopyBoThayDoiE01800);"ticket"->AppText.text(TextKey.CopyMuaVeE9dab8);"adjust"->AppText.text(TextKey.CopyApDungFf6315);"permission","clear"->AppText.text(TextKey.CopyTiepTuc87a7ae);"clear-final"->AppText.text(TextKey.CopyXoaDuLieu398085);"recover"->AppText.text(TextKey.CopyKhoiPhucC652d9);"confirm-import"->AppText.text(TextKey.CopyThayTheE4d19c);"focus-end"->AppText.text(TextKey.CopyKetThuc351849);else->AppText.text(TextKey.CopyXacNhan1503b0)}
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
    Row(Modifier.fillMaxWidth().heightIn(min=AppDimensions.Dp76).background(if(selected)bp.soft else bp.surface,bioShape(AppRadii.Value16)).border(AppDimensions.Dp1,if(selected)bp.deep else bp.line,bioShape(AppRadii.Value16)).clickable(onClick=onClick).padding(AppDimensions.Dp12),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){
        BioPlant(species,Modifier.size(AppDimensions.Dp50));Column(Modifier.weight(1f)){Bt(title,size=AppTypography.Size13,semi=true);Spacer(Modifier.height(AppDimensions.Dp3));Bt(subtitle,size=AppTypography.Size11,tint=bp.muted)};Bi(if(selected)"check"else icon,size=AppIcons.Size18)
    }
}
