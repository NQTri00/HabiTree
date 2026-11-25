package com.mamxanh.app.presentation.bio

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.mamxanh.app.core.components.design.*
import com.mamxanh.app.feature.home.domain.model.HomeTaskSummary
import com.mamxanh.app.feature.home.presentation.navigation.HomeRouteContent
import com.mamxanh.app.feature.home.presentation.ui.home.HomeAction
import com.mamxanh.app.feature.home.presentation.ui.home.HomeEvent
import com.mamxanh.app.feature.home.presentation.ui.home.HomeReward
import com.mamxanh.app.feature.home.presentation.ui.home.HomeScreenAction
import com.mamxanh.app.feature.home.presentation.ui.home.HomeScreenState
import com.mamxanh.app.feature.home.presentation.ui.home.HomeState
import com.mamxanh.app.feature.home.presentation.ui.home.HomeViewModel
import com.mamxanh.app.feature.tasks.presentation.navigation.TaskListRouteContent
import com.mamxanh.app.feature.tasks.presentation.ui.list.TaskListAction
import com.mamxanh.app.feature.tasks.presentation.ui.list.TaskListEvent
import com.mamxanh.app.feature.tasks.presentation.ui.list.TaskListItem
import com.mamxanh.app.feature.tasks.presentation.ui.list.TaskListRow
import com.mamxanh.app.feature.tasks.presentation.ui.list.TaskListState
import com.mamxanh.app.feature.tasks.presentation.ui.list.TaskListViewModel
import com.mamxanh.app.feature.tree.domain.repository.AccessorySnapshot
import com.mamxanh.app.feature.tree.presentation.ui.detail.TreeAction
import com.mamxanh.app.feature.tree.presentation.ui.detail.TreeEvent
import com.mamxanh.app.feature.tree.presentation.ui.detail.TreeViewModel
import com.mamxanh.app.feature.tree.presentation.ui.detail.component.TreeHero
import com.mamxanh.app.feature.tree.presentation.ui.detail.component.TreeHeroAction
import com.mamxanh.app.feature.tree.presentation.ui.detail.component.TreeHeroState
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun liveHomeRouteContent(
    world: BioWorld,
    ui: BioController,
    short: Boolean,
    accessories: AccessorySnapshot?,
): HomeRouteContent {
    val viewModel = koinViewModel<HomeViewModel>()
    val tasks = koinViewModel<TaskListViewModel>(key = "home-task-work")
    val trees = koinViewModel<TreeViewModel>(key = "home-tree-care")
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(world, ui.homeTreeState.selectedId) {
        if (!world.loading) viewModel.dispatch(HomeAction.Refresh)
    }
    LaunchedEffect(viewModel, tasks, trees, lifecycle, ui) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch {
                tasks.events.collect { event ->
                    if (ui.route == "home" && event is TaskListEvent.Feedback) ui.message = event.message
                }
            }
            launch {
                trees.events.collect { event ->
                    if (ui.route == "home" && event is TreeEvent.Feedback) ui.message = event.message
                }
            }
            viewModel.events.collect { event ->
                if (ui.route != "home") return@collect
                when (event) {
                    is HomeEvent.Shortcut -> ui.openHomeShortcut(event.action)
                    HomeEvent.ChooseTree -> ui.show("tree-picker")
                    is HomeEvent.TreeSelected -> {
                        ui.homeTreeState.selectedId = event.id
                        if (ui.modal == "tree-picker") ui.modal = ""
                    }
                    HomeEvent.ClaimReward -> tasks.dispatch(TaskListAction.ClaimReward)
                    is HomeEvent.OpenTree -> ui.go("tree", event.id.toString())
                    is HomeEvent.OpenTask -> ui.go(if (event.startTimer) "focus" else "task", event.id)
                    is HomeEvent.CompleteTask -> tasks.dispatch(TaskListAction.Complete(event.id, timer = false))
                    is HomeEvent.DecorateTree -> ui.act("bio-tree-decor:${event.id}")
                    is HomeEvent.WaterTree -> trees.dispatch(TreeAction.Water(event.id))
                    is HomeEvent.FertilizeTree -> ui.show("fertilizer-help", event.id.toString())
                    is HomeEvent.Feedback -> ui.message = event.message
                    HomeEvent.OpenTasks -> ui.go("tasks")
                    HomeEvent.OpenGarden -> ui.go("garden")
                    HomeEvent.OpenShop -> ui.go("shop")
                }
            }
        }
    }
    return homeRouteContent(world, ui, short, state, viewModel::dispatch, accessories)
}

