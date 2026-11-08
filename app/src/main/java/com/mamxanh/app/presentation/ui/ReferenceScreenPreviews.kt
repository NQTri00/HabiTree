package com.mamxanh.app.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.mamxanh.app.feature.home.domain.model.HomeDashboard
import com.mamxanh.app.feature.home.domain.model.HomeTree
import com.mamxanh.app.feature.home.presentation.HomeUiState
import com.mamxanh.app.feature.garden.domain.model.Garden
import com.mamxanh.app.feature.garden.domain.model.Tree
import com.mamxanh.app.feature.garden.presentation.GardenUiState
import com.mamxanh.app.feature.tasks.domain.model.Task
import com.mamxanh.app.feature.tasks.domain.model.today
import com.mamxanh.app.feature.tasks.presentation.TasksUiState
import com.mamxanh.app.feature.shop.domain.model.ShopWallet
import com.mamxanh.app.feature.shop.presentation.ShopUiState
import com.mamxanh.app.feature.stats.domain.model.GardenStatistics
import com.mamxanh.app.feature.stats.presentation.StatsUiState
import com.mamxanh.app.feature.tree.presentation.TreeUiState

/** Same phone, state and shell across all six visual comparisons. */
class ReferenceRoutes : PreviewParameterProvider<AppUiState> {
    override val values = sequenceOf(
        AppUiState(destination = AppDestination.HOME),
        AppUiState(destination = AppDestination.TASKS),
        AppUiState(destination = AppDestination.SHOP),
        AppUiState(destination = AppDestination.GARDEN),
        AppUiState(destination = AppDestination.GARDEN, selectedTreeId = 1L),
        AppUiState(destination = AppDestination.STATS)
    )
}

@Preview(name = "Reference comparison · full app", widthDp = 360, heightDp = 800)
@Composable
fun ReferenceScreenPreview(@PreviewParameter(ReferenceRoutes::class) route: AppUiState) {
    ReferenceWorld(route)
}

@Preview(name = "Reference comparison · large text", widthDp = 360, heightDp = 800, fontScale = 1.3f)
@Composable
fun ReferenceLargeTextPreview(@PreviewParameter(ReferenceRoutes::class) route: AppUiState) {
    ReferenceWorld(route)
}

@Preview(name = "Reference comparison · empty", widthDp = 360, heightDp = 800)
@Composable
fun ReferenceEmptyPreview(@PreviewParameter(ReferenceRoutes::class) route: AppUiState) {
    ReferenceWorld(route, empty = true)
}

@Composable
private fun ReferenceWorld(route: AppUiState, empty: Boolean = false) {
    val trees = if (empty) emptyList() else listOf(
        Tree(1L, "cherry", "Đào hồng xuân", 68, "2026-11-01", "FLOWERS"),
        Tree(2L, "pine", "Thông nhỏ", 24, "2026-11-01", wateredToday = true),
        Tree(3L, "succulent", "Sen đá", 12, "2026-11-01")
    )
    val inventory = if (empty) emptyMap() else mapOf("cherry" to 1, "pine" to 1, "fertilizer" to 2)
    MamXanhPreviewTheme {
        MamXanhAppContent(
            state = route,
            dashboard = HomeUiState(HomeDashboard(
                trees.map { HomeTree(it.id, it.speciesId, it.name, it.growth, it.decoration, it.wateredToday) },
                completedTasks = if (empty) 0 else 2, totalTasks = if (empty) 0 else 4, coins = if (empty) 0 else 120, wateringCanMl = 500
            ), false),
            tasks = TasksUiState(tasks = if (empty) emptyList() else listOf(
                Task(1, "Đọc 10 trang sách", "HABIT", "Học tập", 10, "EVERY_DAY", "", "", 4, ""),
                Task(2, "Uống đủ nước", "DAILY", "Sức khỏe", 10, "EVERY_DAY", "", today(), 2, today()),
                Task(3, "Đi bộ 20 phút", "TODO", "Sức khỏe", 10, "ONCE", "", "", 0, "")
            ), isLoading = false),
            garden = GardenUiState(Garden(trees, inventory), false),
            shop = ShopUiState(ShopWallet(if (empty) 0 else 120, inventory), false),
            stats = StatsUiState(if (empty) GardenStatistics() else GardenStatistics(
                coins = 120, total = 32, today = 3, week = 12, streak = 5, treeCount = 3,
                recentDailyCompletions = listOf(1,0,2,1,3,1,3)
            ), false),
            treeState = TreeUiState(isLoading = false)
        )
    }
}
