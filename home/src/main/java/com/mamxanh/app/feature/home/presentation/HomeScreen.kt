package com.mamxanh.app.feature.home.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mamxanh.app.feature.home.domain.model.HomeDashboard
import com.mamxanh.app.feature.home.domain.model.HomeTree
import com.mamxanh.app.feature.home.domain.model.homeSpeciesCatalog
import com.mamxanh.app.presentation.ui.Deep
import com.mamxanh.app.presentation.ui.Green
import com.mamxanh.app.presentation.ui.Muted
import com.mamxanh.app.presentation.ui.Pale
import com.mamxanh.app.presentation.ui.components.TreeIllustration

private val Soil = Color(0xFFC98C4E)
private val SoilLight = Color(0xFFE4B66F)
private val Sky = Color(0xFFD9F0F3)

@Composable
fun HomeScreen(
    dashboard: HomeDashboard,
    onOpenTree: (Long) -> Unit,
    onOpenTasks: () -> Unit,
    onOpenGarden: () -> Unit,
    modifier: Modifier = Modifier
) {
    val mainTree = dashboard.trees.firstOrNull()
    val completed = dashboard.completedTasks
    val total = dashboard.totalTasks

    Box(modifier.fillMaxSize()) {
        PastelFarmBackdrop(Modifier.fillMaxSize())
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("KHU VƯỜN CỦA BẠN", color = Green, style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold)
                        Text("Mỗi ngày một mầm xanh", color = Deep, style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold)
                    }
                    Surface(color = Color(0xFFFFE5A6), shape = RoundedCornerShape(50)) {
                        Text("🪙  ${dashboard.coins}", color = Color(0xFF65471D),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
            item {
                if (mainTree == null) {
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFF4F5E7).copy(alpha = .94f)),
                        shape = RoundedCornerShape(28.dp)) {
                        Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("🌱", style = MaterialTheme.typography.displayLarge)
                            Text("Mảnh đất đang chờ bạn gieo hạt", color = Deep, fontWeight = FontWeight.ExtraBold)
                            Text("Chọn một hạt giống ở cửa hàng rồi gieo xuống khu vườn nhé.", color = Muted)
                            Button(onClick = onOpenGarden) { Text("Đến khu vườn") }
                        }
                    }
                } else {
                    GardenIslandCard(mainTree, onOpen = { onOpenTree(mainTree.id) })
                }
            }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9E9).copy(alpha = .96f)),
                    shape = RoundedCornerShape(24.dp)) {
                    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🌼", style = MaterialTheme.typography.titleLarge)
                            Column(Modifier.weight(1f).padding(start = 9.dp)) {
                                Text("Việc tốt hôm nay", color = Deep, fontWeight = FontWeight.ExtraBold)
                                Text("$completed / $total nhiệm vụ đã hoàn thành", color = Muted,
                                    style = MaterialTheme.typography.bodySmall)
                            }
                            Text(if (total == 0) "0%" else "${completed * 100 / total}%",
                                color = Green, fontWeight = FontWeight.ExtraBold)
                        }
                        LinearProgressIndicator(
                            progress = { if (total == 0) 0f else completed.toFloat() / total },
                            modifier = Modifier.fillMaxWidth().height(10.dp),
                            color = Color(0xFF64B77A), trackColor = Color(0xFFE6E8D8)
                        )
                        Button(onClick = onOpenTasks, modifier = Modifier.align(Alignment.End),
                            colors = ButtonDefaults.buttonColors(containerColor = Green)) {
                            Text("Hoàn thành nhiệm vụ  →")
                        }
                    }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ResourceTile("🌳", "Cây đang trồng", dashboard.trees.size.toString(), Modifier.weight(1f))
                    ResourceTile("🌰", "Hạt giống", dashboard.seedCount.toString(), Modifier.weight(1f))
                    ResourceTile("✨", "Điểm chăm sóc", dashboard.trees.sumOf { it.growth }.toString(), Modifier.weight(1f))
                }
            }
            if (dashboard.trees.size > 1) {
                item { Text("Những người bạn khác", color = Deep, fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.titleLarge) }
                items(dashboard.trees.drop(1).size) { index ->
                    val tree = dashboard.trees[index + 1]
                    val species = homeSpeciesCatalog.firstOrNull { it.id == tree.speciesId } ?: homeSpeciesCatalog.first()
                    Card(Modifier.fillMaxWidth().clickable { onOpenTree(tree.id) },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F6E9).copy(alpha = .96f)),
                        shape = RoundedCornerShape(20.dp)) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(species.emoji, style = MaterialTheme.typography.headlineMedium)
                            Column(Modifier.weight(1f).padding(start = 12.dp)) {
                                Text(tree.name, color = Deep, fontWeight = FontWeight.Bold)
                                Text("${tree.growth.coerceAtMost(120)} / 120 điểm lớn lên", color = Muted,
                                    style = MaterialTheme.typography.labelSmall)
                            }
                            Text("Chăm cây  ›", color = Green, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GardenIslandCard(tree: HomeTree, onOpen: () -> Unit) {
    val species = homeSpeciesCatalog.firstOrNull { it.id == tree.speciesId } ?: homeSpeciesCatalog.first()
    val progress = (tree.growth / 120f).coerceIn(0f, 1f)
    val stage = when {
        progress >= 1f -> "Cây trưởng thành"
        progress >= .5f -> "Cây non khỏe mạnh"
        progress >= .1f -> "Mầm cây đang lớn"
        else -> "Hạt giống vừa gieo"
    }
    Card(Modifier.fillMaxWidth().clickable(onClick = onOpen),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF3E9).copy(alpha = .94f)),
        shape = RoundedCornerShape(30.dp)) {
        Column(Modifier.fillMaxWidth().padding(13.dp), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(tree.name, color = Deep, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                    Text("${species.emoji} ${species.name} · $stage", color = Green, fontWeight = FontWeight.SemiBold)
                }
                Surface(color = Color(0xFFFFE5A6), shape = RoundedCornerShape(50)) {
                    Text("CHĂM CÂY  ↗", color = Deep, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold)
                }
            }
            Box(Modifier.fillMaxWidth().height(258.dp)) {
                Canvas(Modifier.fillMaxSize()) {
                    val w = size.width; val h = size.height
                    drawOval(Color(0xFFD6E9D1), androidx.compose.ui.geometry.Offset(w*.04f,h*.62f),
                        androidx.compose.ui.geometry.Size(w*.92f,h*.30f))
                    drawOval(Soil, androidx.compose.ui.geometry.Offset(w*.12f,h*.48f),
                        androidx.compose.ui.geometry.Size(w*.76f,h*.39f))
                    drawOval(SoilLight, androidx.compose.ui.geometry.Offset(w*.19f,h*.53f),
                        androidx.compose.ui.geometry.Size(w*.62f,h*.12f))
                    val flowers = listOf(.15f to .65f,.25f to .78f,.77f to .66f,.86f to .76f,.67f to .84f)
                    flowers.forEachIndexed { i, p ->
                        val center = androidx.compose.ui.geometry.Offset(w*p.first,h*p.second)
                        drawCircle(Color(0xFFFFFCED), w*.014f, center)
                        drawCircle(Color(0xFFFFD36B), w*.006f, center)
                    }
                    drawCircle(Color(0xFFFFD98B), w*.035f, androidx.compose.ui.geometry.Offset(w*.84f,h*.16f))
                    drawPath(Path().apply {
                        moveTo(0f,h*.94f); cubicTo(w*.25f,h*.82f,w*.55f,h*.98f,w,h*.84f)
                        lineTo(w,h); lineTo(0f,h); close()
                    }, Color(0xFFB9D9C4).copy(alpha=.55f))
                }
                TreeIllustration(tree.growth, tree.speciesId, tree.decoration,
                    Modifier.align(Alignment.TopCenter).fillMaxHeight(.94f).fillMaxWidth(.76f))
                Surface(color = Color(0xFFFFF7DD), shape = RoundedCornerShape(50),
                    modifier = Modifier.align(Alignment.BottomStart).padding(start = 8.dp, bottom = 4.dp)) {
                    Text("🌱 ${tree.growth.coerceAtMost(120)} / 120", color = Deep,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp), fontWeight = FontWeight.Bold)
                }
                Surface(color = Color(0xFFFFF7DD), shape = RoundedCornerShape(50),
                    modifier = Modifier.align(Alignment.BottomEnd).padding(end = 8.dp, bottom = 4.dp)) {
                    Text("✨ ${(progress*100).toInt()}%", color = Deep,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp), fontWeight = FontWeight.Bold)
                }
            }
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(9.dp),
                color = Color(0xFF64B77A), trackColor = Color.White)
            Text("Chạm vào hòn đảo để tưới nước, bón phân và trang trí cây", color = Deep,
                style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun ResourceTile(emoji: String, label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier, colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9E9).copy(alpha = .96f)),
        shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, style = MaterialTheme.typography.titleLarge)
            Text(value, color = Deep, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleLarge)
            Text(label, color = Muted, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun PastelFarmBackdrop(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width; val h = size.height
        drawRect(Sky)
        drawCircle(Color(0xFFFFF1C8), w*.10f, androidx.compose.ui.geometry.Offset(w*.82f,h*.08f))
        drawPath(Path().apply {
            moveTo(0f,h*.18f); cubicTo(w*.24f,h*.04f,w*.44f,h*.18f,w*.65f,h*.12f)
            cubicTo(w*.83f,h*.03f,w*.94f,h*.11f,w,h*.15f); lineTo(w,h*.43f); lineTo(0f,h*.43f); close()
        }, Color(0xFFB9D5DF))
        drawPath(Path().apply {
            moveTo(0f,h*.28f); cubicTo(w*.22f,h*.16f,w*.47f,h*.28f,w*.66f,h*.22f)
            cubicTo(w*.82f,h*.17f,w*.92f,h*.23f,w,h*.25f); lineTo(w,h); lineTo(0f,h); close()
        }, Color(0xFFB8DCCB))
        // Distant rounded tree crowns and trunks.
        for (i in 0..5) {
            val x = w*(.05f+i*.19f)
            val y = h*(.25f+(i%2)*.025f)
            drawRoundRect(Color(0xFF9CC7B2), androidx.compose.ui.geometry.Offset(x-w*.025f,y),
                androidx.compose.ui.geometry.Size(w*.05f,h*.13f), androidx.compose.ui.geometry.CornerRadius(w*.02f))
            drawCircle(if(i%2==0) Color(0xFF9CCDB8) else Color(0xFF8DBDAA), w*.09f,
                androidx.compose.ui.geometry.Offset(x,y))
            drawCircle(Color(0xFFB8DCCB), w*.065f, androidx.compose.ui.geometry.Offset(x-w*.035f,y-h*.035f))
        }
        // Meadow flower speckles.
        for (i in 0..34) {
            val x = w*((i*37%97)/100f)
            val y = h*(.43f+(i*19%52)/100f)
            drawCircle(Color(0xFFFFFCF0), w*.009f, androidx.compose.ui.geometry.Offset(x,y))
            drawCircle(Color(0xFFFFD77D), w*.0035f, androidx.compose.ui.geometry.Offset(x,y))
        }
    }
}
