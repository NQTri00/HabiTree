package com.mamxanh.app.feature.tasks.presentation

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
class TaskDraftState {
    private val values = mutableStateMapOf<String, String>()
    var isDirty by mutableStateOf(false)
        private set

    val error: String get() = values[ErrorKey].orEmpty()
    val errorAttempt: String? get() = values[AttemptKey]

    operator fun get(field: TaskFormField): String? = values[field.savedKey]

    fun update(field: TaskFormField, value: String) {
        values[field.savedKey] = value
        isDirty = true
        if (error == field.savedKey) values[ErrorKey] = ""
    }

    fun validate(value: (TaskFormField) -> String): Boolean {
        val invalid = taskFormValidationError(value)
        values[ErrorKey] = invalid?.savedKey.orEmpty()
        values[AttemptKey] = ((errorAttempt?.toIntOrNull() ?: 0) + 1).toString()
        return invalid == null
    }

    fun clear() {
        values.clear()
        isDirty = false
    }

    fun savedValues(): Map<String, String> = values.toMap()

    fun restore(saved: Map<String, String>, dirty: Boolean): Map<String, String> {
        values.clear()
        val other = saved.toMutableMap()
        StoredKeys.forEach { key -> other.remove(key)?.let { values[key] = it } }
        isDirty = dirty
        return other
    }

    private companion object {
        const val ErrorKey = "task-error"
        const val AttemptKey = "task-error-attempt"
        val StoredKeys = TaskFormField.entries.map { it.savedKey } + listOf(ErrorKey, AttemptKey)
    }
}
