package com.mamxanh.app.feature.tasks.presentation.ui.engagement

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.*
import com.mamxanh.app.core.components.components.*
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppTypography
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.model.*

@Preview(
    name = "Engagement components · full review",
    widthDp = 360,
    heightDp = 1000,
    device = "spec:width=360dp,height=1000dp,dpi=160",
)
@Composable
fun EngagementComponentsPreview() {
    MamXanhPreviewTheme {
        ProvideTextStyle(MaterialTheme.typography.bodySmall.copy(fontSize = AppTypography.Sp15, lineHeight = AppTypography.Sp21)) {
            Column(Modifier.fillMaxWidth().padding(AppDimensions.Dp12), verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp16)) {
                ReviewContent(
                    EngagementUiState(
                        snapshot =
                            EngagementSnapshot(
                                review =
                                    WeeklyReview(
                                        "2026-11-09",
                                        5,
                                        3,
                                        null,
                                        25,
                                        24,
                                        0,
                                        2,
                                        0,
                                        AppText.canonical(TextKey.CopyHocTapA51516),
                                        "",
                                        "",
                                    ),
                                days = listOf(ReviewDay("2026-11-10", "REST")),
                                tomorrow = "2026-11-13",
                            ),
                    ),
                    {},
                )
            }
        }
    }
}

@Preview(widthDp = 414, heightDp = 1000, device = "spec:width=414dp,height=1000dp,dpi=160")
@Composable
fun FocusWebDesignPreview() {
    com.mamxanh.app.core.components.MamXanhPreviewTheme {
        MintBackdrop {
            Column(Modifier.padding(AppDimensions.Dp24), verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp18)) {
                FocusContent(
                    EngagementUiState(
                        loading = false,
                        snapshot =
                            EngagementSnapshot(
                                focus =
                                    listOf(
                                        FocusSession(
                                            "preview",
                                            0,
                                            AppText.text(TextKey.CopyMotKhoangChoRiengMinhCcf66e),
                                            "2026-11-16",
                                            1500,
                                            750,
                                            "PAUSED",
                                        ),
                                    ),
                            ),
                    ),
                    {
                    },
                    { _, _ -> },
                )
            }
        }
    }
}

@Preview(widthDp = 414, heightDp = 1100, device = "spec:width=414dp,height=1100dp,dpi=160")
@Composable
fun FocusIdleWebDesignPreview() {
    com.mamxanh.app.core.components.MamXanhPreviewTheme {
        MintBackdrop {
            Column(Modifier.padding(AppDimensions.Dp24), verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp18)) {
                FocusContent(EngagementUiState(loading = false), {}, { _, _ -> })
            }
        }
    }
}

private fun webEngagementState(route: String) =
    EngagementUiState(
        loading = false,
        route = route,
        tasks =
            listOf(
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
            ),
        snapshot =
            EngagementSnapshot(
                insights =
                    listOf(
                        TaskInsight(
                            1,
                            AppText.text(TextKey.CopyDoc10TrangSachA3c873),
                            (1..7).map {
                                ReviewDay(
                                    "2026-11-${it.toString().padStart(2,'0')}",
                                    if (it ==
                                        3
                                    ) {
                                        "MISSED"
                                    } else {
                                        "SUCCESS"
                                    },
                                )
                            },
                            86,
                            4,
                            6,
                            "20:30",
                            45,
                            AppText.text(TextKey.CopyChonMotKhungGioVuaSucD8a536),
                        ),
                    ),
                review =
                    WeeklyReview(
                        "2026-11-09",
                        15,
                        10,
                        null,
                        45,
                        40,
                        0,
                        3,
                        0,
                        AppText.canonical(TextKey.CopyHocTapA51516),
                        AppText.canonical(TextKey.CopyNepSong8d1a98),
                        "",
                    ),
                achievements =
                    listOf(
                        Achievement(
                            "first",
                            AppText.text(TextKey.CopyViecDauTien28bcbf),
                            AppText.text(TextKey.CopyHoanThanhMotViec1c20b2),
                            1,
                            1,
                            1,
                        ),
                        Achievement(
                            "three",
                            AppText.text(TextKey.CopyBaNgayXanh0c655e),
                            AppText.text(TextKey.CopyGiuNhipBaNgayB90ddb),
                            2,
                            3,
                        ),
                    ),
                memories =
                    listOf(
                        MemoryEvent("first", "TASK", 1, AppText.text(TextKey.CopyViecDauTienDaXongB1f060), 1794819600000),
                    ),
                days =
                    (1..12).map {
                        ReviewDay("2026-11-${it.toString().padStart(2,'0')}", if (it % 3 == 0) "REST" else "SUCCESS")
                    },
                tomorrow = "2026-11-17",
            ),
    )

@Preview(widthDp = 414, heightDp = 1400, device = "spec:width=414dp,height=1400dp,dpi=160")
@Composable
fun TemplatesWebDesignPreview() {
    MamXanhPreviewTheme { EngagementScreen(webEngagementState("TEMPLATES"), { _, _ -> }, {}, {}, {}, {}, {}) }
}

@Preview(widthDp = 414, heightDp = 1400, device = "spec:width=414dp,height=1400dp,dpi=160")
@Composable
fun ReviewWebDesignPreview() {
    MamXanhPreviewTheme { EngagementScreen(webEngagementState("REVIEW"), { _, _ -> }, {}, {}, {}, {}, {}) }
}

@Preview(widthDp = 414, heightDp = 1400, device = "spec:width=414dp,height=1400dp,dpi=160")
@Composable
fun RestWebDesignPreview() {
    MamXanhPreviewTheme { EngagementScreen(webEngagementState("REST"), { _, _ -> }, {}, {}, {}, {}, {}) }
}

@Preview(widthDp = 414, heightDp = 1400, device = "spec:width=414dp,height=1400dp,dpi=160")
@Composable
fun AchievementsWebDesignPreview() {
    MamXanhPreviewTheme { EngagementScreen(webEngagementState("ACHIEVEMENTS"), { _, _ -> }, {}, {}, {}, {}, {}) }
}

@Preview(widthDp = 414, heightDp = 1400, device = "spec:width=414dp,height=1400dp,dpi=160")
@Composable
fun TimelineWebDesignPreview() {
    MamXanhPreviewTheme { EngagementScreen(webEngagementState("TIMELINE"), { _, _ -> }, {}, {}, {}, {}, {}) }
}

@Preview(widthDp = 414, heightDp = 1400, device = "spec:width=414dp,height=1400dp,dpi=160")
@Composable
fun InsightsWebDesignPreview() {
    MamXanhPreviewTheme { EngagementScreen(webEngagementState("INSIGHTS"), { _, _ -> }, {}, {}, {}, {}, {}) }
}

@Preview(widthDp = 414, heightDp = 1400, device = "spec:width=414dp,height=1400dp,dpi=160")
@Composable
fun InsightDetailWebDesignPreview() {
    MamXanhPreviewTheme { EngagementScreen(webEngagementState("INSIGHTS").copy(selectedTask = 1), { _, _ -> }, {}, {}, {}, {}, {}) }
}

@Preview(widthDp = 414, heightDp = 1000, device = "spec:width=414dp,height=1000dp,dpi=160")
@Composable
fun MoreWebDesignPreview() {
    MamXanhPreviewTheme { EngagementScreen(webEngagementState("MORE"), { _, _ -> }, {}, {}, {}, {}, {}) }
}
