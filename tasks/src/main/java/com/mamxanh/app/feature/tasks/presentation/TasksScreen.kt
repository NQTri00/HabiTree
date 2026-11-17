package com.mamxanh.app.feature.tasks.presentation

import com.mamxanh.app.presentation.ui.design.AppColors
import com.mamxanh.app.presentation.ui.design.AppDimensions
import com.mamxanh.app.presentation.ui.design.AppTypography

import com.mamxanh.shared.localization.TextKey

import com.mamxanh.shared.localization.AppText

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
import com.mamxanh.app.presentation.ui.components.GardenNavigationCard
import com.mamxanh.app.presentation.ui.components.GardenScreenIntro
import com.mamxanh.app.presentation.ui.components.GardenPanelSkin
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
import com.mamxanh.app.presentation.ui.components.MintCompactHeader
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
fun TasksScreen(state: TasksUiState, onIntent: (TasksIntent) -> Unit, modifier: Modifier = Modifier, coins: Int = 0,
    onEngagement: (String,Long)->Unit = {_,_->}) {
    val listState = rememberLazyListState()
    // Completion updates a row in place; moving the first visible key would move the viewport too.
    var selectedFilter by rememberSaveable { mutableStateOf("ACTIVE") }
    val filtered = state.tasks.filter { it.lifecycle == selectedFilter && (selectedFilter!="ACTIVE" || it.scheduledToday || it.doneToday || it.finished) }
        .sortedBy { it.id }
    val done = state.phase.completed
    MintBackdrop(modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            MintCompactHeader(coins, title = AppText.text(TextKey.CopyMamXanh692a0a))
            GardenScreenIntro(AppText.text(TextKey.CopyViecHomNay22a0a4),AppText.text(TextKey.CopyTungChutMot238966),modifier=Modifier.padding(horizontal=AppDimensions.Dp24,vertical=AppDimensions.Dp12))
            GardenBotanicalFrame(Modifier.padding(horizontal=AppDimensions.Dp24).fillMaxWidth(),skin=GardenPanelSkin.HINT,contentPadding=PaddingValues(AppDimensions.Dp16)) {
                Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)) {
                    Text(AppText.text(TextKey.Copy01ViecBatBuocDaXong0ffc34,done,state.phase.required),color=Deep,fontSize=AppTypography.Sp14,fontWeight=FontWeight.SemiBold)
                    MintProgress(done.toFloat()/state.phase.required.coerceAtLeast(1),Modifier.fillMaxWidth())
                }
            }
            Row(Modifier.padding(horizontal=AppDimensions.Dp24,vertical=AppDimensions.Dp12),horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)) {
                GardenPaintedButton(AppText.text(TextKey.CopyTapTrung964002),{onEngagement("FOCUS",0)},Modifier.weight(1f),primary=false)
                GardenPaintedButton(AppText.text(TextKey.CopyViecMau535dc1),{onEngagement("TEMPLATES",0)},Modifier.weight(1f),primary=false)
            }
            Row(Modifier.padding(horizontal=AppDimensions.Dp24),horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp8)) {
                listOf("ACTIVE" to AppText.text(TextKey.CopyHomNayE6747a),"PAUSED" to AppText.text(TextKey.TabPaused),"ARCHIVED" to AppText.text(TextKey.CopyLuuTru7e7c5f)).forEach{(id,label)->
                    GardenChoiceChip(label,selectedFilter==id,{selectedFilter=id},Modifier.weight(1f))
                }
            }
            LazyColumn(Modifier.weight(1f).mintScrollEdges(listState.canScrollBackward, listState.canScrollForward),
                state = listState, contentPadding = PaddingValues(horizontal=AppDimensions.Dp24,vertical=AppDimensions.Dp16),
                verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp14)) {
                if(state.isLoading && state.tasks.isEmpty()) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
                if(!state.isLoading && filtered.isEmpty()) item {
                    Column(Modifier.fillMaxWidth().padding(vertical = AppDimensions.Dp12), horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp12)) {
                        com.mamxanh.app.presentation.ui.components.MintEmptySprout(Modifier.size(AppDimensions.Dp55))
                        Text(AppText.text(TextKey.CopyChuaCoViecNao2c72b0),
                            color = Deep, fontSize = AppTypography.Sp14, fontWeight = FontWeight.Bold,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        Text(AppText.text(TextKey.CopyHayThemViecNhoDeXayDungThoiQuenMoiNgay8aeb29), color = Deep,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    }
                }
                items(filtered, key = { it.id }) { task ->
                    val session = state.phase.sessions.firstOrNull { it.taskId == task.id }
                    TaskCard(task, { onIntent(TasksIntent.Complete(task.id)) },
                        { onIntent(TasksIntent.Timer(task.id, if (session?.running == true) "PAUSE" else "START")) },
                        readOnly = task.finished, session = session, busy = state.busy, onOpen={onIntent(TasksIntent.Detail(task.id))})
                    if(task.availability.isNotEmpty()) Text(task.availability,color=Muted,fontSize=AppTypography.Sp12)
                }
                item {GardenNavigationCard(AppText.text(TextKey.CopyNhipThoiQuen1b8324),{onEngagement("INSIGHTS",0)})}
                if(state.phase.required > 0) item {
                    DailyRewardCard(state.canClaimDailyBonus && !state.isLoading && !state.busy,
                        { onIntent(TasksIntent.ClaimBonus) }, state.isDailyBonusClaimed)
                }
            }
            GardenPaintedButton(AppText.text(TextKey.CopyThemViec3e34ed), { onIntent(TasksIntent.ShowAddDialog) },
                Modifier.fillMaxWidth().padding(horizontal = AppDimensions.Dp24, vertical = AppDimensions.Dp12))
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
        Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp14)) {
            Text(AppText.text(TextKey.CopyDaHoanThanh01ViecE78274,done,total), fontWeight = FontWeight.Bold, color = Deep)
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
                shape = RoundedCornerShape(AppDimensions.Dp22), color = AppColors.SwatchFFE0DFD1,
                border = BorderStroke(AppDimensions.Dp1, AppColors.SwatchFFC8C7B7)) {
                Row(Modifier.padding(horizontal = AppDimensions.Dp14, vertical = AppDimensions.Dp8),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp14)) {
                    Surface(shape = CircleShape, color = AppColors.SwatchFF939C80, modifier = Modifier.size(AppDimensions.Dp20)) {
                        Icon(Icons.Rounded.Check, null, tint = AppColors.SwatchFFFFF9E9, modifier = Modifier.padding(AppDimensions.Dp5))
                    }
                    Text(AppText.text(TextKey.CopyDaNhanThuong98b708), Modifier.weight(1f), color = AppColors.SwatchFF626756,
                        fontSize = AppTypography.Sp12, lineHeight = AppTypography.Sp18, fontWeight = FontWeight.Bold)
                }
            }
        } else GardenBotanicalFrame(Modifier.fillMaxWidth().clickable(enabled = canClaim,
            role = androidx.compose.ui.semantics.Role.Button,
            onClickLabel = AppText.text(TextKey.CopyNhanThuongHomNay7b23cc), onClick = onClaim),
            contentPadding = PaddingValues(horizontal = AppDimensions.Dp10, vertical = AppDimensions.Dp8),
            skin = com.mamxanh.app.presentation.ui.components.GardenPanelSkin.HINT) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp6)) {
                Icon(Icons.Rounded.CardGiftcard, null, Modifier.size(AppDimensions.Dp20), tint = Green)
                Text(AppText.text(TextKey.CopyThuongHomNayC015c6), Modifier.weight(1f),
                    color = Deep, fontSize = AppTypography.Sp12, lineHeight = AppTypography.Sp18, fontWeight = FontWeight.Bold)
                Text(AppText.text(TextKey.Copy0Xu7c2391,com.mamxanh.shared.model.Balance.DAILY_CLEAR), color = Deep, fontSize = AppTypography.Sp12, lineHeight = AppTypography.Sp15, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun TaskFilterSearchArtwork() {
    Box(Modifier.size(AppDimensions.Dp48)) {
        Canvas(Modifier.fillMaxSize()) {
            val center = Offset(size.width * .42f, size.height * .40f)
            val radius = size.minDimension * .29f
            drawLine(AppColors.SwatchFFD7A15D, Offset(size.width * .63f, size.height * .61f),
                Offset(size.width * .86f, size.height * .84f), AppDimensions.Dp10.toPx(), cap = StrokeCap.Round)
            drawCircle(AppColors.SwatchFFFFF8E8, radius, center)
            drawCircle(AppColors.SwatchFFE3AD68, radius, center,
                style = androidx.compose.ui.graphics.drawscope.Stroke(AppDimensions.Dp3.toPx()))
        }
        GardenArtwork(GardenArt.SPROUT,
            Modifier.align(Alignment.TopStart).offset(AppDimensions.Dp11, AppDimensions.Dp10).size(AppDimensions.Dp18))
    }
}

@Composable
private fun TaskCard(task: Task, onComplete: () -> Unit, onTimer: () -> Unit, readOnly: Boolean = false,
    session: com.mamxanh.shared.model.TimerSession? = null, busy: Boolean = false, onOpen: ()->Unit = {}) {
    val done = task.doneToday || readOnly
    val timer = task.config.verification == "TIMER"
    Column {
        Row(Modifier.fillMaxWidth().heightIn(min = AppDimensions.Dp48)
            .mintDepth(RoundedCornerShape(AppDimensions.Dp18), if (done) MintDepth.hint else MintDepth.card, AppDimensions.Dp4)
            .clickable(onClick = onOpen)
            .padding(horizontal = AppDimensions.Dp12, vertical = AppDimensions.Dp14), verticalAlignment = Alignment.CenterVertically) {
            GardenArtwork(when {
                task.title.contains(AppText.canonical(TextKey.CopyBoEecf2d), ignoreCase = true) -> GardenArt.BOOTS
                task.title.contains(AppText.canonical(TextKey.CopyNuocA2a970), ignoreCase = true) -> GardenArt.GLASS
                task.title.contains(AppText.canonical(TextKey.CopyDon673082), ignoreCase = true) -> GardenArt.BROOM
                task.title.contains(AppText.canonical(TextKey.CopyTuVungE67acb), ignoreCase = true) -> GardenArt.PENCIL
                task.title.contains(AppText.canonical(TextKey.CopyChuanBiE65470), ignoreCase = true) -> GardenArt.CALENDAR
                task.category.contains(AppText.canonical(TextKey.CopyHoc380b3f), ignoreCase = true) -> GardenArt.BOOK
                else -> GardenArt.SPROUT
            }, Modifier.size(AppDimensions.Dp30, AppDimensions.Dp36))
            Column(Modifier.weight(1f).padding(horizontal = AppDimensions.Dp3)) {
                Text(task.title, color = Deep, fontSize = AppTypography.Sp12, fontWeight = FontWeight.Bold)
                if (!done) Text(AppText.category(task.category), color = Muted, fontSize = AppTypography.Sp12)
                if (task.kind == "TODO" && task.due.isNotBlank()) Text(AppText.text(TextKey.CopyHan59b874) + task.due, color = Muted, fontSize = AppTypography.Sp11)
                if (done) Text(AppText.text(TextKey.CopyDaXong565e93), color = Green, fontSize = AppTypography.Sp12)
                else if (timer) {
                    val seconds = session?.activeSeconds ?: 0L
                    Text(AppText.text(TextKey.Copy012PhutB73be0,seconds / 60,(seconds % 60).toString().padStart(2,'0'),task.config.minutes),
                        color = Green, fontSize = AppTypography.Sp12)
                }
            }
            if (!readOnly) Text("+${task.reward}", color = Green, fontSize = AppTypography.Sp12, fontWeight = FontWeight.SemiBold)
            IconButton(onClick = if (timer) onTimer else onComplete,
                enabled = !done && !busy && task.lifecycle=="ACTIVE" && (task.available || session?.running == true),
                modifier = Modifier.size(AppDimensions.Dp48).semantics {
                    contentDescription = when { done -> AppText.text(TextKey.CopyDaHoanThanh14eefb); timer && session?.running == true -> AppText.text(TextKey.CopyTamDungBoDemE82e42); timer -> AppText.text(TextKey.CopyBatDauBoDem35c256); else -> AppText.text(TextKey.CopyHoanThanhNhiemVuE5ef43) }
                }) {
                Surface(shape = CircleShape, color = if (done) AppColors.SwatchFF7B9D3D else AppColors.Transparent,
                    border = if (done) null else BorderStroke(AppDimensions.Dp1,AppColors.SwatchFF9FAF93), modifier = Modifier.size(AppDimensions.Dp32)) {
                    if (done) Icon(Icons.Rounded.Check, null, tint = AppColors.SwatchFFFFF5DC, modifier = Modifier.padding(AppDimensions.Dp3))
                    else if (timer) Icon(if (session?.running == true) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        null, tint = Green, modifier = Modifier.padding(AppDimensions.Dp2))
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
            Task(1, AppText.text(TextKey.CopyDoc10TrangSachA3c873), "HABIT", AppText.canonical(TextKey.CopyHocTapA51516), 10, "EVERY_DAY", "", "", 4, ""),
            Task(2, AppText.text(TextKey.CopyUongDuNuoc081b30), "DAILY", AppText.canonical(TextKey.CopySucKhoe071df7), 10, "EVERY_DAY", "", today(), 2, today()),
            Task(3, AppText.text(TextKey.CopyDiBo20PhutB36d50), "TODO", AppText.canonical(TextKey.CopySucKhoe071df7), 10, "ONCE", "", "", 0, "")
        ), isLoading = false), {})
    }
}

@Preview(name = "Tasks · extended document audit", widthDp = 360, heightDp = 900,
    device = "spec:width=360dp,height=900dp,dpi=160")
@Composable
fun TasksExtendedDocumentPreview() {
    MamXanhPreviewTheme {
        TasksScreen(TasksUiState(tasks = listOf(
            Task(1, AppText.text(TextKey.CopyDoc10TrangSachA3c873), "HABIT", AppText.canonical(TextKey.CopyHocTapA51516), 10, "EVERY_DAY", "", "", 0, ""),
            Task(2, AppText.text(TextKey.CopyUongDuNuoc081b30), "DAILY", AppText.canonical(TextKey.CopySucKhoe071df7), 10, "EVERY_DAY", "", today(), 1, today()),
            Task(3, AppText.text(TextKey.CopyDiBo20PhutB36d50), "DAILY", AppText.canonical(TextKey.CopySucKhoe071df7), 10, "EVERY_DAY", "", today(), 1, today()),
            Task(4, AppText.text(TextKey.CopyOnTuVung15PhutAcc66f), "DAILY", AppText.text(TextKey.CopyTiengAnh7ecaa5), 10, "EVERY_DAY", "", "", 0, ""),
            Task(5, AppText.text(TextKey.CopyDonGocHocTapE45d91), "TODO", AppText.text(TextKey.CopyGiuGonGang63ed82), 10, "ONCE", "", "", 0, ""),
            Task(6, AppText.text(TextKey.CopyChuanBiBaiChoNgayMai0c86eb), "TODO", AppText.canonical(TextKey.CopyHocTapA51516), 10, "ONCE", "", "", 0, "")
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
            Task(1, AppText.text(TextKey.CopyUongDuNuoc081b30), "DAILY", AppText.canonical(TextKey.CopySucKhoe071df7), 10, "EVERY_DAY", "", "", 0, "")
        ), filter = TaskFilter.HABIT, isLoading = false), {})
    }
}

@Preview(name = "Tasks · daily reward claimed", widthDp = 300, heightDp = 90,
    device = "spec:width=300dp,height=90dp,dpi=160")
@Composable
fun DailyRewardClaimedPreview() {
    MamXanhPreviewTheme {
        Box(Modifier.padding(AppDimensions.Dp16)) { DailyRewardCard(false, {}, claimed = true) }
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
        TaskCard(Task(1, AppText.text(TextKey.CopyDoc10TrangSachA3c873), "HABIT", AppText.canonical(TextKey.CopyHocTapA51516), 10, "EVERY_DAY", "", "", 4, ""), {}, {})
    }
}
