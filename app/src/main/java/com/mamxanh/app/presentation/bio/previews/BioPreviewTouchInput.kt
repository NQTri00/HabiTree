package com.mamxanh.app.presentation.bio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.InputModeManager
import androidx.compose.ui.platform.LocalInputModeManager

@Composable internal fun BioPreviewTouchInput(content:@Composable ()->Unit) {
    val input=remember { object:InputModeManager {
        override val inputMode=InputMode.Touch
        override fun requestInputMode(inputMode:InputMode)=inputMode==InputMode.Touch
    } }
    CompositionLocalProvider(LocalInputModeManager provides input,content=content)
}
