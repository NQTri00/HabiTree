package com.mamxanh.app.presentation.bio

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.mamxanh.app.core.components.design.*
import com.mamxanh.app.feature.settings.presentation.navigation.BackupRouteContent
import com.mamxanh.app.feature.settings.presentation.navigation.SettingsRouteContent
import com.mamxanh.app.feature.settings.presentation.ui.backup.BackupAction
import com.mamxanh.app.feature.settings.presentation.ui.backup.BackupDisplay
import com.mamxanh.app.feature.settings.presentation.ui.backup.BackupScreenAction
import com.mamxanh.app.feature.settings.presentation.ui.backup.BackupViewModel
import com.mamxanh.app.feature.settings.presentation.ui.settings.SettingsDisplay
import com.mamxanh.app.feature.settings.presentation.ui.settings.SettingsLink
import com.mamxanh.app.feature.tasks.presentation.navigation.FocusRouteContent
import com.mamxanh.app.feature.tasks.presentation.navigation.TaskDetailRouteContent
import com.mamxanh.app.feature.tasks.presentation.navigation.TaskFormRouteContent
import com.mamxanh.app.feature.tasks.presentation.navigation.TemplatesRouteContent
import com.mamxanh.app.feature.tasks.presentation.ui.detail.TaskCompletionDisplay
import com.mamxanh.app.feature.tasks.presentation.ui.detail.TaskDetailDisplay
import com.mamxanh.app.feature.tasks.presentation.ui.focus.FocusDisplay
import com.mamxanh.app.feature.tasks.presentation.ui.focus.FocusTaskChoice
import com.mamxanh.app.feature.tasks.presentation.ui.form.TaskFormDisplay
import com.mamxanh.app.feature.tasks.presentation.ui.form.TaskFormField
import com.mamxanh.app.feature.tasks.presentation.ui.form.taskFormReward
import com.mamxanh.app.feature.tasks.presentation.ui.templates.TaskTemplateDisplay
import com.mamxanh.app.feature.tasks.presentation.ui.templates.TemplateRow
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.model.habitTemplates
import org.json.JSONObject

internal fun taskDetailRouteContent(
    world: BioWorld,
    ui: BioController,
    argument: String,
): TaskDetailRouteContent {
    val task = world.task(argument)
    val id = task?.s("id").orEmpty()
    val display =
        task?.let {
            TaskDetailDisplay(
                category = it.s("category"),
                title = it.s("title"),
                kind = it.s("kind"),
                frequency = it.s("frequency"),
                days = it.s("days"),
                description = it.s("description"),
                start = it.s("start"),
                end = it.s("end"),
                lastDone = it.s("lastDone"),
                due = it.s("due", AppText.text(TextKey.CopyKhongCoAf48b6)),
                lifecycle = it.s("lifecycle", "ACTIVE"),
                minutes = it.i("minutes"),
                streak = it.i("streak"),
                reward = taskReward(it),
                done = it.b("done") || it.b("finished"),
                timer = it.s("verification") == "TIMER",
                history =
                    it.a("history").objects().map { completion ->
                        TaskCompletionDisplay(completion.s("date"), completion.i("granted"), completion.i("nominal"))
                    },
            )
        }
    return TaskDetailRouteContent(
        t = display,
        onTasks = { ui.go("tasks") },
        onComplete = { if (display?.timer == true) ui.go("focus", id) else Unit },
        onEdit = { ui.go("task-new", id) },
        onInsights = { ui.go("insights", id) },
        onLifecycle = { Unit },
        onArchive = { ui.show("archive", id) },
    )
}

internal fun taskFormRouteContent(
    world: BioWorld,
    ui: BioController,
    argument: String,
    actions: (@Composable () -> Unit)?,
): TaskFormRouteContent {
    val initial = taskFormInitial(world, argument)
    val values =
        TaskFormField.entries.associateWith { field ->
            val key = field.savedKey
            ui.taskDraft[field] ?: initial.s(key, if (field == TaskFormField.Minutes) "5" else "")
        }
    val reward =
        taskFormReward(
            values[TaskFormField.Verification].orEmpty(),
            values[TaskFormField.Minutes].orEmpty(),
        )
    return TaskFormRouteContent(
        state = TaskFormDisplay(values, ui.taskDraft.error, ui.taskDraft.errorAttempt, reward),
        onFieldChanged = ui.taskDraft::update,
        actions = actions,
    )
}

