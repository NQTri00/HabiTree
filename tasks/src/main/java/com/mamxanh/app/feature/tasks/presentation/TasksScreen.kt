package com.mamxanh.app.feature.tasks.presentation

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.feature.tasks.domain.model.Task
import com.mamxanh.app.feature.tasks.domain.model.today
import com.mamxanh.app.feature.tasks.presentation.dialog.AddTaskDialog
import com.mamxanh.app.presentation.ui.*
import com.mamxanh.app.presentation.ui.components.GardenScene
import com.mamxanh.app.presentation.ui.components.GardenSceneBackdrop
import com.mamxanh.app.presentation.ui.components.GardenBotanicalFrame

@Composable
fun TasksScreen(state: TasksUiState, onIntent: (TasksIntent) -> Unit, modifier: Modifier = Modifier) {
    val tasks = state.tasks
    val labels = listOf(
        TaskFilter.ALL to "🌿 Tất cả",
        TaskFilter.HABIT to "🔥 Thói quen",
        TaskFilter.DAILY to "☀️ Hằng ngày",
        TaskFilter.ONCE to "📌 Một lần"
    )
    val filtered = tasks.filter { task ->
        when (state.filter) {
            TaskFilter.ALL -> true
            TaskFilter.HABIT -> task.kind == "HABIT"
            TaskFilter.DAILY -> task.kind == "DAILY"
            TaskFilter.ONCE -> task.kind == "TODO"
        }
    }
    val active = filtered.filter { !it.finished }
    val daily = tasks.filter { it.kind == "DAILY" }
    val done = filtered.count { it.doneToday || it.finished }
    val progress = done.toFloat() / filtered.size.coerceAtLeast(1)

    Box(modifier.fillMaxSize()) {
        GardenSceneBackdrop(GardenScene.JOURNAL, Modifier.fillMaxSize())
        LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 112.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            TaskQuestHeader(
                done = done,
                total = filtered.size,
                progress = progress,
                allComplete = filtered.isNotEmpty() && done == filtered.size,
                onAddTask = { onIntent(TasksIntent.ShowAddDialog) }
            )
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                labels.forEach { (filter, label) ->
                    FilterChip(
                        selected = state.filter == filter,
                        onClick = { onIntent(TasksIntent.SetFilter(filter)) },
                        label = { Text(label, maxLines = 1) },
                        shape = RoundedCornerShape(50),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Pale,
                            selectedLabelColor = Deep
                        )
                    )
                }
            }
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Việc cần làm", color = Deep, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f))
                Surface(color = Pale, shape = RoundedCornerShape(50)) {
                    Text(
                        active.size.toString() + " việc",
                        color = Green,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp)
                    )
                }
            }
        }
        if (state.isLoading && tasks.isEmpty()) {
            item { LinearProgressIndicator(Modifier.fillMaxWidth(), color = Green, trackColor = Pale) }
        } else if (active.isEmpty()) {
            item {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(22.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Green.copy(alpha = 0.12f))
                ) {
                    Column(
                        Modifier.fillMaxWidth().padding(22.dp),
                        verticalArrangement = Arrangement.spacedBy(7.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🍃", style = MaterialTheme.typography.headlineMedium)
                        Text("Kệ việc này đang trống", color = Deep, fontWeight = FontWeight.Bold)
                        Text("Thêm một việc nhỏ để bắt đầu nhé.", color = Muted, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        } else {
            items(active, key = { it.id }) { task ->
                TaskCard(task, { onIntent(TasksIntent.Complete(task.id)) }, { onIntent(TasksIntent.Delete(task.id)) })
            }
        }

        if (daily.isNotEmpty() && state.filter in setOf(TaskFilter.ALL, TaskFilter.DAILY)) item {
            DailyRewardCard(
                canClaim = daily.all { it.doneToday },
                onClaim = { onIntent(TasksIntent.ClaimBonus) }
            )
        }
        val finished = filtered.filter { it.finished }
        if (finished.isNotEmpty()) {
            item {
                Text("Đã hoàn tất", color = Muted, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
            }
            items(finished, key = { "finished-" + it.id }) {
                TaskCard(it, {}, { onIntent(TasksIntent.Delete(it.id)) }, true)
            }
        }
        }
    }

    if (state.isAddingTask) {
        AddTaskDialog(
            onDismiss = { onIntent(TasksIntent.DismissAddDialog) },
            onSave = { title, kind, category, reward, frequency, due ->
                onIntent(TasksIntent.Add(title, kind, category, reward, frequency, due))
                onIntent(TasksIntent.DismissAddDialog)
            }
        )
    }
}

@Composable
private fun TaskQuestHeader(
    done: Int,
    total: Int,
    progress: Float,
    allComplete: Boolean,
    onAddTask: () -> Unit
) {
    GardenBotanicalFrame(Modifier.fillMaxWidth()) {
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("🌿  KHU VƯỜN HÔM NAY", color = Green, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("Việc nhỏ, lớn lên", color = Deep, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                    Text(
                        if (allComplete) "Bạn đã chăm khu vườn đủ đầy hôm nay. ✨"
                        else "Mỗi việc hoàn thành giúp cây thêm khỏe.",
                        color = Muted,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Box(Modifier.size(64.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxSize(),
                        color = Color(0xFFE7AE42),
                        trackColor = Pale,
                        strokeWidth = 6.dp
                    )
                    Text("$done/$total", color = Deep, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                }
            }
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(7.dp).clip(CircleShape),
                color = Green,
                trackColor = Pale
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "🪙 Nhận xu khi hoàn thành",
                    color = Muted,
                    fontSize = 10.sp,
                    maxLines = 2,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = onAddTask,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Green),
                    contentPadding = PaddingValues(horizontal = 11.dp, vertical = 7.dp)
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(17.dp))
                    Spacer(Modifier.width(3.dp))
                    Text("Thêm việc", fontWeight = FontWeight.Bold, fontSize = 11.sp, maxLines = 1)
                }
            }
        }
    }
}