private fun BioController.openHomeShortcut(action: HomeScreenAction) {
    when (action) {
        HomeScreenAction.PickSeed -> show("seed-picker")
        HomeScreenAction.ViewTasks -> go("tasks")
        HomeScreenAction.AddTask -> go("task-new")
        HomeScreenAction.BrowseSeeds -> act("bio-seed-picker")
        HomeScreenAction.BrowseAccessories -> act("bio-accessory-shop")
        HomeScreenAction.Review -> go("review")
    }
}

internal fun homeRouteContent(
    world: BioWorld,
    ui: BioController,
    short: Boolean,
    liveState: HomeState? = null,
    dispatch: ((HomeAction) -> Unit)? = null,
    accessories: AccessorySnapshot? = null,
): HomeRouteContent {
    val tree = if (liveState == null) world.tree(ui.homeTreeState.selectedId) else null
    val dashboardTrees = liveState?.dashboard?.trees.orEmpty()
    val selected = dashboardTrees.firstOrNull { it.id.toString() == liveState?.selectedTreeId }
    val next =
        if (liveState != null) {
            null
        } else {
            world.tasks.firstOrNull {
                !it.b("done") &&
                    !it.b("finished") &&
                    it.s("lifecycle", "ACTIVE") == "ACTIVE" &&
                    it.b("scheduledToday", true)
            }
        }
    val phase = world.data.o("phase")
    return HomeRouteContent(
        state =
            HomeScreenState(
                date =
                    world.data
                        .s("date")
                        .split('-')
                        .takeLast(2)
                        .reversed()
                        .joinToString("-"),
                completed = liveState?.dashboard?.completedTasks ?: phase.i("completed"),
                required = liveState?.dashboard?.totalTasks ?: phase.i("required"),
                earned = liveState?.dashboard?.earnedToday ?: world.state.i("earned"),
                streak = liveState?.dashboard?.streak ?: phase.i("streak"),
            ),
        onAction = { action ->
            if (dispatch == null) ui.openHomeShortcut(action) else dispatch(HomeAction.Shortcut(action))
        },
        tree =
            when {
                selected != null && dispatch != null -> {
                    {
                        val id = selected.id.toString()
                        val care = selected.status
                        val stageLabel =
                            when {
                                !care.alive -> TextKey.CopyDaKhoHeoA9c1b9
                                care.mature -> TextKey.CopyTruongThanh8c14d7
                                else -> TextKey.CopyDangLon40daab
                            }
                        val caption =
                            when {
                                !care.alive -> TextKey.CopyMamXanhDaDungLai6dabc6
                                care.mature -> TextKey.CopyDaTruongThanh54f165
                                else -> TextKey.CopyDangLonCungBan6913c2
                            }
                        val decoration = accessories?.trees?.firstOrNull { it.id == id }?.decoration
                        TreeHero(
                            TreeHeroState(
                                species = selected.speciesId,
                                name = AppText.plantName(selected.speciesId, selected.name),
                                stageLabel = AppText.text(stageLabel),
                                growthCaption = AppText.text(caption),
                                stage = care.stageIndex + 1,
                                stages = world.species(selected.speciesId).i("stages", 6),
                                progress = care.percentage,
                                home = true,
                                compact = short,
                                dead = !care.alive,
                                watered = care.waterActive,
                                busy = ui.busy,
                                selectedOnHome = true,
                                canBrowseTrees = dashboardTrees.size > 1,
                                accessoryIcon = decoration?.takeIf { accessories?.owns(it) == true }?.let(::accessoryIcon),
                            ),
                        ) { action ->
                            when (action) {
                                TreeHeroAction.ChooseTree -> dispatch(HomeAction.ChooseTree)
                                TreeHeroAction.OpenTree -> dispatch(HomeAction.OpenTree(selected.id))
                                TreeHeroAction.Water -> dispatch(HomeAction.WaterTree(selected.id))
                                TreeHeroAction.Decorate -> dispatch(HomeAction.DecorateTree(id))
                                TreeHeroAction.PinTree, TreeHeroAction.PreviousTree, TreeHeroAction.NextTree -> Unit
                            }
                        }
                    }
                }
                tree != null -> {
                    { BioHero(world, ui, tree, true, short) }
                }
                else -> null
            },
        nextTask =
            when {
                dispatch == null -> next?.let { task -> { BioTaskRow(task, ui) } }
                else ->
                    liveState?.dashboard?.nextTasks?.firstOrNull()?.let { task ->
                        {
                            val item = task.toTaskListItem()
                            TaskListRow(item, ui.busy, { dispatch(HomeAction.OpenTask(item.id)) }, {
                                dispatch(
                                    if (item.timer) HomeAction.OpenTask(item.id, startTimer = true) else HomeAction.CompleteTask(item.id),
                                )
                            })
                        }
                    }
            },
        reward =
            if (liveState == null) {
                if (phase.b("canClaim") && !world.state.b("claimed")) ({ BioReward(world, ui) }) else null
            } else if (liveState.dashboard.canClaimReward && dispatch != null) {
                { HomeReward(false, true) { dispatch(HomeAction.ClaimReward) } }
            } else {
                null
            },
    )
}

