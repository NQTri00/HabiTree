package com.mamxanh.app.feature.stats.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.mamxanh.core.ui.R
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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
    val axisMax = ((recent.max().coerceAtLeast(3) + 2) / 3) * 3
    val days = (6 downTo 0).map { offset ->
        val day = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -offset) }.get(Calendar.DAY_OF_WEEK)
        if (day == Calendar.SUNDAY) "CN" else "T$day"
    }
    Box(modifier.fillMaxSize()) {
        GardenSceneBackdrop(GardenScene.GROWTH_JOURNAL, Modifier.fillMaxSize())
        GardenJournalPage(Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 6.dp)) {
            LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(18.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
                item {
                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text("Nhật ký khu vườn", color = Deep, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text("Mỗi ngày một chút, bạn đang lớn lên", color = Color(0xFF78552D), fontSize = 13.sp, fontStyle = FontStyle.Italic)
                    }
                }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        JourneyCard("", "Việc đã xong", s.total.toString(), "", Modifier.weight(1f))
                        VerticalDivider(Modifier.height(68.dp), color = Color(0xFFBB985B).copy(alpha = .4f))
                        JourneyCard("", "Cây trong vườn", s.treeCount.toString(), "", Modifier.weight(1f))
                    }
                    HorizontalDivider(color = Color(0xFFBC9659).copy(alpha = .4f))
                }
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        GardenBotanicalFrame(Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)) {
                            Text("Nhịp xanh 7 ngày", color = Deep, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                        IconButton(onClick = { onIntent(StatsIntent.Refresh) }) {
                            Box(Modifier.size(34.dp).background(Color(0xFFFFDF9D), RoundedCornerShape(50))
                                .border(1.5.dp, Color(0xFFC99654), RoundedCornerShape(50)), contentAlignment = Alignment.Center) {
                                Icon(Icons.Rounded.Refresh, "Làm mới thống kê", tint = Green)
                            }
                        }
                    }
                    if (state.isLoading) LinearProgressIndicator(Modifier.fillMaxWidth(), color = Green)
                    Row(Modifier.fillMaxWidth().height(176.dp), verticalAlignment = Alignment.Bottom) {
                        Column(Modifier.width(18.dp).fillMaxHeight().padding(top = 27.dp, bottom = 22.dp),
                            verticalArrangement = Arrangement.SpaceBetween) {
                            (3 downTo 0).forEach { tick -> Text((tick * axisMax / 3).toString(), color = Deep, fontSize = 12.sp, lineHeight = 14.sp) }
                        }
                        Row(Modifier.weight(1f).fillMaxHeight(), horizontalArrangement = Arrangement.spacedBy(7.dp), verticalAlignment = Alignment.Bottom) {
                        recent.forEachIndexed { index, count ->
                            Column(Modifier.weight(1f).fillMaxHeight().semantics { contentDescription = days[index] + ": " + count + " việc hoàn thành" },
                                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
                                Text(count.toString(), color = Deep, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                GardenArtwork(GardenArt.SPROUT, Modifier.width(26.dp).height(18.dp))
                                Box(Modifier.width(27.dp).height((if (count == 0) 2f else 105f * count / axisMax).dp)
                                    .clip(RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp))
                                    .background(Brush.horizontalGradient(listOf(Color(0xFF789C22), Color(0xFFA6BD45), Color(0xFF6B9022)))))
                                Spacer(Modifier.height(6.dp))
                                Text(days[index], color = Deep, fontSize = 11.sp)
                            }
                        }
                    }
                    }
                    HorizontalDivider(color = Color(0xFF8B6A39))
                }
                item {
                    GardenBotanicalFrame(Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)) {
                        Text("Tuần này", Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            color = Deep, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(14.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("🌱 ${s.week} việc hoàn thành", Modifier.weight(1f), color = Deep, fontSize = 12.5.sp)
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
                    Text("Những bước nhỏ đều được ghi nhớ", Modifier.fillMaxWidth().padding(top = 14.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center, color = Color(0xFF78552D), fontSize = 13.sp, fontStyle = FontStyle.Italic)
                }
            }
        }
    }
}

@Composable
private fun JourneyCard(emoji: String, title: String, value: String, caption: String, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = Deep, fontSize = 40.sp, fontWeight = FontWeight.Bold)
        Text(title, color = Deep, fontSize = 15.sp)
        if (caption.isNotBlank()) Text(caption, color = Muted, fontSize = 11.sp)
    }
}

@Preview(name = "Painted garden journal", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun StatsScreenPreview() {
    MamXanhPreviewTheme {
        StatsScreen(StatsUiState(com.mamxanh.app.feature.stats.domain.model.GardenStatistics(
            coins = 120, total = 32, today = 3, week = 12, streak = 5, treeCount = 3,
            recentDailyCompletions = listOf(1, 0, 2, 1, 3, 1, 3)
        ), false), {})
    }
}

@Preview(name = "Journal summary", showBackground = true)
@Composable
fun JourneyCardPreview() { MamXanhPreviewTheme { JourneyCard("", "Cây trong vườn", "3", "") } }
