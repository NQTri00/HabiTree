package com.mamxanh.app

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.mamxanh.app.feature.home.domain.usecase.HomeTreeSelection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.koin.core.context.GlobalContext

class HomeTreeSelectionPersistenceTest {
    @Test
    fun featureBindingReadsAndUpdatesTheExistingSelection() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        check(context.packageName != "com.mamxanh.app") { "Use the isolated verification app." }
        val preferences = context.getSharedPreferences("bio_ui", Context.MODE_PRIVATE)
        val key = "home_tree_id"
        val previousId = preferences.getLong(key, 0L).takeIf { preferences.contains(key) }
        try {
            assertTrue(preferences.edit().putLong(key, 42L).commit())
            val first = GlobalContext.get().get<HomeTreeSelection>()
            assertEquals("42", first.savedId())
            first.save("71")
            assertEquals(71L, preferences.getLong(key, 0L))
            assertEquals("71", GlobalContext.get().get<HomeTreeSelection>().savedId())
        } finally {
            val editor = preferences.edit()
            if (previousId == null) editor.remove(key) else editor.putLong(key, previousId)
            assertTrue(editor.commit())
        }
    }
}
