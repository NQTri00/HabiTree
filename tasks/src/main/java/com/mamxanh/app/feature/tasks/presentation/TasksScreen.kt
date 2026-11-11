package com.mamxanh.app.feature.tasks.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.rememberLazyListState
import com.mamxanh.app.presentation.ui.components.mintScrollEdges
import androidx.compose.foundation.lazy.items
import com.mamxanh.app.presentation.ui.components.MintDepth
import com.mamxanh.app.presentation.ui.components.mintDepth
import com.mamxanh.app.presentation.ui.components.MintBackdrop
import com.mamxanh.app.presentation.ui.components.MintHeader
import com.mamxanh.app.presentation.ui.components.MintProgress
import com.mamxanh.app.presentation.ui.components.MintSegments
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
fun TasksScreen(state: TasksUiState, onIntent: (TasksIntent) -> Unit, modifier: Modifier = Modifier, coins: Int = 0) {
    val listState = rememberLazyListState()
    val filtered = state.tasks.filter { it.lifecycle == "ACTIVE" && (it.scheduledToday || it.doneToday || it.finished) }
        .sortedWith(compareBy<Task> { it.doneToday || it.finished }.thenBy { it.id })
    val done = state.phase.completed
    MintBackdrop(modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            MintHeader("Việc nhỏ", coins)
            GardenBotanicalFrame(Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                contentPadding = PaddingValues(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    GardenArtwork(GardenArt.SPROUT, Modifier.size(30.dp))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Đã hoàn thành $done / ${state.phase.required} việc bắt buộc", color = Deep, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        MintProgress(done.toFloat() / state.phase.required.coerceAtLeast(1), Modifier.fillMaxWidth())
                    }
                }
            }
            LazyColumn(Modifier.weight(1f).mintScrollEdges(listState.canScrollBackward, listState.canScrollForward),
                state = listState, contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if(state.isLoading && state.tasks.isEmpty()) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
                if(!state.isLoading && filtered.isEmpty()) item {
                    Column(Modifier.fillMaxWidth().padding(vertical = 24.dp), horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        com.mamxanh.app.presentation.ui.components.MintEmptySprout(Modifier.size(110.dp))
                        Text("Chưa có việc nào",
                            color = Deep, fontSize = 21.sp, fontWeight = FontWeight.Bold,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        Text("Hãy thêm việc nhỏ để xây dựng thói quen mỗi ngày.", color = Deep,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    }
                }
                items(filtered, key = { it.id }) { task ->
                    val session = state.phase.sessions.firstOrNull { it.taskId == task.id }
                    TaskCard(task, { onIntent(TasksIntent.Complete(task.id)) },
                        { onIntent(TasksIntent.Timer(task.id, if (session?.running == true) "PAUSE" else "START")) },
                        readOnly = task.finished, session = session, busy = state.busy)
                    if(task.availability.isNotEmpty()) Text(task.availability,color=Muted,fontSize=12.sp)
                }
                if(state.phase.required > 0) item {
                    DailyRewardCard(state.canClaimDailyBonus && !state.isLoading && !state.busy,
                        { onIntent(TasksIntent.ClaimBonus) }, state.isDailyBonusClaimed)
                }
            }
            GardenPaintedButton("+ Thêm việc", { onIntent(TasksIntent.ShowAddDialog) },
                Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp))
        }
    }
    if (state.isAddingTask) AddTaskDialog(
        draft = state.draft,
        onDraftChange = { onIntent(TasksIntent.UpdateDraft(it)) },
        onDismiss = { onIntent(TasksIntent.DismissAddDialog) },
        initial = state.tasks.firstOrNull{it.id==state.editingId}?.config ?: com.mamxanh.shared.model.TaskConfig(),
        onSave = { onIntent(TasksIntent.Save(it)) }
    )
}

