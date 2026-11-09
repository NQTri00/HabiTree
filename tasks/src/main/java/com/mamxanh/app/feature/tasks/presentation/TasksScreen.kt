package com.mamxanh.app.feature.tasks.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.combinedClickable
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.mamxanh.core.ui.R
import com.mamxanh.app.presentation.ui.components.GardenChoiceChip
import com.mamxanh.app.presentation.ui.components.GardenBotanicalFrame
import com.mamxanh.app.presentation.ui.components.GardenRichText as Text
import com.mamxanh.app.presentation.ui.components.GardenArt
import com.mamxanh.app.presentation.ui.components.GardenArtwork
import com.mamxanh.app.presentation.ui.components.GardenSprig
import com.mamxanh.app.presentation.ui.components.GardenPaintedButton
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamxanh.app.feature.tasks.domain.model.Task
import com.mamxanh.app.feature.tasks.domain.model.today
import com.mamxanh.app.feature.tasks.presentation.dialog.AddTaskDialog
import com.mamxanh.app.presentation.ui.*
import com.mamxanh.app.presentation.ui.components.GardenScene
import com.mamxanh.app.presentation.ui.components.GardenSceneBackdrop
import com.mamxanh.app.presentation.ui.components.GardenJournalPage

@Composable
fun TasksScreen(state: TasksUiState, onIntent: (TasksIntent) -> Unit, modifier: Modifier = Modifier) {
    val filtered = state.tasks.filter { task ->
        when (state.filter) {
            TaskFilter.ALL -> true
            TaskFilter.HABIT -> task.kind == "HABIT"
            TaskFilter.DAILY -> task.kind == "DAILY"
            TaskFilter.ONCE -> task.kind == "TODO"
        }
    }
    val done = state.tasks.count { it.doneToday || it.finished }
    val daily = state.tasks.filter { it.kind == "DAILY" }
    Box(modifier.fillMaxSize()) {
        GardenSceneBackdrop(GardenScene.JOURNAL, Modifier.fillMaxSize())
        GardenJournalPage(Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 6.dp)) {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 12.dp)) {
                item {
                    TaskQuestHeader(done, state.tasks.size, done.toFloat() / state.tasks.size.coerceAtLeast(1),
                        state.tasks.isNotEmpty() && done == state.tasks.size, {})
                }
                item {
                    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        listOf(TaskFilter.ALL to "Tất cả", TaskFilter.HABIT to "Thói quen", TaskFilter.DAILY to "Hằng ngày", TaskFilter.ONCE to "Một lần").forEach { (filter, label) ->
                            GardenChoiceChip(label, state.filter == filter,
                                { onIntent(TasksIntent.SetFilter(filter)) })
                        }
                    }
                }
                if (state.isLoading && state.tasks.isEmpty()) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
                if (!state.isLoading && filtered.isEmpty()) item {
                    Column(Modifier.fillMaxWidth().padding(vertical = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🌱", fontSize = 30.sp)
                        Text(if (state.tasks.isEmpty()) "Gieo một việc nhỏ hôm nay" else "Không có việc nào phù hợp",
                            color = Deep, fontWeight = FontWeight.Bold)
                        if (state.tasks.isEmpty()) {
                            Text("Mỗi bước nhỏ đều giúp bạn lớn lên.", color = Muted, fontSize = 13.sp)
                        } else {
                            GardenPaintedButton("Xem tất cả", { onIntent(TasksIntent.SetFilter(TaskFilter.ALL)) },
                                primary = false)
                        }
                    }
                }
                items(filtered, key = { it.id }) { task ->
                    TaskCard(task, { onIntent(TasksIntent.Complete(task.id)) }, { onIntent(TasksIntent.Delete(task.id)) }, task.finished)
                }
                val completed = filtered.filter { it.doneToday || it.finished }
                if (completed.isNotEmpty()) {
                    item(key = "completed-heading") {
                        GardenBotanicalFrame(Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            skin = com.mamxanh.app.presentation.ui.components.GardenPanelSkin.HINT) {
                            Text("✓ Đã xong · ${completed.size}", color = Deep, fontWeight = FontWeight.Bold)
                        }
                    }
                    items(completed, key = { "completed-${it.id}" }) { task ->
                        TaskCard(task, {}, { onIntent(TasksIntent.Delete(task.id)) }, readOnly = true)
                    }
                }
                if (daily.isNotEmpty() && state.filter in setOf(TaskFilter.ALL, TaskFilter.DAILY)) item {
                    DailyRewardCard(state.canClaimDailyBonus && !state.isLoading,
                        { onIntent(TasksIntent.ClaimBonus) }, state.isDailyBonusClaimed)
                }
            }
            GardenPaintedButton("+ Thêm việc", { onIntent(TasksIntent.ShowAddDialog) }, Modifier.fillMaxWidth())
            }
        }
    }
    if (state.isAddingTask) AddTaskDialog(
        onDismiss = { onIntent(TasksIntent.DismissAddDialog) },
        onSave = { title, kind, category, reward, frequency, due ->
            onIntent(TasksIntent.Add(title, kind, category, reward, frequency, due))
            onIntent(TasksIntent.DismissAddDialog)
        }
    )
}

