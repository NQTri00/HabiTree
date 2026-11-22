package com.mamxanh.shared

import com.mamxanh.shared.localization.AppLanguage
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import com.mamxanh.shared.model.catalog
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class AppTextTest {
    @Test fun defaultPlantNamesFollowTheLanguageWithoutChangingCustomNicknames() {
        val key = TextKey.CopyDaoHongXuanC2fd99
        val vietnamese = AppText.forLanguage(key, AppLanguage.Vietnamese)
        val english = AppText.forLanguage(key, AppLanguage.English)
        AppText.language = AppLanguage.English
        assertEquals(english, AppText.plantName("cherry", vietnamese))
        assertEquals("My balcony tree", AppText.plantName("cherry", "My balcony tree"))
        assertEquals(vietnamese, AppText.plantName("pine", vietnamese))
        AppText.language = AppLanguage.Vietnamese
        assertEquals(vietnamese, AppText.plantName("cherry", english))
    }

    @AfterTest fun resetLanguage() {
        AppText.language = AppLanguage.Vietnamese
    }

    @Test fun categoryIdsRemainCompatibleWhenLanguageChanges() {
        val key = TextKey.CopyHocTapA51516
        val storage = AppText.canonical(key)
        AppText.language = AppLanguage.English
        assertEquals(storage, AppText.canonical(key))
        assertEquals(AppText.text(key), AppText.category(storage))
        assertEquals("My own category", AppText.category("My own category"))
    }

    @Test fun catalogRefreshesWithoutTranslatingUserContent() {
        AppText.language = AppLanguage.Vietnamese
        val vietnameseName = catalog.first().name
        AppText.language = AppLanguage.English
        assertNotEquals(vietnameseName, catalog.first().name)
        assertEquals("My plant {0}", AppText.builtIn("My plant {0}"))
    }

    @Test fun languageTagsAcceptRegionQualifiers() {
        assertEquals(AppLanguage.English, AppLanguage.fromTag("en-US"))
        assertEquals(AppLanguage.Vietnamese, AppLanguage.fromTag("vi-VN"))
        assertEquals(AppLanguage.Vietnamese, AppLanguage.fromTag("fr"))
    }

    @Test fun pluralAndHistoryUseTheSelectedLanguage() {
        AppText.language = AppLanguage.English
        assertEquals("1 day", AppText.text(TextKey.Copy0Ngay45fa48, 1))
        assertEquals("2 days", AppText.text(TextKey.Copy0Ngay45fa48, 2))
        val oldTitle = AppText.forLanguage(TextKey.Copy01ViecEc6e1e, AppLanguage.Vietnamese, "My {0} list", 2)
        assertEquals("My {0} list: 2 tasks", AppText.history(oldTitle))
    }

    @Test fun formattingDoesNotInterpretUserArgumentsAsTemplates() {
        val userName = "Custom {1} name"
        assertEquals("$userName: 2 tasks", AppText.forLanguage(TextKey.Copy01ViecEc6e1e, AppLanguage.English, userName, 2))
    }

    @Test fun legacyHistoryCodesTranslateWhileTreeNamesRemainUntouched() {
        val treeName = "My {0} tree"
        AppText.language = AppLanguage.English
        assertEquals("Tree sold · $treeName", AppText.history("SOLD · $treeName"))
        AppText.language = AppLanguage.Vietnamese
        assertEquals(AppText.text(TextKey.HistoryTreeRemoved, treeName), AppText.history("REMOVED · $treeName"))
    }
}
