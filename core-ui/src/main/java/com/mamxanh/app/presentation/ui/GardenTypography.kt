package com.mamxanh.app.presentation.ui

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily

private val baseTypography = Typography()

/** Storybook serif typography shared by the real app and all screen previews. */
val GardenTypography = Typography(
    displayLarge = baseTypography.displayLarge.copy(fontFamily = FontFamily.Serif),
    displayMedium = baseTypography.displayMedium.copy(fontFamily = FontFamily.Serif),
    displaySmall = baseTypography.displaySmall.copy(fontFamily = FontFamily.Serif),
    headlineLarge = baseTypography.headlineLarge.copy(fontFamily = FontFamily.Serif),
    headlineMedium = baseTypography.headlineMedium.copy(fontFamily = FontFamily.Serif),
    headlineSmall = baseTypography.headlineSmall.copy(fontFamily = FontFamily.Serif),
    titleLarge = baseTypography.titleLarge.copy(fontFamily = FontFamily.Serif),
    titleMedium = baseTypography.titleMedium.copy(fontFamily = FontFamily.Serif),
    titleSmall = baseTypography.titleSmall.copy(fontFamily = FontFamily.Serif),
    bodyLarge = baseTypography.bodyLarge.copy(fontFamily = FontFamily.Serif),
    bodyMedium = baseTypography.bodyMedium.copy(fontFamily = FontFamily.Serif),
    bodySmall = baseTypography.bodySmall.copy(fontFamily = FontFamily.Serif),
    labelLarge = baseTypography.labelLarge.copy(fontFamily = FontFamily.Serif),
    labelMedium = baseTypography.labelMedium.copy(fontFamily = FontFamily.Serif),
    labelSmall = baseTypography.labelSmall.copy(fontFamily = FontFamily.Serif)
)
