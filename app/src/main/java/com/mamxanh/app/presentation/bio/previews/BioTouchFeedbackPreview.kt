package com.mamxanh.app.presentation.bio

import com.mamxanh.app.core.components.design.*

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.AppLanguage
import com.mamxanh.shared.localization.TextKey

/** Controlled interaction states of actual controls, not a simulated device tap. */
@Preview(name="touch-feedback",widthDp=375,heightDp=812,locale="en",device="spec:width=375dp,height=812dp,dpi=160")
@Composable fun BioTouchFeedbackPreview() {
    AppText.language=AppLanguage.fromTag(LocalConfiguration.current.locales[0].language)
    val world=BioWorld.reference(LocalContext.current)
    BioPreviewTouchInput { BioTheme(world,"1") {
        Column(Modifier.fillMaxSize().background(bp.bg).padding(AppDimensions.Dp20),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp18)) {
            listOf("Resting" to BioTouchIndication(bp.deep),"Held press — no overlay" to BioTouchIndication(bp.deep,initiallyPressed=true),"Keyboard focus" to BioTouchIndication(bp.deep,initiallyFocused=true)).forEach { (caption,indication) ->
                Bt(caption,bold=true)
                CompositionLocalProvider(LocalIndication provides indication) {
                    Row(horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)) {
                        BioButton(AppText.text(TextKey.CopyTuoiCay0990f6),{},Modifier.weight(1f),"water",interactionSource=remember{MutableInteractionSource()})
                        BioButton(AppText.text(TextKey.CopyTrangTriD48231),{},Modifier.weight(1f),"spark",secondary=true,interactionSource=remember{MutableInteractionSource()})
                    }
                    BioCard(Modifier.fillMaxWidth(),onClick={},interactionSource=remember{MutableInteractionSource()}) { Bt(AppText.text(TextKey.CopyCayCuaBan64f9d7),bold=true) }
                }
            }
        }
    } }
}
