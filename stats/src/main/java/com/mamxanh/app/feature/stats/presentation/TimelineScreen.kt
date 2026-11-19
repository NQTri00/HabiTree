package com.mamxanh.app.feature.stats.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.presentation.ui.design.*
import com.mamxanh.app.feature.stats.domain.TimelineRelated
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

data class TimelineMemory(
    val id: String,
    val date: String,
    val title: String,
    val related: TimelineRelated?,
    val note: String,
)

@Composable
fun TimelineScreen(
    memories: List<TimelineMemory>,
    onRelated: (TimelineRelated) -> Unit,
    onSaveNote: (String, String) -> Unit,
) {
    Bt(AppText.text(TextKey.CopyDieuMinhMuonNho665bc8),size=AppTypography.Size27,bold=true,spacing=AppTypography.TrackingMinus1)
    val timelineLine=bp.line;val timelineDot=bp.deep
    if(memories.isEmpty())BioEmpty(AppText.text(TextKey.CopyChuaCoKyNiemDa130b),AppText.text(TextKey.CopyNhungViecBanLamSeXuatHienODay735541),"book")else Column(Modifier.fillMaxWidth().padding(start=AppDimensions.Dp8).drawBehind{drawLine(timelineLine,Offset.Zero,Offset(0f,size.height),AppDimensions.Dp1.toPx())}.padding(start=AppDimensions.Dp20),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp28)){
        memories.forEach{m->var note by rememberSaveable(m.id){mutableStateOf(m.note)}
            Column(
                Modifier.drawBehind {
                    drawCircle(
                        timelineDot,
                        AppDimensions.Dp4_5.toPx(),
                        Offset(AppDimensions.DpMinus20.toPx(), AppDimensions.Dp8.toPx()),
                    )
                },
                verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp0),
            ) {
                Bt(m.date, size = AppTypography.Size10, tint = bp.muted)
                Spacer(Modifier.height(AppDimensions.Dp4))
                Bt(m.title, size = AppTypography.Size12, semi = true)
                m.related?.let { target ->
                    BioTextAction(AppText.text(TextKey.CopyXemLienQuan2167e0), { onRelated(target) }, "next")
                }
                BioField(
                    "",
                    note,
                    { note = it.take(1000) },
                    multiline = true,
                    placeholder = AppText.text(TextKey.CopyMotDieuMuonNho200cc4),
                )
                Spacer(Modifier.height(AppDimensions.Dp8))
                BioTextAction(
                    AppText.text(TextKey.CopyLuuGhiChuE3d8bd),
                    { onSaveNote(m.id, note) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun TimelineScreenPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp18)) {
        TimelineScreen(listOf(TimelineMemory("1", "2026-11-17", "First watering", TimelineRelated.Tree("1"), "")), {}, { _, _ -> })
    }
}
