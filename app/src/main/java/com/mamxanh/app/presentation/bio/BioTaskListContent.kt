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
import com.mamxanh.app.feature.tasks.presentation.navigation.TaskListRouteContent
import com.mamxanh.app.feature.tasks.presentation.ui.list.TaskListAction
import com.mamxanh.app.feature.tasks.presentation.ui.list.TaskListItem
import com.mamxanh.app.feature.tasks.presentation.ui.list.TaskListRow
import com.mamxanh.app.feature.tasks.presentation.ui.list.TaskListState
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import org.json.JSONObject

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
    val filter = ui.preview.taskSelection.filter
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
                is TaskListAction.Filter -> ui.preview.taskSelection.filter = action.value
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
