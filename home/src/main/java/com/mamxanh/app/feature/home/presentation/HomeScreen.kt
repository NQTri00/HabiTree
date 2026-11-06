package com.mamxanh.app.feature.home.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mamxanh.app.feature.home.domain.model.HomeDashboard
import com.mamxanh.app.feature.home.domain.model.HomeTree
import com.mamxanh.app.feature.home.domain.model.homeSpeciesCatalog
import com.mamxanh.app.feature.tree.presentation.components.TreeIllustration
import com.mamxanh.app.presentation.ui.Deep
import com.mamxanh.app.presentation.ui.Green
import com.mamxanh.app.presentation.ui.Muted
import com.mamxanh.app.presentation.ui.Pale

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
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(horizontal = 18.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Khu vườn của bạn", style = MaterialTheme.typography.headlineSmall, color = Deep, fontWeight = FontWeight.ExtraBold)
                Text("Chăm chút từng ngày, để mầm xanh lớn lên.", color = Muted)
            }
        }
        item {
            if (mainTree == null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Pale),
                    shape = RoundedCornerShape(26.dp)
                ) {
                    Column(
                        Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("🌱", style = MaterialTheme.typography.displayLarge)
                        Text("Khu vườn đang chờ mầm xanh đầu tiên", color = Deep, fontWeight = FontWeight.Bold)
                        Text("Mua hạt giống trong cửa hàng, sau đó gieo cây trong khu vườn.", color = Deep)
                        Button(onClick = onOpenGarden) { Text("Đến khu vườn") }
                    }
                }
            } else {
                MainTreeCard(tree = mainTree, onClick = { onOpenTree(mainTree.id) })
            }
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(22.dp)) {
                Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("Hôm nay", color = Deep, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                            Text("$completed / $total nhiệm vụ hoàn thành", color = Muted, style = MaterialTheme.typography.bodySmall)
                        }
                        Text(if (total == 0) "0%" else "${(completed * 100 / total)}%", color = Green, fontWeight = FontWeight.ExtraBold)
                    }
                    LinearProgressIndicator(
                        progress = { if (total == 0) 0f else completed.toFloat() / total },
                        modifier = Modifier.fillMaxWidth().height(9.dp),
                        color = Green,
                        trackColor = Pale
                    )
                    TextButton(onClick = onOpenTasks, modifier = Modifier.align(Alignment.End)) { Text("Xem nhiệm vụ") }
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HomeMetric("🌳", "Cây", state.garden.trees.size.toString(), Modifier.weight(1f))
                HomeMetric("🌰", "Hạt giống", dashboard.seedCount.toString(), Modifier.weight(1f))
                HomeMetric("🪙", "Xu", dashboard.coins.toString(), Modifier.weight(1f))
            }
        }
        item {
            Text("Cây trong khu vườn", color = Deep, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text("Chọn cây để chăm sóc và xem chi tiết.", color = Muted, style = MaterialTheme.typography.bodySmall)
            state.garden.trees.drop(1).forEach { tree ->
                val species = homeSpeciesCatalog.firstOrNull { it.id == tree.speciesId } ?: catalog.first()
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp).clickable { onOpenTree(tree.id) },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(species.emoji, style = MaterialTheme.typography.headlineMedium)
                        Column(Modifier.weight(1f).padding(start = 12.dp)) {
                            Text(tree.name, color = Deep, fontWeight = FontWeight.Bold)
                            Text("${tree.growth.coerceAtMost(120)} / 120 điểm phát triển", color = Muted, style = MaterialTheme.typography.labelSmall)
                        }
                        Text("›", color = Green, style = MaterialTheme.typography.headlineMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun MainTreeCard(tree: HomeTree, onClick: () -> Unit) {
    val species = catalog.firstOrNull { it.id == tree.speciesId } ?: catalog.first()
    val progress = (tree.growth / 120f).coerceIn(0f, 1f)
    val stage = when {
        tree.growth >= 120 -> "Trưởng thành"
        tree.growth >= 50 -> "Cây non"
        tree.growth >= 10 -> "Cây con"
        tree.growth > 0 -> "Nảy mầm"
        else -> "Hạt giống"
    }
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Pale),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(tree.name, color = Deep, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                    Text("${species.name} · $stage", color = Green, style = MaterialTheme.typography.labelLarge)
                }
                Text("CHĂM SÓC  ›", color = Green, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            }
            TreeIllustration(
                growth = tree.growth,
                speciesId = tree.speciesId,
                decoration = tree.decoration,
                modifier = Modifier.fillMaxWidth().height(270.dp)
            )
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Tiến trình phát triển", color = Deep, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                Text("${tree.growth.coerceAtMost(120)} / 120", color = Green, fontWeight = FontWeight.Bold)
            }
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = Green,
                trackColor = Color.White
            )
            Text("Chạm vào cây để tưới nước, bón phân và trang trí", color = Deep, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun HomeMetric(emoji: String, label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier, colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji)
            Text(value, color = Deep, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleLarge)
            Text(label, color = Muted, style = MaterialTheme.typography.labelSmall)
        }
    }
}
