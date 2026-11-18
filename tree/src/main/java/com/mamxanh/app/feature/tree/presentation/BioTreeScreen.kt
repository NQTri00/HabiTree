package com.mamxanh.app.feature.tree.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.presentation.ui.design.*
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

data class TreeScreenFertilizer(val id: String, val name: String, val rate: String, val hours: Int, val quantity: Int)

data class TreeScreenState(
    val growth: String,
    val watered: Boolean,
    val fertilizerNoteAllowed: Boolean,
    val canFertilize: Boolean,
    val busy: Boolean,
    val hasDecoration: Boolean,
    val decorationName: String,
    val speciesName: String,
    val stage: Int,
    val stages: Int,
    val progress: Int,
    val planted: String,
    val description: String,
    val fertilizers: List<TreeScreenFertilizer>,
)

sealed interface TreeScreenAction {
    data object Encyclopedia : TreeScreenAction
    data object Water : TreeScreenAction
    data class Fertilize(val id: String) : TreeScreenAction
    data object BuyFertilizer : TreeScreenAction
    data object Unequip : TreeScreenAction
    data object BuyAccessory : TreeScreenAction
    data object Species : TreeScreenAction
    data object Sell : TreeScreenAction
    data object Remove : TreeScreenAction
}

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
    if(t==null){BioEmpty(AppText.text(TextKey.CopyCayDaRoiVuonA1f818),AppText.text(TextKey.CopyBanVanCoTheXemLichSuTrongBachThao92d81b),action=AppText.text(TextKey.CopyBachThaoF2ed64),onClick={onAction(TreeScreenAction.Encyclopedia)});return}
    hero()
    BioTabs(listOf("care" to AppText.text(TextKey.CopyChamSoc7b7df6),"decor" to AppText.text(TextKey.CopyTrangTriD48231),"info" to AppText.text(TextKey.CopyThongTin4aa16c)), tab, onTab)
    when(tab){
        "care"->{Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Bt(if(t.growth=="dead")AppText.text(TextKey.CopyCayDaKhoHeo9c0717)else AppText.text(TextKey.CopyMotChutNuocNhe30fe89),size=AppTypography.Size17,bold=true);Bt(if(t.watered)AppText.text(TextKey.CopyDaTuoiDe2042)else AppText.text(TextKey.CopyCanTuoiA0957f),size=AppTypography.Size12,tint=bp.muted);Bt(if(t.growth=="dead")AppText.text(TextKey.CopyKhongTheHoiPhuc086975)else AppText.text(TextKey.CopyTuoiMienPhiKhongCongDonDe3e6a),size=AppTypography.Size11,tint=bp.muted)};BioButton(AppText.text(TextKey.CopyTuoi617613),{onAction(TreeScreenAction.Water)},icon="water",enabled=!t.watered&&t.growth!="dead")}
            if(t.growth!="dead"){Bt(AppText.text(TextKey.CopyDinhDuong3c8978),size=AppTypography.Size17,bold=true);Bt(if(t.growth=="mature")AppText.text(TextKey.CopyCayDaTruongThanhKhongCanBonThemEeed14)else if(!t.watered)AppText.text(TextKey.CopyTuoiCayTruocKhiBonPhanCfd0cb)else if(!t.fertilizerNoteAllowed) AppText.text(TextKey.CopyCayDangDuocBonChoHetThoiGianTruocKhiDungT72bbf2)else AppText.text(TextKey.CopyChonPhanDangCoDeBonChoCay540e72),size=AppTypography.Size12,tint=bp.muted);Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){t.fertilizers.forEach{p->BioCard(Modifier.fillMaxWidth(),padding=AppSpacing.Value12,radius=AppRadii.Value18){Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){Box(Modifier.size(AppDimensions.Dp32).background(bp.soft,bioShape(AppRadii.Value10)),contentAlignment=Alignment.Center){Bi("fertilizer",size=AppIcons.Size19)};Column(Modifier.weight(1f)){Bt(p.name,size=AppTypography.Size12,semi=true);Bt(AppText.text(TextKey.Copy01GioCon252cf45,p.rate,p.hours,p.quantity),size=AppTypography.Size11,tint=bp.muted)};BioButton(if(p.quantity==0)AppText.text(TextKey.CopyHetA89ea0)else AppText.text(TextKey.CopyBon4fd741),{onAction(TreeScreenAction.Fertilize(p.id))},enabled=t.canFertilize&&p.quantity>0&&t.growth=="growing"&&!t.busy)}}}};BioTextAction(AppText.text(TextKey.CopyBoSungVatPhamA4c18c),{onAction(TreeScreenAction.BuyFertilizer)},"next",modifier=Modifier.fillMaxWidth())}}
        "decor"->{if(t.growth=="dead")Bt(AppText.text(TextKey.CopyCayDaKhoHeoKhongTheDeoPhuKien7658f1),tint=bp.muted)else{Row(Modifier.fillMaxWidth().height(if(t.hasDecoration)AppDimensions.Dp32 else AppDimensions.Dp26),verticalAlignment=Alignment.CenterVertically){Bt(AppText.text(TextKey.CopyDiemToChoCay027c8d),Modifier.weight(1f),size=AppTypography.Size16,bold=true,spacing=AppTypography.TrackingMinus_35);if(t.hasDecoration)BioTextAction(AppText.text(TextKey.CopyThaoD39d4a),{onAction(TreeScreenAction.Unequip)},modifier=Modifier.requiredHeight(AppDimensions.Dp48))};if(t.hasDecoration)Bt(AppText.text(TextKey.CopyDangDungB6dcf3)+t.decorationName,size=AppTypography.Size12,tint=bp.muted);accessories();BioButton(AppText.text(TextKey.CopyTiemPhuKienE4d7d0),{onAction(TreeScreenAction.BuyAccessory)},Modifier.fillMaxWidth(),"shop",true)}}
        else->{BioCard(Modifier.fillMaxWidth()){BioFacts(listOf(AppText.text(TextKey.CopyLoaiD0aa6d) to t.speciesName,AppText.text(TextKey.CopyGiaiDoanAa70a1) to "${t.stage} / ${t.stages}",AppText.text(TextKey.CopyPhatTrien2d33cb) to "${t.progress}%",AppText.text(TextKey.CopyNgayTrong2eb870) to t.planted));Bt(t.description,size=AppTypography.Size12,tint=bp.muted)};BioLink(AppText.text(TextKey.CopyHoSoLoaiCfff22),"book",{onAction(TreeScreenAction.Species)})}
    }
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){if(t.growth=="mature")BioTextAction(AppText.text(TextKey.CopyBanCay5210c7),{onAction(TreeScreenAction.Sell)});BioTextAction(AppText.text(TextKey.CopyDonCayE3d410),{onAction(TreeScreenAction.Remove)},"trash",AppColors.Danger,iconFirst=true)}
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun BioTreeScreenPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp18)) {
        BioTreeScreen(null, "care", {}, {}, {}, {})
    }
}
