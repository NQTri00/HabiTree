package com.mamxanh.app

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

private val Leaf = Color(0xFF397A50)
private val PaleGreen = Color(0xFFEAF4E8)
private val Cream = Color(0xFFFAF8F0)
private val Ink = Color(0xFF26352A)

private enum class Tab(val label: String) { QUESTS("Nhiệm vụ"), GARDEN("Khu vườn"), STORE("Cửa hàng") }
private enum class Category(val label: String, val emoji: String) {
    STUDY("Học tập & Tập trung", "📚"), HEALTH("Sức khỏe & Vận động", "💚"), LIFE("Vệ sinh & Nếp sống", "🌿")
}
private data class Quest(val id: String, val title: String, val category: Category, val reward: Int)
private data class UiState(
    val quests: List<Quest>,
    val completed: Set<String>,
    val coins: Int,
    val earnedToday: Int,
    val dailyClearClaimed: Boolean
) {
    val progress get() = if (quests.isEmpty()) 0f else completed.size.toFloat() / quests.size
    val allDone get() = quests.isNotEmpty() && completed.size == quests.size
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("mam_xanh_local", MODE_PRIVATE)
        setContent {
            val vm: MamXanhViewModel = viewModel(factory = MamXanhViewModel.factory(prefs))
            MamXanhApp(vm)
        }
    }
}

private class MamXanhViewModel(private val prefs: SharedPreferences) : ViewModel() {
    private val today = LocalDate.now().toString()
    private val questList = listOf(
        Quest("vocab", "Học 10 từ vựng mới", Category.STUDY, 8),
        Quest("reading", "Đọc sách 20 phút", Category.STUDY, 10),
        Quest("water", "Uống đủ nước hôm nay", Category.HEALTH, 5),
        Quest("fruit", "Ăn thêm rau hoặc trái cây", Category.HEALTH, 5),
        Quest("bed", "Dọn giường và gấp chăn", Category.LIFE, 5),
        Quest("phone", "Không dùng điện thoại 30 phút trước khi ngủ", Category.LIFE, 10)
    )
    private val _state = MutableStateFlow(load())
    val state = _state.asStateFlow()

    private fun load(): UiState {
        if (prefs.getString("date", null) != today) {
            prefs.edit().putString("date", today).putStringSet("completed", emptySet())
                .putInt("earned", 0).putBoolean("clear", false).apply()
        }
        return UiState(
            questList,
            prefs.getStringSet("completed", emptySet()).orEmpty().toSet(),
            prefs.getInt("coins", 0),
            prefs.getInt("earned", 0),
            prefs.getBoolean("clear", false)
        )
    }

    fun complete(id: String) {
        val old = _state.value
        val quest = old.quests.firstOrNull { it.id == id } ?: return
        if (id in old.completed || old.earnedToday >= DAILY_CAP) return
        val reward = minOf(quest.reward, DAILY_CAP - old.earnedToday)
        save(old.copy(completed = old.completed + id, coins = old.coins + reward, earnedToday = old.earnedToday + reward))
    }

    fun claimDailyClear() {
        val old = _state.value
        if (!old.allDone || old.dailyClearClaimed) return
        val reward = minOf(20, DAILY_CAP - old.earnedToday)
        save(old.copy(coins = old.coins + reward, earnedToday = old.earnedToday + reward, dailyClearClaimed = true))
    }

    private fun save(value: UiState) {
        prefs.edit().putString("date", today).putStringSet("completed", value.completed)
            .putInt("coins", value.coins).putInt("earned", value.earnedToday)
            .putBoolean("clear", value.dailyClearClaimed).apply()
        _state.value = value
    }

    companion object {
        private const val DAILY_CAP = 150
        fun factory(prefs: SharedPreferences) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(MamXanhViewModel::class.java)) return MamXanhViewModel(prefs) as T
                error("Unknown ViewModel: ${modelClass.name}")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MamXanhApp(vm: MamXanhViewModel) {
    val state by vm.state.collectAsState()
    var tab by remember { mutableStateOf(Tab.QUESTS) }
    MaterialTheme {
        Scaffold(
            containerColor = Cream,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("Mầm Xanh", fontWeight = FontWeight.Bold, color = Ink)
                            Text("Mỗi ngày một chút, khu vườn thêm xanh", style = MaterialTheme.typography.labelSmall, color = Leaf)
                        }
                    },
                    actions = {
                        Surface(color = Color(0xFFFFF0C9), shape = CircleShape, modifier = Modifier.padding(end = 14.dp)) {
                            Text("🪙 ${state.coins}", modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), fontWeight = FontWeight.Bold)
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar(containerColor = Color.White) {
                    Tab.entries.forEach { item ->
                        val icon = when (item) {
                            Tab.QUESTS -> Icons.Rounded.Check
                            Tab.GARDEN -> Icons.Rounded.Eco
                            Tab.STORE -> Icons.Rounded.Storefront
                        }
                        NavigationBarItem(
                            selected = tab == item, onClick = { tab = item },
                            icon = { Icon(icon, contentDescription = item.label) }, label = { Text(item.label) }
                        )
                    }
                }
            }
        ) { padding ->
            when (tab) {
                Tab.QUESTS -> QuestScreen(state, vm::complete, vm::claimDailyClear, Modifier.padding(padding))
                Tab.GARDEN -> GardenScreen(Modifier.padding(padding))
                Tab.STORE -> StoreScreen(Modifier.padding(padding))
            }
        }
    }
}

