package com.mamxanh.app.core.components

import com.mamxanh.app.core.components.design.AppTypography

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.mamxanh.core.ui.R

/** Sans-serif typography used by the current mint reference concepts. */
val GardenFontFamily = FontFamily(
    Font(R.font.be_vietnam_pro_regular, FontWeight.Normal),
    Font(R.font.be_vietnam_pro_semibold, FontWeight.SemiBold),
    Font(R.font.be_vietnam_pro_bold, FontWeight.Bold)
)
private val baseTypography = Typography()
val GardenTypography = Typography(
    displayLarge=baseTypography.displayLarge.copy(fontFamily=GardenFontFamily,letterSpacing=AppTypography.Sp0),
    displayMedium=baseTypography.displayMedium.copy(fontFamily=GardenFontFamily,letterSpacing=AppTypography.Sp0),
    displaySmall=baseTypography.displaySmall.copy(fontFamily=GardenFontFamily,letterSpacing=AppTypography.Sp0),
    headlineLarge=baseTypography.headlineLarge.copy(fontFamily=GardenFontFamily,letterSpacing=AppTypography.Sp0),
    headlineMedium=baseTypography.headlineMedium.copy(fontFamily=GardenFontFamily,letterSpacing=AppTypography.Sp0),
    headlineSmall=baseTypography.headlineSmall.copy(fontFamily=GardenFontFamily,letterSpacing=AppTypography.Sp0),
    titleLarge=baseTypography.titleLarge.copy(fontFamily=GardenFontFamily,letterSpacing=AppTypography.Sp0),
    titleMedium=baseTypography.titleMedium.copy(fontFamily=GardenFontFamily,letterSpacing=AppTypography.Sp0),
    titleSmall=baseTypography.titleSmall.copy(fontFamily=GardenFontFamily,letterSpacing=AppTypography.Sp0),
    bodyLarge=baseTypography.bodyLarge.copy(fontFamily=GardenFontFamily,letterSpacing=AppTypography.Sp0),
    bodyMedium=baseTypography.bodyMedium.copy(fontFamily=GardenFontFamily,letterSpacing=AppTypography.Sp0),
    bodySmall=baseTypography.bodySmall.copy(fontFamily=GardenFontFamily,letterSpacing=AppTypography.Sp0),
    labelLarge=baseTypography.labelLarge.copy(fontFamily=GardenFontFamily,letterSpacing=AppTypography.Sp0),
    labelMedium=baseTypography.labelMedium.copy(fontFamily=GardenFontFamily,letterSpacing=AppTypography.Sp0),
    labelSmall=baseTypography.labelSmall.copy(fontFamily=GardenFontFamily,letterSpacing=AppTypography.Sp0)
)
