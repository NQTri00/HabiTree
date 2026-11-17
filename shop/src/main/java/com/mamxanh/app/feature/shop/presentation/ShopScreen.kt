package com.mamxanh.app.feature.shop.presentation

import com.mamxanh.app.presentation.ui.design.AppDimensions
import com.mamxanh.app.presentation.ui.design.AppTypography

import com.mamxanh.shared.localization.TextKey

import com.mamxanh.shared.localization.AppText

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamxanh.app.feature.shop.domain.model.*
import com.mamxanh.app.presentation.ui.*
import com.mamxanh.app.presentation.ui.components.*
import com.mamxanh.shared.model.careProducts
import com.mamxanh.shared.model.lifecycleProfile

@Composable
fun ShopScreen(state: ShopUiState, onIntent: (ShopIntent) -> Unit, modifier: Modifier = Modifier, onLiving: (String,String)->Unit = {_,_->}, onDiscovered:(List<String>)->Unit={}) {
    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }
    val seedGrid = rememberLazyGridState()
    val itemGrid = rememberLazyGridState()
    val activeGrid = if(state.filter == ShopFilter.ITEMS) itemGrid else seedGrid
    val visibleSpecies by remember(seedGrid) { derivedStateOf {
        seedGrid.layoutInfo.visibleItemsInfo.mapNotNull{it.key as? String}.filter{id->shopCatalog.any{it.id==id}}
    } }
    LaunchedEffect(state.filter,visibleSpecies) {
        if(state.filter==ShopFilter.SEEDS && visibleSpecies.isNotEmpty())onDiscovered(visibleSpecies)
    }
    MintBackdrop(modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            MintCompactHeader(state.wallet.coins,title=AppText.text(TextKey.CopyMamXanh692a0a))
            GardenScreenIntro(AppText.text(TextKey.CopyTiemNho87d6e3),AppText.text(TextKey.CopyThemMotChutXanh946fa7),modifier=Modifier.padding(horizontal=AppDimensions.Dp24,vertical=AppDimensions.Dp12))
            MintCatalogTabs(if(state.filter==ShopFilter.ITEMS)0 else 1,
                {onIntent(ShopIntent.SetFilter(if(it==0)ShopFilter.ITEMS else ShopFilter.SEEDS))},Modifier.padding(horizontal=AppDimensions.Dp24,vertical=AppDimensions.Dp8))
            if(state.isLoading) LinearProgressIndicator(Modifier.fillMaxWidth())
            BoxWithConstraints(Modifier.weight(1f)) {
                val fontScale=androidx.compose.ui.platform.LocalDensity.current.fontScale
                val columns=if(maxWidth>=AppDimensions.Dp600)3 else 2
                key(state.filter) {
                LazyVerticalGrid(GridCells.Fixed(columns),Modifier.fillMaxSize()
                    .mintScrollEdges(activeGrid.canScrollBackward,activeGrid.canScrollForward),state=activeGrid,
                    contentPadding=PaddingValues(horizontal=AppDimensions.Dp24,vertical=AppDimensions.Dp16),horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp14),
                    verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp14)) {
                    if(state.filter == ShopFilter.ITEMS) {
                        items(careProducts,key={it.id}) { product ->
                            val owned=(state.wallet.inventory[product.id] ?: 0)>0
                            ShopProductCard(product.name,
                                if(product.permanent && owned) if(state.wallet.equippedCan==product.id) AppText.text(TextKey.CopyDangDung275ca2) else AppText.text(TextKey.CopyDaSoHuu822625) else AppText.text(TextKey.Copy0Xu251e68,product.price),
                                {selectedId=product.id}) {
                                GardenArtwork(if(product.permanent)GardenArt.WATERING_CAN else GardenArt.FERTILIZER,it)
                            }
                        }
                        item(span={GridItemSpan(maxLineSpan)}) {
                            Text(AppText.text(TextKey.CopyBinhTuoiKeoDaiThoiGianChamSocKhongCanMuaND611f4),color=Muted,fontSize=AppTypography.Sp11)
                        }
                    } else {
                        items(shopCatalog,key={it.id}) { seed ->
                            ShopProductCard(seed.name,AppText.text(TextKey.Copy0Xu251e68,seed.price),
                                {selectedId=seed.id}) {PaintedGardenPlant(seed.id,it)}
                        }
                        item(span={GridItemSpan(maxLineSpan)}) {
                            Text(AppText.text(TextKey.CopyExclusiveSapRaMatChuaMoBan366438),color=Muted,fontSize=AppTypography.Sp11)
                        }
                    }
                    item(span={GridItemSpan(maxLineSpan)}) {GardenNavigationCard(AppText.text(TextKey.CopyTiemTrangTriFbd50e),{onLiving("SHOP","")})}
                    item(span={GridItemSpan(maxLineSpan)}) {GardenNavigationCard(AppText.text(TextKey.CopyBachThaoF2ed64),{onLiving("ENCYCLOPEDIA","")})}
                    item(span={GridItemSpan(maxLineSpan)}) {
                        GardenPaintedButton(AppText.text(TextKey.CopyGheVuonUomCa45c6),{onIntent(ShopIntent.OpenGarden)},Modifier.fillMaxWidth().padding(top=AppDimensions.Dp8),primary=false)
                    }
                }
                }
            }
        }
    }
    selectedId?.let { id ->
        if(state.outcome==null) PurchaseConfirmation(id,state,{selectedId=null},
            onEquip={onIntent(ShopIntent.Equip(id));selectedId=null}) { quantity,key ->onIntent(ShopIntent.Purchase(id,quantity,key))}
    }
    state.outcome?.let { outcome ->
        LaunchedEffect(outcome) { selectedId = null }
        MintFeedback(if(outcome.success) AppText.text(TextKey.CopyDaThem0VaoTuiFe68af,outcome.name) else outcome.message,
            {selectedId=null;onIntent(ShopIntent.DismissOutcome)})
    }
}

@Composable
private fun ShopProductCard(name:String,price:String,onClick:()->Unit,art: @Composable (Modifier) -> Unit) {
    MintCatalogCard(name,price,onClick,detail=com.mamxanh.shared.model.catalog.firstOrNull{it.name==name}?.tier?:"",artwork=art)
}
@Preview(widthDp=360,heightDp=640)
@Composable
fun ShopScreenPreview(){MamXanhPreviewTheme{ShopScreen(ShopUiState(ShopWallet(120,emptyMap()),false),{})}}
@Composable fun ShopExtendedDocumentPreview(){ShopScreenPreview()}
@Composable fun ShopSeedDetailDialogPreview(background: (@Composable () -> Unit)? = null){
    MamXanhPreviewTheme { background?.invoke(); PurchaseConfirmation(shopCatalog.first().id,ShopUiState(isLoading=false),{},onBuy={_,_->}) }
}
@Composable fun ShopSeedDetailLargeTextPreview(){ShopSeedDetailDialogPreview()}
@androidx.compose.ui.tooling.preview.Preview(widthDp=414,heightDp=1100,device="spec:width=414dp,height=1100dp,dpi=160")
@Composable fun ShopWebDesignPreview(){MamXanhPreviewTheme{ShopScreen(ShopUiState(ShopWallet(620,emptyMap()),false),{})}}
@androidx.compose.ui.tooling.preview.Preview(widthDp=414,heightDp=1100,device="spec:width=414dp,height=1100dp,dpi=160")
@Composable fun CareShopWebDesignPreview(){MamXanhPreviewTheme{ShopScreen(ShopUiState(ShopWallet(620,emptyMap()),false,filter=ShopFilter.ITEMS),{})}}
