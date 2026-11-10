package com.mamxanh.app.feature.tasks.presentation.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
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
fun AddTaskDialog(
    onDismiss: () -> Unit, onSave: (String, String, String, Int, String, String) -> Unit,
    draft: List<String> = emptyList(), onDraftChange: (List<String>) -> Unit = {}
) {
    val form = rememberSaveable(saver = AddTaskFormSaver) { restoreTaskForm(draft) }
    val updateDraft by rememberUpdatedState(onDraftChange)
    LaunchedEffect(form) {
        snapshotFlow { form.savedValues() }.collect { updateDraft(it) }
    }
    var confirmingDiscard by rememberSaveable { mutableStateOf(false) }
    val requestDismiss = {
        if (form.isDirty) confirmingDiscard = true else onDismiss()
    }
    Dialog(onDismissRequest = requestDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Box(Modifier.matchParentSize().pointerInput(requestDismiss) {
                detectTapGestures { requestDismiss() }
            })
            Box(Modifier.safeDrawingPadding()) {
                AddTaskParchment(form, requestDismiss, onSave, TaskDialogBounds.pointerInput(Unit) {
                    detectTapGestures { /* Keep blank parchment taps inside the form. */ }
                })
            }
        }
    }
    if (confirmingDiscard) {
        Dialog(onDismissRequest = { confirmingDiscard = false },
            properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)) {
            Box(Modifier.fillMaxSize().safeDrawingPadding().padding(16.dp), contentAlignment = Alignment.Center) {
                TaskDiscardConfirmation(
                    onContinue = { confirmingDiscard = false },
                    onDiscard = { confirmingDiscard = false; onDismiss() })
            }
        }
    }
}

@Composable
private fun TaskDiscardConfirmation(onContinue: () -> Unit, onDiscard: () -> Unit) {
    GardenBotanicalFrame(Modifier.widthIn(max = 400.dp).fillMaxWidth().heightIn(max = 400.dp),
        contentPadding = PaddingValues(18.dp), cornerSize = 28.dp, skin = GardenPanelSkin.DIALOG) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Bỏ thay đổi?", color = Deep, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Bạn có muốn bỏ các thông tin đang nhập?", color = Deep,
                fontStyle = FontStyle.Italic, textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState()))
            Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GardenPaintedButton("Tiếp tục sửa", onContinue, Modifier.weight(1f).fillMaxHeight(), primary = false)
                GardenPaintedButton("Bỏ thay đổi", onDiscard, Modifier.weight(1f).fillMaxHeight())
            }
        }
    }
}

@Preview(name = "Discard task draft · narrow, large text", widthDp = 324, fontScale = 1.3f,
    device = "spec:width=324dp,height=300dp,dpi=160")
@Composable
fun TaskDiscardConfirmationPreview() {
    MamXanhPreviewTheme { TaskDiscardConfirmation({}, {}) }
}

@Composable
private fun AddTaskParchment(
    form: AddTaskFormState, onDismiss: () -> Unit,
    onSave: (String, String, String, Int, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier) {
        val shortWindow = maxHeight < 360.dp * androidx.compose.ui.platform.LocalDensity.current.fontScale
        val veryShortWindow = maxHeight < 220.dp
        val scroll = rememberScrollState()
        GardenBotanicalFrame(Modifier.fillMaxWidth().heightIn(max = maxHeight),
            contentPadding = PaddingValues(horizontal = 24.dp,
                vertical = if (veryShortWindow) 0.dp else if (shortWindow) 12.dp else 24.dp),
            cornerSize = 40.dp, skin = GardenPanelSkin.TASK_DIALOG) {
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(if (veryShortWindow) 0.dp else 8.dp)) {
                if (!shortWindow) AddTaskDialogHeader(onDismiss)
                Column(Modifier.weight(1f, fill = false).verticalScroll(scroll)) {
                    if (shortWindow) {
                        AddTaskDialogHeader(onDismiss)
                        Spacer(Modifier.height(8.dp))
                    }
                    AddTaskDialogFields(form)
                }
                Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    GardenPaintedButton("Để sau", onDismiss, Modifier.weight(.8f).fillMaxHeight(), primary = false)
                    GardenPaintedButton("Gieo nhiệm vụ", {
                        form.validationRequested = true
                        val reward = form.reward.toIntOrNull()
                        if (form.title.isNotBlank() && reward != null && reward in 1..100) {
                            onSave(form.title.trim(), form.kind, form.category, reward, form.frequency, form.due.trim())
                        }
                    }, Modifier.weight(1.3f).fillMaxHeight())
                }
            }
        }
    }
}

