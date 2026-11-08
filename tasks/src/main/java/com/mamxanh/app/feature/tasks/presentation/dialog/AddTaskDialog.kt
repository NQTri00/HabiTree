package com.mamxanh.app.feature.tasks.presentation.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mamxanh.app.presentation.ui.*

@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, Int, String, String) -> Unit
) {
    val form = remember { AddTaskFormState() }
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.94f).widthIn(max = 480.dp).heightIn(max = 700.dp),
            shape = RoundedCornerShape(30.dp),
            color = Cream,
            shadowElevation = 18.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White)
        ) {
            Column {
                AddTaskDialogHeader()
                Column(
                    modifier = Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    AddTaskDialogFields(form)
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 18.dp, end = 18.dp, bottom = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Để sau", color = Green, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = {
                            onSave(
                                form.title.trim(),
                                form.kind,
                                form.category,
                                (form.reward.toIntOrNull() ?: 10).coerceIn(1, 100),
                                form.frequency,
                                form.due.trim()
                            )
                        },
                        enabled = form.title.isNotBlank(),
                        modifier = Modifier.weight(1.4f),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Green)
                    ) {
                        Text("🌱  Gieo nhiệm vụ", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun AddTaskDialogHeader() {
    Surface(color = Green, shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)) {
        Box {
            Surface(
                color = Color.White.copy(alpha = 0.08f),
                shape = CircleShape,
                modifier = Modifier.size(130.dp).align(Alignment.TopEnd).offset(x = 24.dp, y = (-44).dp)
            ) {}
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Surface(color = Color.White.copy(alpha = 0.16f), shape = RoundedCornerShape(20.dp), modifier = Modifier.size(58.dp)) {
                    Box(contentAlignment = Alignment.Center) { Text("🌱", style = MaterialTheme.typography.headlineMedium) }
                }
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text("Gieo một nhiệm vụ", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                    Text("Bắt đầu từ một việc nhỏ hôm nay.", color = Color.White.copy(alpha = 0.86f), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun AddTaskDialogFields(form: AddTaskFormState) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = form.title,
            onValueChange = { form.title = it },
            label = { Text("Tên nhiệm vụ") },
            placeholder = { Text("Ví dụ: Đọc 10 trang sách") },
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
            colors = taskFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )
        TaskFormSection(title = "Chọn nhịp điệu", emoji = "🗓️") {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf("HABIT" to "Thói quen", "DAILY" to "Hằng ngày", "TODO" to "Một lần")
                    .chunked(2).forEach { row ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            row.forEach { (value, label) ->
                                TaskChoiceChip(
                                    label = label,
                                    selected = form.kind == value,
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        form.kind = value
                                        form.frequency = if (value == "TODO") "ONCE" else "EVERY_DAY"
                                    }
                                )
                            }
                            if (row.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
            }
        }
        TaskFormSection(title = "Chọn khu vườn", emoji = "🪴") {
            Box {
                OutlinedButton(
                    onClick = { form.categoryMenu = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Deep)
                ) { Text("${form.category}     ▾", fontWeight = FontWeight.SemiBold) }
                DropdownMenu(expanded = form.categoryMenu, onDismissRequest = { form.categoryMenu = false }) {
                    listOf("Học tập", "Sức khỏe", "Nếp sống", "Công việc", "Phát triển bản thân", "Khác").forEach {
                        DropdownMenuItem(text = { Text(it) }, onClick = { form.category = it; form.categoryMenu = false })
                    }
                }
            }
        }
        if (form.kind != "TODO") {
            TaskFormSection(title = "Lặp lại", emoji = "☀️") {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("EVERY_DAY" to "Mỗi ngày", "WEEKDAYS" to "Ngày thường", "WEEKENDS" to "Cuối tuần")
                        .chunked(2).forEach { row ->
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                row.forEach { (value, label) ->
                                    TaskChoiceChip(label, form.frequency == value, Modifier.weight(1f)) {
                                        form.frequency = value
                                    }
                                }
                                if (row.size == 1) Spacer(Modifier.weight(1f))
                            }
                        }
                }
            }
        } else {
            OutlinedTextField(
                value = form.due,
                onValueChange = { form.due = it },
                label = { Text("Hạn chót (không bắt buộc)") },
                placeholder = { Text("YYYY-MM-DD") },
                singleLine = true,
                shape = RoundedCornerShape(18.dp),
                colors = taskFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )
        }
        TaskFormSection(title = "Phần thưởng", emoji = "🪙") {
            OutlinedTextField(
                value = form.reward,
                onValueChange = { form.reward = it.filter(Char::isDigit).take(3) },
                label = { Text("Xu (1–100)") },
                singleLine = true,
                shape = RoundedCornerShape(18.dp),
                colors = taskFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun TaskFormSection(title: String, emoji: String, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        color = Color.White.copy(alpha = 0.82f),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Green.copy(alpha = 0.10f))
    ) {
        Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("$emoji  $title", color = Deep, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
            content()
        }
    }
}

@Composable
private fun TaskChoiceChip(label: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        label = { Text(label, maxLines = 1) },
        shape = RoundedCornerShape(14.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Pale,
            selectedLabelColor = Deep
        )
    )
}

@Composable
private fun taskFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Green,
    focusedLabelColor = Green,
    cursorColor = Green,
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White
)

private class AddTaskFormState(
    initialTitle: String = "",
    initialKind: String = "DAILY",
    initialCategory: String = "Học tập",
    initialReward: String = "10",
    initialFrequency: String = "EVERY_DAY",
    initialDue: String = ""
) {
    var title by mutableStateOf(initialTitle)
    var kind by mutableStateOf(initialKind)
    var category by mutableStateOf(initialCategory)
    var reward by mutableStateOf(initialReward)
    var frequency by mutableStateOf(initialFrequency)
    var due by mutableStateOf(initialDue)
    var categoryMenu by mutableStateOf(false)
}

@Preview(name = "Task popup · daily, large text", showBackground = true, widthDp = 360, heightDp = 800, fontScale = 1.3f)
@Preview(name = "Task popup · daily", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun AddTaskDialogPreview() {
    MamXanhPreviewTheme { AddTaskDialogPreviewContent() }
}

@Preview(name = "Task dialog surface", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun AddTaskDialogPreviewContent(initialKind: String = "DAILY") {
    Surface(
        modifier = Modifier.padding(12.dp).widthIn(max = 440.dp),
        shape = RoundedCornerShape(30.dp),
        color = Cream,
        shadowElevation = 12.dp
    ) {
        Column {
            AddTaskDialogHeader()
            val form = remember(initialKind) {
                AddTaskFormState(
                    if (initialKind == "TODO") "Gom góc học tập" else "Đọc 10 trang sách",
                    initialKind,
                    if (initialKind == "TODO") "Nếp sống" else "Phát triển bản thân",
                    if (initialKind == "TODO") "15" else "10",
                    if (initialKind == "TODO") "ONCE" else "EVERY_DAY"
                )
            }
            Column(
                Modifier.heightIn(max = 560.dp).verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) { AddTaskDialogFields(form) }
            Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TextButton(onClick = {}) { Text("Để sau", color = Green) }
                Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = Green)) { Text("🌱  Gieo nhiệm vụ") }
            }
        }
    }
}

