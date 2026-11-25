package com.mamxanh.app.feature.home.presentation.ui.onboarding

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.mamxanh.app.core.components.design.*

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun OnboardingScreenPreview() {
    Column(Modifier.fillMaxSize()) {
        OnboardingScreen(OnboardingDisplay(0, "succulent", "", 0, emptyList(), emptyMap(), 0, false), 20, {}, {})
    }
}