internal fun focusRouteContent(
    world: BioWorld,
    ui: BioController,
    argument: String,
    actions: (@Composable () -> Unit)?,
    selection: LiveFocus? = null,
): FocusRouteContent {
    val focus = world.state.o("focus")
    return FocusRouteContent(
        state =
            selection?.display(bioDisplayDate(world.data.s("date"))) ?: FocusDisplay(
                title = focus.s("title", AppText.text(TextKey.CopyMotKhoangRiengMinh9ead0d)),
                present = focus.s("id").isNotBlank(),
                running = focus.s("state") == "RUNNING",
                onBreak = focus.b("break"),
                remaining = focus.i("remaining", 1500),
                date = bioDisplayDate(world.data.s("date")),
                minutes = focusMinutes(world, ui, argument),
                task = focusTask(ui, argument),
                eligibleTasks =
                    world.tasks
                        .filter {
                            it.s("verification") == "TIMER" &&
                                !it.b("done") &&
                                it.b("available", true) &&
                                it.s("lifecycle", "ACTIVE") == "ACTIVE"
                        }.map { FocusTaskChoice(it.s("id"), it.s("title"), it.i("minutes")) },
                history =
                    world.state
                        .a("focusHistory")
                        .objects()
                        .map { it.s("title") },
            ),
        onTask = selection?.let { it::selectTask } ?: { ui.taskSelection.focusTask = it },
        onMinutes = selection?.let { it::setMinutes } ?: { ui.taskSelection.focusMinutes = it },
        actions = actions,
    )
}

internal fun displayedTemplates(reference: BioWorld?) =
    if (reference == null) {
        habitTemplates.map {
            TaskTemplateDisplay(
                id = it.id,
                title = it.config.title,
                category = it.config.category,
                minutes = it.config.minutes,
                effort = it.effort,
                timer = it.config.verification == "TIMER",
                weekends = it.config.frequency == "WEEKENDS",
                reward = it.config.reward,
            )
        }
    } else {
        reference.catalog
            .a("templates")
            .objects()
            .map { it.toTemplateDisplay() }
    }

private fun JSONObject.toTemplateDisplay() =
    TaskTemplateDisplay(
        id = s("id"),
        title = s("title"),
        category = s("category"),
        minutes = i("minutes"),
        effort = s("effort"),
        timer = s("verification") == "TIMER",
        weekends = s("frequency") == "WEEKENDS",
        reward = taskReward(this),
    )

@Composable
internal fun BioTemplateRow(
    template: JSONObject,
    selected: Boolean,
    onboarding: Boolean = false,
    onChange: () -> Unit,
) {
    TemplateRow(template.toTemplateDisplay(), selected, onboarding, onChange)
}

internal fun templatesRouteContent(
    world: BioWorld,
    ui: BioController,
    actions: (@Composable () -> Unit)?,
    selection: LiveTemplates? = null,
): TemplatesRouteContent {
    val chosen = selection?.selected ?: selectedBioTemplates(ui).toSet()
    return TemplatesRouteContent(
        templates = displayedTemplates(world.takeIf { selection == null }),
        chosen = chosen,
        onToggle = selection?.let { it::toggle } ?: ui.taskSelection::toggleTemplate,
        actions = actions,
    )
}

internal fun settingsRouteContent(
    world: BioWorld,
    ui: BioController,
): SettingsRouteContent {
    val settings = world.settings
    val options = ui.settingsRequests.reminderOptions { settings.b(it) }
    return SettingsRouteContent(
        state = SettingsDisplay(options, settings.s("quietStart", "22:00"), settings.s("quietEnd", "07:00")),
        onLanguage = { Unit },
        onToggle = ui.settingsRequests::setReminder,
        onSave = { start, end ->
            Unit
        },
        onLink = { link ->
            when (link) {
                SettingsLink.Permission -> ui.show("permission")
                SettingsLink.Insights -> ui.go("insights")
                SettingsLink.Data -> ui.go("data")
                SettingsLink.Onboarding -> ui.go("onboard")
            }
        },
    )
}

internal fun backupRouteContent(
    world: BioWorld,
    ui: BioController,
    model: BackupViewModel? = null,
    display: BackupDisplay? = null,
): BackupRouteContent =
    BackupRouteContent(display ?: BackupDisplay(world.state.i("bytes", 3072) / 1024, world.state.b("safety"))) { action ->
        if (model != null) {
            model.dispatch(BackupAction.Screen(action))
        } else {
            when (action) {
                BackupScreenAction.Export -> Unit
                BackupScreenAction.Import -> Unit
                BackupScreenAction.Recover -> ui.show("recover")
                BackupScreenAction.Clear -> ui.show("clear")
                BackupScreenAction.License -> ui.show("license")
                BackupScreenAction.Diagnostics -> Unit
            }
        }
    }

@Preview(name = "Selected template", widthDp = 335, showBackground = true)
@Preview(name = "Selected template at large text", widthDp = 280, fontScale = 1.5f, showBackground = true)
@Composable
private fun BioTemplateRowPreview() {
    val context = LocalContext.current
    val world = remember(context) { BioWorld.reference(context) }
    BioTheme(world, "1") {
        BioTemplateRow(
            world.catalog
                .a("templates")
                .objects()
                .first(),
            selected = true,
            onChange = {},
        )
    }
}
