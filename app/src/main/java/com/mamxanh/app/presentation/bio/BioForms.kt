package com.mamxanh.app.presentation.bio

import com.mamxanh.app.feature.settings.presentation.BackupDisplay
import com.mamxanh.app.feature.settings.presentation.BackupScreenAction
import com.mamxanh.app.feature.settings.presentation.SettingsDisplay
import com.mamxanh.app.feature.settings.presentation.SettingsLink
import com.mamxanh.app.feature.settings.presentation.navigation.BackupRouteContent
import com.mamxanh.app.feature.settings.presentation.navigation.SettingsRouteContent

import com.mamxanh.app.feature.tasks.presentation.TaskCompletionDisplay
import com.mamxanh.app.feature.tasks.presentation.TaskDetailDisplay
import com.mamxanh.app.feature.tasks.presentation.navigation.TaskDetailRouteContent
import com.mamxanh.app.feature.tasks.presentation.TaskFormDisplay
import com.mamxanh.app.feature.tasks.presentation.TaskFormField
import com.mamxanh.app.feature.tasks.presentation.taskFormReward
import com.mamxanh.app.feature.tasks.presentation.navigation.TaskFormRouteContent
import com.mamxanh.app.feature.tasks.presentation.TaskTemplateDisplay
import com.mamxanh.app.feature.tasks.presentation.navigation.TemplatesRouteContent
import com.mamxanh.app.feature.tasks.presentation.TemplateRow
import com.mamxanh.app.feature.tasks.presentation.FocusDisplay
import com.mamxanh.app.feature.tasks.presentation.navigation.FocusRouteContent
import com.mamxanh.app.feature.tasks.presentation.FocusTaskChoice

import com.mamxanh.app.presentation.ui.design.*

import com.mamxanh.app.presentation.ui.design.AppColors
import com.mamxanh.app.presentation.ui.design.AppDimensions
import com.mamxanh.app.presentation.ui.design.AppIcons
import com.mamxanh.app.presentation.ui.design.AppOpacity
import com.mamxanh.app.presentation.ui.design.AppRadii
import com.mamxanh.app.presentation.ui.design.AppSpacing
import com.mamxanh.app.presentation.ui.design.AppTypography

import com.mamxanh.shared.localization.TextKey

import com.mamxanh.shared.localization.AppText

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.selection.toggleable
import androidx.compose.ui.semantics.*
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.mamxanh.app.presentation.ui.GardenFontFamily
import org.json.JSONObject

internal fun taskDetailRouteContent(world: BioWorld, ui: BioController, argument: String): TaskDetailRouteContent {
    val task = world.task(argument)
    val id = task?.s("id").orEmpty()
    val display = task?.let {
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
            history = it.a("history").objects().map { completion ->
                TaskCompletionDisplay(completion.s("date"), completion.i("granted"), completion.i("nominal"))
            },
        )
    }
    return TaskDetailRouteContent(
        t = display,
        onTasks = { ui.go("tasks") },
        onComplete = { if (display?.timer == true) ui.go("focus", id) else ui.send("complete:$id") },
        onEdit = { ui.go("task-new", id) },
        onInsights = { ui.go("insights", id) },
        onLifecycle = { ui.send("lifecycle:$id:$it") },
        onArchive = { ui.show("archive", id) },
    )
}

internal fun taskFormRouteContent(world: BioWorld, ui: BioController, argument: String): TaskFormRouteContent {
    val initial = taskFormInitial(world, argument)
    val values = TaskFormField.entries.associateWith { field ->
        val key = field.savedKey
        ui.taskDraft[field] ?: initial.s(key, if (field == TaskFormField.Minutes) "5" else "")
    }
    val reward = taskFormReward(
        values[TaskFormField.Verification].orEmpty(),
        values[TaskFormField.Minutes].orEmpty(),
    )
    return TaskFormRouteContent(
        state = TaskFormDisplay(values, ui.taskDraft.error, ui.taskDraft.errorAttempt, reward),
        onFieldChanged = ui.taskDraft::update,
    )
}

internal fun focusRouteContent(world: BioWorld, ui: BioController, argument: String): FocusRouteContent {
    val focus = world.state.o("focus")
    return FocusRouteContent(
        state = FocusDisplay(
            title = focus.s("title", AppText.text(TextKey.CopyMotKhoangRiengMinh9ead0d)),
            present = focus.s("id").isNotBlank(),
            running = focus.s("state") == "RUNNING",
            onBreak = focus.b("break"),
            remaining = focus.i("remaining", 1500),
            date = bioDisplayDate(world.data.s("date")),
            minutes = focusMinutes(world, ui, argument),
            task = focusTask(ui, argument),
            eligibleTasks = world.tasks.filter {
                it.s("verification") == "TIMER" && !it.b("done") && it.b("available", true) &&
                    it.s("lifecycle", "ACTIVE") == "ACTIVE"
            }.map { FocusTaskChoice(it.s("id"), it.s("title"), it.i("minutes")) },
            history = world.state.a("focusHistory").objects().map { it.s("title") },
        ),
        onTask = { ui.taskSelection.focusTask = it },
        onMinutes = { ui.taskSelection.focusMinutes = it },
    )
}

private fun JSONObject.toTemplateDisplay() = TaskTemplateDisplay(
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
internal fun BioTemplateRow(template: JSONObject, selected: Boolean, onboarding: Boolean = false, onChange: () -> Unit) {
    TemplateRow(template.toTemplateDisplay(), selected, onboarding, onChange)
}

internal fun templatesRouteContent(world: BioWorld, ui: BioController): TemplatesRouteContent {
    val chosen = selectedBioTemplates(ui).toSet()
    return TemplatesRouteContent(
        templates = world.catalog.a("templates").objects().map { it.toTemplateDisplay() },
        chosen = chosen,
        onToggle = ui.taskSelection::toggleTemplate,
    )
}

internal fun settingsRouteContent(world: BioWorld, ui: BioController): SettingsRouteContent {
    val settings = world.settings
    val options = ui.settingsRequests.reminderOptions { settings.b(it) }
    return SettingsRouteContent(
        state = SettingsDisplay(options, settings.s("quietStart", "22:00"), settings.s("quietEnd", "07:00")),
        onLanguage = { ui.send("language", mapOf("tag" to it)) },
        onToggle = ui.settingsRequests::setReminder,
        onSave = { start, end ->
            ui.send("settings", options.mapValues { it.value.toString() } + mapOf("quietStart" to start, "quietEnd" to end))
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

internal fun backupRouteContent(world: BioWorld, ui: BioController): BackupRouteContent {
    return BackupRouteContent(BackupDisplay(world.state.i("bytes", 3072) / 1024, world.state.b("safety"))) { action ->
        when (action) {
            BackupScreenAction.Export -> ui.send("export")
            BackupScreenAction.Import -> ui.send("import")
            BackupScreenAction.Recover -> ui.show("recover")
            BackupScreenAction.Clear -> ui.show("clear")
            BackupScreenAction.License -> ui.show("license")
            BackupScreenAction.Diagnostics -> ui.send("diagnostics")
        }
    }
}
