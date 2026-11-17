package com.mamxanh.app.presentation.ui

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
    displayLarge=baseTypography.displayLarge.copy(fontFamily=GardenFontFamily,letterSpacing=0.sp),
    displayMedium=baseTypography.displayMedium.copy(fontFamily=GardenFontFamily,letterSpacing=0.sp),
    displaySmall=baseTypography.displaySmall.copy(fontFamily=GardenFontFamily,letterSpacing=0.sp),
    headlineLarge=baseTypography.headlineLarge.copy(fontFamily=GardenFontFamily,letterSpacing=0.sp),
    headlineMedium=baseTypography.headlineMedium.copy(fontFamily=GardenFontFamily,letterSpacing=0.sp),
    headlineSmall=baseTypography.headlineSmall.copy(fontFamily=GardenFontFamily,letterSpacing=0.sp),
    titleLarge=baseTypography.titleLarge.copy(fontFamily=GardenFontFamily,letterSpacing=0.sp),
    titleMedium=baseTypography.titleMedium.copy(fontFamily=GardenFontFamily,letterSpacing=0.sp),
    titleSmall=baseTypography.titleSmall.copy(fontFamily=GardenFontFamily,letterSpacing=0.sp),
    bodyLarge=baseTypography.bodyLarge.copy(fontFamily=GardenFontFamily,letterSpacing=0.sp),
    bodyMedium=baseTypography.bodyMedium.copy(fontFamily=GardenFontFamily,letterSpacing=0.sp),
    bodySmall=baseTypography.bodySmall.copy(fontFamily=GardenFontFamily,letterSpacing=0.sp),
    labelLarge=baseTypography.labelLarge.copy(fontFamily=GardenFontFamily,letterSpacing=0.sp),
    labelMedium=baseTypography.labelMedium.copy(fontFamily=GardenFontFamily,letterSpacing=0.sp),
    labelSmall=baseTypography.labelSmall.copy(fontFamily=GardenFontFamily,letterSpacing=0.sp)
)
