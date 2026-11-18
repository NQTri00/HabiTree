package com.mamxanh.app.feature.shop.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.mamxanh.app.presentation.ui.design.*
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

data class ShopTreeContext(val id: String, val name: String, val species: String)

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
    BioIntro(AppText.text(TextKey.CopyTiemNho87d6e3),AppText.text(TextKey.CopyMotChutChoMamXanh34d909),action={BioIconButton("bag",onInventory)})
    BioTabs(listOf("seeds" to AppText.text(TextKey.CopyHatGiongF02257),"care" to AppText.text(TextKey.CopyChamSoc7b7df6),"accessories" to AppText.text(TextKey.CopyPhuKien0852d7)), tab, onTab)
    if(shoppingTree!=null)Row(Modifier.fillMaxWidth().background(AppColors.White.copy(alpha=AppOpacity.Value_44),RoundedCornerShape(AppDimensions.Dp15)).border(AppDimensions.Dp1,AppColors.White.copy(alpha=AppOpacity.Value_6),RoundedCornerShape(AppDimensions.Dp15)).padding(horizontal=AppDimensions.Dp11,vertical=AppDimensions.Dp9),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp10)){
        BioPlant(shoppingTree.species,Modifier.size(AppDimensions.Dp38))
        Column(Modifier.weight(1f)){Bt(AppText.text(TextKey.CopyDangChonCho3a6c0f),size=AppTypography.Size10,tint=bp.muted);Bt(shoppingTree.name,size=AppTypography.Size12,semi=true)}
        BioTextAction(AppText.text(TextKey.CopyVeCayB033ce),{onTree(shoppingTree.id)},"next")
    }
    if (tab == "accessories") {
        Bt(AppText.text(TextKey.CopyChonMotMonDeoNgayChoCay32c723), size = AppTypography.Size12, tint = bp.muted)
    }
    if (itemCount > 0) BioGrid(itemCount, content = item) else BioEmpty(AppText.text(TextKey.CopyTuiDangTrong37bf7b),AppText.text(TextKey.CopyBanCoTheMuaVaDungNgayTrongTiem784a6a),"bag",AppText.text(TextKey.CopyGheTiemCb74e9),onShop)
    if(tab=="care")BioDetails(AppText.text(TextKey.CopyDungVatPham1cb958)){Bt(AppText.text(TextKey.CopyBinhTuoiDungLauDaiPhanBonDungTrucTiepTaiC180e06),size=AppTypography.Size12,tint=bp.muted)}
    if(tab=="seeds")Bt(AppText.text(TextKey.CopyChonHatMuaGieoChamCayAd0e4c),Modifier.fillMaxWidth(),size=AppTypography.Size11,tint=bp.muted,align=TextAlign.Center)
}
