package com.mamxanh.app.feature.shop.presentation

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
            MintCompactHeader(state.wallet.coins)
            Row(Modifier.fillMaxWidth().padding(horizontal=10.dp,vertical=3.dp),
                verticalAlignment=Alignment.CenterVertically) {
                MintCatalogTabs(if(state.filter == ShopFilter.ITEMS) 0 else 1,
                    {onIntent(ShopIntent.SetFilter(if(it==0)ShopFilter.ITEMS else ShopFilter.SEEDS))},
                    Modifier.weight(1f))
                IconButton({onLiving("SHOP","")}){Icon(Icons.Rounded.Palette,"Trang trí",Modifier.size(18.dp),tint=Green)}
                IconButton({onLiving("ENCYCLOPEDIA","")}){Icon(Icons.AutoMirrored.Rounded.MenuBook,"Bách thảo",Modifier.size(18.dp),tint=Green)}
            }
            if(state.isLoading) LinearProgressIndicator(Modifier.fillMaxWidth())
            BoxWithConstraints(Modifier.weight(1f)) {
                val fontScale=androidx.compose.ui.platform.LocalDensity.current.fontScale
                val columns=if(maxWidth<340.dp && fontScale>1.2f) 2 else if(maxWidth>=600.dp) (maxWidth.value/140).toInt() else 3
                key(state.filter) {
                LazyVerticalGrid(GridCells.Fixed(columns),Modifier.fillMaxSize()
                    .mintScrollEdges(activeGrid.canScrollBackward,activeGrid.canScrollForward),state=activeGrid,
                    contentPadding=PaddingValues(10.dp),horizontalArrangement=Arrangement.spacedBy(6.dp),
                    verticalArrangement=Arrangement.spacedBy(6.dp)) {
                    if(state.filter == ShopFilter.ITEMS) {
                        items(careProducts,key={it.id}) { product ->
                            val owned=(state.wallet.inventory[product.id] ?: 0)>0
                            ShopProductCard(product.name,
                                if(product.permanent && owned) if(state.wallet.equippedCan==product.id) "Đang dùng" else "Đã sở hữu" else "${product.price} xu",
                                {selectedId=product.id}) {
                                GardenArtwork(if(product.permanent)GardenArt.WATERING_CAN else GardenArt.FERTILIZER,it)
                            }
                        }
                        item(span={GridItemSpan(maxLineSpan)}) {
                            Text("Bình tưới kéo dài thời gian chăm sóc; không cần mua nước. Phân bón là tiện ích tùy chọn.",color=Muted,fontSize=11.sp)
                        }
                    } else {
                        items(shopCatalog,key={it.id}) { seed ->
                            ShopProductCard(seed.name,"${seed.price} xu",
                                {selectedId=seed.id}) {PaintedGardenPlant(seed.id,it)}
                        }
                        item(span={GridItemSpan(maxLineSpan)}) {
                            Text("Exclusive · Sắp ra mắt. Chưa mở bán.",color=Muted,fontSize=11.sp)
                        }
                    }
                    item(span={GridItemSpan(maxLineSpan)}) {
                        GardenPaintedButton("Ghé Vườn ươm",{onIntent(ShopIntent.OpenGarden)},Modifier.fillMaxWidth().padding(top=8.dp),primary=false)
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
        MintFeedback(if(outcome.success) "Đã thêm ${outcome.name} vào túi" else outcome.message,
            {selectedId=null;onIntent(ShopIntent.DismissOutcome)})
    }
}

@Composable
private fun ShopProductCard(name:String,price:String,onClick:()->Unit,art: @Composable (Modifier) -> Unit) {
    MintCatalogCard(name,price,onClick,art)
}
@Preview(widthDp=360,heightDp=640)
@Composable
fun ShopScreenPreview(){MamXanhPreviewTheme{ShopScreen(ShopUiState(ShopWallet(120,emptyMap()),false),{})}}
@Composable fun ShopExtendedDocumentPreview(){ShopScreenPreview()}
@Composable fun ShopSeedDetailDialogPreview(background: (@Composable () -> Unit)? = null){
    MamXanhPreviewTheme { background?.invoke(); PurchaseConfirmation(shopCatalog.first().id,ShopUiState(isLoading=false),{},onBuy={_,_->}) }
}
@Composable fun ShopSeedDetailLargeTextPreview(){ShopSeedDetailDialogPreview()}
