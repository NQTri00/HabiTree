package com.mamxanh.app.feature.tasks.presentation.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.mamxanh.app.core.components.components.*
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.feature.tasks.domain.model.TaskConfig
import com.mamxanh.app.feature.tasks.presentation.ui.form.message
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.model.Balance
import com.mamxanh.shared.model.clockLabel

@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onSave: (TaskConfig) -> Unit,
    initial: TaskConfig = TaskConfig(category = AppText.canonical(TextKey.CopyHocTapA51516)),
    draft: List<String> = emptyList(),
    onDraftChange: (List<String>) -> Unit = {},
) {
    val defaults =
        listOf(
            initial.title,
            initial.kind,
            initial.category,
            initial.description,
            initial.frequency,
            initial.days,
            initial.due,
            initial.verification,
            initial.minutes.toString(),
            clockLabel(initial.startMinute),
            clockLabel(initial.endMinute),
            clockLabel(initial.reminderMinute),
        )
    var values by rememberSaveable { mutableStateOf(if (draft.size == 12)draft else defaults) }
    var error by rememberSaveable { mutableStateOf("") }
    var discard by rememberSaveable { mutableStateOf(false) }
    val latestDraft by rememberUpdatedState(onDraftChange)
    LaunchedEffect(values) { latestDraft(values) }

    fun update(
        index: Int,
        value: String,
    ) {
        values = values.toMutableList().apply { this[index] = value }
    }

    fun dismiss() {
        if (values != defaults)discard = true else onDismiss()
    }

    fun minute(value: String): Int {
        if (value.isBlank())return -1
        val p = value.split(':').map { it.toIntOrNull() ?: return -2 }
        return if (p.size == 2 && p[0] in 0..23 && p[1] in 0..59)p[0] * 60 + p[1] else -2
    }
    MintDialog(
        if (initial.title.isBlank()) {
            AppText.text(
                TextKey.CopyThemViecC371c2,
            )
        } else {
            AppText.text(TextKey.CopySuaViec55ba70)
        },
        ::dismiss,
        AppText.text(TextKey.CopyLuuE91f26),
        {
            val c =
                TaskConfig(
                    values[0],
                    values[1],
                    values[2],
                    values[3],
                    if (values[1] == "TODO")"ONCE" else values[4],
                    values[5],
                    values[6],
                    values[7],
                    values[8].toIntOrNull() ?: 0,
                    minute(values[9]),
                    minute(values[10]),
                    minute(values[11]),
                )
            error = c.validationError()?.message().orEmpty()
            if (error.isEmpty())onSave(c)
        },
        cancelLabel = AppText.text(TextKey.CopyHuy74fcd3),
    ) {
        Text(
            AppText.text(TextKey.CopyViecMoiLaTuyChonHomNayLichMoiTuNgayMai984ba2),
            color = com.mamxanh.app.core.components.Muted,
        )
        TaskInput(values[0], { update(0, it.take(100)) }, AppText.text(TextKey.CopyTenViec1100312f5b), Modifier.fillMaxWidth())
        TaskChoices(
            AppText.text(TextKey.CopyLoai3bfbf9),
            listOf(
                "HABIT" to AppText.text(TextKey.CopyThoiQuenE6e6e8),
                "DAILY" to AppText.text(TextKey.CopyHangNgay545b54),
                "TODO" to AppText.text(TextKey.CopyMotLanCa8e62),
            ),
            values[1],
        ) { update(1, it) }
        TaskChoices(
            AppText.text(TextKey.CopyNhomCd7a71),
            listOf(
                AppText.canonical(TextKey.CopyHocTapA51516),
                AppText.canonical(TextKey.CopySucKhoe071df7),
                AppText.canonical(TextKey.CopyNepSong8d1a98),
                AppText.canonical(TextKey.CopyViecNha126ef3),
                AppText.canonical(TextKey.CopyCaNhan983dcc),
            ).map {
                it to
                    AppText.category(it)
            },
            values[2],
        ) { update(2, it) }
        if (values[1] != "TODO") {
            TaskChoices(
                AppText.text(TextKey.CopyLichBe87b3),
                listOf(
                    "EVERY_DAY" to AppText.text(TextKey.CopyMoiNgay8270cb),
                    "WEEKDAYS" to AppText.text(TextKey.CopyNgayThuongEf45e7),
                    "WEEKENDS" to AppText.text(TextKey.CopyCuoiTuanA0fb27),
                    "CUSTOM" to AppText.text(TextKey.CopyTuChon905069),
                ),
                values[4],
            ) { update(4, it) }
            if (values[4] == "CUSTOM") {
                Text(AppText.text(TextKey.CopyChonNgayD724ec))
                FlowRow(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp4),
                    verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp4),
                ) {
                    listOf("CN", "T2", "T3", "T4", "T5", "T6", "T7").forEachIndexed { i, label ->
                        GardenChoiceChip(label, i.toString() in values[5], onClick = {
                            update(5, if (i.toString() in values[5])values[5].replace(i.toString(), "") else values[5] + i)
                        }, modifier = Modifier.widthIn(min = AppDimensions.Dp48))
                    }
                }
            }
        } else {
            TaskInput(
                values[6],
                { update(6, it) },
                AppText.text(TextKey.CopyHanChotYyyyMmDdTuyChonB14023),
                Modifier.fillMaxWidth(),
            )
        }
        TaskChoices(
            AppText.text(TextKey.CopyXacNhan1503b0),
            listOf("CLICK" to "Click", "TIMER" to "Timer"),
            values[7],
        ) { update(7, it) }
        if (values[7] ==
            "TIMER"
        ) {
            TaskInput(values[8], {
                update(8, it.filter(Char::isDigit).take(3))
            }, AppText.text(TextKey.CopyPhutHoatDong52400e7a6f), Modifier.fillMaxWidth())
        }
        GardenExpandableSection(AppText.text(TextKey.CopyThemChiTietAe1911)) {
            TaskInput(values[3], { update(3, it.take(500)) }, AppText.text(TextKey.CopyMoTaToiDa5005b2042), Modifier.fillMaxWidth())
            Text(AppText.text(TextKey.CopyKhungGioTuyChonHhMmKetThucSomHonBatDauNgA49a89))
            TaskInput(values[9], { update(9, it) }, AppText.text(TextKey.CopyBatDauHhMmAf5f85), Modifier.fillMaxWidth())
            TaskInput(values[10], { update(10, it) }, AppText.text(TextKey.CopyKetThucHhMm8a0762), Modifier.fillMaxWidth())
            TaskInput(values[11], { update(11, it) }, AppText.text(TextKey.CopyNhacViecHhMmTuyChon952c81), Modifier.fillMaxWidth())
            Text(AppText.text(TextKey.CopyThongBaoCanQuyenHeThongTuChoiQuyenVanLuuD26414d))
        }
        Text(
            AppText.text(
                TextKey.CopyThuong0XuTongToiDa1XuNgay717b49,
                Balance.reward(values[7], values[8].toIntOrNull() ?: 5),
                Balance.DAILY_CAP,
            ),
        )
        if (error.isNotEmpty())Text(error, color = MaterialTheme.colorScheme.error)
    }
    if (discard) {
        MintDialog(
            AppText.text(TextKey.CopyBoThayDoiB07820),
            { discard = false },
            AppText.text(TextKey.CopyBoThayDoiE01800),
            onDismiss,
            cancelLabel = AppText.text(TextKey.CopyTiepTucSua13f645),
            destructive = true,
        ) {
            Text(AppText.text(TextKey.CopyNoiDungChuaLuuSeBiMat4228e1))
        }
    }
}

@Composable
private fun TaskInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier,
) {
    Column {
        Text(label, color = com.mamxanh.app.core.components.Deep)
        GardenTextField(value, onValueChange, label, modifier)
    }
}

@Composable
private fun TaskChoices(
    title: String,
    options: List<Pair<String, String>>,
    value: String,
    onChange: (String) -> Unit,
) {
    Column {
        Text(title)
        var expanded by remember { mutableStateOf(false) }
        Box {
            GardenFieldButton(options.firstOrNull { it.first == value }?.second ?: value, { expanded = true }, Modifier.fillMaxWidth())
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { (key, label) ->
                    DropdownMenuItem(text = { Text(label) }, onClick = {
                        onChange(key)
                        expanded = false
                    })
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(widthDp = 414, heightDp = 1100, device = "spec:width=414dp,height=1100dp,dpi=160")
@Composable
fun TaskFormWebDesignPreview() {
    com.mamxanh.app.core.components
        .MamXanhPreviewTheme { AddTaskDialog({}, {}) }
}
