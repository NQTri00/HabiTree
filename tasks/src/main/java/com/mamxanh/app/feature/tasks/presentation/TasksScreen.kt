package com.mamxanh.app.feature.tasks.presentation
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.mamxanh.app.domain.model.State
import com.mamxanh.app.domain.model.Task
import com.mamxanh.app.presentation.ui.*

@Composable
fun TasksScreen(state: State, onComplete: (Long) -> Unit, onDelete: (Long) -> Unit, onBonus: () -> Unit, modifier: Modifier = Modifier) {
    var filter by remember { mutableStateOf("Tất cả") }
    val active = state.tasks.filter { !it.finished && when (filter) {
        "Thói quen" -> it.kind == "HABIT"; "Hằng ngày" -> it.kind == "DAILY"
        "Một lần" -> it.kind == "TODO"; else -> true
    } }
    val daily = state.tasks.filter { it.kind == "DAILY" }
    val done = state.tasks.count { it.doneToday }
    LazyColumn(modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Pale), shape = RoundedCornerShape(24.dp)) {
                Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Chào ngày mới! 🌿", color = Deep, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Mỗi việc nhỏ hôm nay là một vòng cây lớn lên.", color = Deep)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(done.toString() + " / " + state.tasks.size + " việc đã xong", color = Green,
                            fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Text((done * 100 / state.tasks.size.coerceAtLeast(1)).toString() + "%", color = Green)
                    }
                    LinearProgressIndicator(progress = { done.toFloat() / state.tasks.size.coerceAtLeast(1) },
                        modifier = Modifier.fillMaxWidth().height(8.dp), color = Green, trackColor = Color.White)
                }
            }
        }
        item {
            val filters = listOf("Tất cả", "Thói quen", "Hằng ngày", "Một lần")
            ScrollableTabRow(filters.indexOf(filter), edgePadding = 0.dp, containerColor = Color.Transparent, divider = {}) {
                filters.forEach { Tab(filter == it, { filter = it }, text = { Text(it) }) }
            }
        }
        if (active.isEmpty()) item { Text("🍃 Không còn việc nào ở mục này. Tận hưởng khu vườn một chút nhé.", color = Muted) }
        items(active, key = { it.id }) { task -> TaskCard(task, { onComplete(task.id) }, { onDelete(task.id) }) }
        if (daily.isNotEmpty()) item {
            Card(colors = CardDefaults.cardColors(containerColor = Gold), shape = RoundedCornerShape(20.dp)) {
                Row(Modifier.fillMaxWidth().padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("🌟 Thưởng hoàn thành ngày", color = Deep, fontWeight = FontWeight.Bold)
                        Text("Hoàn thành mọi nhiệm vụ hằng ngày để nhận thêm 20 xu.", color = Muted,
                            style = MaterialTheme.typography.bodySmall)
                    }
                    Button(onClick = onBonus, enabled = daily.all { it.doneToday }) { Text("Nhận") }
                }
            }
        }
        val finished = state.tasks.filter { it.finished }
        if (finished.isNotEmpty()) {
            item { Text("Đã hoàn tất", color = Muted, fontWeight = FontWeight.Bold) }
            items(finished, key = { "finished-" + it.id }) { TaskCard(it, {}, { vm.delete(it.id) }, true) }
        }
    }
}

@Composable
private fun TaskCard(task: Task, onComplete: () -> Unit, onDelete: () -> Unit, readOnly: Boolean = false) {
    var askDelete by remember { mutableStateOf(false) }
    val done = task.doneToday || readOnly
    val repeat = when (task.frequency) {
        "WEEKDAYS" -> "Thứ 2 – Thứ 6"; "WEEKENDS" -> "Cuối tuần"
        "ONCE" -> if (task.due.isBlank()) "Không lặp lại" else "Hạn: " + task.due
        else -> "Mỗi ngày"
    }
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(18.dp)) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(Modifier.size(42.dp).clip(CircleShape).background(if (done) Pale else Cream), contentAlignment = Alignment.Center) {
                Text(when (task.kind) { "HABIT" -> "🌱"; "DAILY" -> "☀️"; else -> "📌" })
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(task.title, color = if (done) Muted else Deep, fontWeight = FontWeight.SemiBold,
                    textDecoration = if (done) TextDecoration.LineThrough else TextDecoration.None)
                Text(task.category + " · +" + task.reward + " xu", color = Green, style = MaterialTheme.typography.labelSmall)
                Text(repeat + if (task.streak > 1) " · 🔥 " + task.streak + " ngày" else "",
                    color = Muted, style = MaterialTheme.typography.labelSmall)
            }
            if (readOnly) Text("Hoàn tất", color = Green, style = MaterialTheme.typography.labelSmall)
            else {
                if (task.doneToday) Icon(Icons.Rounded.Check, contentDescription = "Đã xong", tint = Green)
                else FilledTonalButton(onClick = onComplete) { Text("Xong") }
                IconButton(onClick = { askDelete = true }) { Icon(Icons.Rounded.Delete, contentDescription = "Xóa", tint = Muted) }
            }
        }
    }
    if (askDelete) AlertDialog(onDismissRequest = { askDelete = false }, title = { Text("Xóa nhiệm vụ?") },
        text = { Text("“" + task.title + "” sẽ bị xóa khỏi danh sách.") },
        confirmButton = { TextButton(onClick = { onDelete(); askDelete = false }) { Text("Xóa") } },
        dismissButton = { TextButton(onClick = { askDelete = false }) { Text("Giữ lại") } })
}
