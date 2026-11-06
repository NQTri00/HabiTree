package com.mamxanh.app.feature.shop.presentation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mamxanh.app.feature.shop.domain.model.ShopWallet
import com.mamxanh.app.feature.shop.domain.model.shopCatalog
import com.mamxanh.app.presentation.ui.*

@Composable
fun ShopScreen(wallet: ShopWallet, onBuySeed: (String) -> Unit, onBuyFertilizer: () -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(modifier.fillMaxSize().padding(horizontal = 18.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Gold), shape = RoundedCornerShape(26.dp)) {
                Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🏡", style = MaterialTheme.typography.displayMedium)
                    Column(Modifier.padding(start = 14.dp)) {
                        Text("Tiệm hạt giống nhỏ", color = Deep, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Tìm một người bạn xanh mới cho khu vườn.", color = Deep)
                        Text("🪙 " + wallet.coins + " xu trong túi", color = Deep, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
        item { ShopSection("Hạt giống", "Mỗi hạt giống sẽ trở thành một cây riêng.") }
        items(shopCatalog, key = { it.id }) { seed ->
            ShopItem(seed.emoji, seed.name, seed.description, seed.price,
                wallet.inventory[seed.id] ?: 0, state.stats.coins >= seed.price) { onBuySeed(seed.id) }
        }
        item { ShopSection("Chăm sóc cây", "Một chút trợ giúp khi cây cần lớn nhanh hơn.") }
        item {
            ShopItem("🧪", "Phân bón hữu cơ", "Giúp một cây tăng thêm 25 điểm phát triển.", 25,
                state.garden.inventory["fertilizer"] ?: 0, state.stats.coins >= 25, onBuyFertilizer)
        }
        item { Text("Cảm ơn bạn đã ghé tiệm. Chúc khu vườn có thêm nhiều mầm xanh! 🌱", color = Muted) }
    }
}

@Composable
private fun ShopSection(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(title, color = Deep, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(subtitle, color = Muted, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun ShopItem(emoji: String, name: String, description: String, price: Int,
    owned: Int, canAfford: Boolean, onBuy: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
        shape = RoundedCornerShape(20.dp)) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(emoji, style = MaterialTheme.typography.displaySmall)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(name, color = Deep, fontWeight = FontWeight.Bold)
                Text(description, color = Muted, style = MaterialTheme.typography.bodySmall)
                Text("Đang có: " + owned, color = Green, style = MaterialTheme.typography.labelSmall)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("🪙 " + price, color = Deep, fontWeight = FontWeight.Bold)
                Button(onClick = onBuy, enabled = canAfford) { Text("Mua") }
            }
        }
    }
}
