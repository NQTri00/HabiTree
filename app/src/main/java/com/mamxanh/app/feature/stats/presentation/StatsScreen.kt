package com.mamxanh.app.feature.stats.presentation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mamxanh.app.domain.model.State
import com.mamxanh.app.presentation.ui.*

@Composable
fun StatsScreen(state: State, modifier: Modifier = Modifier) {
    val s = state.stats
    LazyColumn(modifier.fillMaxSize().padding(horizontal = 18.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Pale), shape = RoundedCornerShape(26.dp)) {
                Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("📖", style = MaterialTheme.typography.displayMedium)
                    Column(Modifier.padding(start = 14.dp)) {
                        Text("Nhật ký khu vườn", color = Deep, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(if (s.streak > 0) "Bạn đã ghé thăm " + s.streak + " ngày liên tiếp. Thật đáng yêu!"
                            else "Mỗi ngày quay lại là thêm một trang mới.", color = Deep)
                        Text("Không cần vội, cây lớn theo nhịp riêng. 🍃", color = Green)
                    }
                }
            }
        }
        item { JourneyCard("🌱", "Những việc nhỏ đã hoàn thành", s.total.toString(),
            "Mỗi lần cố gắng đều được khu vườn ghi nhớ.") }
        item { JourneyCard("🌳", "Những người bạn xanh", s.treeCount.toString(),
            "Mỗi cây là một dấu mốc bạn đã gieo trồng.") }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Gold), shape = RoundedCornerShape(22.dp)) {
                Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Tuần này trong khu vườn", color = Deep, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🍃", style = MaterialTheme.typography.headlineMedium)
                        Text(s.week.toString() + " lần hoàn thành", color = Deep, fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f))
                        Text("☀️ " + s.today + " hôm nay", color = Deep)
                    }
                    Text("Bạn đang có " + s.coins + " xu để tiếp tục chăm sóc những người bạn xanh.",
                        color = Muted, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun JourneyCard(emoji: String, title: String, value: String, caption: String) {
    Card(colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
        shape = RoundedCornerShape(22.dp)) {
        Row(Modifier.fillMaxWidth().padding(17.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(emoji, style = MaterialTheme.typography.displaySmall)
            Column(Modifier.weight(1f).padding(start = 14.dp)) {
                Text(title, color = Deep, fontWeight = FontWeight.Bold)
                Text(caption, color = Muted, style = MaterialTheme.typography.bodySmall)
            }
            Text(value, color = Green, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
        }
    }
}
