package com.mamxanh.app.feature.home.presentation.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.*
import com.mamxanh.app.core.components.components.*
import com.mamxanh.app.feature.home.domain.model.*
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

internal fun previewHomeState(treeName: String = AppText.text(TextKey.CopyDaoHongXuanC2fd99)) =
    HomeState(
        HomeDashboard(
            trees = listOf(HomeTree(1L, "cherry", treeName, 120, "FLOWERS", false)),
            completedTasks = 2,
            totalTasks = 4,
            coins = 120,
            equippedCan = "can_basic",
        ),
        false,
    )

@Preview(widthDp = 360, heightDp = 640, device = "spec:width=360dp,height=640dp,dpi=160")
@Composable
fun HomeScreenPreview() {
    MamXanhPreviewTheme { HomeScreen(previewHomeState(), {}, 2) }
}

@Preview(name = "Web design comparison", widthDp = 414, heightDp = 1200, device = "spec:width=414dp,height=1200dp,dpi=160")
@Composable
fun HomeWebDesignPreview() {
    MamXanhPreviewTheme {
        val base = previewHomeState()
        HomeScreen(
            base.copy(
                dashboard =
                    base.dashboard.copy(
                        coins = 620,
                        completedTasks = 1,
                        seedCount = 2,
                        nextGoal = AppText.text(TextKey.CopyChonHatChoGocVuon3c30f3),
                        trees =
                            base.dashboard.trees.map {
                                it.copy(
                                    status =
                                        it.status.copy(
                                            percentage = 46,
                                            stageProgress = .46f,
                                            stageIndex = 3,
                                            stageCount = 9,
                                            stageLabel = AppText.text(TextKey.CopyGiaiDoan01C0a0f8, 4, 9),
                                        ),
                                )
                            },
                        nextTasks =
                            listOf(
                                HomeTaskSummary(
                                    1,
                                    AppText.text(TextKey.CopyDoc10TrangSachA3c873),
                                    AppText.canonical(TextKey.CopyHocTapA51516),
                                    8,
                                ),
                            ),
                        earnedToday = 24,
                        streak = 5,
                    ),
            ),
            {},
            3,
        )
    }
}

@Preview(widthDp = 360, heightDp = 640, device = "spec:width=360dp,height=640dp,dpi=160")
@Composable
fun HomeScreenEmptyPreview() {
    MamXanhPreviewTheme { HomeScreen(HomeState(HomeDashboard(coins = 60), false), {}) }
}

@Preview(widthDp = 320, heightDp = 1000, fontScale = 1.3f, device = "spec:width=320dp,height=1000dp,dpi=160")
@Composable
fun HomeLongNamePreview() {
    MamXanhPreviewTheme { HomeScreen(previewHomeState(AppText.text(TextKey.CopyCayDaoBinhYenBenCuaSoCuaGiaDinh3c7da8)), {}, 2) }
}

// Each preview renders the actual composable in isolation, with home-screen sample data.
