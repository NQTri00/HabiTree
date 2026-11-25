package com.mamxanh.app.presentation.bio

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.mamxanh.app.core.components.design.*
import com.mamxanh.app.core.components.design.AppRadii
import com.mamxanh.app.core.components.design.AppSpacing
import com.mamxanh.app.core.components.design.AppTypography
import com.mamxanh.app.feature.stats.presentation.navigation.StatisticsRouteContent
import com.mamxanh.app.feature.stats.presentation.ui.stats.StatisticsScreenState
import com.mamxanh.core.ui.R

@Composable internal fun BioToast(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BioCard(modifier.fillMaxWidth(), padding = AppSpacing.VALUE_14, radius = AppRadii.VALUE_14) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Bt(message, Modifier.weight(1f), size = AppTypography.SIZE_12)
            BioIconButton("close", onDismiss)
        }
    }
}

internal fun statisticsRouteContent(
    world: BioWorld,
    ui: BioController,
): StatisticsRouteContent {
    val statistics = world.data.o("statistics")
    return StatisticsRouteContent(
        state =
            StatisticsScreenState(
                total = statistics.i("total"),
                treeCount = world.treeIds.size,
                week = statistics.i("week"),
                today = statistics.i("today"),
                streak = statistics.i("streak"),
                dailyCompletions = world.statisticsDailyCompletions(),
                dayLabels = world.statisticsDayLabels(),
            ),
        onNavigate = { ui.go(it.destination().id) },
        onRefresh = { Unit },
    )
}

internal fun BioWorld.statisticsDayLabels(): List<String> =
    bioDates(data.s("date"), 7).map { date ->
        val calendar =
            java.util.Calendar.getInstance().apply {
                time = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).parse(date)!!
            }
        bioWeekday(calendar.get(java.util.Calendar.DAY_OF_WEEK))
    }

internal fun BioWorld.statisticsDailyCompletions(): List<Int> {
    val daily = data.o("statistics").a("recentDailyCompletions")
    return List(7) { daily.optInt(it).coerceAtLeast(0) }
}

@Preview(name = "Application shell", widthDp = 375, heightDp = 812)
@Preview(name = "Application shell landscape", widthDp = 844, heightDp = 390)
@Composable
private fun BioApplicationShellPreview() {
    BioReferenceSurface("home")
}

@Preview(name = "Toast", widthDp = 335, showBackground = true)
@Composable
private fun BioToastPreview() {
    val context = LocalContext.current
    val world = remember(context) { BioWorld.reference(context) }
    BioTheme(world, "1") {
        BioToast(stringResource(R.string.copy_da_tuoi_de2042), onDismiss = {})
    }
}
