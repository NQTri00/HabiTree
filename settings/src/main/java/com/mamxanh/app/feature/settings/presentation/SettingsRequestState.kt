package com.mamxanh.app.feature.settings.presentation

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

data class BackupImportSummary(
    val tasks: String,
    val trees: String,
    val occurrences: String,
    val created: String,
)

enum class NotificationPermissionResult {
    Granted,
    Denied;

    fun message(): String = AppText.text(
        when (this) {
            Granted -> TextKey.CopyDaBatThongBao5f71c6
            Denied -> TextKey.CopyThongBaoTatViecVaBoDemVanDungDuoc70a974
        },
    )
}

@Stable
class SettingsRequestState {
    private val overrides = mutableStateMapOf<String, String>()
    var backupImport by mutableStateOf<BackupImportSummary?>(null)

    fun reminderOptions(persisted: (String) -> Boolean): Map<String, Boolean> =
        ReminderKeys.associateWith { key -> overrides[key]?.toBoolean() ?: persisted(key) }

    fun setReminder(key: String, enabled: Boolean) {
        overrides[key] = enabled.toString()
    }

    fun savedFields(): Map<String, String> = buildMap {
        overrides.forEach { (key, value) -> put(Prefix + key, value) }
        backupImport?.let {
            put("backupTasks", it.tasks)
            put("backupTrees", it.trees)
            put("backupOccurrences", it.occurrences)
            put("backupCreated", it.created)
        }
    }

    fun restoreFields(saved: Map<String, String>): Map<String, String> {
        val other = saved.toMutableMap()
        saved.forEach { (key, value) ->
            if (key.startsWith(Prefix)) {
                overrides[key.removePrefix(Prefix)] = value
                other.remove(key)
            }
        }
        val tasks = other.remove("backupTasks")
        val trees = other.remove("backupTrees")
        val occurrences = other.remove("backupOccurrences")
        val created = other.remove("backupCreated")
        if (listOf(tasks, trees, occurrences, created).any { it != null }) {
            backupImport = BackupImportSummary(tasks ?: "0", trees ?: "0", occurrences ?: "0", created.orEmpty())
        }
        return other
    }

    fun clear() {
        overrides.clear()
        backupImport = null
    }

    private companion object {
        const val Prefix = "settings:"
        val ReminderKeys = listOf("paused", "treeReminders", "breakPrompts", "reducedMotion")
    }
}
