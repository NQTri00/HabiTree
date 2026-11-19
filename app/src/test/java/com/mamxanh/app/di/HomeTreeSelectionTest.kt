package com.mamxanh.app.di

import com.mamxanh.app.feature.home.domain.HomeTreeSelection
import com.mamxanh.app.feature.home.domain.HomeTreeSelectionRepository
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeTreeSelectionTest {
    @Test
    fun savedSelectionSurvivesRecreationAndMissingTreesUseTheExistingFallback() {
        val repository = object : HomeTreeSelectionRepository {
            private var id = 0L
            override fun read(): Long = id
            override fun write(treeId: Long) {
                id = treeId
            }
        }
        HomeTreeSelection(repository).save("42")
        val recreated = HomeTreeSelection(repository)

        assertEquals("42", recreated.savedId())
        assertEquals("42", recreated.resolve(recreated.savedId(), listOf("7", "42")))
        assertEquals("7", recreated.resolve(recreated.savedId(), listOf("7")))
        assertEquals("0", recreated.resolve(recreated.savedId(), emptyList()))
        assertEquals("42", recreated.savedId())
    }
}
