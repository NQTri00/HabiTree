package com.mamxanh.app.feature.tasks.presentation.ui.detail
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mamxanh.app.core.components.*
import com.mamxanh.app.core.components.components.*
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppTypography
import com.mamxanh.app.feature.tasks.domain.model.Task
import com.mamxanh.app.feature.tasks.presentation.ui.list.TasksAction
import com.mamxanh.app.feature.tasks.presentation.ui.list.TasksState
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

@Composable
fun TaskDetails(
    task: Task,
    state: TasksState,
    onIntent: (TasksAction) -> Unit,
) {
    val body: @Composable () -> Unit = {
        Surface(
            Modifier.widthIn(max = AppDimensions.Dp440).fillMaxWidth(.94f).heightIn(max = AppDimensions.Dp650),
            shape = RoundedCornerShape(AppDimensions.Dp28),
            color = Cream,
        ) {
            Column(
                Modifier.verticalScroll(rememberScrollState()).padding(AppDimensions.Dp20),
                verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp16),
            ) {
                GardenBotanicalFrame(Modifier.fillMaxWidth(), skin = GardenPanelSkin.HINT, cornerSize = AppDimensions.Dp24) {
                    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp10)) {
                        MintStatusPill(AppText.category(task.category))
                        Text(
                            task.title,
                            color = Deep,
                            fontSize = AppTypography.Sp26,
                            lineHeight = AppTypography.Sp34,
                            fontWeight = FontWeight.Bold,
                        )
                        if (task.config.description.isNotBlank())Text(task.config.description, color = Muted, fontSize = AppTypography.Sp12)
                    }
                }
                MintMetricStrip(
                    listOf(
                        AppText.text(TextKey.CopyThuong657cfa) to AppText.text(TextKey.Copy0Xu7c2391, task.reward),
                        AppText.text(TextKey.CopyChuoi8f6fe1) to AppText.text(TextKey.Copy0Ngay45fa48, task.streak),
                    ),
                )
                GardenExpandableSection(AppText.text(TextKey.CopyLichXacNhan263bc4)) {
                    Text(
                        "${AppText.category(task.category)} · ${when (task.kind){
                            "HABIT" -> AppText.text(TextKey.CopyThoiQuenE6e6e8)
                            "DAILY" -> AppText.text(TextKey.CopyHangNgay545b54)
                            else -> AppText.text(TextKey.CopyMotLanCa8e62)
                        }}",
                    )
                    Text(
                        when (task.frequency) {
                            "WEEKDAYS" -> AppText.text(TextKey.CopyThuHaiDenThuSau78c8f6)
                            "WEEKENDS" -> AppText.text(TextKey.CopyCuoiTuanA0fb27)
                            "CUSTOM" ->
                                AppText.text(
                                    TextKey.CopyNgay0D9f894,
                                    task.config.days
                                        .map {
                                            if (it ==
                                                '0'
                                            ) {
                                                "CN"
                                            } else {
                                                "T${it.digitToInt() + 1}"
                                            }
                                        }.joinToString(),
                                )
                            ;"ONCE" ->
                                AppText.text(
                                    TextKey.CopyMotLanHan00809b4,
                                    task.due.ifBlank { AppText.text(TextKey.CopyKhongCoAf48b6) },
                                )
                            else -> AppText.text(TextKey.CopyMoiNgay8270cb)
                        },
                    )
                    Text(
                        if (task.config.verification ==
                            "TIMER"
                        ) {
                            AppText.text(TextKey.CopyTimer0PhutHoatDong0027f1, task.config.minutes)
                        } else {
                            AppText.text(TextKey.CopyClickXacNhanMotLan8007ac)
                        },
                    )
                }
                Text(
                    when {
                        task.doneToday || task.finished -> AppText.text(TextKey.CopyDaHoanThanh14eefb)
                        task.lifecycle == "PAUSED" -> AppText.text(TextKey.CopyDangTamDungE40cc1)
                        task.lifecycle ==
                            "ARCHIVED" -> AppText.text(TextKey.CopyDaLuuTru6aff2a)
                        else -> AppText.text(TextKey.CopyDangHoatDong767d67)
                    },
                )
                if (task.availability.isNotEmpty())Text(task.availability)
                GardenExpandableSection(AppText.text(TextKey.Copy7NgayGanDay486803)) {
                    Text(
                        AppText.text(
                            TextKey.CopyLanCuoi097f1a4,
                            task.lastDone.ifBlank {
                                AppText.text(TextKey.CopyChuaCoBc2b97)
                            },
                        ),
                        color = Muted,
                        fontSize = AppTypography.Sp12,
                    )
                    task.history.forEach { Text(AppText.text(TextKey.Copy012Xu9a6d2e, it.date, it.granted, it.nominal)) }
                    if (task.history.isEmpty())Text(AppText.text(TextKey.CopyChuaCoLanHoanThanh5b7c8b))
                }
                if (task.config.verification ==
                    "CLICK"
                ) {
                    GardenPaintedButton(
                        AppText.text(TextKey.CopyHoanThanh5eb36f),
                        { onIntent(TasksAction.Complete(task.id)) },
                        Modifier.fillMaxWidth(),
                        enabled = !state.busy && task.available && !task.doneToday && !task.finished && task.lifecycle == "ACTIVE",
                    )
                }
                state.phase.sessions.firstOrNull { it.taskId == task.id }?.let { session ->
                    Text(
                        AppText.text(
                            TextKey.CopyHoatDong012Phut79ed9b,
                            session.activeSeconds / 60,
                            (session.activeSeconds % 60).toString().padStart(2, '0'),
                            session.requiredSeconds / 60,
                        ),
                    )
                    Text(AppText.text(TextKey.CopyRaNenSeTamDungKhoiDongLaiCanTiepTucThuCo560f6d))
                    GardenPaintedButton(
                        if (session.running) {
                            AppText.text(
                                TextKey.CopyTamDung676358,
                            )
                        } else {
                            AppText.text(TextKey.CopyTiepTuc87a7ae)
                        },
                        {
                            onIntent(TasksAction.Timer(task.id, if (session.running)"PAUSE" else "RESUME"))
                        },
                        Modifier.fillMaxWidth(),
                        enabled = !state.busy,
                    )
                    GardenPaintedButton(
                        AppText.text(TextKey.CopyHoanThanh5eb36f),
                        { onIntent(TasksAction.Complete(task.id)) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.busy && session.activeSeconds >= session.requiredSeconds,
                    )
                } ?: if (task.config.verification == "TIMER" && !task.doneToday) {
                    GardenPaintedButton(
                        AppText.text(TextKey.CopyBatDauTimer141cca),
                        { onIntent(TasksAction.Timer(task.id, "START")) },
                        Modifier.fillMaxWidth(),
                        enabled =
                            !state.busy && task.available && task.lifecycle == "ACTIVE" && !task.finished,
                    )
                } else {
                    Unit
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp12)) {
                    GardenPaintedButton(
                        AppText.text(TextKey.CopySuaD4e076),
                        { onIntent(TasksAction.Edit(task.id)) },
                        Modifier.weight(1f),
                        primary = false,
                    )
                    GardenPaintedButton(
                        if (task.lifecycle ==
                            "ACTIVE"
                        ) {
                            AppText.text(TextKey.CopyTamDungViecC4c909)
                        } else {
                            AppText.text(TextKey.CopyTiepTucViec255a2e)
                        },
                        {
                            onIntent(TasksAction.Lifecycle(task.id, if (task.lifecycle == "ACTIVE")"PAUSED" else "ACTIVE"))
                        },
                        Modifier.weight(1f),
                        primary = false,
                    )
                }
                GardenExpandableSection(AppText.text(TextKey.CopyQuanLyViec64cecb)) {
                    GardenPaintedButton(AppText.text(TextKey.ActionArchive), {
                        onIntent(TasksAction.Lifecycle(task.id, "ARCHIVED"))
                        onIntent(TasksAction.Detail(null))
                    }, Modifier.fillMaxWidth(), primary = false)
                }
                GardenPaintedButton(
                    AppText.text(TextKey.CopyDong5d54c2),
                    { onIntent(TasksAction.Detail(null)) },
                    Modifier.fillMaxWidth(),
                    primary = false,
                )
            }
        }
    }
    if (androidx.compose.ui.platform.LocalInspectionMode.current) {
        MintBackdrop { Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) { body() } }
    } else {
        Dialog(
            onDismissRequest = { onIntent(TasksAction.Detail(null)) },
            properties = DialogProperties(usePlatformDefaultWidth = false),
        ) { body() }
    }
}

@androidx.compose.ui.tooling.preview.Preview(widthDp = 414, heightDp = 1100, device = "spec:width=414dp,height=1100dp,dpi=160")
@Composable
fun TaskDetailWebDesignPreview() {
    MamXanhPreviewTheme {
        TaskDetails(
            Task(
                1,
                AppText.text(TextKey.CopyDoc10TrangSachA3c873),
                "HABIT",
                AppText.canonical(TextKey.CopyHocTapA51516),
                8,
                "EVERY_DAY",
                "",
                "",
                4,
                "",
            ),
            TasksState(isLoading = false),
            {
            },
        )
    }
}