@Composable
private fun TaskQuestHeader(done: Int, total: Int, progress: Float, allComplete: Boolean, onAddTask: () -> Unit) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text("Việc nhỏ, lớn lên", color = Deep, fontSize = 25.sp, fontWeight = FontWeight.Bold)
            GardenSprig(Modifier.align(Alignment.CenterEnd).offset(x = 14.dp).size(24.dp, 36.dp), mirrored = true)
        }
        Text("$done / $total việc hôm nay", color = Deep, fontSize = 16.sp, lineHeight = 20.sp)
        Canvas(Modifier.fillMaxWidth().height(44.dp).semantics {
            contentDescription = "$done trên $total việc đã hoàn thành"
        }) {
            val y = size.height * .84f
            val stem = Path().apply {
                moveTo(size.width * .04f,y)
                cubicTo(size.width*.28f,y-9.dp.toPx(),size.width*.60f,y+9.dp.toPx(),size.width*.96f,y)
            }
            drawPath(stem,Color(0xFFB38351),style=androidx.compose.ui.graphics.drawscope.Stroke(4.dp.toPx(),cap=StrokeCap.Round))
            val leafCount = total.coerceIn(0, 12)
            repeat(leafCount) { index ->
                val x = size.width * (.08f + (index + .15f) * .84f / leafCount)
                val leafWidth = minOf(23.dp.toPx(), size.width * .65f / leafCount)
                val leaf = Path().apply {
                    moveTo(x,y)
                    cubicTo(x-leafWidth*.3f,y-22.dp.toPx(),x+leafWidth*.4f,y-27.dp.toPx(),x+leafWidth,y-27.dp.toPx())
                    cubicTo(x+leafWidth*.87f,y-10.dp.toPx(),x+leafWidth*.48f,y-3.dp.toPx(),x,y)
                }
                val filled = progress >= (index + 1).toFloat() / leafCount
                drawPath(leaf,if(filled) Color(0xFF93B43B) else Color(0xFFF7E3BE))
                drawPath(leaf,if(filled) Color(0xFF7E9E37) else Color(0xFFDFC29C),
                    style=androidx.compose.ui.graphics.drawscope.Stroke(1.dp.toPx()))
            }
        }
        if (allComplete) Text("Mọi việc đã xong. Khu vườn cảm ơn bạn!", color = Green, fontSize = 12.sp)
    }
}

