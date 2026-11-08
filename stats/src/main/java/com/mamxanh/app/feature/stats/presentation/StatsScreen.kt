package com.mamxanh.app.feature.stats.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.presentation.ui.*
import com.mamxanh.app.presentation.ui.components.GardenPageHeader
import com.mamxanh.app.presentation.ui.components.GardenScene
import com.mamxanh.app.presentation.ui.components.GardenSceneBackdrop
import com.mamxanh.app.presentation.ui.components.GardenBotanicalFrame
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun StatsScreen(state: StatsUiState, onIntent: (StatsIntent) -> Unit, modifier: Modifier = Modifier) {
    val s = state.statistics
    val recent = s.recentDailyCompletions.takeLast(7).let { values ->
        if (values.size == 7) values else List(7 - values.size) { 0 } + values
    }
    val days = (6 downTo 0).map { offset ->
        val day = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -offset) }.time
        SimpleDateFormat("EE", Locale.forLanguageTag("vi")).format(day).replaceFirstChar { it.uppercase() }
    }

    Box(modifier.fillMaxSize()) {
        GardenSceneBackdrop(GardenScene.GROWTH_JOURNAL, Modifier.fillMaxSize())
        LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            GardenPageHeader(
                emoji = "🌱",
                title = "Nhật ký khu vườn",
                subtitle = if (s.streak > 0) "Bạn đang giữ nhịp chăm cây " + s.streak + " ngày liên tiếp."
                    else "Những bước nhỏ của bạn đều được ghi nhớ."
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                JourneyCard("✅", "Việc đã xong", s.total.toString(), "Tổng cộng", Modifier.weight(1f))
                JourneyCard("🪴", "Cây trong vườn", s.treeCount.toString(), "Người bạn xanh", Modifier.weight(1f))
            }
        }
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(1.dp, Green.copy(alpha = 0.12f))
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("Hoạt động 7 ngày qua", color = Deep, fontWeight = FontWeight.Bold)
                            Text("Mỗi cột là số việc bạn hoàn thành trong ngày.", color = Muted, style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = { onIntent(StatsIntent.Refresh) }) {
                            Icon(Icons.Rounded.Refresh, contentDescription = "Làm mới thống kê", tint = Green)
                        }
                    }
                    if (state.isLoading && recent.all { it == 0 }) {
                        LinearProgressIndicator(Modifier.fillMaxWidth(), color = Green, trackColor = Pale)
                    } else {
                        Row(
                            Modifier.fillMaxWidth().height(100.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            recent.forEachIndexed { index, count ->
                                Column(
                                    Modifier.weight(1f).fillMaxHeight(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Bottom
                                ) {
                                    Text(count.toString(), color = Muted, style = MaterialTheme.typography.labelSmall)
                                    Spacer(Modifier.height(4.dp))
                                    val barHeight = if (count == 0) 4.dp else (52f * count / recent.max().coerceAtLeast(1)).dp
                                    Box(
                                        Modifier.width(20.dp).height(barHeight).clip(RoundedCornerShape(topStart = 7.dp, topEnd = 7.dp))
                                            .background(if (index == 6) Green else Color(0xFF9CC9A5))
                                    )
                                    Spacer(Modifier.height(5.dp))
                                    Text(days[index], color = if (index == 6) Deep else Muted, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }
        }
        item {
            GardenBotanicalFrame(Modifier.fillMaxWidth()) {
                Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Tuần này trong khu vườn", color = Deep, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🌿", style = MaterialTheme.typography.headlineMedium)
                        Text(s.week.toString() + " việc hoàn thành", color = Deep, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                        Text("☀️ " + s.today + " hôm nay", color = Deep)
                    }
                    HorizontalDivider(color = Green.copy(alpha = 0.12f))
                    Text("Bạn đang có " + s.coins + " xu để tiếp tục chăm sóc những người bạn xanh.", color = Muted, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        }
    }
}

@Composable
private fun JourneyCard(emoji: String, title: String, value: String, caption: String, modifier: Modifier = Modifier) {
    GardenBotanicalFrame(modifier) {
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(emoji, fontSize = 22.sp)
                Text(value, color = Green, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
            }
            Text(title, color = Deep, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            Text(caption, color = Muted, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Preview(name = "Garden journal · populated", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun StatsScreenPreview() {
    MamXanhPreviewTheme {
        StatsScreen(
            state = StatsUiState(
                statistics = com.mamxanh.app.feature.stats.domain.model.GardenStatistics(
                    coins = 48,
                    total = 32,
                    today = 3,
                    week = 12,
                    streak = 5,
                    treeCount = 3,
                    recentDailyCompletions = listOf(1, 0, 2, 1, 3, 1, 3)
                ),
                isLoading = false
            ),
            onIntent = {}
        )
    }
}

@Preview(name = "Journey summary card", showBackground = true)
@Composable
fun JourneyCardPreview() {
    MamXanhPreviewTheme {
        JourneyCard("🪴", "Cây trong vườn", "3", "Người bạn xanh")
    }
}
