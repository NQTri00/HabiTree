package com.mamxanh.app.feature.tasks.domain

import com.mamxanh.app.feature.tasks.domain.model.TaskConfig

fun taskConfigFromDraft(fields: Map<String, String>, clock: (String) -> Int): TaskConfig {
    val kind = fields["kind"].orEmpty()
    return TaskConfig(
        title = fields["title"].orEmpty(),
        kind = kind,
        category = fields["category"].orEmpty(),
        description = fields["description"].orEmpty(),
        frequency = if (kind == "TODO") "ONCE" else fields["frequency"].orEmpty(),
        days = fields["days"].orEmpty(),
        due = fields["due"].orEmpty(),
        verification = fields["verification"].orEmpty(),
        minutes = fields["minutes"]?.toIntOrNull() ?: 5,
        startMinute = clock(fields["start"].orEmpty()),
        endMinute = clock(fields["end"].orEmpty()),
        reminderMinute = clock(fields["reminder"].orEmpty()),
    )
}
