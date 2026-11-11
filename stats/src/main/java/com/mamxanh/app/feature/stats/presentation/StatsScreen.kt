package com.mamxanh.app.feature.stats.presentation

import com.mamxanh.app.presentation.ui.components.GardenRichText as Text
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamxanh.app.presentation.ui.*
import com.mamxanh.app.presentation.ui.components.*
import java.util.Calendar

@Composable
fun StatsScreen(state: StatsUiState, onIntent: (StatsIntent) -> Unit, modifier: Modifier = Modifier) {
    val s = state.statistics
    val listState = rememberLazyListState()
    val firstUse = s.total == 0 && s.treeCount == 0
    val recent = s.recentDailyCompletions.takeLast(7).let { if (it.size == 7) it else List(7 - it.size) { 0 } + it }
    val days = (6 downTo 0).map { offset ->
        val day = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -offset) }.get(Calendar.DAY_OF_WEEK)
        if (day == Calendar.SUNDAY) "CN" else "T$day"
    }
    MintBackdrop(modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            MintCompactHeader(s.coins, title="Nhật ký", titleSize=18.sp)
            LazyColumn(Modifier.weight(1f).mintScrollEdges(listState.canScrollBackward, listState.canScrollForward),
                state = listState, contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    GardenBotanicalFrame(Modifier.fillMaxWidth()) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            JourneyCard("✓", "Việc đã xong", s.total.toString(), "", Modifier.weight(1f))
                            VerticalDivider(Modifier.height(54.dp))
                            JourneyCard("🌱", "Cây trong vườn", s.treeCount.toString(), "", Modifier.weight(1f))
                        }
                    }
                }
                item {
                    GardenBotanicalFrame(Modifier.fillMaxWidth(), contentPadding = PaddingValues(14.dp)) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Nhịp xanh 7 ngày", Modifier.weight(1f), color = Deep, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                IconButton(onClick = { onIntent(StatsIntent.Refresh) }, modifier = Modifier.semantics {
                                    stateDescription = if(state.isLoading) "Đang làm mới thống kê" else "Sẵn sàng làm mới"
                                    liveRegion = LiveRegionMode.Polite
                                }) { Icon(Icons.Rounded.Refresh, "Làm mới thống kê", tint = Green) }
                            }
                            Box {
                                WeekSproutChart(recent, days, Modifier.padding(top = 4.dp))
                                if(state.isLoading) LinearProgressIndicator(Modifier.fillMaxWidth().height(3.dp).clearAndSetSemantics {})
                            }
                        }
                    }
                }
                if(!firstUse) item {
                    GardenBotanicalFrame(Modifier.fillMaxWidth(), contentPadding = PaddingValues(14.dp)) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Tuần này", color = Deep, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            listOf("☑ ${s.week} việc hoàn thành", "🌱 ${s.today} việc hôm nay",
                                "📅 ${s.streak} ngày đều đặn", "🪙 ${s.coins} xu trong túi").forEachIndexed { index, text ->
                                if(index > 0) HorizontalDivider(color = Color(0xFFE0EAE5))
                                Row(Modifier.fillMaxWidth().heightIn(min = 36.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text(text, Modifier.weight(1f), color = Deep, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
                item {
                    if(firstUse) GardenBotanicalFrame(Modifier.fillMaxWidth(), skin = GardenPanelSkin.HINT) {
                        Text("🌱 Hãy bắt đầu chăm cây để ghi lại hành trình của bạn nhé!",
                            color = Deep, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    } else Text("Số liệu được lưu trên thiết bị", Modifier.fillMaxWidth(),
                        color = Muted, fontSize = 13.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }
        }
    }
}

@Composable
private fun JourneyCard(emoji: String, title: String, value: String, caption: String, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("$emoji $value", Modifier.fillMaxWidth(), color = Green, fontSize = 25.sp, fontWeight = FontWeight.Bold,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        Text(title, color = Deep, fontSize = 12.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        if (caption.isNotBlank()) Text(caption, color = Muted, fontSize = 11.sp)
    }
}

@Preview(name = "Painted garden journal", showBackground = true, widthDp = 360, heightDp = 800,
    device = "spec:width=360dp,height=800dp,dpi=160")
@Composable
fun StatsScreenPreview() {
    MamXanhPreviewTheme {
        StatsScreen(StatsUiState(com.mamxanh.app.feature.stats.domain.model.GardenStatistics(
            coins = 120, total = 32, today = 3, week = 12, streak = 5, treeCount = 3,
            recentDailyCompletions = listOf(1, 0, 2, 1, 3, 1, 3)
        ), false), {})
    }
}

@Preview(name = "Journal · extended document audit", widthDp = 360, heightDp = 900,
    device = "spec:width=360dp,height=900dp,dpi=160")
@Composable
fun StatsExtendedDocumentPreview() { StatsScreenPreview() }

@Preview(name = "Journal · refreshing", widthDp = 360, heightDp = 740,
    device = "spec:width=360dp,height=740dp,dpi=160")
@Composable
fun StatsRefreshingPreview() {
    MamXanhPreviewTheme {
        StatsScreen(StatsUiState(com.mamxanh.app.feature.stats.domain.model.GardenStatistics(
            coins = 120, total = 32, today = 3, week = 12, streak = 5, treeCount = 3,
            recentDailyCompletions = listOf(1, 0, 2, 1, 3, 1, 3)
        ), true), {})
    }
}

@Preview(name = "Journal · large values, extended", widthDp = 360, heightDp = 1040, fontScale = 1.3f,
    device = "spec:width=360dp,height=1040dp,dpi=160")
@Composable
fun StatsLargeValuesPreview() {
    MamXanhPreviewTheme {
        StatsScreen(StatsUiState(com.mamxanh.app.feature.stats.domain.model.GardenStatistics(
            coins = 1_234_567_890, total = 1_234_567_890, today = 12_345, week = 1_234_567,
            streak = 12_345, treeCount = 1_234_567_890,
            recentDailyCompletions = listOf(0, 1, 1_234, Int.MAX_VALUE, 12_345, 1, 3)
        ), false), {})
    }
}

@Preview(name = "Journal · first use", widthDp = 360, heightDp = 680,
    device = "spec:width=360dp,height=680dp,dpi=160")
@Composable
fun StatsZeroHistoryPreview() {
    MamXanhPreviewTheme {
        StatsScreen(StatsUiState(com.mamxanh.app.feature.stats.domain.model.GardenStatistics(
            coins = 60, total = 0, today = 0, week = 0, streak = 0, treeCount = 0,
            recentDailyCompletions = List(7) { 0 }
        ), false), {})
    }
}

@Preview(name = "Journal summary", showBackground = true)
@Composable
fun JourneyCardPreview() { MamXanhPreviewTheme { JourneyCard("", "Cây trong vườn", "3", "") } }
