package com.mamxanh.app.presentation.bio

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.mamxanh.app.core.components.design.*
import com.mamxanh.app.feature.home.presentation.navigation.HomeRouteContent
import com.mamxanh.app.feature.home.presentation.ui.home.HomeScreenAction
import com.mamxanh.app.feature.home.presentation.ui.home.HomeScreenState
import com.mamxanh.app.feature.tasks.presentation.navigation.TaskListRouteContent
import com.mamxanh.app.feature.tasks.presentation.ui.list.TaskListAction
import com.mamxanh.app.feature.tasks.presentation.ui.list.TaskListItem
import com.mamxanh.app.feature.tasks.presentation.ui.list.TaskListRow
import com.mamxanh.app.feature.tasks.presentation.ui.list.TaskListState
import org.json.JSONObject

internal fun homeRouteContent(
    world: BioWorld,
    ui: BioController,
    short: Boolean,
): HomeRouteContent {
    val tree = world.tree(ui.homeTreeState.selectedId)
    val next =
        world.tasks.firstOrNull {
            !it.b("done") &&
                !it.b("finished") &&
                it.s("lifecycle", "ACTIVE") == "ACTIVE" &&
                it.b("scheduledToday", true)
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
                completed = phase.i("completed"),
                required = phase.i("required"),
                earned = world.state.i("earned"),
                streak = phase.i("streak"),
            ),
        onAction = { action ->
            when (action) {
                HomeScreenAction.PickSeed -> ui.show("seed-picker")
                HomeScreenAction.ViewTasks -> ui.go("tasks")
                HomeScreenAction.AddTask -> ui.go("task-new")
                HomeScreenAction.BrowseSeeds -> ui.act("bio-seed-picker")
                HomeScreenAction.BrowseAccessories -> ui.act("bio-accessory-shop")
                HomeScreenAction.Review -> ui.go("review")
            }
        },
        tree =
            if (tree != null) {
                { BioHero(world, ui, tree, true, short) }
            } else {
                null
            },
        nextTask =
            if (next != null) {
                { BioTaskRow(next, ui) }
            } else {
                null
            },
        reward =
            if (phase.b("canClaim") && !world.state.b("claimed")) {
                { BioReward(world, ui) }
            } else {
                null
            },
    )
}

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
    if (timer) go("focus", id) else send("complete:$id")
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
    BioTheme(world, "1") { BioTaskRow(world.tasks.first(), remember { BioController("tasks") }) }
}
