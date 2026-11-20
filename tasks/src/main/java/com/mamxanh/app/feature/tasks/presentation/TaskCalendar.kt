package com.mamxanh.app.feature.tasks.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.presentation.ui.design.*
import com.mamxanh.core.ui.R

private const val DaysPerWeek = 7

@Composable
fun TaskCalendar(days: List<TaskCalendarDay>) {
    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp6)) {
        days.chunked(DaysPerWeek).forEach { week ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp6)) {
                week.forEach { day ->
                    TaskCalendarCell(day, Modifier.weight(1f))
                }
                repeat(DaysPerWeek - week.size) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
        Bt(stringResource(R.string.tasks_calendar_legend), size = AppTypography.Size10, tint = bp.muted)
    }
}

@Composable
private fun TaskCalendarCell(day: TaskCalendarDay, modifier: Modifier) {
    val (marker, description) = when (day.status) {
        TaskCalendarStatus.Completed -> "✓" to R.string.tasks_calendar_completed
        TaskCalendarStatus.Rest -> stringResource(R.string.calendar_rest_marker) to R.string.tasks_calendar_rest
        TaskCalendarStatus.Protected -> "◇" to R.string.tasks_calendar_protected
        TaskCalendarStatus.Missed -> "·" to R.string.tasks_calendar_missed
        TaskCalendarStatus.Unrecorded -> "" to R.string.tasks_calendar_unrecorded
    }
    val fill = when (day.status) {
        TaskCalendarStatus.Completed -> bp.deep
        TaskCalendarStatus.Rest -> AppColors.SwatchFFE7E1F5
        TaskCalendarStatus.Protected -> AppColors.RewardSurface
        else -> AppColors.SwatchFFF0F2EB
    }
    val status = stringResource(description)
    val accessibilityLabel = stringResource(R.string.tasks_calendar_day_status, bioDisplayDate(day.date), status)
    Box(
        modifier.heightIn(min = AppDimensions.Dp35)
            .background(fill, RoundedCornerShape(AppDimensions.Dp10))
            .clearAndSetSemantics { contentDescription = accessibilityLabel }
            .padding(vertical = AppDimensions.Dp4),
        contentAlignment = Alignment.Center,
    ) {
        Bt(
            day.date.takeLast(2) + if (marker.isNotEmpty()) " $marker" else "",
            size = AppTypography.Size10,
            tint = if (day.status == TaskCalendarStatus.Completed) AppColors.White else bp.ink,
            align = TextAlign.Center,
        )
    }
}

@Preview(name = "Task calendar status colors", widthDp = 299, showBackground = true)
@Preview(name = "Task calendar at large text", widthDp = 280, fontScale = 1.5f, showBackground = true)
@Composable
private fun TaskCalendarPreview() {
    TaskCalendar(List(14) { index ->
        val date = "2026-11-" + (index + 1).toString().padStart(2, '0')
        TaskCalendarDay(date, TaskCalendarStatus.entries[index % TaskCalendarStatus.entries.size])
    })
}
