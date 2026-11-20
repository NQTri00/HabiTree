package com.mamxanh.app

import android.content.res.Configuration
import androidx.test.platform.app.InstrumentationRegistry
import com.mamxanh.core.ui.R
import com.mamxanh.shared.localization.AppLanguage
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Test

class AndroidCopyResourcesTest {
    @Test
    fun androidSelectsTheSamePluralAsTheCatalogForEachLanguage() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        for (language in AppLanguage.entries) {
            val configuration = Configuration(context.resources.configuration).apply {
                setLocale(Locale.forLanguageTag(language.tag))
            }
            val resources = context.createConfigurationContext(configuration).resources

            for (required in 0..2) {
                val expected = AppText.forLanguage(TextKey.Copy01ViecDaXongCf7a5d, language, 0, required)
                val actual = resources.getQuantityString(R.plurals.copy_0_1_viec_da_xong_cf7a5d, required, 0, required)
                assertEquals("${language.tag}, required=$required", expected, actual)
            }
        }
    }

    @Test
    fun formattedResourcesPreserveUserTextNumbersAndLiteralPercentSigns() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val title = "Plan 100% + review"
        for (language in AppLanguage.entries) {
            val configuration = Configuration(context.resources.configuration).apply {
                setLocale(Locale.forLanguageTag(language.tag))
            }
            val resources = context.createConfigurationContext(configuration).resources

            assertEquals(
                AppText.forLanguage(TextKey.CopyHoanThanhF183ae, language, title),
                resources.getString(R.string.copy_hoan_thanh_f183ae, title),
            )
            assertEquals(
                AppText.forLanguage(TextKey.CopyO5b7240, language, "03"),
                resources.getString(R.string.copy_o_5b7240, "03"),
            )
            assertEquals(
                AppText.forLanguage(TextKey.CopyTuanTruoc0SoLieuDenHomNay87b0c8, language, 75),
                resources.getString(R.string.copy_tuan_truoc_0_so_lieu_den_hom_nay_87b0c8, 75),
            )
        }
    }
}
