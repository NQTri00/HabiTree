package com.mamxanh.app.feature.shop.presentation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mamxanh.app.feature.shop.domain.model.shopCatalog
import com.mamxanh.app.presentation.ui.components.*
import com.mamxanh.shared.model.Balance
@Composable
fun PurchaseConfirmation(id:String,state:ShopUiState,onDismiss:()->Unit,onBuy:(Int,String)->Unit) {
    val seed=shopCatalog.firstOrNull{it.id==id}
    val price=seed?.price ?: Balance.FERTILIZER
    var quantity by rememberSaveable(id){mutableIntStateOf(1)}
    val key=rememberSaveable(id){java.util.UUID.randomUUID().toString()}
    val total=price*quantity
    Dialog(onDismissRequest={if(!state.isPurchasing)onDismiss()}) {
        GardenBotanicalFrame(Modifier.fillMaxWidth().heightIn(max=640.dp)) {
            Column(Modifier.verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(12.dp)) {
                if(seed!=null)PaintedGardenPlant(id,Modifier.fillMaxWidth().height(120.dp))
                else GardenArtwork(GardenArt.FERTILIZER,Modifier.fillMaxWidth().height(100.dp))
                Text(seed?.name ?: "Phân hữu cơ",style=MaterialTheme.typography.titleLarge)
                Text(seed?.description ?: "Giúp cây lớn thêm, chỉ dùng khi cây còn có thể tăng trưởng.")
                Text("Đơn giá $price xu · Có ${state.wallet.inventory[id] ?: 0}")
                Text("Ước tính ${kotlin.math.ceil(((price-state.wallet.coins).coerceAtLeast(0)).toDouble()/state.averageEarnings).toInt()} ngày để đủ xu · Trung bình ${state.averageEarnings}/ngày (7 ngày; mặc định 40)")
                Row(horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick={quantity--},enabled=quantity>1 && !state.isPurchasing){Text("−")}
                    Text("$quantity")
                    OutlinedButton(onClick={quantity++},enabled=quantity<99 && !state.isPurchasing){Text("+")}
                }
                Text("Số dư ${state.wallet.coins} xu · Tổng $total xu")
                Text("Sau mua: ${state.wallet.coins-total} xu")
                if(state.wallet.coins<total)Text("Chưa đủ xu")
                GardenPaintedButton("Mua $quantity",{onBuy(quantity,key)},
                    enabled=state.wallet.coins>=total && !state.isPurchasing && !state.isLoading)
                GardenPaintedButton("Hủy",onDismiss,primary=false,enabled=!state.isPurchasing)
            }
        }
    }
}
