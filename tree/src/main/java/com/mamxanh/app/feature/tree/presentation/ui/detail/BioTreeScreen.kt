package com.mamxanh.app.feature.tree.presentation.ui.detail

import androidx.compose.ui.res.stringResource
import com.mamxanh.core.ui.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.design.*

@Composable
fun BioTreeScreen(
    state: TreeScreenState?,
    tab: String,
    onTab: (String) -> Unit,
    onAction: (TreeScreenAction) -> Unit,
    hero: @Composable () -> Unit,
    accessories: @Composable () -> Unit,
) {
    val t = state
    if(t==null){BioEmpty(stringResource(R.string.copy_cay_da_roi_vuon_a1f818),stringResource(R.string.copy_ban_van_co_the_xem_lich_su_trong_bach_thao_92d81b),action=stringResource(R.string.copy_bach_thao_f2ed64),onClick={onAction(TreeScreenAction.Encyclopedia)});return}
    hero()
    BioTabs(listOf("care" to stringResource(R.string.copy_cham_soc_7b7df6),"decor" to stringResource(R.string.copy_trang_tri_d48231),"info" to stringResource(R.string.copy_thong_tin_4aa16c)), tab, onTab)
    when(tab){
        "care"->{Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Bt(if(t.growth=="dead")stringResource(R.string.copy_cay_da_kho_heo_9c0717)else stringResource(R.string.copy_mot_chut_nuoc_nhe_30fe89),size=AppTypography.Size17,bold=true);Bt(if(t.watered)stringResource(R.string.copy_da_tuoi_de2042)else stringResource(R.string.copy_can_tuoi_a0957f),size=AppTypography.Size12,tint=bp.muted);Bt(if(t.growth=="dead")stringResource(R.string.copy_khong_the_hoi_phuc_086975)else stringResource(R.string.copy_tuoi_mien_phi_khong_cong_don_de3e6a),size=AppTypography.Size11,tint=bp.muted)};BioButton(stringResource(R.string.copy_tuoi_617613),{onAction(TreeScreenAction.Water)},icon="water",enabled=t.canWater)}
            if(t.growth!="dead"){Bt(stringResource(R.string.copy_dinh_duong_3c8978),size=AppTypography.Size17,bold=true);Bt(if(t.growth=="mature")stringResource(R.string.copy_cay_da_truong_thanh_khong_can_bon_them_eeed14)else if(!t.watered)stringResource(R.string.copy_tuoi_cay_truoc_khi_bon_phan_cfd0cb)else if(!t.fertilizerNoteAllowed) stringResource(R.string.copy_cay_dang_duoc_bon_cho_het_thoi_gian_truoc_khi_dung_t_72bbf2)else stringResource(R.string.copy_chon_phan_dang_co_de_bon_cho_cay_540e72),size=AppTypography.Size12,tint=bp.muted);Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){t.fertilizers.forEach{p->BioCard(Modifier.fillMaxWidth(),padding=AppSpacing.Value12,radius=AppRadii.Value18){Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){Box(Modifier.size(AppDimensions.Dp32).background(bp.soft,bioShape(AppRadii.Value10)),contentAlignment=Alignment.Center){Bi("fertilizer",size=AppIcons.Size19)};Column(Modifier.weight(1f)){Bt(p.name,size=AppTypography.Size12,semi=true);Bt(stringResource(R.string.copy_0_1_gio_con_2_52cf45, p.rate, p.hours, p.quantity),size=AppTypography.Size11,tint=bp.muted)};BioButton(if(p.quantity==0)stringResource(R.string.copy_het_a89ea0)else stringResource(R.string.copy_bon_4fd741),{onAction(TreeScreenAction.Fertilize(p.id))},enabled=t.canApplyFertilizer(p))}}}};BioTextAction(stringResource(R.string.copy_bo_sung_vat_pham_a4c18c),{onAction(TreeScreenAction.BuyFertilizer)},"next",modifier=Modifier.fillMaxWidth())}}
        "decor"->{if(t.growth=="dead")Bt(stringResource(R.string.copy_cay_da_kho_heo_khong_the_deo_phu_kien_7658f1),tint=bp.muted)else{Row(Modifier.fillMaxWidth().height(if(t.hasDecoration)AppDimensions.Dp32 else AppDimensions.Dp26),verticalAlignment=Alignment.CenterVertically){Bt(stringResource(R.string.copy_diem_to_cho_cay_027c8d),Modifier.weight(1f),size=AppTypography.Size16,bold=true,spacing=AppTypography.TrackingMinus_35);if(t.hasDecoration)BioTextAction(stringResource(R.string.copy_thao_d39d4a),{onAction(TreeScreenAction.Unequip)},modifier=Modifier.requiredHeight(AppDimensions.Dp48))};if(t.hasDecoration)Bt(stringResource(R.string.copy_dang_dung_b6dcf3, t.decorationName),size=AppTypography.Size12,tint=bp.muted);accessories();BioButton(stringResource(R.string.copy_tiem_phu_kien_e4d7d0),{onAction(TreeScreenAction.BuyAccessory)},Modifier.fillMaxWidth(),"shop",true)}}
        else->{BioCard(Modifier.fillMaxWidth()){BioFacts(listOf(stringResource(R.string.copy_loai_d0aa6d) to t.speciesName,stringResource(R.string.copy_giai_doan_aa70a1) to "${t.stage} / ${t.stages}",stringResource(R.string.copy_phat_trien_2d33cb) to "${t.progress}%",stringResource(R.string.copy_ngay_trong_2eb870) to t.planted));Bt(t.description,size=AppTypography.Size12,tint=bp.muted)};BioLink(stringResource(R.string.copy_ho_so_loai_cfff22),"book",{onAction(TreeScreenAction.Species)})}
    }
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){if(t.growth=="mature")BioTextAction(stringResource(R.string.copy_ban_cay_5210c7),{onAction(TreeScreenAction.Sell)});BioTextAction(stringResource(R.string.copy_don_cay_e3d410),{onAction(TreeScreenAction.Remove)},"trash",AppColors.Danger,iconFirst=true)}
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun BioTreeScreenPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp18)) {
        BioTreeScreen(null, "care", {}, {}, {}, {})
    }
}
