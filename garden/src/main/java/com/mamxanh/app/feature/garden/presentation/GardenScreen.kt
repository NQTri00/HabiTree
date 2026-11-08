package com.mamxanh.app.feature.garden.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.feature.garden.domain.model.Species
import com.mamxanh.app.feature.garden.domain.model.Tree
import com.mamxanh.app.feature.garden.domain.model.seedCatalog
import com.mamxanh.app.feature.shop.presentation.components.KawaiiPottedPlant
import com.mamxanh.app.presentation.ui.*
import com.mamxanh.app.presentation.ui.components.GardenPastoralBackdrop

@Composable
fun GardenScreen(
    state: GardenUiState,
    onIntent: (GardenIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val garden = state.garden
    var selectedSeed by remember { mutableStateOf<Species?>(null) }
    var treeName by remember { mutableStateOf("") }
    val availableSeeds = seedCatalog.filter { (garden.inventory[it.id] ?: 0) > 0 }
    val treesNeedingWater = garden.trees.count { !it.wateredToday }
    val totalSeeds = availableSeeds.sumOf { garden.inventory[it.id] ?: 0 }

    Box(modifier = modifier.fillMaxSize()) {
        GardenPastoralBackdrop(Modifier.fillMaxSize())
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 14.dp, bottom = 112.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                GardenOverviewCard(
                    treeCount = garden.trees.size,
                    treesNeedingWater = treesNeedingWater,
                    seedCount = totalSeeds
                )
            }
            item {
                GardenSectionHeading(
                    title = "Những bé mầm của bạn",
                    subtitle = if (garden.trees.isEmpty()) "Một khu vườn mới đang chờ bạn gieo hạt."
                        else "Chạm vào chậu cây để chăm sóc và xem hành trình lớn lên."
                )
            }

            if (garden.trees.isEmpty()) {
                item {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(24.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Green.copy(alpha = 0.14f))
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(26.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = Color(0xFFFFF2D9),
                                shape = RoundedCornerShape(22.dp),
                                modifier = Modifier.size(112.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    KawaiiPottedPlant(
                                        varietyIndex = 0,
                                        potExpression = 0,
                                        modifier = Modifier.size(76.dp, 92.dp)
                                    )
                                }
                            }
                            Text("Mầm xanh đầu tiên đang chờ", color = Deep, fontWeight = FontWeight.Bold)
                            Text(
                                "Gieo một hạt giống để bắt đầu câu chuyện khu vườn của bạn.",
                                color = Muted,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            } else {
                items(garden.trees.chunked(2)) { rowTrees ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowTrees.forEach { tree ->
                            val species = seedCatalog.firstOrNull { it.id == tree.speciesId } ?: seedCatalog.first()
                            val varietyIndex = seedCatalog.indexOfFirst { it.id == tree.speciesId }.coerceAtLeast(0)
                            GardenTreeCard(
                                tree = tree,
                                speciesName = species.name,
                                varietyIndex = varietyIndex,
                                onClick = { onIntent(GardenIntent.OpenTree(tree.id)) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowTrees.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }

            item {
                GardenSectionHeading(
                    title = "Hạt giống trong túi",
                    subtitle = if (totalSeeds > 0) "$totalSeeds hạt sẵn sàng nảy mầm."
                        else "Mua thêm hạt giống để mở rộng khu vườn."
                )
            }
            if (availableSeeds.isEmpty()) {
                item {
                    Surface(
                        color = Color(0xFFFFF4D9),
                        shape = RoundedCornerShape(18.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEBD9AA))
                    ) {
                        Row(
                            Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Surface(color = Color.White.copy(alpha = 0.7f), shape = RoundedCornerShape(16.dp)) {
                                Text("🧺", fontSize = 28.sp, modifier = Modifier.padding(10.dp))
                            }
                            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Text("Túi hạt giống đang trống", color = Deep, fontWeight = FontWeight.Bold)
                                Text("Ghé cửa hàng tìm mầm mới cho khu vườn nhé.", color = Muted, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            } else {
                items(availableSeeds, key = { "seed-" + it.id }) { seed ->
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Green.copy(alpha = 0.12f))
                    ) {
                        Row(
                            Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(color = Pale, shape = RoundedCornerShape(14.dp), modifier = Modifier.size(48.dp)) {
                                Box(contentAlignment = Alignment.Center) { Text(seed.emoji, fontSize = 26.sp) }
                            }
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(seed.name, color = Deep, fontWeight = FontWeight.Bold)
                                Text(
                                    "Còn ${garden.inventory[seed.id] ?: 0} hạt · ${seed.description}",
                                    color = Muted,
                                    fontSize = 12.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Button(
                                onClick = { selectedSeed = seed },
                                colors = ButtonDefaults.buttonColors(containerColor = Green),
                                shape = RoundedCornerShape(14.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                            ) { Text("Gieo") }
                        }
                    }
                }
            }
        }
    }

    selectedSeed?.let { seed ->
        AlertDialog(
            onDismissRequest = { selectedSeed = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("🌱", fontSize = 24.sp)
                    Text("Gieo ${seed.name}")
                }
            },
            text = {
                OutlinedTextField(
                    value = treeName,
                    onValueChange = { treeName = it },
                    label = { Text("Đặt tên cho chậu cây (tùy chọn)") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onIntent(GardenIntent.Plant(seed.id, treeName))
                        treeName = ""
                        selectedSeed = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Green)
                ) { Text("Gieo cây") }
            },
            dismissButton = { TextButton(onClick = { selectedSeed = null }) { Text("Để sau") } }
        )
    }
}

@Composable
private fun GardenOverviewCard(treeCount: Int, treesNeedingWater: Int, seedCount: Int) {
    Surface(
        color = Green,
        shape = RoundedCornerShape(26.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.55f))
    ) {
        Box {
            Surface(
                color = Color.White.copy(alpha = 0.07f),
                shape = CircleShape,
                modifier = Modifier.align(Alignment.TopEnd).offset(x = 30.dp, y = (-32).dp).size(150.dp)
            ) {}
            Column(
                Modifier.fillMaxWidth().padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("KHU VƯỜN CỦA BẠN", color = Color.White.copy(alpha = 0.82f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("Mầm xanh đang lớn", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                        Text("Chăm từng chiếc lá, nuôi từng niềm vui.", color = Color.White.copy(alpha = 0.9f), style = MaterialTheme.typography.bodySmall)
                    }
                    Text("🌿", fontSize = 48.sp)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    GardenMetric("🪴", "$treeCount cây", Modifier.weight(1f))
                    GardenMetric("💧", "$treesNeedingWater cần tưới", Modifier.weight(1.35f))
                    GardenMetric("🌰", "$seedCount hạt", Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun GardenMetric(emoji: String, label: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = Color.White.copy(alpha = 0.16f),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.18f))
    ) {
        Row(
            Modifier.padding(horizontal = 8.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(emoji, fontSize = 13.sp)
            Text(
                label,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun GardenSectionHeading(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(title, color = Deep, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
        Text(subtitle, color = Muted, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun GardenTreeCard(
    tree: Tree,
    speciesName: String,
    varietyIndex: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = (tree.growth / 120f).coerceIn(0f, 1f)
    val accent = if (tree.wateredToday) Pale else Color(0xFFFFF2D9)
    Surface(
        onClick = onClick,
        modifier = modifier,
        color = Color.White,
        shape = RoundedCornerShape(22.dp),
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Green.copy(alpha = 0.12f))
    ) {
        Column(
            Modifier.fillMaxWidth().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(color = accent, shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
                Box(Modifier.fillMaxWidth().height(112.dp)) {
                    Text(
                        if (tree.wateredToday) "✨" else "💧",
                        modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
                        fontSize = 14.sp
                    )
                    KawaiiPottedPlant(
                        varietyIndex = varietyIndex,
                        potExpression = (tree.id % 3).toInt(),
                        modifier = Modifier.align(Alignment.Center).size(78.dp, 94.dp)
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(
                    if (tree.wateredToday) "ĐANG LỚN KHỎE" else "ĐANG CHỜ TƯỚI",
                    color = if (tree.wateredToday) Green else Color(0xFFB56A00),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 9.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                tree.name,
                color = Deep,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text("Loài · $speciesName", color = Muted, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(7.dp).clip(CircleShape),
                color = Green,
                trackColor = Pale
            )
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    "${tree.growth.coerceAtMost(120)} / 120",
                    color = Deep,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1
                )
                Text(
                    if (tree.wateredToday) "Đã tưới ✓" else "Cần nước 💧",
                    color = if (tree.wateredToday) Green else Color(0xFFB56A00),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }
    }
}

@Preview(name = "Garden · planted trees, large text", showBackground = true, widthDp = 360, heightDp = 800, fontScale = 1.3f)
@Preview(name = "Garden · planted trees", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun GardenScreenPopulatedPreview() {
    MamXanhPreviewTheme {
        GardenScreen(
            state = GardenUiState(
                garden = com.mamxanh.app.feature.garden.domain.model.Garden(
                    trees = listOf(
                        Tree(1, "cherry", "Đào hồng", 82, "2026-01-01", wateredToday = true),
                        Tree(2, "pine", "Thông xanh", 24, "2026-01-02"),
                        Tree(3, "lavender", "Oải hương", 48, "2026-01-03", wateredToday = true)
                    ),
                    inventory = mapOf("succulent" to 3, "cactus" to 1)
                ),
                isLoading = false
            ),
            onIntent = {}
        )
    }
}

@Preview(name = "Garden · empty", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun GardenScreenEmptyPreview() {
    MamXanhPreviewTheme {
        GardenScreen(
            state = GardenUiState(
                garden = com.mamxanh.app.feature.garden.domain.model.Garden(),
                isLoading = false
            ),
            onIntent = {}
        )
    }
}

@Preview(name = "Garden overview card", showBackground = true)
@Composable
fun GardenOverviewCardPreview() {
    MamXanhPreviewTheme { GardenOverviewCard(treeCount = 3, treesNeedingWater = 1, seedCount = 4) }
}

@Preview(name = "Garden metric", showBackground = true)
@Composable
fun GardenMetricPreview() {
    MamXanhPreviewTheme {
        Surface(color = Green, shape = RoundedCornerShape(16.dp)) {
            GardenMetric("💧", "3 cần tưới", Modifier.width(180.dp))
        }
    }
}

@Preview(name = "Garden section heading", showBackground = true)
@Composable
fun GardenSectionHeadingPreview() {
    MamXanhPreviewTheme { GardenSectionHeading("Những bé mầm của bạn", "Chạm vào chậu cây để chăm sóc.") }
}

@Preview(name = "Garden tree card, large text", showBackground = true, widthDp = 220, fontScale = 1.3f)
@Preview(name = "Garden tree card", showBackground = true, widthDp = 220)
@Composable
fun GardenTreeCardPreview() {
    MamXanhPreviewTheme {
        GardenTreeCard(
            tree = Tree(1, "cherry", "Đào hồng xuân", 82, "2026-01-01", wateredToday = false),
            speciesName = "Đào hồng xuân",
            varietyIndex = 8,
            onClick = {}
        )
    }
}