@Composable
private fun TaskQuestHeader(done: Int, total: Int, progress: Float, allComplete: Boolean, onAddTask: () -> Unit) {
    GardenBotanicalFrame(Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Đã hoàn thành $done / $total việc", fontWeight = FontWeight.Bold, color = Deep)
            MintProgress(progress, Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun DailyRewardCard(canClaim: Boolean, onClaim: () -> Unit, claimed: Boolean = false) {
    Box(Modifier.fillMaxWidth()) {
        if (claimed) {
            Surface(Modifier.fillMaxWidth().clickable(enabled = false,
                role = androidx.compose.ui.semantics.Role.Button, onClick = onClaim),
                shape = RoundedCornerShape(22.dp), color = Color(0xFFE0DFD1),
                border = BorderStroke(1.dp, Color(0xFFC8C7B7))) {
                Row(Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(shape = CircleShape, color = Color(0xFF939C80), modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Rounded.Check, null, tint = Color(0xFFFFF9E9), modifier = Modifier.padding(5.dp))
                    }
                    Text("Đã nhận thưởng", Modifier.weight(1f), color = Color(0xFF626756),
                        fontSize = 15.sp, lineHeight = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        } else GardenBotanicalFrame(Modifier.fillMaxWidth().clickable(enabled = canClaim,
            role = androidx.compose.ui.semantics.Role.Button,
            onClickLabel = "Nhận thưởng hôm nay", onClick = onClaim),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
            skin = com.mamxanh.app.presentation.ui.components.GardenPanelSkin.HINT) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Rounded.CardGiftcard, null, Modifier.size(32.dp), tint = Green)
                Text("Thưởng hôm nay", Modifier.weight(1f),
                    color = Deep, fontSize = 15.sp, lineHeight = 18.sp, fontWeight = FontWeight.Bold)
                Text("+${com.mamxanh.shared.model.Balance.DAILY_CLEAR} xu", color = Deep, fontSize = 17.sp, lineHeight = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun TaskFilterSearchArtwork() {
    Box(Modifier.size(48.dp)) {
        Canvas(Modifier.fillMaxSize()) {
            val center = Offset(size.width * .42f, size.height * .40f)
            val radius = size.minDimension * .29f
            drawLine(Color(0xFFD7A15D), Offset(size.width * .63f, size.height * .61f),
                Offset(size.width * .86f, size.height * .84f), 10.dp.toPx(), cap = StrokeCap.Round)
            drawCircle(Color(0xFFFFF8E8), radius, center)
            drawCircle(Color(0xFFE3AD68), radius, center,
                style = androidx.compose.ui.graphics.drawscope.Stroke(3.dp.toPx()))
        }
        GardenArtwork(GardenArt.SPROUT,
            Modifier.align(Alignment.TopStart).offset(11.dp, 10.dp).size(18.dp))
    }
}

@Composable
private fun TaskCard(task: Task, onComplete: () -> Unit, onTimer: () -> Unit, readOnly: Boolean = false,
    session: com.mamxanh.shared.model.TimerSession? = null, busy: Boolean = false) {
    val done = task.doneToday || readOnly
    val timer = task.config.verification == "TIMER"
    Column {
        Row(Modifier.fillMaxWidth().heightIn(min = if (readOnly) 56.dp else 64.dp)
            .mintDepth(RoundedCornerShape(18.dp), if (done) MintDepth.hint else MintDepth.card, 4.dp)
            .clickable(enabled = !timer && !done && !busy && task.available, onClick = onComplete)
            .padding(horizontal = 8.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            GardenArtwork(when {
                task.title.contains("bộ", ignoreCase = true) -> GardenArt.BOOTS
                task.title.contains("nước", ignoreCase = true) -> GardenArt.GLASS
                task.title.contains("Dọn", ignoreCase = true) -> GardenArt.BROOM
                task.title.contains("từ vựng", ignoreCase = true) -> GardenArt.PENCIL
                task.title.contains("Chuẩn bị", ignoreCase = true) -> GardenArt.CALENDAR
                task.category.contains("Học", ignoreCase = true) -> GardenArt.BOOK
                else -> GardenArt.SPROUT
            }, Modifier.size(36.dp, 48.dp))
            Column(Modifier.weight(1f).padding(horizontal = 3.dp)) {
                Text(task.title, color = Deep, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                if (!done) Text(task.category, color = Color(0xFF80562C), fontSize = 12.sp)
                if (task.kind == "TODO" && task.due.isNotBlank()) Text("Hạn: " + task.due, color = Muted, fontSize = 11.sp)
                if (done) Text("Đã xong", color = Green, fontSize = 12.sp)
                else if (timer) {
                    val seconds = session?.activeSeconds ?: 0L
                    Text("${seconds / 60}:${(seconds % 60).toString().padStart(2,'0')} / ${task.config.minutes} phút",
                        color = Green, fontSize = 12.sp)
                }
            }
            if (!readOnly) GardenBotanicalFrame(contentPadding = PaddingValues(horizontal = 5.dp, vertical = 3.dp),
                cornerSize = 9.dp, skin = com.mamxanh.app.presentation.ui.components.GardenPanelSkin.PRICE) {
                Text("🪙+${task.reward} xu", color = Deep, fontSize = 11.sp, lineHeight = 14.sp, fontWeight = FontWeight.Bold)
            }
            IconButton(onClick = if (timer) onTimer else onComplete,
                enabled = !done && !busy && (task.available || session?.running == true),
                modifier = Modifier.size(48.dp).semantics {
                    contentDescription = when { done -> "Đã hoàn thành"; timer && session?.running == true -> "Tạm dừng bộ đếm"; timer -> "Bắt đầu bộ đếm"; else -> "Hoàn thành nhiệm vụ" }
                }) {
                Surface(shape = CircleShape, color = if (done) Color(0xFF7B9D3D) else Color.Transparent,
                    border = if (done) null else BorderStroke(1.5.dp,Color(0xFF9B7854)), modifier = Modifier.size(24.dp)) {
                    if (done) Icon(Icons.Rounded.Check, null, tint = Color(0xFFFFF5DC), modifier = Modifier.padding(3.dp))
                    else if (timer) Icon(if (session?.running == true) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        null, tint = Green, modifier = Modifier.padding(2.dp))
                }
            }

        }

    }
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

@Preview(name = "Tasks · extended document audit", widthDp = 360, heightDp = 900,
    device = "spec:width=360dp,height=900dp,dpi=160")
@Composable
fun TasksExtendedDocumentPreview() {
    MamXanhPreviewTheme {
        TasksScreen(TasksUiState(tasks = listOf(
            Task(1, "Đọc 10 trang sách", "HABIT", "Học tập", 10, "EVERY_DAY", "", "", 0, ""),
            Task(2, "Uống đủ nước", "DAILY", "Sức khỏe", 10, "EVERY_DAY", "", today(), 1, today()),
            Task(3, "Đi bộ 20 phút", "DAILY", "Sức khỏe", 10, "EVERY_DAY", "", today(), 1, today()),
            Task(4, "Ôn từ vựng 15 phút", "DAILY", "Tiếng Anh", 10, "EVERY_DAY", "", "", 0, ""),
            Task(5, "Dọn góc học tập", "TODO", "Giữ gọn gàng", 10, "ONCE", "", "", 0, ""),
            Task(6, "Chuẩn bị bài cho ngày mai", "TODO", "Học tập", 10, "ONCE", "", "", 0, "")
        ), isLoading = false), {})
    }
}

@Preview(name = "Tasks · empty", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun TasksScreenEmptyPreview() { MamXanhPreviewTheme { TasksScreen(TasksUiState(isLoading = false), {}) } }

@Preview(name = "Tasks · empty filter", widthDp = 360, heightDp = 514,
    device = "spec:width=360dp,height=514dp,dpi=160")
@Composable
fun TasksEmptyFilterPreview() {
    MamXanhPreviewTheme {
        TasksScreen(TasksUiState(tasks = listOf(
            Task(1, "Uống đủ nước", "DAILY", "Sức khỏe", 10, "EVERY_DAY", "", "", 0, "")
        ), filter = TaskFilter.HABIT, isLoading = false), {})
    }
}

@Preview(name = "Tasks · daily reward claimed", widthDp = 300, heightDp = 90,
    device = "spec:width=300dp,height=90dp,dpi=160")
@Composable
fun DailyRewardClaimedPreview() {
    MamXanhPreviewTheme {
        Box(Modifier.padding(16.dp)) { DailyRewardCard(false, {}, claimed = true) }
    }
}

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
