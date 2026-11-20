package com.mamxanh.app

import com.mamxanh.app.feature.tasks.presentation.ui.focus.focusSessionTitle
import com.mamxanh.shared.localization.AppLanguage
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import org.junit.Assert.assertEquals
import org.junit.Test

class FocusSessionTitleTest {
    @Test
    fun freeSessionTitleUsesTheSelectedLanguage() {
        val key = TextKey.CopyTapTrungTuDo7ed36c
        val saved = AppText.forLanguage(key, AppLanguage.Vietnamese)

        assertEquals(AppText.forLanguage(key, AppLanguage.English), focusSessionTitle(0, saved, AppLanguage.English))
        assertEquals(saved, focusSessionTitle(0, saved, AppLanguage.Vietnamese))
    }

    @Test
    fun anAttachedTaskKeepsItsSavedTitleAcrossLanguageChanges() {
        val saved = AppText.forLanguage(TextKey.CopyTapTrungTuDo7ed36c, AppLanguage.Vietnamese)

        assertEquals(saved, focusSessionTitle(82, saved, AppLanguage.English))
    }
}
