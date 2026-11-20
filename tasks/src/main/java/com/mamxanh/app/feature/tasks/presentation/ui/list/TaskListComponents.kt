package com.mamxanh.app.feature.tasks.presentation.ui.list

import com.mamxanh.app.core.components.components.GardenRichText as Text
import com.mamxanh.app.core.components.design.AppColors
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppTypography
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.localization.AppText
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import com.mamxanh.app.core.components.components.GardenBotanicalFrame
import com.mamxanh.app.core.components.components.GardenPanelSkin
import com.mamxanh.app.core.components.components.GardenArt
import com.mamxanh.app.core.components.components.GardenArtwork
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import com.mamxanh.app.core.components.components.MintDepth
import com.mamxanh.app.core.components.components.mintDepth
import com.mamxanh.app.core.components.components.MintProgress
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.feature.tasks.domain.model.TaskTimerProgress
import com.mamxanh.app.feature.tasks.domain.model.Task
import com.mamxanh.app.core.components.*

@Composable
internal fun TaskQuestHeader(done: Int, total: Int, progress: Float, allComplete: Boolean, onAddTask: () -> Unit) {
    GardenBotanicalFrame(Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp14)) {
            Text(AppText.text(TextKey.CopyDaHoanThanh01ViecE78274,done,total), fontWeight = FontWeight.Bold, color = Deep)
            MintProgress(progress, Modifier.fillMaxWidth())
        }
    }
}

@Composable
internal fun DailyRewardCard(canClaim: Boolean, onClaim: () -> Unit, claimed: Boolean = false) {
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
            skin = com.mamxanh.app.core.components.components.GardenPanelSkin.HINT) {
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
internal fun TaskFilterSearchArtwork() {
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
internal fun TaskCard(task: Task, onComplete: () -> Unit, onTimer: () -> Unit, readOnly: Boolean = false,
    session: TaskTimerProgress? = null, busy: Boolean = false, onOpen: ()->Unit = {}) {
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
