package com.mamxanh.app.presentation.bio

import com.mamxanh.app.feature.settings.presentation.BackupDisplay
import com.mamxanh.app.feature.settings.presentation.BackupScreenAction
import com.mamxanh.app.feature.settings.presentation.SettingsDisplay
import com.mamxanh.app.feature.settings.presentation.SettingsLink
import com.mamxanh.app.feature.settings.presentation.navigation.BackupRouteContent
import com.mamxanh.app.feature.settings.presentation.navigation.SettingsRouteContent

import com.mamxanh.app.feature.tasks.presentation.TaskCompletionDisplay
import com.mamxanh.app.feature.tasks.presentation.TaskDetailDisplay
import com.mamxanh.app.feature.tasks.presentation.TaskDetailScreen
import com.mamxanh.app.feature.tasks.presentation.TaskFormDisplay
import com.mamxanh.app.feature.tasks.presentation.TaskFormField
import com.mamxanh.app.feature.tasks.presentation.TaskFormScreen
import com.mamxanh.app.feature.tasks.presentation.TaskTemplateDisplay
import com.mamxanh.app.feature.tasks.presentation.TaskTemplatesScreen
import com.mamxanh.app.feature.tasks.presentation.TemplateRow
import com.mamxanh.app.feature.tasks.presentation.FocusDisplay
import com.mamxanh.app.feature.tasks.presentation.FocusScreen
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

@Composable
internal fun BioTaskDetail(world: BioWorld, ui: BioController) {
    val task = world.task(ui.param)
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
    TaskDetailScreen(
        t = display,
        onTasks = { ui.go("tasks") },
        onComplete = { if (display?.timer == true) ui.go("focus", id) else ui.send("complete:$id") },
        onEdit = { ui.go("task-new", id) },
        onInsights = { ui.go("insights", id) },
        onLifecycle = { ui.send("lifecycle:$id:$it") },
        onArchive = { ui.show("archive", id) },
    )
}

@Composable
internal fun BioTaskForm(world: BioWorld, ui: BioController) {
    val initial = taskFormInitial(world, ui)
    val values = TaskFormField.entries.associateWith { field ->
        val key = field.name.lowercase()
        ui.fields[key] ?: initial.s(key, if (field == TaskFormField.Minutes) "5" else "")
    }
    val timer = values[TaskFormField.Verification] == "TIMER"
    val minutes = values[TaskFormField.Minutes].orEmpty().toIntOrNull() ?: 5
    val reward = if (timer) when {
        minutes >= 45 -> 20
        minutes >= 20 -> 15
        else -> 10
    } else 8
    TaskFormScreen(
        state = TaskFormDisplay(values, ui.fields["task-error"].orEmpty(), ui.fields["task-error-attempt"], reward),
        onFieldChanged = { field, value ->
            val key = field.name.lowercase()
            ui.fields[key] = value
            ui.dirty = true
            if (ui.fields["task-error"] == key) ui.fields["task-error"] = ""
        },
    )
}

@Composable
internal fun ColumnScope.BioFocus(world: BioWorld, ui: BioController) {
    val focus = world.state.o("focus")
    FocusScreen(
        state = FocusDisplay(
            title = focus.s("title", AppText.text(TextKey.CopyMotKhoangRiengMinh9ead0d)),
            present = focus.s("id").isNotBlank(),
            running = focus.s("state") == "RUNNING",
            onBreak = focus.b("break"),
            remaining = focus.i("remaining", 1500),
            date = bioDisplayDate(world.data.s("date")),
            minutes = focusMinutes(world, ui),
            task = focusTask(ui),
            eligibleTasks = world.tasks.filter {
                it.s("verification") == "TIMER" && !it.b("done") && it.b("available", true) &&
                    it.s("lifecycle", "ACTIVE") == "ACTIVE"
            }.map { FocusTaskChoice(it.s("id"), it.s("title"), it.i("minutes")) },
            history = world.state.a("focusHistory").objects().map { it.s("title") },
        ),
        onTask = { ui.fields["focus:task"] = it },
        onMinutes = { ui.fields["focus:minutes"] = it },
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

@Composable
internal fun BioTemplates(world: BioWorld, ui: BioController) {
    val chosen = selectedBioTemplates(ui).toSet()
    TaskTemplatesScreen(
        templates = world.catalog.a("templates").objects().map { it.toTemplateDisplay() },
        chosen = chosen,
        onToggle = { id -> ui.fields["template:$id"] = (id !in chosen).toString() },
    )
}

internal fun settingsRouteContent(world: BioWorld, ui: BioController): SettingsRouteContent {
    val settings = world.settings
    val options = listOf("paused", "treeReminders", "breakPrompts", "reducedMotion").associateWith { key ->
        ui.fields["settings:$key"]?.toBoolean() ?: settings.b(key)
    }
    return SettingsRouteContent(
        state = SettingsDisplay(options, settings.s("quietStart", "22:00"), settings.s("quietEnd", "07:00")),
        onLanguage = { ui.send("language", mapOf("tag" to it)) },
        onToggle = { key, value -> ui.fields["settings:$key"] = value.toString() },
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
