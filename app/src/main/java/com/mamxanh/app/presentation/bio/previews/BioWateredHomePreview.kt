package com.mamxanh.app.presentation.bio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import com.mamxanh.shared.localization.AppLanguage
import com.mamxanh.shared.localization.AppText

@Composable internal fun BioWateredHomeReference() {
    val context=LocalContext.current
    AppText.language=AppLanguage.fromTag(LocalConfiguration.current.locales[0].language)
    val world=remember { BioWorld.reference(context).also { it.tree("1")?.put("watered",true) } }
    val ui=remember { BioController("home") }
    BioPreviewTouchInput { BioApplication(world,ui) }
}
