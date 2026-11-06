package com.mamxanh.app.feature.garden.presentation
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mamxanh.app.domain.model.*
import com.mamxanh.app.domain.model.State as GameState
import com.mamxanh.app.feature.tree.presentation.components.TreeIllustration
import com.mamxanh.app.presentation.ui.*

@Composable
fun GardenScreen(state: GameState, onPlant: (String, String) -> Unit, onOpenTree: (Long) -> Unit, modifier: Modifier = Modifier) {
    var selectedSeed by remember { mutableStateOf<Species?>(null) }
    var treeName by remember { mutableStateOf("") }
    val seeds = catalog.filter { (state.garden.inventory[it.id] ?: 0) > 0 }
    LazyColumn(modifier.fillMaxSize().padding(horizontal = 18.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Pale), shape = RoundedCornerShape(26.dp)) {
                Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🌿", style = MaterialTheme.typography.displayMedium)
                    Column(Modifier.padding(start = 14.dp)) {
                        Text("Một góc xanh của riêng bạn", color = Deep, fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge)
                        Text(if (state.garden.trees.isEmpty()) "Khu vườn đang chờ mầm xanh đầu tiên!"
                            else state.garden.trees.size.toString() + " cây đang lớn lên cùng bạn.", color = Green)
                    }
                }
            }
        }
        item { Text("Những mầm xanh của bạn", color = Deep, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
        if (state.garden.trees.isEmpty()) item {
            GardenEmptyCard()
        } else items(state.garden.trees, key = { it.id }) { tree ->
            val species = catalog.firstOrNull { it.id == tree.speciesId } ?: catalog.first()
            Card(Modifier.fillMaxWidth().clickable { onOpenTree(tree.id) },
                colors = CardDefaults.cardColors(containerColor = Pale), shape = RoundedCornerShape(24.dp)) {
                Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(tree.name, color = Deep, fontWeight = FontWeight.Bold)
                            Text(species.name + " · " + tree.growth.coerceAtMost(120) + " / 120 điểm", color = Green,
                                style = MaterialTheme.typography.bodySmall)
                        }
                        Text("Chăm cây  ›", color = Green, fontWeight = FontWeight.Bold)
                    }
                    TreeIllustration(tree.growth, tree.speciesId, tree.decoration, Modifier.fillMaxWidth().height(185.dp))
                }
            }
        }
        item { Text("Hạt giống đang chờ", color = Deep, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
        if (seeds.isEmpty()) item { Text("Túi hạt giống đang trống. Ghé Cửa hàng để tìm bạn xanh mới nhé. 🧺", color = Muted) }
        else items(seeds, key = { "seed-" + it.id }) { seed ->
            Card(colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
                shape = RoundedCornerShape(18.dp)) {
                Row(Modifier.fillMaxWidth().padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(seed.emoji, style = MaterialTheme.typography.headlineMedium)
                    Column(Modifier.weight(1f).padding(start = 12.dp)) {
                        Text(seed.name, color = Deep, fontWeight = FontWeight.Bold)
                        Text("Còn " + (state.garden.inventory[seed.id] ?: 0) + " hạt · " + seed.description,
                            color = Muted, style = MaterialTheme.typography.bodySmall)
                    }
                    Button(onClick = { selectedSeed = seed }) { Text("Gieo") }
                }
            }
        }
    }
    selectedSeed?.let { seed ->
        AlertDialog(onDismissRequest = { selectedSeed = null }, title = { Text("Gieo " + seed.name + " 🌱") },
            text = { OutlinedTextField(treeName, { treeName = it }, label = { Text("Đặt tên cho cây (tùy chọn)") }, singleLine = true) },
            confirmButton = { Button(onClick = { onPlant(seed.id, treeName); treeName = ""; selectedSeed = null }) { Text("Gieo cây") } },
            dismissButton = { TextButton(onClick = { selectedSeed = null }) { Text("Để sau") } })
    }
}

@Composable
private fun GardenEmptyCard() {
    Card(colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
        shape = RoundedCornerShape(22.dp)) {
        Column(Modifier.fillMaxWidth().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🌱", style = MaterialTheme.typography.displayMedium)
            Text("Chỗ này đang đợi một chiếc cây", color = Deep, fontWeight = FontWeight.Bold)
            Text("Ghé Cửa hàng để chọn hạt giống đầu tiên nhé.", color = Muted)
        }
    }
}
