package com.mamxanh.app.feature.tasks.presentation.ui.list

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
class TaskScreenSelection {
    var filter by mutableStateOf("today")
    private val fields = mutableStateMapOf<String, String>()

    var focusTask: String?
        get() = fields[FOCUS_TASK_KEY]
        set(value) {
            setFocusField(FOCUS_TASK_KEY, value)
        }
    var focusMinutes: String?
        get() = fields[FOCUS_MINUTES_KEY]
        set(value) {
            setFocusField(FOCUS_MINUTES_KEY, value)
        }

    fun selectedTemplates(): List<String> =
        fields
            .filter { (key, value) -> key.startsWith(TEMPLATE_PREFIX) && value == "true" }
            .keys
            .map { it.removePrefix(TEMPLATE_PREFIX) }

    fun toggleTemplate(id: String) {
        val key = TEMPLATE_PREFIX + id
        fields[key] = (fields[key] != "true").toString()
    }

    fun clearFocus() {
        focusTask = null
        focusMinutes = null
    }

    fun clearFields() = fields.clear()

    fun savedFields(): Map<String, String> = fields.toMap()

    fun restoreFields(saved: Map<String, String>): Map<String, String> {
        val other = saved.toMutableMap()
        saved.forEach { (key, value) ->
            if (key == FOCUS_TASK_KEY || key == FOCUS_MINUTES_KEY || key.startsWith(TEMPLATE_PREFIX)) {
                fields[key] = value
                other.remove(key)
            }
        }
        return other
    }

    private fun setFocusField(
        key: String,
        value: String?,
    ) {
        if (value == null) fields.remove(key) else fields[key] = value
    }

    companion object {
        const val FILTER_KEY = "taskFilter"
        private const val FOCUS_TASK_KEY = "focus:task"
        private const val FOCUS_MINUTES_KEY = "focus:minutes"
        private const val TEMPLATE_PREFIX = "template:"
    }
}
