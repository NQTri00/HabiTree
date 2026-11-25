package com.mamxanh.app.feature.tasks.presentation.ui.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.mamxanh.app.core.components.*
import com.mamxanh.app.core.components.components.GardenBotanicalFrame
import com.mamxanh.app.core.components.components.GardenChoiceChip
import com.mamxanh.app.core.components.components.GardenNavigationCard
import com.mamxanh.app.core.components.components.GardenPaintedButton
import com.mamxanh.app.core.components.components.GardenPanelSkin
import com.mamxanh.app.core.components.components.GardenScreenIntro
import com.mamxanh.app.core.components.components.MintBackdrop
import com.mamxanh.app.core.components.components.MintCompactHeader
import com.mamxanh.app.core.components.components.MintProgress
import com.mamxanh.app.core.components.components.mintScrollEdges
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppTypography
import com.mamxanh.app.feature.tasks.presentation.dialog.AddTaskDialog
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.app.core.components.components.GardenRichText as Text

@Composable
fun TasksScreen(
    state: TasksState,
    onIntent: (TasksAction) -> Unit,
    modifier: Modifier = Modifier,
    coins: Int = 0,
    onEngagement: (String, Long) -> Unit = { _, _ -> },
) {
    val listState = rememberLazyListState()
    // Completion updates a row in place; moving the first visible key would move the viewport too.
    var selectedFilter by rememberSaveable { mutableStateOf("ACTIVE") }
    val filtered =
        state.tasks
            .filter {
                it.lifecycle == selectedFilter &&
                    (selectedFilter != "ACTIVE" || it.scheduledToday || it.doneToday || it.finished)
            }.sortedBy { it.id }
    val done = state.phase.completed
    MintBackdrop(modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            MintCompactHeader(coins, title = AppText.text(TextKey.CopyMamXanh692a0a))
            GardenScreenIntro(
                AppText.text(TextKey.CopyViecHomNay22a0a4),
                AppText.text(TextKey.CopyTungChutMot238966),
                modifier = Modifier.padding(horizontal = AppDimensions.Dp24, vertical = AppDimensions.Dp12),
            )
            GardenBotanicalFrame(
                Modifier.padding(horizontal = AppDimensions.Dp24).fillMaxWidth(),
                skin = GardenPanelSkin.HINT,
                contentPadding = PaddingValues(AppDimensions.Dp16),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp12)) {
                    Text(
                        AppText.text(TextKey.Copy01ViecBatBuocDaXong0ffc34, done, state.phase.required),
                        color = Deep,
                        fontSize = AppTypography.Sp14,
                        fontWeight = FontWeight.SemiBold,
                    )
                    MintProgress(done.toFloat() / state.phase.required.coerceAtLeast(1), Modifier.fillMaxWidth())
                }
            }
            Row(
                Modifier.padding(horizontal = AppDimensions.Dp24, vertical = AppDimensions.Dp12),
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp12),
            ) {
                GardenPaintedButton(
                    AppText.text(TextKey.CopyTapTrung964002),
                    { onEngagement("FOCUS", 0) },
                    Modifier.weight(1f),
                    primary = false,
                )
                GardenPaintedButton(
                    AppText.text(TextKey.CopyViecMau535dc1),
                    { onEngagement("TEMPLATES", 0) },
                    Modifier.weight(1f),
                    primary = false,
                )
            }
            Row(Modifier.padding(horizontal = AppDimensions.Dp24), horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp8)) {
                listOf(
                    "ACTIVE" to AppText.text(TextKey.CopyHomNayE6747a),
                    "PAUSED" to AppText.text(TextKey.TabPaused),
                    "ARCHIVED" to AppText.text(TextKey.CopyLuuTru7e7c5f),
                ).forEach { (id, label) ->
                    GardenChoiceChip(label, selectedFilter == id, { selectedFilter = id }, Modifier.weight(1f))
                }
            }
            LazyColumn(
                Modifier.weight(1f).mintScrollEdges(listState.canScrollBackward, listState.canScrollForward),
                state = listState,
                contentPadding = PaddingValues(horizontal = AppDimensions.Dp24, vertical = AppDimensions.Dp16),
                verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp14),
            ) {
                if (state.isLoading && state.tasks.isEmpty()) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
                if (!state.isLoading && filtered.isEmpty()) {
                    item {
                        Column(
                            Modifier.fillMaxWidth().padding(vertical = AppDimensions.Dp12),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp12),
                        ) {
                            com.mamxanh.app.core.components.components
                                .MintEmptySprout(Modifier.size(AppDimensions.Dp55))
                            Text(
                                AppText.text(TextKey.CopyChuaCoViecNao2c72b0),
                                color = Deep,
                                fontSize = AppTypography.Sp14,
                                fontWeight = FontWeight.Bold,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            )
                            Text(
                                AppText.text(TextKey.CopyHayThemViecNhoDeXayDungThoiQuenMoiNgay8aeb29),
                                color = Deep,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            )
                        }
                    }
                }
                items(filtered, key = { it.id }) { task ->
                    val session = state.phase.sessions.firstOrNull { it.taskId == task.id }
                    TaskCard(
                        task,
                        { onIntent(TasksAction.Complete(task.id)) },
                        { onIntent(TasksAction.Timer(task.id, if (session?.running == true) "PAUSE" else "START")) },
                        readOnly = task.finished,
                        session = session,
                        busy = state.busy,
                        onOpen = { onIntent(TasksAction.Detail(task.id)) },
                    )
                    if (task.availability.isNotEmpty()) Text(task.availability, color = Muted, fontSize = AppTypography.Sp12)
                }
                item { GardenNavigationCard(AppText.text(TextKey.CopyNhipThoiQuen1b8324), { onEngagement("INSIGHTS", 0) }) }
                if (state.phase.required > 0) {
                    item {
                        DailyRewardCard(
                            state.canClaimDailyBonus && !state.isLoading && !state.busy,
                            { onIntent(TasksAction.ClaimBonus) },
                            state.isDailyBonusClaimed,
                        )
                    }
                }
            }
            GardenPaintedButton(
                AppText.text(TextKey.CopyThemViec3e34ed),
                { onIntent(TasksAction.ShowAddDialog) },
                Modifier.fillMaxWidth().padding(horizontal = AppDimensions.Dp24, vertical = AppDimensions.Dp12),
            )
        }
    }
    if (state.isAddingTask) {
        AddTaskDialog(
            draft = state.draft,
            onDraftChange = { onIntent(TasksAction.UpdateDraft(it)) },
            onDismiss = { onIntent(TasksAction.DismissAddDialog) },
            initial =
                state.tasks.firstOrNull { it.id == state.editingId }?.config
                    ?: com.mamxanh.app.feature.tasks.domain.model
                        .TaskConfig(category = AppText.canonical(TextKey.CopyHocTapA51516)),
            onSave = { onIntent(TasksAction.Save(it)) },
        )
    }
}
