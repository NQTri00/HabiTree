package com.mamxanh.app.feature.tree.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mamxanh.app.domain.model.Tree
import com.mamxanh.app.domain.model.catalog
import com.mamxanh.app.feature.tree.presentation.components.TreeIllustration
import com.mamxanh.app.presentation.ui.Deep
import com.mamxanh.app.presentation.ui.Green
import com.mamxanh.app.presentation.ui.Muted
import com.mamxanh.app.presentation.ui.Pale

@Composable
fun TreeInteractionScreen(
    tree: Tree,
    fertilizerCount: Int,
    onWater: () -> Unit,
    onFertilize: () -> Unit,
    onDecorate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var previewGrowth by remember(tree.id, tree.growth) { mutableFloatStateOf(tree.growth.toFloat()) }
    val species = catalog.firstOrNull { it.id == tree.speciesId } ?: catalog.first()
    val progress = (tree.growth / 120f).coerceIn(0f, 1f)
    val stage = when {
        tree.growth >= 120 -> "Trưởng thành"
        tree.growth >= 50 -> "Cây non"
        tree.growth >= 10 -> "Cây con"
        tree.growth > 0 -> "Nảy mầm"
        else -> "Hạt giống"
    }
    Column(
        modifier.fillMaxSize().padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(tree.name, color = Deep, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
            Text("${species.name} · $stage", color = Green)
        }
        Card(colors = CardDefaults.cardColors(containerColor = Pale), shape = RoundedCornerShape(26.dp)) {
            Column(Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                TreeIllustration(
                    growth = previewGrowth.toInt(),
                    speciesId = tree.speciesId,
                    decoration = tree.decoration,
                    modifier = Modifier.fillMaxWidth().height(300.dp)
                )
                Text("Cây của bạn", color = Deep, fontWeight = FontWeight.Bold)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Phát triển", color = Deep, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            Text("${tree.growth.coerceAtMost(120)} / 120", color = Green, fontWeight = FontWeight.Bold)
        }
        LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(9.dp), color = Green, trackColor = Pale)
        Text("Chăm sóc cây", color = Deep, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = onWater, enabled = !tree.wateredToday, modifier = Modifier.weight(1f)) {
                Text(if (tree.wateredToday) "Đã tưới hôm nay" else "💧 Tưới nước")
            }
            OutlinedButton(onClick = onFertilize, enabled = fertilizerCount > 0, modifier = Modifier.weight(1f)) {
                Text("🌿 Bón phân ($fertilizerCount)")
            }
        }
        Text("Trang trí", color = Deep, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
            listOf("NONE" to "Không", "FLOWERS" to "Hoa", "LANTERNS" to "Đèn", "RIBBON" to "Nơ").forEach { (code, label) ->
                FilterChip(selected = tree.decoration == code, onClick = { onDecorate(code) }, label = { Text(label) })
            }
        }
        Divider(color = Pale)
        Text("Xem trước giai đoạn phát triển", color = Deep, fontWeight = FontWeight.SemiBold)
        Slider(value = previewGrowth, onValueChange = { previewGrowth = it }, valueRange = 0f..120f, steps = 11)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Bản xem trước: ${previewGrowth.toInt()} / 120", color = Muted, modifier = Modifier.weight(1f))
            TextButton(onClick = { previewGrowth = tree.growth.coerceAtMost(120).toFloat() }) { Text("Đặt lại") }
        }
        Text("Thanh trượt chỉ thay đổi hình xem trước, không sửa tiến trình đã lưu.", color = Muted, style = MaterialTheme.typography.bodySmall)
    }
}
