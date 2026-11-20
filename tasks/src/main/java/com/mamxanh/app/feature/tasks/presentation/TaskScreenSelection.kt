package com.mamxanh.app.feature.tasks.presentation

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
        get() = fields[FocusTaskKey]
        set(value) { setFocusField(FocusTaskKey, value) }
    var focusMinutes: String?
        get() = fields[FocusMinutesKey]
        set(value) { setFocusField(FocusMinutesKey, value) }

    fun selectedTemplates(): List<String> =
        fields.filter { (key, value) -> key.startsWith(TemplatePrefix) && value == "true" }
            .keys.map { it.removePrefix(TemplatePrefix) }

    fun toggleTemplate(id: String) {
        val key = TemplatePrefix + id
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
            if (key == FocusTaskKey || key == FocusMinutesKey || key.startsWith(TemplatePrefix)) {
                fields[key] = value
                other.remove(key)
            }
        }
        return other
    }

    private fun setFocusField(key: String, value: String?) {
        if (value == null) fields.remove(key) else fields[key] = value
    }

    companion object {
        const val FilterKey = "taskFilter"
        private const val FocusTaskKey = "focus:task"
        private const val FocusMinutesKey = "focus:minutes"
        private const val TemplatePrefix = "template:"
    }
}