@Composable
private fun AddTaskDialogHeader(onDismiss: () -> Unit = {}) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            Text("Gieo nhiệm vụ", Modifier.weight(1f).padding(top = 6.dp), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Deep)
            Box(Modifier.offset(x = 12.dp, y = (-12).dp).size(48.dp).clickable(role = Role.Button, onClick = onDismiss)
                .semantics { contentDescription = "Đóng" }, contentAlignment = Alignment.Center) {
                Box(Modifier.size(34.dp).background(Color(0xFFF8FCFA), CircleShape)
                    .border(2.dp, Color(0xFFD2E3DA), CircleShape), contentAlignment = Alignment.Center) {
                    Text("×", color = Deep, fontSize = 24.sp, lineHeight = 26.sp)
                }
            }
        }
        GardenArtwork(GardenArt.SEED, Modifier.size(110.dp, 70.dp).padding(top = 3.dp))
    }
}

@Composable
private fun AddTaskDialogFields(form: AddTaskFormState) {
    var frequencyMenu by remember { mutableStateOf(false) }
    val frequencies = listOf("EVERY_DAY" to "Mỗi ngày", "WEEKDAYS" to "Ngày thường", "WEEKENDS" to "Cuối tuần")
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        TaskFormSection("Tên việc", "📖") {
            GardenTextField(form.title, { form.title = it }, "Tên việc", Modifier.fillMaxWidth(),
                placeholder = "Đọc 10 trang sách",
                errorMessage = if (form.validationRequested && form.title.isBlank()) "Thêm tên việc" else null)
            if (form.validationRequested && form.title.isBlank()) {
                Text("Thêm tên việc", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text("🌱  Loại việc", color = Deep, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf("HABIT" to "Thói quen", "DAILY" to "Hằng ngày", "TODO" to "Một lần").forEach { (value, label) ->
                    TaskChoiceChip(label, form.kind == value, Modifier.weight(1f).fillMaxHeight()) {
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
        TaskFormSection("Phần thưởng", "🪙") {
            GardenTextField(form.reward, { form.reward = it.filter(Char::isDigit).take(3) }, "Phần thưởng từ 1 đến 100 xu",
                Modifier.fillMaxWidth(), trailingText = "xu", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                errorMessage = if (form.validationRequested && form.reward.toIntOrNull()?.let { it in 1..100 } != true)
                    "Nhập từ 1 đến 100 xu" else null)
        }
        if (form.validationRequested && form.reward.toIntOrNull()?.let { it in 1..100 } != true) {
            Text("Nhập từ 1 đến 100 xu", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
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
            Text("$emoji $title", color = Deep, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.width(120.dp))
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
    var validationRequested by mutableStateOf(false)
    val isDirty: Boolean
        get() = title.isNotEmpty() || kind != "DAILY" || category != "Học tập" ||
            reward != "10" || frequency != "EVERY_DAY" || due.isNotEmpty()
}

private fun AddTaskFormState.savedValues() =
    listOf(title, kind, category, reward, frequency, due, validationRequested.toString())

private fun restoreTaskForm(values: List<String>): AddTaskFormState =
    if (values.size < 6) AddTaskFormState()
    else AddTaskFormState(values[0], values[1], values[2], values[3], values[4], values[5]).apply {
        validationRequested = values.getOrNull(6)?.toBooleanStrictOrNull() ?: false
    }

private val AddTaskFormSaver = listSaver<AddTaskFormState, String>(
    save = { it.savedValues() }, restore = ::restoreTaskForm
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
        if (background == null) MintBackdrop(Modifier.fillMaxSize()) {}
        else {
            background()
            Box(Modifier.matchParentSize().background(Color.Black.copy(alpha = .32f)))
        }
        val form = remember(initialKind) { AddTaskFormState("Đọc 10 trang sách", initialKind, initialFrequency = if (initialKind == "TODO") "ONCE" else "EVERY_DAY") }
        AddTaskParchment(form, {}, { _,_,_,_,_,_ -> }, TaskDialogBounds)
    }
}

@Preview(name = "Task popup · short window", widthDp = 640, heightDp = 180, fontScale = 1.3f,
    device = "spec:width=640dp,height=180dp,dpi=160")
@Composable
fun AddTaskShortWindowPreview() { MamXanhPreviewTheme { AddTaskDialogPreviewContent() } }

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