private fun HomeTaskSummary.toTaskListItem(): TaskListItem =
    TaskListItem(
        id = id.toString(),
        title = title,
        category = category,
        minutes = minutes,
        reward =
            when {
                !timer -> 8
                minutes >= 45 -> 20
                minutes >= 20 -> 15
                else -> 10
            },
        done = false,
        timer = timer,
        active = true,
    )

private fun JSONObject.toTaskListItem() =
    TaskListItem(
        id = s("id"),
        title = s("title"),
        category = s("category"),
        minutes = i("minutes"),
        reward = taskReward(this),
        done = b("done") || b("finished"),
        timer = s("verification") == "TIMER",
        active = s("lifecycle", "ACTIVE") == "ACTIVE",
    )

private fun BioController.completeTask(
    id: String,
    timer: Boolean,
) {
    if (timer) go("focus", id)
}

@Composable
internal fun BioTaskRow(
    task: JSONObject,
    ui: BioController,
) {
    val item = task.toTaskListItem()
    TaskListRow(item, ui.busy, { ui.go("task", item.id) }, { ui.completeTask(item.id, item.timer) })
}

internal fun taskListRouteContent(
    world: BioWorld,
    ui: BioController,
): TaskListRouteContent {
    val filter = ui.taskSelection.filter
    val lifecycle =
        when (filter) {
            "paused" -> "PAUSED"
            "archived" -> "ARCHIVED"
            else -> "ACTIVE"
        }
    val phase = world.data.o("phase")
    return TaskListRouteContent(
        state =
            TaskListState(
                filter = filter,
                items = world.tasks.filter { it.s("lifecycle", "ACTIVE") == lifecycle }.map { it.toTaskListItem() },
                completed = phase.i("completed"),
                required = phase.i("required"),
                earned = world.state.i("earned"),
                busy = ui.busy,
            ),
        onAction = { action ->
            when (action) {
                TaskListAction.ClaimReward -> Unit
                TaskListAction.Add -> ui.go("task-new")
                TaskListAction.Focus -> ui.go("focus")
                TaskListAction.Templates -> ui.go("templates")
                TaskListAction.Insights -> ui.go("insights")
                is TaskListAction.Filter -> ui.taskSelection.filter = action.value
                is TaskListAction.Open -> ui.go("task", action.id)
                is TaskListAction.Complete -> ui.completeTask(action.id, action.timer)
            }
        },
        reward = { BioReward(world, ui) },
    )
}

@Preview(name = "Task row", widthDp = 335, showBackground = true)
@Composable
private fun BioTaskRowPreview() {
    val context = LocalContext.current
    val world = remember(context) { BioWorld.reference(context) }
    val title = AppText.text(TextKey.CopyDoc10TrangSachA3c873)
    val task = remember(world, title) { JSONObject(world.tasks.first().toString()).put("title", title) }
    BioTheme(world, "1") { BioTaskRow(task, remember { BioController("tasks") }) }
}
