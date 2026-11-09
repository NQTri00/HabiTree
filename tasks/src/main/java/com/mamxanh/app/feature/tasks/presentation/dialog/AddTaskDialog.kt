package com.mamxanh.app.feature.tasks.presentation.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import com.mamxanh.app.presentation.ui.components.GardenRichText as Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mamxanh.app.presentation.ui.*
import com.mamxanh.app.presentation.ui.components.*

private val TaskDialogBounds = Modifier.widthIn(max = 480.dp).fillMaxWidth(.94f).heightIn(max = 700.dp)

@Composable
fun AddTaskDialog(onDismiss: () -> Unit, onSave: (String, String, String, Int, String, String) -> Unit) {
    val form = rememberSaveable(saver = AddTaskFormSaver) { AddTaskFormState() }
    Dialog(onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)) {
        Box(Modifier.fillMaxSize().safeDrawingPadding(), contentAlignment = Alignment.Center) {
            AddTaskParchment(form, onDismiss, onSave, TaskDialogBounds)
        }
    }
}

@Composable
private fun AddTaskParchment(
    form: AddTaskFormState, onDismiss: () -> Unit,
    onSave: (String, String, String, Int, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    GardenBotanicalFrame(modifier, contentPadding = PaddingValues(horizontal = 24.dp, vertical = 24.dp), cornerSize = 40.dp, skin = GardenPanelSkin.TASK_DIALOG) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            AddTaskDialogHeader()
            Column(Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState())) { AddTaskDialogFields(form) }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                GardenPaintedButton("Để sau", onDismiss, Modifier.weight(.8f), primary = false)
                GardenPaintedButton("Gieo nhiệm vụ", {
                    onSave(form.title.trim(), form.kind, form.category, (form.reward.toIntOrNull() ?: 10).coerceIn(1, 100), form.frequency, form.due.trim())
                }, Modifier.weight(1.3f), enabled = form.title.isNotBlank())
            }
        }
    }
}

@Composable
private fun AddTaskDialogHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        GardenLeafPlaque("Gieo một việc nhỏ", Modifier.fillMaxWidth())
        GardenArtwork(GardenArt.SEED, Modifier.size(70.dp, 42.dp).padding(top = 3.dp))
    }
}

