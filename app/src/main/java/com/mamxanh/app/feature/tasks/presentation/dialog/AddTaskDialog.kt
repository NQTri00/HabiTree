package com.mamxanh.app.feature.tasks.presentation.dialog
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
@Composable
fun AddTaskDialog(onDismiss: () -> Unit, onSave: (String, String, String, Int, String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var kind by remember { mutableStateOf("DAILY") }
    var category by remember { mutableStateOf("Học tập") }
    var reward by remember { mutableStateOf("10") }
    var frequency by remember { mutableStateOf("EVERY_DAY") }
    var due by remember { mutableStateOf("") }
    var categoryMenu by remember { mutableStateOf(false) }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Tạo nhiệm vụ mới", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                OutlinedTextField(title, { title = it }, label = { Text("Tên nhiệm vụ") }, singleLine = true)
                Text("Loại nhiệm vụ", fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("HABIT" to "Thói quen", "DAILY" to "Hằng ngày", "TODO" to "Một lần").forEach { (value, label) ->
                        FilterChip(kind == value, { kind = value; frequency = if (value == "TODO") "ONCE" else "EVERY_DAY" },
                            label = { Text(label) })
                    }
                }
                Box {
                    OutlinedButton(onClick = { categoryMenu = true }) { Text(category) }
                    DropdownMenu(categoryMenu, { categoryMenu = false }) {
                        listOf("Học tập", "Sức khỏe", "Nếp sống", "Công việc", "Phát triển bản thân", "Khác").forEach {
                            DropdownMenuItem(text = { Text(it) }, onClick = { category = it; categoryMenu = false })
                        }
                    }
                }
                if (kind != "TODO") Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    listOf("EVERY_DAY" to "Mỗi ngày", "WEEKDAYS" to "Ngày thường", "WEEKENDS" to "Cuối tuần").forEach { (value, label) ->
                        FilterChip(frequency == value, { frequency = value }, label = { Text(label) })
                    }
                } else OutlinedTextField(due, { due = it }, label = { Text("Hạn chót YYYY-MM-DD (tùy chọn)") }, singleLine = true)
                OutlinedTextField(reward, { reward = it.filter(Char::isDigit).take(3) },
                    label = { Text("Xu thưởng (1–100)") }, singleLine = true)
            }
        },
        confirmButton = { Button(onClick = { onSave(title, kind, category, reward.toIntOrNull() ?: 10, frequency, due) },
            enabled = title.isNotBlank()) { Text("Tạo") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Hủy") } })
}
