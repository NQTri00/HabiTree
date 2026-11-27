package com.mamxanh.app.presentation.bio
import android.content.res.Configuration
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.InputModeManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.mamxanh.app.feature.settings.presentation.BackupImportSummary
import com.mamxanh.app.feature.shop.presentation.ui.shop.ShopPendingAction
import com.mamxanh.shared.localization.AppLanguage
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

@Composable internal fun BioReferenceSurface(
    route: String,
    param: String = "",
    modal: String = "",
    id: String = "",
    tab: String = "",
    home: String = "1",
    step: Int = 0,
    expanded: Boolean = false,
    bottom: Boolean = false,
    sheetBottom: Boolean = false,
    referenceFields: Map<String, String> = emptyMap(),
    busy: Boolean = false,
) {
    val baseContext = LocalContext.current
    val configuration = LocalConfiguration.current
    val language = if (configuration.locales[0].language == "en")AppLanguage.English else AppLanguage.Vietnamese
    AppText.language = language
    val context =
        remember(baseContext, configuration, language) {
            baseContext.createConfigurationContext(
                Configuration(configuration).apply { setLocale(java.util.Locale.forLanguageTag(language.tag)) },
            )
        }
    val requestedBusy = busy
    val initialFocus = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    val touch =
        remember {
            object : InputModeManager {
                override val inputMode = InputMode.Touch

                override fun requestInputMode(inputMode: InputMode) = inputMode == InputMode.Touch
            }
        }
    val world =
        remember(context) {
            BioWorld.reference(context).also {
                if (modal ==
                    "focus-end"
                ) {
                    it.state.put(
                        "focus",
                        json(
                            "id" to "reference",
                            "title" to AppText.forLanguage(TextKey.CopyMotKhoangChoRiengMinhCcf66e, language),
                            "state" to "RUNNING",
                            "remaining" to 1500,
                        ),
                    )
                }
            }
        }
    val ui =
        remember {
            BioController(route, param, modal).apply {
                modalId = id
                homeTreeState.selectedId = home
                preview.onboarding.step = step
                if (requestedBusy) {
                    this.busy = true
                    previewPurchase = ShopPendingAction.Buy
                }
                preview.restoreFields(referenceFields)
                if (modal == "confirm-import") {
                    preview.settingsRequests.backupImport =
                        BackupImportSummary(
                            tasks = "4",
                            trees = "3",
                            occurrences = "1",
                            created = "01:20:01 17/11/2026",
                            version = "1",
                        )
                }
                preview.expanded = expanded
                preview.scrollBottom = bottom
                preview.sheetBottom = sheetBottom
                if (tab.isNotBlank()) {
                    val parts = tab.split(':')
                    preview.setTab(parts[0], parts[1])
                }
            }
        }
    CompositionLocalProvider(
        LocalInputModeManager provides touch,
        LocalContext provides context,
        LocalResources provides context.resources,
    ) {
        Box(Modifier.fillMaxSize().focusRequester(initialFocus).focusable()) { BioApplication(world, ui) }
    }
    LaunchedEffect(Unit) {
        initialFocus.requestFocus()
        keyboard?.hide()
    }
}