@Composable
private fun DailyRewardCard(canClaim: Boolean, onClaim: () -> Unit, claimed: Boolean = false) {
    Box(Modifier.fillMaxWidth()) {
        GardenBotanicalFrame(Modifier.fillMaxWidth().clickable(enabled = canClaim && !claimed,
            onClickLabel = "Nhận thưởng hôm nay", onClick = onClaim),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
            skin = com.mamxanh.app.presentation.ui.components.GardenPanelSkin.PRICE) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                GardenArtwork(GardenArt.COIN, Modifier.size(32.dp))
                Text(if (claimed) "✓ Đã nhận thưởng" else "Thưởng hôm nay", Modifier.weight(1f),
                    color = if (claimed) Muted else Deep, fontSize = 15.sp, lineHeight = 18.sp, fontWeight = FontWeight.Bold)
                if (!claimed) Text("+20 xu", color = Deep, fontSize = 17.sp, lineHeight = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
        GardenSprig(Modifier.align(Alignment.BottomStart).offset(x = (-10).dp, y = 4.dp).size(18.dp, 28.dp))
        GardenSprig(Modifier.align(Alignment.BottomEnd).offset(x = 10.dp, y = 4.dp).size(18.dp, 28.dp), mirrored = true)
    }
}

@Composable
private fun TaskCard(task: Task, onComplete: () -> Unit, onDelete: () -> Unit, readOnly: Boolean = false) {
    var askDelete by remember { mutableStateOf(false) }
    val done = task.doneToday || readOnly
    Column {
        Row(Modifier.fillMaxWidth().heightIn(min = 86.dp)
            .combinedClickable(onClick = { if (!done) onComplete() }, onLongClickLabel = "Xóa nhiệm vụ", onLongClick = { askDelete = true })
            .semantics { customActions = listOf(CustomAccessibilityAction("Xóa nhiệm vụ") { askDelete = true; true }) }
            .padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            GardenArtwork(when {
                task.title.contains("bộ", ignoreCase = true) -> GardenArt.BOOTS
                task.title.contains("nước", ignoreCase = true) -> GardenArt.GLASS
                task.category.contains("Học", ignoreCase = true) -> GardenArt.BOOK
                else -> GardenArt.SPROUT
            }, Modifier.size(36.dp, 48.dp))
            Column(Modifier.weight(1f).padding(horizontal = 3.dp)) {
                Text(task.title, color = Deep, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                if (!done) Text(task.category, color = Color(0xFF80562C), fontSize = 12.sp)
                if (task.kind == "TODO" && task.due.isNotBlank()) Text("Hạn: " + task.due, color = Muted, fontSize = 11.sp)
                if (done) Text("Đã xong", color = Green, fontSize = 12.sp)
            }
            if (!done) GardenBotanicalFrame(contentPadding = PaddingValues(horizontal = 5.dp, vertical = 3.dp),
                cornerSize = 9.dp, skin = com.mamxanh.app.presentation.ui.components.GardenPanelSkin.PRICE) {
                Text("🪙+${task.reward} xu", color = Deep, fontSize = 11.sp, lineHeight = 14.sp, fontWeight = FontWeight.Bold)
            }
            IconButton(onClick = onComplete, enabled = !done, modifier = Modifier.size(36.dp, 48.dp).semantics { contentDescription = if (done) "Đã hoàn thành" else "Hoàn thành nhiệm vụ" }) {
                Surface(shape = CircleShape, color = if (done) Color(0xFF7B9D3D) else Color.Transparent,
                    border = if (done) null else BorderStroke(1.5.dp,Color(0xFF9B7854)), modifier = Modifier.size(24.dp)) {
                    if (done) Icon(Icons.Rounded.Check, null, tint = Color(0xFFFFF5DC), modifier = Modifier.padding(3.dp))
                }
            }

        }
        HorizontalDivider(color = Color(0xFFBE9454).copy(alpha = .45f), thickness = .6.dp)
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
        TasksScreen(TasksUiState(tasks = listOf(
            Task(1, "Đọc 10 trang sách", "HABIT", "Học tập", 10, "EVERY_DAY", "", "", 4, ""),
            Task(2, "Uống đủ nước", "DAILY", "Sức khỏe", 10, "EVERY_DAY", "", today(), 2, today()),
            Task(3, "Đi bộ 20 phút", "TODO", "Sức khỏe", 10, "ONCE", "", "", 0, "")
        ), isLoading = false), {})
    }
}

@Preview(name = "Tasks · empty", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun TasksScreenEmptyPreview() { MamXanhPreviewTheme { TasksScreen(TasksUiState(isLoading = false), {}) } }

@Preview(name = "Quest progress header", showBackground = true, widthDp = 360)
@Composable
fun TaskQuestHeaderPreview() { MamXanhPreviewTheme { TaskQuestHeader(2, 6, 2f / 6f, false, {}) } }

@Preview(name = "Daily reward card", showBackground = true, widthDp = 360)
@Composable
fun DailyRewardCardPreview() { MamXanhPreviewTheme { DailyRewardCard(true, {}) } }

@Preview(name = "Task cards, large text", showBackground = true, widthDp = 360, fontScale = 1.3f)
@Preview(name = "Task cards · active and complete", showBackground = true, widthDp = 360)
@Composable
fun TaskCardPreview() {
    MamXanhPreviewTheme {
        TaskCard(Task(1, "Đọc 10 trang sách", "HABIT", "Học tập", 10, "EVERY_DAY", "", "", 4, ""), {}, {})
    }
}