@Composable
private fun AddTaskDialogFields(form: AddTaskFormState) {
    var frequencyMenu by remember { mutableStateOf(false) }
    val frequencies = listOf("EVERY_DAY" to "Mỗi ngày", "WEEKDAYS" to "Ngày thường", "WEEKENDS" to "Cuối tuần")
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        TaskFormSection("Tên việc", "📖") {
            GardenTextField(form.title, { form.title = it }, "Tên việc", Modifier.fillMaxWidth(), placeholder = "Đọc 10 trang sách")
        }
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text("🌱  Loại việc", color = Deep, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf("HABIT" to "Thói quen", "DAILY" to "Hằng ngày", "TODO" to "Một lần").forEach { (value, label) ->
                    TaskChoiceChip(label, form.kind == value, Modifier.weight(1f)) {
                        form.kind = value
                        form.frequency = if (value == "TODO") "ONCE" else "EVERY_DAY"
                    }
                }
            }
        }
        TaskFormSection("Nhóm", "📚") {
            Box {
                GardenFieldButton(form.category, { form.categoryMenu = true }, Modifier.fillMaxWidth())
                DropdownMenu(expanded = form.categoryMenu, onDismissRequest = { form.categoryMenu = false }) {
                    listOf("Học tập", "Sức khỏe", "Nếp sống", "Công việc", "Phát triển bản thân", "Khác").forEach { category ->
                        DropdownMenuItem(text = { Text(category) }, onClick = { form.category = category; form.categoryMenu = false })
                    }
                }
            }
        }
        if (form.kind != "TODO") {
            TaskFormSection("Lặp lại", "🗓") {
                Box {
                    GardenFieldButton(frequencies.firstOrNull { it.first == form.frequency }?.second ?: "Mỗi ngày", { frequencyMenu = true }, Modifier.fillMaxWidth())
                    DropdownMenu(expanded = frequencyMenu, onDismissRequest = { frequencyMenu = false }) {
                        frequencies.forEach { (value, label) ->
                            DropdownMenuItem(text = { Text(label) }, onClick = { form.frequency = value; frequencyMenu = false })
                        }
                    }
                }
            }
        } else {
            TaskFormSection("Hạn chót", "🗓") {
                GardenTextField(form.due, { form.due = it }, "Hạn chót không bắt buộc", Modifier.fillMaxWidth(), placeholder = "YYYY-MM-DD")
            }
            Text("Hạn chót không bắt buộc.", color = Deep, fontSize = 12.sp, fontStyle = FontStyle.Italic)
        }
        TaskFormSection("Thưởng", "🪙") {
            GardenTextField(form.reward, { form.reward = it.filter(Char::isDigit).take(3) }, "Phần thưởng từ 1 đến 100 xu",
                Modifier.fillMaxWidth(), trailingText = "xu", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        }
        Text("Từ 1 đến 100 xu", color = Deep, fontSize = 13.sp, fontStyle = FontStyle.Italic, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun TaskFormSection(title: String, emoji: String, content: @Composable ColumnScope.() -> Unit) {
    if (androidx.compose.ui.platform.LocalDensity.current.fontScale > 1.15f) {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("$emoji $title", color = Deep, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Column(Modifier.fillMaxWidth(), content = content)
        }
    } else {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("$emoji $title", color = Deep, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.width(95.dp))
            Column(Modifier.weight(1f), content = content)
        }
    }
}

@Composable
private fun TaskChoiceChip(label: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    GardenChoiceChip(label, selected, onClick, modifier)
}

private class AddTaskFormState(
    initialTitle: String = "", initialKind: String = "DAILY",
    initialCategory: String = "Học tập", initialReward: String = "10",
    initialFrequency: String = "EVERY_DAY", initialDue: String = ""
) {
    var title by mutableStateOf(initialTitle)
    var kind by mutableStateOf(initialKind)
    var category by mutableStateOf(initialCategory)
    var reward by mutableStateOf(initialReward)
    var frequency by mutableStateOf(initialFrequency)
    var due by mutableStateOf(initialDue)
    var categoryMenu by mutableStateOf(false)
}

private val AddTaskFormSaver = listSaver<AddTaskFormState, String>(
    save = { listOf(it.title, it.kind, it.category, it.reward, it.frequency, it.due) },
    restore = { AddTaskFormState(it[0], it[1], it[2], it[3], it[4], it[5]) }
)

@Preview(name = "Task popup · daily", widthDp = 360, heightDp = 800,
    device = "spec:width=360dp,height=800dp,dpi=160")
@Preview(name = "Task popup · daily, large text", widthDp = 360, heightDp = 800, fontScale = 1.3f,
    device = "spec:width=360dp,height=800dp,dpi=160")
@Composable
fun AddTaskDialogPreview() { MamXanhPreviewTheme { AddTaskDialogPreviewContent() } }

@Preview(name = "Task dialog surface", widthDp = 360, heightDp = 800)
@Composable
fun AddTaskDialogPreviewContent(initialKind: String = "DAILY", background: (@Composable () -> Unit)? = null) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (background == null) GardenForestScene(Modifier.fillMaxSize(), false)
        else {
            background()
            Box(Modifier.matchParentSize().background(Color.Black.copy(alpha = .32f)))
        }
        val form = remember(initialKind) { AddTaskFormState("Đọc 10 trang sách", initialKind, initialFrequency = if (initialKind == "TODO") "ONCE" else "EVERY_DAY") }
        AddTaskParchment(form, {}, { _,_,_,_,_,_ -> }, TaskDialogBounds)
    }
}

@Preview(name = "Task popup · header", widthDp = 300)
@Composable
fun AddTaskDialogHeaderPreview() { MamXanhPreviewTheme { AddTaskDialogHeader() } }

@Preview(name = "Task popup · fields", widthDp = 320)
@Composable
fun TaskDialogFieldsCardPreview() { MamXanhPreviewTheme { AddTaskDialogFields(remember { AddTaskFormState("Đọc 10 trang sách") }) } }

@Preview(name = "Task form section", widthDp = 320)
@Composable
fun TaskFormSectionPreview() { MamXanhPreviewTheme { TaskFormSection("Nhóm", "📚") { Text("Học tập", color = Deep) } } }

@Preview(name = "Task choice", widthDp = 100)
@Composable
fun TaskChoiceChipPreview() { MamXanhPreviewTheme { TaskChoiceChip("Hằng ngày", true, onClick = {}) } }

@Preview(name = "Task popup · one time", widthDp = 360, heightDp = 800,
    device = "spec:width=360dp,height=800dp,dpi=160")
@Preview(name = "Task popup · one time, large text", widthDp = 360, heightDp = 800, fontScale = 1.3f,
    device = "spec:width=360dp,height=800dp,dpi=160")
@Composable
fun AddTaskDialogOneTimePreview() { MamXanhPreviewTheme { AddTaskDialogPreviewContent("TODO") } }