@Composable
private fun QuestScreen(state: UiState, onComplete: (String) -> Unit, onClaim: () -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(Modifier.height(4.dp))
            Card(colors = CardDefaults.cardColors(containerColor = PaleGreen), shape = RoundedCornerShape(22.dp)) {
                Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Hôm nay, bạn muốn vun trồng điều gì?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Ink)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("${state.completed.size}/${state.quests.size} nhiệm vụ", color = Leaf, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.weight(1f))
                        Text("${(state.progress * 100).toInt()}%", color = Leaf, fontWeight = FontWeight.Bold)
                    }
                    LinearProgressIndicator(progress = { state.progress }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape), color = Leaf, trackColor = Color.White)
                    Text("Hoàn thành nhiệm vụ để nhận xu chăm sóc khu vườn nhé.", style = MaterialTheme.typography.bodySmall, color = Ink)
                }
            }
        }
        Category.entries.forEach { category ->
            item { Text("${category.emoji}  ${category.label}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Ink) }
            items(state.quests.filter { it.category == category }, key = { it.id }) { quest ->
                val done = quest.id in state.completed
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(18.dp)) {
                    Row(
                        Modifier.fillMaxWidth().padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(Modifier.size(38.dp).clip(CircleShape).background(if (done) PaleGreen else Cream), contentAlignment = Alignment.Center) {
                            if (done) Icon(Icons.Rounded.Check, contentDescription = "Đã xong", tint = Leaf) else Text("🌱")
                        }
                        Column(Modifier.weight(1f)) {
                            Text(quest.title, color = Ink, fontWeight = FontWeight.Medium)
                            Text("+${quest.reward} xu", color = Leaf, style = MaterialTheme.typography.labelMedium)
                        }
                        if (done) Text("Đã xong", color = Leaf, fontWeight = FontWeight.Bold)
                        else Button(onClick = { onComplete(quest.id) }, enabled = state.earnedToday < 150) { Text("Xong") }
                    }
                }
            }
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF5DC)), shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("🌟 Daily Clear", fontWeight = FontWeight.Bold, color = Ink)
                    Text("Hoàn thành tất cả nhiệm vụ để nhận thêm 20 xu.", color = Ink)
                    Button(onClick = onClaim, enabled = state.allDone && !state.dailyClearClaimed) {
                        Text(if (state.dailyClearClaimed) "Đã nhận thưởng" else "Nhận thưởng ngày")
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun GardenScreen(modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Khu vườn của bạn", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Ink)
        Card(colors = CardDefaults.cardColors(containerColor = PaleGreen), shape = RoundedCornerShape(26.dp)) {
            Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("🌱", style = MaterialTheme.typography.displayLarge)
                Text("Một khu vườn đang chờ bạn", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Ink)
                Text("Chọn hạt giống đầu tiên, chăm sóc cây và ngắm khu vườn thay đổi theo thời gian.", color = Ink)
            }
        }
        Text("Ba cây khởi đầu", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Ink)
        TreeCard("🌸", "Cây đào", "Hoa hồng rực rỡ khi vào mùa xuân")
        TreeCard("🍊", "Cây quýt", "Quả chín chuyển màu theo mùa")
        TreeCard("🌲", "Thông bonsai", "Dáng cây xanh quanh năm")
    }
}

@Composable
private fun TreeCard(emoji: String, title: String, subtitle: String) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(emoji, style = MaterialTheme.typography.headlineMedium)
            Column(Modifier.weight(1f).padding(start = 12.dp)) {
                Text(title, fontWeight = FontWeight.Bold, color = Ink)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Text("Miễn phí", color = Leaf, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun StoreScreen(modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Cửa hàng khu vườn", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Ink)
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF0C9)), shape = RoundedCornerShape(20.dp)) {
            Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("🪙", style = MaterialTheme.typography.headlineMedium)
                Column(Modifier.padding(start = 12.dp)) {
                    Text("Ví xu của bạn", fontWeight = FontWeight.Bold, color = Ink)
                    Text("Dùng xu để mở rộng khu vườn", color = Ink, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        Text("Sắp mở bán", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Ink)
        Text("Hạt giống, bình tưới, phân bón, chậu cây và đồ trang trí sẽ xuất hiện tại đây.", color = Ink)
    }
}