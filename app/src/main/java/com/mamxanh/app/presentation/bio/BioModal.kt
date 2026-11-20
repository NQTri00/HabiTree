package com.mamxanh.app.presentation.bio

import androidx.compose.ui.res.stringResource
import com.mamxanh.core.ui.R
import com.mamxanh.app.presentation.ui.design.*

import com.mamxanh.app.presentation.ui.design.AppColors
import com.mamxanh.app.presentation.ui.design.AppDimensions
import com.mamxanh.app.presentation.ui.design.AppIcons
import com.mamxanh.app.presentation.ui.design.AppOpacity
import com.mamxanh.app.presentation.ui.design.AppRadii
import com.mamxanh.app.presentation.ui.design.AppSpacing
import com.mamxanh.app.presentation.ui.design.AppTypography



import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*

@Composable internal fun BioModal(world:BioWorld,ui:BioController){
    val type=ui.modal;val id=ui.modalId;val item=world.product(id);val tree=world.tree(id);val task=world.task(id)
    val seed=world.species.any{it.s("id")==id};val permanent=item.b("permanent");val owned=if(type=="accessory")world.owned(id)>0 else permanent&&world.count(id)>0
    val purchase = ui.shopSelection.purchaseAvailability(world.coins, item.i("price"), owned, permanent, ui.busy)
    val total = purchase.total
    val living=world.trees.filter{it.s("state")!="dead"}
    val targetId = ui.shopSelection.accessoryTarget(living.map { it.s("id") }, ui.homeTreeState.selectedId)
    val target = living.firstOrNull { it.s("id") == targetId }
    val full=world.emptyPlots().isEmpty()
    val title=when(type){"tree-picker"->stringResource(R.string.copy_cay_tren_trang_chu_76c8b5);"seed-picker"->stringResource(R.string.copy_gieo_mot_mam_moi_2a80ed);"purchase"->if(permanent)stringResource(R.string.copy_dung_cu_cham_soc_9f1ca9)else stringResource(R.string.copy_them_vao_tui_a0e7dc);"accessory"->item.s("name");"plant"->if(full)stringResource(R.string.copy_chua_co_o_trong_6abd67)else stringResource(R.string.copy_gieo_mot_mam_xanh_776075);"water-all"->stringResource(R.string.copy_tuoi_ca_vuon_20a14a);"unlock"->stringResource(R.string.copy_mo_o_0_01668e, id);"fertilizer-help"->stringResource(R.string.copy_dung_phan_bon_79fd5f);"remove"->stringResource(R.string.copy_don_cay_e3d410);"sell"->stringResource(R.string.copy_ban_cay_5210c7);"archive"->stringResource(R.string.copy_luu_tru_viec_50123f);"discard"->stringResource(R.string.copy_bo_thay_doi_b07820);"ticket"->stringResource(R.string.copy_mua_ve_bao_ve_acdfc5);"adjust"->stringResource(R.string.copy_dieu_chinh_lich_bdb141);"permission"->stringResource(R.string.copy_nhac_viec_tuy_chon_907252);"permission-choice"->stringResource(R.string.copy_cho_phep_thong_bao_6c0308);"permission-settings"->stringResource(R.string.copy_cai_dat_thong_bao_765600);"clear"->stringResource(R.string.copy_xoa_du_lieu_8ce180);"clear-final"->stringResource(R.string.copy_xac_nhan_lan_cuoi_8421a1);"recover"->stringResource(R.string.copy_khoi_phuc_ban_an_toan_9361dd);"confirm-import"->stringResource(R.string.copy_khoi_phuc_du_lieu_feaa4a);"license"->stringResource(R.string.copy_giay_phep_nguon_mo_2ecf92);"focus-end"->stringResource(R.string.copy_ket_thuc_phien_7ea116);else->stringResource(R.string.copy_xac_nhan_1503b0)}
    var plantName by rememberSaveable(type,id){mutableStateOf("")};var selectedPlot by rememberSaveable(type,id){mutableStateOf(ui.plantingSelection.initialDialogPlot(world.emptyPlots()))}
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
                    "tree-picker"->{Bt(stringResource(R.string.copy_chon_cay_sac_mau_se_di_cung_ban_58b284),size=AppTypography.Size12,tint=bp.muted);Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp9)){world.trees.forEach{t->BioChoice(t.s("name"),world.data.o("palettes").o(t.s("species")).s("name"),t.s("species"),ui.homeTreeState.selectedId==t.s("id"),{ui.act("bio-pin:"+t.s("id"))})}};if(world.trees.isEmpty())BioButton(stringResource(R.string.copy_gieo_cay_dau_tien_afd3c1),{ui.show("seed-picker")},Modifier.fillMaxWidth(),"plus")}
                    "seed-picker"->{Bt(stringResource(R.string.copy_hat_dang_co_chon_de_gieo_237689),size=AppTypography.Size12,tint=bp.muted);Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp9)){world.species.filter{world.count(it.s("id"))>0}.forEach{s->BioChoice(s.s("name"),stringResource(R.string.copy_con_0_hat_149c5e, world.count(s.s("id"))),s.s("id"),false,{ui.show("plant",s.s("id"))},"plus")}};if(world.species.none{world.count(it.s("id"))>0})Bt(stringResource(R.string.copy_chua_co_hat_trong_tui_095661),size=AppTypography.Size12,tint=bp.muted);BioButton(stringResource(R.string.copy_chon_hat_o_tiem_a4c334),{ui.shopSelection.shopTab="seeds";ui.go("shop")},Modifier.fillMaxWidth(),"shop",true)}
                    "purchase"->{
                        Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp16)){
                            if(seed)BioPlant(id,Modifier.size(AppDimensions.Dp90))else Box(Modifier.size(AppDimensions.Dp48),contentAlignment=Alignment.Center){Bi(if(permanent)"water"else "fertilizer",size=AppIcons.Size32)}
                            Column(Modifier.weight(1f)) {
                                Bt(item.s("name"), size = AppTypography.Size17, bold = true)
                                val price = stringResource(R.string.copy_0_xu_mon_3dd1cb, item.i("price"))
                                val inInventory = stringResource(R.string.copy_trong_tui_0_79a061, world.count(id))
                                Bt("$price · $inInventory", size = AppTypography.Size12, tint = bp.muted)
                            }
                        }
                        Bt(item.s("description").ifBlank{if(permanent)stringResource(R.string.copy_tuoi_trong_0_gio_dung_lau_dai_7c36d5, item.i("hours"))else stringResource(R.string.copy_0_trong_1_gio_dung_khi_cay_da_tuoi_mot_lan_moi_24_gi_acf259, item.s("rate"),item.i("hours"))},size=AppTypography.Size12,tint=bp.muted)
                        if(seed)BioTag(stringResource(R.string.copy_0_giai_doan_26f2b4, bioTier(item.s("tier")), item.i("stages")),modifier=Modifier.fillMaxWidth())
                        if (!permanent) BioPurchaseQuantity(
                            quantity = ui.shopSelection.quantity,
                            busy = ui.busy,
                            onQuantity = { ui.shopSelection.updateQuantity(it) },
                        )
                        BioMetrics(listOf(stringResource(R.string.copy_tong_13fdba) to total.toString(),stringResource(R.string.copy_vi_39a29f) to world.coins.toString(),(if(world.coins<total)stringResource(R.string.copy_can_them_e011ae)else stringResource(R.string.copy_con_lai_b3edb1)) to kotlin.math.abs(world.coins-total).toString()))
                        if(world.coins<total)Bt(stringResource(R.string.copy_khoang_0_ngay_uoc_tinh_40_xu_ngay_8be6c6, kotlin.math.ceil((total-world.coins)/40.0).toInt()),size=AppTypography.Size12,tint=bp.muted)
                        if(seed&&full)Bt(stringResource(R.string.copy_vuon_da_day_ban_van_co_the_mua_hat_vao_tui_e14bdf),size=AppTypography.Size12,tint=bp.muted)
                    }
                    "accessory"->{
                        Box(Modifier.fillMaxWidth().height(AppDimensions.Dp135).background(Brush.linearGradient(listOf(AppColors.White.copy(alpha=AppOpacity.Value_6),bp.soft)),bioShape(AppRadii.Value20)).border(AppDimensions.Dp1,AppColors.White.copy(alpha=AppOpacity.Value_6),bioShape(AppRadii.Value20)),contentAlignment=Alignment.Center){Bi(accessoryIcon(id),size=AppIcons.Size56);BioTag(if(owned)stringResource(R.string.copy_da_so_huu_822625)else stringResource(R.string.copy_0_xu_251e68, item.i("price")),modifier=Modifier.align(Alignment.BottomEnd).padding(AppDimensions.Dp12))}
                        if(living.isNotEmpty())BioSelect(stringResource(R.string.copy_deo_cho_cay_afc821),living.map{it.s("id") to it.s("name")},target?.s("id").orEmpty(),onSelect={ui.shopSelection.targetTree=it})else Bt(stringResource(R.string.copy_gieo_mot_cay_truoc_khi_deo_phu_kien_a3a9b9),size=AppTypography.Size12,tint=bp.muted)
                        val other=world.trees.firstOrNull{it.s("decor")==id&&it.s("id")!=target?.s("id")}
                        if(other!=null&&owned)Bt(stringResource(R.string.copy_chuyen_tu_0_khong_mat_them_xu_c8b60f, other.s("name")),size=AppTypography.Size12,tint=bp.muted)
                        Bt(stringResource(R.string.copy_chi_trang_tri_khong_thay_doi_toc_do_lon_c1bae1),size=AppTypography.Size12,tint=bp.muted)
                        if(!owned&&world.coins<item.i("price"))BioError(stringResource(R.string.copy_can_them_0_xu_d7d41b, item.i("price")-world.coins))
                        val using=target?.s("decor")==id
                        Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp10)){
                            BioButton(if(using)stringResource(R.string.copy_dang_dung_275ca2)else if(owned)stringResource(R.string.copy_deo_cho_cay_afc821)else stringResource(R.string.copy_mua_deo_d9c74d),{ui.send("accessory-use",mapOf("id" to id,"tree" to target?.s("id").orEmpty()))},Modifier.fillMaxWidth(),enabled=purchase.canEquipAccessory(target != null, using))
                            if(!owned)BioButton(stringResource(R.string.copy_chi_mua_vao_tui_cbc02d),{ui.send("accessory-buy",mapOf("id" to id))},Modifier.fillMaxWidth(),secondary=true,enabled=purchase.canBuyAccessory)
                        }
                    }
                    "plant"->{if(full)Bt(stringResource(R.string.copy_hat_van_o_trong_tui_mo_them_o_de_gieo_17fb08),size=AppTypography.Size12,tint=bp.muted)else{
                        Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp16)){BioPlant(id,Modifier.size(AppDimensions.Dp85));Column{Bt(item.s("name"),size=AppTypography.Size17,bold=true);Bt(stringResource(R.string.copy_trong_tui_0_hat_d56e0e, world.count(id)),size=AppTypography.Size12,tint=bp.muted)}}
                        BioField(stringResource(R.string.copy_ten_cay_tuy_chon_1af251),plantName,{plantName=it.take(100)},placeholder=stringResource(R.string.copy_mam_binh_yen_df7062))
                        BioSelect(stringResource(R.string.copy_o_trong_5da6bd),world.emptyPlots().map{it.toString() to stringResource(R.string.copy_o_5b7240, it.toString().padStart(2, '0'))},selectedPlot,onSelect={selectedPlot=it})
                        Bt(stringResource(R.string.copy_dung_1_hat_de_trong_ten_de_dung_ten_loai_61bbff),size=AppTypography.Size12,tint=bp.muted)
                        BioButton(stringResource(R.string.copy_gieo_hat_6d82a9),{ui.send("plant",mapOf("id" to id,"plot" to selectedPlot,"name" to plantName))},Modifier.fillMaxWidth(),enabled=world.count(id)>0&&!ui.busy)
                    }}
                    "water-all"->{BioTag(stringResource(R.string.copy_mien_phi_dfc4f7),modifier=Modifier.fillMaxWidth());BioFacts(listOf(stringResource(R.string.copy_can_tuoi_a0957f) to world.trees.count{!it.b("watered")&&it.s("state")!="dead"}.toString(),stringResource(R.string.copy_da_tuoi_de2042) to world.trees.count{it.b("watered")}.toString(),stringResource(R.string.copy_kho_heo_bf546a) to world.trees.count{it.s("state")=="dead"}.toString()));Bt(stringResource(R.string.copy_chi_tuoi_cay_song_can_nuoc_thoi_gian_khong_cong_don_6737f7),size=AppTypography.Size12,tint=bp.muted)}
                    "unlock"->{val cost=400+(world.state.i("unlocked",5)-5).coerceAtLeast(0)*200;BioFacts(listOf(stringResource(R.string.copy_gia_mo_o_708ec3) to stringResource(R.string.copy_0_xu_251e68, cost),stringResource(R.string.copy_vi_hien_tai_127da5) to stringResource(R.string.copy_0_xu_251e68, world.coins)));if(world.coins<cost)Bt(stringResource(R.string.copy_can_them_0_xu_d7d41b, cost-world.coins),size=AppTypography.Size12,tint=bp.muted)}
                    "fertilizer-help"->Bt(stringResource(R.string.copy_bon_phan_tai_cay_dang_lon_da_duoc_tuoi_ee5c0a),size=AppTypography.Size12,tint=bp.muted)
                    "remove","sell"->{Bt(tree?.s("name").orEmpty(),size=AppTypography.Size17,bold=true);Bt(stringResource(R.string.copy_cay_roi_o_vinh_vien_khong_hoan_lai_hat_hoac_tien_tri_af5127),size=AppTypography.Size12,tint=bp.muted);if(type=="sell"){val sale=world.species(tree?.s("species")?:"succulent").i("sale");BioFacts(listOf(stringResource(R.string.copy_gia_ban_358aea) to stringResource(R.string.copy_0_xu_251e68, sale),stringResource(R.string.copy_nhan_hom_nay_78a91a) to stringResource(R.string.copy_0_xu_251e68, minOf(sale,(50-world.state.i("earned")).coerceAtLeast(0)))));Bt(stringResource(R.string.copy_phan_vuot_50_xu_ngay_se_mat_khong_de_danh_f04e9d),size=AppTypography.Size12,tint=AppColors.Danger)}else BioTag(stringResource(R.string.copy_khong_nhan_xu_hoac_hat_a3884a),modifier=Modifier.fillMaxWidth())}
                    "archive"->Bt(stringResource(R.string.copy_luu_tru_viec_giu_nguyen_lich_su_630f63),size=AppTypography.Size12,tint=bp.muted)
                    "discard"->Bt(stringResource(R.string.copy_noi_dung_chua_luu_se_mat_20dc15),size=AppTypography.Size12,tint=bp.muted)
                    "ticket"->Bt(stringResource(R.string.copy_250_xu_bao_ve_mot_ngay_bo_lo_6a13cf),size=AppTypography.Size12,tint=bp.muted)
                    "adjust"->Bt(stringResource(R.string.copy_chuyen_sang_ngay_thuong_tu_ngay_mai_giu_lich_su_va_k_02be72),size=AppTypography.Size12,tint=bp.muted)
                    "permission"->Bt(stringResource(R.string.copy_chi_nhac_nhung_viec_da_chon_ton_trong_gio_yen_lang_t_ae6dcc),size=AppTypography.Size12,tint=bp.muted)
                    "permission-choice","permission-settings"->{BioButton(stringResource(R.string.copy_cho_phep_f1a437),{ui.send("permission-allow")},Modifier.fillMaxWidth());BioButton(if(type=="permission-choice")stringResource(R.string.copy_de_sau_9be0a7)else stringResource(R.string.copy_tat_thong_bao_9ba509),{if(type=="permission-choice")dismiss()else ui.send("permission-settings")},Modifier.fillMaxWidth(),secondary=true)}
                    "clear"->Bt(stringResource(R.string.copy_ban_co_the_xuat_ban_sao_truoc_khi_tiep_tuc_60f8b1),size=AppTypography.Size12,tint=bp.muted)
                    "clear-final"->Bt(stringResource(R.string.copy_xoa_du_lieu_va_bat_dau_lai_60edd1),size=AppTypography.Size12,tint=bp.muted)
                    "recover"->Bt(stringResource(R.string.copy_thay_du_lieu_hien_tai_tren_thiet_bi_khong_gop_xuat_b_b779a4),size=AppTypography.Size12,tint=bp.muted)
                    "confirm-import"->{BioFacts(listOf(stringResource(R.string.copy_viec_818768) to (ui.settingsRequests.backupImport?.tasks?:"0"),stringResource(R.string.copy_cay_c854d7) to (ui.settingsRequests.backupImport?.trees?:"0"),stringResource(R.string.copy_luot_hoan_thanh_02de15) to (ui.settingsRequests.backupImport?.occurrences?:"0"),stringResource(R.string.copy_tao_luc_b1415f) to (ui.settingsRequests.backupImport?.created?.takeIf{it.isNotBlank()}?:stringResource(R.string.copy_khong_ro_8af7da))));Bt(stringResource(R.string.copy_khong_gop_mot_ban_an_toan_duoc_luu_truoc_khi_thay_ca_11d8fa),size=AppTypography.Size12,tint=bp.muted)}
                    "license"->{val context=LocalContext.current;val license=remember{context.resources.openRawResource(com.mamxanh.app.R.raw.apache_license).bufferedReader().use{it.readText()}.replace("\r\n","\n").replace(Regex("(?m)^[ \\t]+"),"").replace(Regex("(?<!\\n)\\n(?!\\n)")," ").replace(Regex("[ \\t]{2,}")," ").trim()};Bt(license,size=AppTypography.Size13,line=AppTypography.LineHeight21_45)}
                    "focus-end"->Bt(stringResource(R.string.copy_phan_da_lam_se_duoc_luu_1e29e9),size=AppTypography.Size12,tint=bp.muted)
                }
            }
            if(hasFooter){HorizontalDivider(color=bp.line);Row(Modifier.fillMaxWidth().padding(start=AppDimensions.Dp16,end=AppDimensions.Dp16,top=AppDimensions.Dp12,bottom=AppDimensions.Dp16),horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp10)){
                if(type=="purchase"&&seed){
                    BioButton(stringResource(R.string.copy_mua_vao_tui_4cd4d5),{ui.send("buy",mapOf("id" to id,"quantity" to ui.shopSelection.quantity.toString()))},Modifier.weight(1f),secondary=true,enabled=purchase.canSubmit)
                    BioButton(if(ui.shopSelection.quantity>1)stringResource(R.string.copy_mua_gieo_1_690d4f)else stringResource(R.string.copy_mua_gieo_754323),{ui.send("buy-plant",mapOf("id" to id,"quantity" to ui.shopSelection.quantity.toString(),"plot" to ui.plantingSelection.plotKey))},Modifier.weight(1f),enabled=!full&&purchase.canSubmit)
                }else{
                    BioButton(if(type=="purchase"&&permanent&&!owned)stringResource(R.string.copy_de_sau_9be0a7)else stringResource(R.string.copy_huy_74fcd3),dismiss,Modifier.weight(1f),secondary=true,enabled=!ui.busy)
                    val label=when(type){"purchase"->if(permanent)if(owned)if(world.state.s("equipped")==id)stringResource(R.string.copy_dang_dung_275ca2)else stringResource(R.string.copy_trang_bi_9e8e12)else stringResource(R.string.copy_mua_trang_bi_33a520)else stringResource(R.string.copy_mua_c86bcd);"plant"->stringResource(R.string.copy_xem_o_khoa_2d0677);"water-all"->stringResource(R.string.copy_tuoi_cay_0990f6);"unlock"->stringResource(R.string.copy_mo_o_6d274c);"fertilizer-help"->stringResource(R.string.copy_chon_cay_61f0ec);"remove"->stringResource(R.string.copy_don_cay_e3d410);"sell"->stringResource(R.string.copy_ban_cay_5210c7);"archive"->stringResource(R.string.action_archive);"discard"->stringResource(R.string.copy_bo_thay_doi_e01800);"ticket"->stringResource(R.string.copy_mua_ve_e9dab8);"adjust"->stringResource(R.string.copy_ap_dung_ff6315);"permission","clear"->stringResource(R.string.copy_tiep_tuc_87a7ae);"clear-final"->stringResource(R.string.copy_xoa_du_lieu_398085);"recover"->stringResource(R.string.copy_khoi_phuc_c652d9);"confirm-import"->stringResource(R.string.copy_thay_the_e4d19c);"focus-end"->stringResource(R.string.copy_ket_thuc_351849);else->stringResource(R.string.copy_xac_nhan_1503b0)}
                    val enabled=!ui.busy&&when(type){"purchase"->purchase.canSubmit&&!(owned&&world.state.s("equipped")==id);"unlock"->world.coins>=400+(world.state.i("unlocked",5)-5).coerceAtLeast(0)*200;"ticket"->world.coins>=250;else->true}
                    BioButton(label,{
                        when(type){
                            "purchase"->ui.send(if(owned)"equip:$id"else if(permanent)"buy-equip"else "buy",mapOf("id" to id,"quantity" to ui.shopSelection.quantity.toString()))
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

@Composable
private fun BioPurchaseQuantity(quantity: Int, busy: Boolean, onQuantity: (Int) -> Unit) {
    val canDecrease = quantity > 1 && !busy
    val decreaseLabel = stringResource(R.string.shop_decrease_quantity)
    Row(
        Modifier.fillMaxWidth().heightIn(min = AppDimensions.Dp52),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val shape = bioShape(AppRadii.Value12)
        Box(
            Modifier.size(AppDimensions.Dp48)
                .alpha(if (canDecrease) AppOpacity.Value1 else AppOpacity.Value_45)
                .background(bp.surface, shape)
                .border(AppDimensions.Dp1, bp.line, shape)
                .semantics { contentDescription = decreaseLabel }
                .clickable(enabled = canDecrease, role = Role.Button) { onQuantity(quantity - 1) },
            contentAlignment = Alignment.Center,
        ) {
            Bt("\u2212", size = AppTypography.Size22, tint = bp.deep)
        }
        Spacer(Modifier.width(AppDimensions.Dp25))
        BioField(
            "",
            quantity.toString(),
            { onQuantity(it.toIntOrNull() ?: 1) },
            Modifier.width(AppDimensions.Dp65),
            number = true,
            centered = true,
            accessibilityLabel = stringResource(R.string.shop_purchase_quantity),
        )
        Spacer(Modifier.width(AppDimensions.Dp25))
        BioIconButton(
            "plus",
            { onQuantity(quantity + 1) },
            boxed = true,
            enabled = quantity < 99 && !busy,
            label = stringResource(R.string.shop_increase_quantity),
        )
    }
}

@Preview(name = "Purchase quantity", widthDp = 335, showBackground = true)
@Composable
private fun BioPurchaseQuantityPreview() {
    BioPurchaseQuantity(quantity = 1, busy = false) {}
}