@Composable
private fun DailyRewardCard(canClaim: Boolean, onClaim: () -> Unit) {
    Surface(
        color = Color(0xFFFFF3D4),
        shape = RoundedCornerShape(22.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0DCA8))
    ) {
        Row(
            Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            Surface(color = Color.White.copy(alpha = 0.75f), shape = CircleShape, modifier = Modifier.size(46.dp)) {
                Box(contentAlignment = Alignment.Center) { Text("🎁", fontSize = 24.sp) }
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text("Thưởng hoàn thành ngày", color = Deep, fontWeight = FontWeight.Bold)
                Text("Hoàn thành việc hằng ngày để nhận 20 xu.", color = Muted, style = MaterialTheme.typography.bodySmall)
            }
            Button(
                onClick = onClaim,
                enabled = canClaim,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Green)
            ) { Text("Nhận") }
        }
    }
}

@Composable
private fun TaskCard(task: Task, onComplete: () -> Unit, onDelete: () -> Unit, readOnly: Boolean = false) {
    var askDelete by remember { mutableStateOf(false) }
    val done = task.doneToday || readOnly
    val repeat = when (task.frequency) {
        "WEEKDAYS" -> "Thứ 2 – Thứ 6"
        "WEEKENDS" -> "Cuối tuần"
        "ONCE" -> if (task.due.isBlank()) "Không lặp lại" else "Hạn: " + task.due
        else -> "Mỗi ngày"
    }
    val typeLabel = when (task.kind) {
        "HABIT" -> "THÓI QUEN"
        "DAILY" -> "HẰNG NGÀY"
        else -> "MỘT LẦN"
    }
    val typeColor = when (task.kind) {
        "HABIT" -> Color(0xFF5E8B4E)
        "DAILY" -> Color(0xFFCA8B24)
        else -> Color(0xFF75899A)
    }
    val icon = when (task.kind) { "HABIT" -> "🌱"; "DAILY" -> "☀️"; else -> "📌" }

    GardenBotanicalFrame(Modifier.fillMaxWidth()) {
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(11.dp)) {
                Surface(color = typeColor.copy(alpha = 0.13f), shape = RoundedCornerShape(16.dp), modifier = Modifier.size(50.dp)) {
                    Box(contentAlignment = Alignment.Center) { Text(icon, fontSize = 25.sp) }
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Surface(color = typeColor.copy(alpha = 0.12f), shape = RoundedCornerShape(50)) {
                        Text(typeLabel, color = typeColor, fontWeight = FontWeight.ExtraBold, fontSize = 9.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                    Text(
                        task.title,
                        color = if (done) Muted else Deep,
                        fontWeight = FontWeight.Bold,
                        textDecoration = if (done) TextDecoration.LineThrough else TextDecoration.None,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Surface(color = Color(0xFFFFF2D2), shape = RoundedCornerShape(13.dp)) {
                    Column(Modifier.padding(horizontal = 9.dp, vertical = 6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🪙", fontSize = 14.sp)
                        Text("+${task.reward}", color = Color(0xFF895D08), fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                    }
                }
            }
            HorizontalDivider(color = Green.copy(alpha = 0.10f))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(task.category, color = Deep, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelMedium)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text("⏱", fontSize = 11.sp)
                        Text(repeat, color = Muted, style = MaterialTheme.typography.labelSmall)
                        if (task.streak > 1) {
                            Text("🔥 ${task.streak}", color = Color(0xFFB56A00), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
                if (readOnly) {
                    Surface(color = Pale, shape = RoundedCornerShape(13.dp)) {
                        Row(Modifier.padding(horizontal = 10.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Rounded.Check, contentDescription = null, tint = Green, modifier = Modifier.size(17.dp))
                            Text("Xong", color = Green, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                } else if (task.doneToday) {
                    Icon(Icons.Rounded.Check, contentDescription = "Đã xong hôm nay", tint = Green)
                } else {
                    Button(
                        onClick = onComplete,
                        shape = RoundedCornerShape(13.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Green),
                        contentPadding = PaddingValues(horizontal = 13.dp, vertical = 8.dp)
                    ) {
                        Text("Hoàn thành", fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    }
                }
                if (!readOnly) {
                    IconButton(onClick = { askDelete = true }, modifier = Modifier.size(38.dp)) {
                        Icon(Icons.Rounded.Delete, contentDescription = "Xóa nhiệm vụ", tint = Muted)
                    }
                }
            }
        }
    }
    if (askDelete) AlertDialog(
        onDismissRequest = { askDelete = false },
        title = { Text("Xóa nhiệm vụ?") },
        text = { Text("“" + task.title + "” sẽ bị xóa khỏi danh sách.") },
        confirmButton = { TextButton(onClick = { onDelete(); askDelete = false }) { Text("Xóa") } },
        dismissButton = { TextButton(onClick = { askDelete = false }) { Text("Giữ lại") } }
    )
}

@Preview(name = "Tasks · populated, large text", showBackground = true, widthDp = 360, heightDp = 800, fontScale = 1.3f)
@Preview(name = "Tasks · populated", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun TasksScreenPopulatedPreview() {
    MamXanhPreviewTheme {
        TasksScreen(
            state = TasksUiState(
                tasks = listOf(
                    Task(1, "Viết nhật ký 5 phút", "HABIT", "Phát triển bản thân", 5, "EVERY_DAY", "", "", 4, ""),
                    Task(2, "Uống đủ nước", "DAILY", "Sức khỏe", 5, "EVERY_DAY", "", today(), 2, today()),
                    Task(3, "Gọn góc học tập", "TODO", "Nếp sống", 10, "ONCE", "", today(), 0, today())
                ),
                isLoading = false
            ),
            onIntent = {}
        )
    }
}

@Preview(name = "Tasks · empty", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun TasksScreenEmptyPreview() {
    MamXanhPreviewTheme {
        TasksScreen(state = TasksUiState(isLoading = false), onIntent = {})
    }
}

@Preview(name = "Quest progress header", showBackground = true, widthDp = 360)
@Composable
fun TaskQuestHeaderPreview() {
    MamXanhPreviewTheme {
        TaskQuestHeader(done = 2, total = 5, progress = 0.4f, allComplete = false, onAddTask = {})
    }
}

@Preview(name = "Daily reward card", showBackground = true, widthDp = 360)
@Composable
fun DailyRewardCardPreview() {
    MamXanhPreviewTheme { DailyRewardCard(canClaim = true, onClaim = {}) }
}

@Preview(name = "Task cards, large text", showBackground = true, widthDp = 360, fontScale = 1.3f)
@Preview(name = "Task cards · active and complete", showBackground = true, widthDp = 360)
@Composable
fun TaskCardPreview() {
    MamXanhPreviewTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TaskCard(
                task = Task(1, "Viết nhật ký 5 phút", "HABIT", "Phát triển bản thân", 5, "EVERY_DAY", "", "", 4, ""),
                onComplete = {},
                onDelete = {}
            )
            TaskCard(
                task = Task(2, "Gọn góc học tập", "TODO", "Nếp sống", 10, "ONCE", "", "2026-11-08", 0, ""),
                onComplete = {},
                onDelete = {},
                readOnly = true
            )
        }
    }
}

