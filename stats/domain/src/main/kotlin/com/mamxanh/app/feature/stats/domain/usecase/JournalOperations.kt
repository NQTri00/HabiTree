package com.mamxanh.app.feature.stats.domain.usecase

import com.mamxanh.app.core.domain.RepositoryResult

sealed interface JournalNote {
    val text: String

    data class WeeklyReflection(
        val weekStart: String,
        override val text: String,
    ) : JournalNote

    data class TimelineMemory(
        val memoryId: String,
        override val text: String,
    ) : JournalNote
}

interface JournalRepository {
    fun save(note: JournalNote): RepositoryResult<String>
}

class SaveJournalNoteUseCase(
    private val repository: JournalRepository,
) {
    operator fun invoke(note: JournalNote): RepositoryResult<String> = repository.save(note)
}