@Preview(name = "Task popup · header", showBackground = true, widthDp = 360)
@Composable
fun AddTaskDialogHeaderPreview() {
    MamXanhPreviewTheme { AddTaskDialogHeader() }
}

@Preview(name = "Task popup · form fields", showBackground = true, widthDp = 360)
@Composable
fun TaskDialogFieldsCardPreview() {
    MamXanhPreviewTheme {
        Surface(color = Cream, shape = RoundedCornerShape(22.dp), modifier = Modifier.padding(12.dp)) {
            Column(Modifier.padding(12.dp)) {
                AddTaskDialogFields(remember { AddTaskFormState("Đọc 10 trang sách") })
            }
        }
    }
}

@Preview(name = "Task form section", showBackground = true)
@Composable
fun TaskFormSectionPreview() {
    MamXanhPreviewTheme {
        TaskFormSection("Chọn nhịp điệu", "🗓️") {
            Text("Mỗi việc nhỏ giúp khu vườn lớn lên.", color = Muted)
        }
    }
}

@Preview(name = "Task choice", showBackground = true)
@Composable
fun TaskChoiceChipPreview() {
    MamXanhPreviewTheme {
        TaskChoiceChip("Hằng ngày", selected = true, onClick = {})
    }
}

@Preview(name = "Task popup · one time, large text", showBackground = true, widthDp = 360, heightDp = 800, fontScale = 1.3f)
@Preview(name = "Task popup · one time", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun AddTaskDialogOneTimePreview() {
    MamXanhPreviewTheme { AddTaskDialogPreviewContent(initialKind = "TODO") }
}
