package com.mamxanh.app.feature.shop.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextAlign
import com.mamxanh.app.presentation.ui.design.*
import com.mamxanh.core.ui.R

@Composable
fun BioShopScreen(
    tab: String,
    shoppingTree: ShopTreeContext?,
    itemCount: Int,
    onTab: (String) -> Unit,
    onInventory: () -> Unit,
    onTree: (String) -> Unit,
    onShop: () -> Unit,
    item: @Composable (Int, Modifier) -> Unit,
) {
    BioIntro(stringResource(R.string.copy_tiem_nho_87d6e3),stringResource(R.string.copy_mot_chut_cho_mam_xanh_34d909),action={BioIconButton("bag",onInventory)})
    BioTabs(listOf("seeds" to stringResource(R.string.copy_hat_giong_f02257),"care" to stringResource(R.string.copy_cham_soc_7b7df6),"accessories" to stringResource(R.string.copy_phu_kien_0852d7)), tab, onTab)
    if(shoppingTree!=null)Row(Modifier.fillMaxWidth().background(AppColors.White.copy(alpha=AppOpacity.Value_44),RoundedCornerShape(AppDimensions.Dp15)).border(AppDimensions.Dp1,AppColors.White.copy(alpha=AppOpacity.Value_6),RoundedCornerShape(AppDimensions.Dp15)).padding(horizontal=AppDimensions.Dp11,vertical=AppDimensions.Dp9),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp10)){
        BioPlant(shoppingTree.species,Modifier.size(AppDimensions.Dp38))
        Column(Modifier.weight(1f)){Bt(stringResource(R.string.copy_dang_chon_cho_3a6c0f),size=AppTypography.Size10,tint=bp.muted);Bt(shoppingTree.name,size=AppTypography.Size12,semi=true)}
        BioTextAction(stringResource(R.string.copy_ve_cay_b033ce),{onTree(shoppingTree.id)},"next")
    }
    if (tab == "accessories") {
        Bt(stringResource(R.string.copy_chon_mot_mon_deo_ngay_cho_cay_32c723), size = AppTypography.Size12, tint = bp.muted)
    }
    if (itemCount > 0) BioGrid(itemCount, content = item) else BioEmpty(stringResource(R.string.copy_tui_dang_trong_37bf7b),stringResource(R.string.copy_ban_co_the_mua_va_dung_ngay_trong_tiem_784a6a),"bag",stringResource(R.string.copy_ghe_tiem_cb74e9),onShop)
    if(tab=="care")BioDetails(stringResource(R.string.copy_dung_vat_pham_1cb958)){Bt(stringResource(R.string.copy_binh_tuoi_dung_lau_dai_phan_bon_dung_truc_tiep_tai_c_180e06),size=AppTypography.Size12,tint=bp.muted)}
    if(tab=="seeds")Bt(stringResource(R.string.copy_chon_hat_mua_gieo_cham_cay_ad0e4c),Modifier.fillMaxWidth(),size=AppTypography.Size11,tint=bp.muted,align=TextAlign.Center)
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun BioShopScreenPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp18)) {
        BioShopScreen("seeds", null, 0, {}, {}, {}, {}, { _, _ -> })
    }
}
