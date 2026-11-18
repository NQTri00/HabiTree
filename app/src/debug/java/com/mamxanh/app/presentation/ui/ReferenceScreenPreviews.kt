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

@Preview(name = "Reference comparison · full app", widthDp = 360, heightDp = 800,
    device = "spec:width=360dp,height=800dp,dpi=160")
@Composable
fun ReferenceScreenPreview(@PreviewParameter(ReferenceRoutes::class) route: AppUiState) {
    ReferenceWorld(route)
}

@Preview(name = "Reference comparison · large text", widthDp = 360, heightDp = 800, fontScale = 1.3f,
    device = "spec:width=360dp,height=800dp,dpi=160")
@Composable
fun ReferenceLargeTextPreview(@PreviewParameter(ReferenceRoutes::class) route: AppUiState) {
    ReferenceWorld(route)
}

@Preview(name = "Reference comparison · empty", widthDp = 360, heightDp = 800,
    device = "spec:width=360dp,height=800dp,dpi=160")
@Composable
fun ReferenceEmptyPreview(@PreviewParameter(ReferenceRoutes::class) route: AppUiState) {
    ReferenceWorld(route, empty = true)
}

@Preview(name = "Nursery · empty, large text", widthDp = 360, heightDp = 800, fontScale = 1.3f,
    device = "spec:width=360dp,height=800dp,dpi=160")
@Composable
fun ReferenceEmptyNurseryLargePreview() {
    ReferenceWorld(AppUiState(destination = AppDestination.GARDEN), empty = true)
}

@Preview(name = "Care · long name, no supplies", widthDp = 360, heightDp = 800, fontScale = 1.3f,
    device = "spec:width=360dp,height=800dp,dpi=160")
@Composable
fun ReferenceCareUnavailablePreview() {
    ReferenceWorld(AppUiState(destination = AppDestination.GARDEN, selectedTreeId = 1L), careUnavailable = true)
}

@Preview(name = "App shell · actual add-task dialog", widthDp = 360, heightDp = 800,
    device = "spec:width=360dp,height=800dp,dpi=160")
@Composable
fun ReferenceTaskDialogPreview() {
    ReferenceWorld(AppUiState(destination = AppDestination.TASKS), addingTask = true)
}

// Explicit compositions: layoutlib does not capture Android Dialog windows in the app-root image.
@Preview(name = "Composition · add task", widthDp = 360, heightDp = 800,
    device = "spec:width=360dp,height=800dp,dpi=160")
@Composable
fun ReferenceAddTaskCompositionPreview() {
    MamXanhPreviewTheme {
        com.mamxanh.app.feature.tasks.presentation.dialog.AddTaskDialog(
            onDismiss = {}, onSave = {}, initial = com.mamxanh.shared.model.TaskConfig(title = "Đọc 10 trang sách"))
    }
}

@Preview(name = "Composition · seed detail", widthDp = 360, heightDp = 800,
    device = "spec:width=360dp,height=800dp,dpi=160")
@Composable
fun ReferenceSeedCompositionPreview() {
    com.mamxanh.app.feature.shop.presentation.ShopSeedDetailDialogPreview(background = {
        ReferenceWorld(AppUiState(destination = AppDestination.SHOP))
    })
}

@Preview(name = "Composition · planting", widthDp = 360, heightDp = 800,
    device = "spec:width=360dp,height=800dp,dpi=160")
@Composable
fun ReferencePlantingCompositionPreview() {
    com.mamxanh.app.feature.garden.presentation.PlantingDialogPreview(background = {
        ReferenceWorld(AppUiState(destination = AppDestination.GARDEN))
    })
}

@Composable
private fun ReferenceWorld(route: AppUiState, empty: Boolean = false, careUnavailable: Boolean = false,
    addingTask: Boolean = false) {
    val trees = if (empty) emptyList() else listOf(
        Tree(1L, "cherry", if (careUnavailable) "Cây đào bình yên bên cửa sổ của gia đình" else "Đào hồng xuân",
            68, "2026-11-01", "FLOWERS"),
        Tree(2L, "pine", "Thông nhỏ", 24, "2026-11-01", wateredToday = true),
        Tree(3L, "succulent", "Sen đá", 12, "2026-11-01")
    )
    val inventory = if (empty) emptyMap() else mapOf("cherry" to 1, "pine" to 1, "fertilizer" to if (careUnavailable) 0 else 2)
    MamXanhPreviewTheme {
        MamXanhAppContent(
            state = route,
            dashboard = HomeUiState(HomeDashboard(
                trees.map { HomeTree(it.id, it.speciesId, it.name, it.growth, it.decoration, it.wateredToday) },
                completedTasks = if (empty) 0 else 2, totalTasks = if (empty) 0 else 4, coins = if (empty) 0 else 120, equippedCan = "can_basic"
            ), false),
            tasks = TasksUiState(tasks = if (empty) emptyList() else listOf(
                Task(1, "Đọc 10 trang sách", "HABIT", "Học tập", 10, "EVERY_DAY", "", "", 4, ""),
                Task(2, "Uống đủ nước", "DAILY", "Sức khỏe", 10, "EVERY_DAY", "", today(), 2, today()),
                Task(3, "Đi bộ 20 phút", "TODO", "Sức khỏe", 10, "ONCE", "", "", 0, "")
            ), isLoading = false, isAddingTask = addingTask),
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
