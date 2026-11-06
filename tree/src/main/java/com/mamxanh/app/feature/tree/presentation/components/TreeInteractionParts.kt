package com.mamxanh.app.feature.tree.presentation.components
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mamxanh.app.feature.tree.domain.model.Tree
import com.mamxanh.app.presentation.ui.components.TreeIllustration
import com.mamxanh.app.presentation.ui.*

@Composable
fun TreePortrait(tree: Tree) {
    Card(colors = CardDefaults.cardColors(containerColor = Pale), shape = RoundedCornerShape(28.dp)) {
        Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            TreeIllustration(tree.growth, tree.speciesId, tree.decoration, Modifier.fillMaxWidth().height(310.dp))
            LinearProgressIndicator(progress = { (tree.growth / 120f).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(8.dp), color = Green,
                trackColor = androidx.compose.ui.graphics.Color.White)
            Text(when {
                tree.growth >= 120 -> "Cây đã trưởng thành! Thật tuyệt vời."
                tree.growth >= 50 -> "Cây đang vươn cành và đón nắng."
                tree.growth >= 10 -> "Những chiếc lá đầu tiên đang lớn lên."
                tree.growth > 0 -> "Một mầm non bé xíu vừa thức giấc."
                else -> "Một hạt giống nhỏ đang chờ ngày nảy mầm."
            }, color = Deep, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun TreeCareActions(wateredToday: Boolean, fertilizerCount: Int, onWater: () -> Unit, onFertilize: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        Text("Chăm sóc hôm nay", color = Deep, style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = onWater, enabled = !wateredToday, modifier = Modifier.weight(1f)) {
                Text(if (wateredToday) "Đã tưới 💧" else "Tưới nước 💧")
            }
            OutlinedButton(onClick = onFertilize, enabled = fertilizerCount > 0, modifier = Modifier.weight(1f)) {
                Text("Bón phân 🌿")
            }
        }
        Text("Phân bón trong túi: " + fertilizerCount, color = Muted, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun DecorationPicker(selected: String, onSelect: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        Text("Thêm chút xinh xắn", color = Deep, style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
            listOf("NONE" to "Tự nhiên", "FLOWERS" to "Hoa 🌼", "LANTERNS" to "Đèn 🏮", "RIBBON" to "Nơ 🎀")
                .forEach { (code, label) ->
                    FilterChip(selected = selected == code, onClick = { onSelect(code) },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) })
                }
        }
    }
}
