package com.mamxanh.app.feature.stats.data

import com.mamxanh.app.core.domain.RepositoryResult
import com.mamxanh.app.feature.stats.domain.JournalNote
import com.mamxanh.app.feature.stats.domain.JournalRepository
import com.mamxanh.shared.data.GameStorage
import com.mamxanh.shared.model.EngagementAction

internal class OfflineJournalRepositoryImpl(private val storage: GameStorage) : JournalRepository {
    override fun save(note: JournalNote): RepositoryResult<String> {
        val action = when (note) {
            is JournalNote.WeeklyReflection -> EngagementAction.Reflection(note.weekStart, note.text)
            is JournalNote.TimelineMemory -> EngagementAction.MemoryNote(note.memoryId, note.text)
        }
        val result = storage.engage(action)
        return if (result.success) RepositoryResult.Success(result.message) else RepositoryResult.Error(result.message)
    }
}
