package com.mamxanh.app.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mamxanh.app.domain.model.Garden
import com.mamxanh.app.domain.model.Species
import com.mamxanh.app.domain.model.State
import com.mamxanh.app.domain.model.Tree
import com.mamxanh.app.domain.model.catalog
import com.mamxanh.app.presentation.ui.Deep
import com.mamxanh.app.presentation.ui.Green
import com.mamxanh.app.presentation.ui.Muted
import com.mamxanh.app.presentation.ui.Pale
import com.mamxanh.app.presentation.ui.components.TreeIllustration
import com.mamxanh.app.presentation.viewmodel.GardenViewModel

@Composable
fun GardenScreen(state: State, vm: GardenViewModel, modifier: Modifier = Modifier) {
    var selectedSeed by remember { mutableStateOf<Species?>(null) }
    var treeName by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Pale), shape = RoundedCornerShape(24.dp)) {
                Column(
                    Modifier.fillMaxWidth().padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🌱", style = MaterialTheme.typography.displayMedium)
                    Text("Khu vườn đang lớn lên", style = MaterialTheme.typography.titleLarge, color = Deep, fontWeight = FontWeight.Bold)
                    Text(
                        "Hoàn thành nhiệm vụ để cây phát triển. Tưới nước, bón phân và trang trí khu vườn theo cách của bạn.",
                        color = Deep
                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        GardenMetric("Số cây", state.garden.trees.size.toString())
                        GardenMetric("Hạt giống", state.garden.inventory.filterKeys { it != "fertilizer" }.values.sum().toString())
                        GardenMetric("Xu", state.stats.coins.toString())
                    }
                }
            }
        }

        item {
            Text("Cây đã trồng (" + state.garden.trees.size + ")", style = MaterialTheme.typography.titleMedium, color = Deep, fontWeight = FontWeight.Bold)
        }
        if (state.garden.trees.isEmpty()) {
            item { EmptyCard("Khu vườn còn trống", "Mua hạt giống ở Cửa hàng rồi gieo cây đầu tiên tại đây.") }
        }
        items(state.garden.trees, key = { it.id }) { tree ->
            val species = catalog.firstOrNull { it.id == tree.speciesId } ?: catalog.first()
            TreeCard(
                tree = tree,
                species = species,
                fertilizerCount = state.garden.inventory["fertilizer"] ?: 0,
                onWater = { vm.water(tree.id) },
                onFertilize = { vm.fertilize(tree.id) },
                onDecorate = { vm.decorate(tree.id, it) }
            )
        }

        item {
            Text("Hạt giống trong kho", style = MaterialTheme.typography.titleMedium, color = Deep, fontWeight = FontWeight.Bold)
        }
        items(catalog.filter { (state.garden.inventory[it.id] ?: 0) > 0 }, key = { "seed-" + it.id }) { seed ->
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
                Row(Modifier.fillMaxWidth().padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(seed.emoji, style = MaterialTheme.typography.headlineMedium)
                    Column(Modifier.weight(1f).padding(start = 12.dp)) {
                        Text(seed.name, color = Deep, fontWeight = FontWeight.Bold)
                        Text(
                            "Còn " + (state.garden.inventory[seed.id] ?: 0) + " hạt · " + seed.description,
                            color = Muted,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Button(onClick = { selectedSeed = seed }) { Text("Gieo") }
                }
            }
        }
    }

    selectedSeed?.let { seed ->
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { selectedSeed = null },
            title = { Text("Gieo " + seed.name) },
            text = {
                androidx.compose.material3.OutlinedTextField(
                    value = treeName,
                    onValueChange = { treeName = it },
                    label = { Text("Đặt tên cho cây (tùy chọn)") },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(onClick = {
                    vm.plant(seed.id, treeName)
                    treeName = ""
                    selectedSeed = null
                }) { Text("Gieo cây") }
            },
            dismissButton = { TextButton(onClick = { selectedSeed = null }) { Text("Để sau") } }
        )
    }
}

@Composable
private fun GardenMetric(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = Green, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
        Text(label, color = Muted, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun TreeCard(
    tree: Tree,
    species: Species,
    fertilizerCount: Int,
    onWater: () -> Unit,
    onFertilize: () -> Unit,
    onDecorate: (String) -> Unit
) {
    var previewGrowth by remember(tree.id, tree.growth) { mutableFloatStateOf(tree.growth.toFloat()) }
    val stage = when {
        previewGrowth >= 120f -> "Trưởng thành"
        previewGrowth >= 50f -> "Cây non"
        previewGrowth >= 10f -> "Cây con"
        previewGrowth > 0f -> "Nảy mầm"
        else -> "Hạt giống"
    }
    val actualStage = when {
        tree.growth >= 120 -> "Trưởng thành"
        tree.growth >= 50 -> "Cây non"
        tree.growth >= 10 -> "Cây con"
        tree.growth > 0 -> "Nảy mầm"
        else -> "Hạt giống"
    }
    val progress = (tree.growth / 120f).coerceIn(0f, 1f)

    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(22.dp)) {
        Column(
            Modifier.fillMaxWidth().padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Column {
                Text(tree.name, color = Deep, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(species.name + " · " + actualStage, color = Green, style = MaterialTheme.typography.labelMedium)
            }

            TreeIllustration(
                growth = previewGrowth.toInt(),
                speciesId = species.id,
                decoration = tree.decoration,
                modifier = Modifier.fillMaxWidth().height(220.dp)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Tiến trình thật", color = Deep, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                Text(tree.growth.coerceAtMost(120).toString() + " / 120", color = Muted, style = MaterialTheme.typography.labelMedium)
            }
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = Green,
                trackColor = Pale
            )

            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Thử nghiệm tăng trưởng", color = Deep, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                    Text(previewGrowth.toInt().toString() + " / 120", color = Green, style = MaterialTheme.typography.labelMedium)
                }
                Slider(
                    value = previewGrowth,
                    onValueChange = { previewGrowth = it },
                    valueRange = 0f..120f,
                    steps = 11
                )
                Text(
                    "Thanh trượt chỉ thay đổi hình xem trước, không sửa tiến trình đã lưu.",
                    color = Muted,
                    style = MaterialTheme.typography.labelSmall
                )
                Text("Bản xem trước: $stage", color = Green, style = MaterialTheme.typography.labelMedium)
                if (previewGrowth.toInt() != tree.growth.coerceAtMost(120)) {
                    TextButton(onClick = { previewGrowth = tree.growth.coerceAtMost(120).toFloat() }) {
                        Text("Đặt lại theo tiến trình thật")
                    }
                }
            }

            Text("Chăm sóc cây", color = Deep, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = onWater, enabled = !tree.wateredToday, modifier = Modifier.weight(1f)) {
                    Text(if (tree.wateredToday) "Đã tưới hôm nay" else "💧 Tưới nước")
                }
                OutlinedButton(onClick = onFertilize, enabled = fertilizerCount > 0, modifier = Modifier.weight(1f)) {
                    Text("🌿 Bón phân ($fertilizerCount)")
                }
            }

            Text("Trang trí cây", color = Deep, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
                listOf("NONE" to "Không", "FLOWERS" to "Hoa", "LANTERNS" to "Đèn", "RIBBON" to "Nơ").forEach { (code, label) ->
                    FilterChip(
                        selected = tree.decoration == code,
                        onClick = { onDecorate(code) },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }
        }
    }
}
