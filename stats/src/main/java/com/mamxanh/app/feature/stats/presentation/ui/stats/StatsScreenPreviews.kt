package com.mamxanh.app.feature.stats.presentation.ui.stats

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.MamXanhPreviewTheme

@Preview(
    name = "Painted garden journal",
    showBackground = true,
    widthDp = 360,
    heightDp = 800,
    device = "spec:width=360dp,height=800dp,dpi=160",
)
@Composable
fun StatsScreenPreview() {
    MamXanhPreviewTheme {
        StatsScreen(
            StatsState(
                com.mamxanh.app.feature.stats.domain.model.StatisticsSummary(
                    coins = 120,
                    total = 32,
                    today = 3,
                    week = 12,
                    streak = 5,
                    treeCount = 3,
                    recentDailyCompletions = listOf(1, 0, 2, 1, 3, 1, 3),
                ),
                false,
            ),
            {},
        )
    }
}

@Preview(
    name = "Journal · extended document audit",
    widthDp = 360,
    heightDp = 900,
    device = "spec:width=360dp,height=900dp,dpi=160",
)
@Composable
fun StatsExtendedDocumentPreview() {
    StatsScreenPreview()
}

@Preview(
    name = "Journal · refreshing",
    widthDp = 360,
    heightDp = 740,
    device = "spec:width=360dp,height=740dp,dpi=160",
)
@Composable
fun StatsRefreshingPreview() {
    MamXanhPreviewTheme {
        StatsScreen(
            StatsState(
                com.mamxanh.app.feature.stats.domain.model.StatisticsSummary(
                    coins = 120,
                    total = 32,
                    today = 3,
                    week = 12,
                    streak = 5,
                    treeCount = 3,
                    recentDailyCompletions = listOf(1, 0, 2, 1, 3, 1, 3),
                ),
                true,
            ),
            {},
        )
    }
}

@Preview(
    name = "Journal · large values, extended",
    widthDp = 360,
    heightDp = 1040,
    fontScale = 1.3f,
    device = "spec:width=360dp,height=1040dp,dpi=160",
)
@Composable
fun StatsLargeValuesPreview() {
    MamXanhPreviewTheme {
        StatsScreen(
            StatsState(
                com.mamxanh.app.feature.stats.domain.model.StatisticsSummary(
                    coins = 1_234_567_890,
                    total = 1_234_567_890,
                    today = 12_345,
                    week = 1_234_567,
                    streak = 12_345,
                    treeCount = 1_234_567_890,
                    recentDailyCompletions = listOf(0, 1, 1_234, Int.MAX_VALUE, 12_345, 1, 3),
                ),
                false,
            ),
            {},
        )
    }
}

@Preview(
    name = "Journal · first use",
    widthDp = 360,
    heightDp = 680,
    device = "spec:width=360dp,height=680dp,dpi=160",
)
@Composable
fun StatsZeroHistoryPreview() {
    MamXanhPreviewTheme {
        StatsScreen(
            StatsState(
                com.mamxanh.app.feature.stats.domain.model.StatisticsSummary(
                    coins = 60,
                    total = 0,
                    today = 0,
                    week = 0,
                    streak = 0,
                    treeCount = 0,
                    recentDailyCompletions = List(7) { 0 },
                ),
                false,
            ),
            {},
        )
    }
}
