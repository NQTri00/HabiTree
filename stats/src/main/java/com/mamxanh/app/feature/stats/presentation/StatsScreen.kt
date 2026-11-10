package com.mamxanh.app.feature.stats.presentation

import com.mamxanh.app.presentation.ui.components.GardenRichText as Text
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
    val recent = s.recentDailyCompletions.takeLast(7).let { if (it.size == 7) it else List(7 - it.size) { 0 } + it }
    val days = (6 downTo 0).map { offset ->
        val day = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -offset) }.get(Calendar.DAY_OF_WEEK)
        if (day == Calendar.SUNDAY) "CN" else "T$day"
    }
    Box(modifier.fillMaxSize()) {
        GardenSceneBackdrop(GardenScene.GROWTH_JOURNAL, Modifier.fillMaxSize())
        GardenJournalPage(Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 6.dp)) {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("Nhật ký khu vườn", color = Deep, fontSize = 24.sp, fontWeight = FontWeight.Bold,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                            GardenSprig(Modifier.align(Alignment.CenterStart).offset(x = (-14).dp).size(22.dp, 34.dp))
                            GardenSprig(Modifier.align(Alignment.CenterEnd).offset(x = 14.dp).size(22.dp, 34.dp), mirrored = true)
                        }
                        Text("Mỗi ngày một chút, bạn đang lớn lên", color = Color(0xFF78552D), fontSize = 13.sp, fontStyle = FontStyle.Italic, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    }
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(18.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
                item {
                    Box(Modifier.fillMaxWidth()) {
                        Row(Modifier.fillMaxWidth().padding(end = 12.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                            JourneyCard("", "Việc đã xong", s.total.toString(), "", Modifier.weight(1f))
                            GardenSprig(Modifier.size(24.dp, 68.dp))
                            JourneyCard("", "Cây trong vườn", s.treeCount.toString(), "", Modifier.weight(1f))
                        }
                        PressedGinkgo(Modifier.align(Alignment.TopEnd).offset(x = 16.dp, y = (-8).dp).size(44.dp, 72.dp))
                    }
                    HorizontalDivider(color = Color(0xFFBC9659).copy(alpha = .4f))
                }
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        GardenSprig(Modifier.size(22.dp, 34.dp))
                        GardenBotanicalFrame(Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)) {
                            Text("Nhịp xanh 7 ngày", Modifier.fillMaxWidth(), color = Deep, fontWeight = FontWeight.Bold, fontSize = 18.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }
                        IconButton(onClick = { onIntent(StatsIntent.Refresh) }, modifier = Modifier.semantics {
                            stateDescription = if (state.isLoading) "Đang làm mới thống kê" else "Sẵn sàng làm mới"
                            liveRegion = LiveRegionMode.Polite
                        }) {
                            Box(Modifier.size(34.dp).background(Color(0xFFFFDF9D), RoundedCornerShape(50))
                                .border(1.5.dp, Color(0xFFC99654), RoundedCornerShape(50)), contentAlignment = Alignment.Center) {
                                Icon(Icons.Rounded.Refresh, "Làm mới thống kê", tint = Green)
                            }
                        }
                    }
                    Box(Modifier.fillMaxWidth()) {
                        WeekSproutChart(recent, days, Modifier.padding(top = 8.dp))
                        if (state.isLoading) LinearProgressIndicator(
                            Modifier.fillMaxWidth().height(4.dp).align(Alignment.TopCenter).clearAndSetSemantics {},
                            color = Green, trackColor = Color(0xFFEAD8AD))
                    }
                    HorizontalDivider(color = Color(0xFF8B6A39))
                }
                item {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                        GardenSprig(Modifier.size(24.dp, 34.dp))
                        GardenBotanicalFrame(Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)) {
                            Text("Tuần này", Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                color = Deep, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        }
                        GardenArtwork(GardenArt.FLOWERS, Modifier.size(32.dp, 38.dp))
                    }
                    Spacer(Modifier.height(14.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("☑ ${s.week} việc hoàn thành", Modifier.weight(1f), color = Deep, fontSize = 12.5.sp)
                        Text("🌱 ${s.today} việc hôm nay", Modifier.weight(1f), color = Deep, fontSize = 12.5.sp)
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("📅 ${s.streak} ngày đều đặn", Modifier.weight(1f), color = Deep, fontSize = 12.5.sp)
                        Text("🪙 ${s.coins} xu trong túi", Modifier.weight(1f), color = Deep, fontSize = 12.5.sp)
                    }
                }
                item {
                    HorizontalDivider(color = Color(0xFFBC9659).copy(alpha = .4f))
                    Text(if (s.total == 0 && s.treeCount == 0) "Hãy bắt đầu chăm cây để ghi lại hành trình của bạn nhé!"
                        else "Số liệu được lưu trên thiết bị", Modifier.fillMaxWidth().padding(top = 14.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center, color = Color(0xFF78552D), fontSize = 13.sp, fontStyle = FontStyle.Italic)
                }
            }
            }
        }
        GardenArtwork(GardenArt.SPROUT, Modifier.align(Alignment.TopCenter).padding(top = 16.dp).size(26.dp))
    }
}

@Composable
private fun JourneyCard(emoji: String, title: String, value: String, caption: String, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, Modifier.fillMaxWidth(), color = Deep, fontSize = 40.sp, fontWeight = FontWeight.Bold,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        Text(title, color = Deep, fontSize = 15.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
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

@Preview(name = "Journal · extended document audit", widthDp = 360, heightDp = 740,
    device = "spec:width=360dp,height=740dp,dpi=160")
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

@Preview(name = "Journal summary", showBackground = true)
@Composable
fun JourneyCardPreview() { MamXanhPreviewTheme { JourneyCard("", "Cây trong vườn", "3", "") } }
