package com.mamxanh.app.feature.stats.data

import com.mamxanh.app.core.domain.RepositoryResult
import com.mamxanh.app.feature.stats.domain.JournalNote
import com.mamxanh.shared.data.GameDatabase
import com.mamxanh.shared.data.GameStorage
import com.mamxanh.shared.model.ActionResult
import com.mamxanh.shared.model.EngagementAction
import java.lang.reflect.Proxy
import org.junit.Assert.assertEquals
import org.junit.Test

class OfflineJournalRepositoryTest {
    @Test
    fun routesBothNotesToTheirExistingStorageOperations() {
        val expected = listOf(
            EngagementAction.Reflection("2026-11-16", "Ghi chú tuần"),
            EngagementAction.MemoryNote("memory-12", ""),
        )
        val actual = mutableListOf<EngagementAction>()
        val database = Proxy.newProxyInstance(
            GameDatabase::class.java.classLoader,
            arrayOf(GameDatabase::class.java),
        ) { _, method, args ->
            check(method.name == "engage")
            val action = args.single() as EngagementAction
            actual.add(action)
            when (action) {
                expected[0] -> ActionResult(true, "Saved")
                expected[1] -> ActionResult(false, "Memory unavailable")
                else -> error("Unexpected storage action: $action")
            }
        } as GameDatabase
        val repository = OfflineJournalRepositoryImpl(GameStorage(database))

        assertEquals(RepositoryResult.Success("Saved"), repository.save(JournalNote.WeeklyReflection("2026-11-16", "Ghi chú tuần")))
        assertEquals(RepositoryResult.Error("Memory unavailable"), repository.save(JournalNote.TimelineMemory("memory-12", "")))
        assertEquals(expected, actual)
    }
}
