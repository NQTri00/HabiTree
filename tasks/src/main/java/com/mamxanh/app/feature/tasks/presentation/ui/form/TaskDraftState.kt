package com.mamxanh.app.feature.tasks.presentation.ui.form

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

    val error: String get() = values[ERROR_KEY].orEmpty()
    val errorAttempt: String? get() = values[ATTEMPT_KEY]

    operator fun get(field: TaskFormField): String? = values[field.savedKey]

    fun update(
        field: TaskFormField,
        value: String,
    ) {
        values[field.savedKey] = value
        isDirty = true
        if (error == field.savedKey) values[ERROR_KEY] = ""
    }

    fun validate(value: (TaskFormField) -> String): Boolean {
        val invalid = taskFormValidationError(value)
        values[ERROR_KEY] = invalid?.savedKey.orEmpty()
        values[ATTEMPT_KEY] = ((errorAttempt?.toIntOrNull() ?: 0) + 1).toString()
        return invalid == null
    }

    fun clear() {
        values.clear()
        isDirty = false
    }

    fun savedValues(): Map<String, String> = values.toMap()

    fun restore(
        saved: Map<String, String>,
        dirty: Boolean,
    ): Map<String, String> {
        values.clear()
        val other = saved.toMutableMap()
        storedKeys.forEach { key -> other.remove(key)?.let { values[key] = it } }
        isDirty = dirty
        return other
    }

    private companion object {
        const val ERROR_KEY = "task-error"
        const val ATTEMPT_KEY = "task-error-attempt"
        val storedKeys = TaskFormField.entries.map { it.savedKey } + listOf(ERROR_KEY, ATTEMPT_KEY)
    }
}
