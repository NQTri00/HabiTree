package com.mamxanh.app.core.components

import androidx.compose.material3.lightColorScheme
import com.mamxanh.app.core.components.design.AppColors
import com.mamxanh.app.core.components.design.AppDimensions

val Green = AppColors.Green
val Deep = AppColors.Deep
val Pale = AppColors.Pale
val Cream = AppColors.Cream
val Gold = AppColors.RewardSurface
val Muted = AppColors.Muted

val GardenColorScheme =
    lightColorScheme(
        primary = AppColors.Green,
        onPrimary = AppColors.White,
        primaryContainer = Pale,
        onPrimaryContainer = Deep,
        secondary = Green,
        onSecondary = AppColors.White,
        secondaryContainer = Pale,
        onSecondaryContainer = Deep,
        tertiaryContainer = Gold,
        onTertiaryContainer = Deep,
        tertiary = AppColors.SwatchFF467F63,
        background = AppColors.Cream,
        onBackground = Deep,
        surface = AppColors.SwatchFFFFFFFF,
        onSurface = Deep,
        surfaceVariant = AppColors.Pale,
        onSurfaceVariant = Muted,
        surfaceContainerLowest = AppColors.White,
        surfaceContainerLow = AppColors.SwatchFFF5FBF7,
        surfaceContainer = AppColors.SwatchFFF0F8F3,
        surfaceContainerHigh = AppColors.SwatchFFEDF6F0,
        surfaceContainerHighest = AppColors.Pale,
        outline = AppColors.SwatchFF9DB8A9,
        outlineVariant = AppColors.SwatchFFE4E7DC,
    )

// Farm Concept Colors (Image 1)
val SkyPastel = AppColors.SkyPastel
val MeadowPastel = AppColors.MeadowPastel
val MeadowDark = AppColors.MeadowDark
val BarnRed = AppColors.BarnRed
val BarnRedDark = AppColors.BarnRedDark
val BarnWhite = AppColors.BarnWhite
val SoilMound = AppColors.SoilMound
val SoilMoundLight = AppColors.SoilMoundLight
val WaterTeal = AppColors.WaterTeal
val WaterTealDark = AppColors.WaterTealDark
val BrickRed = AppColors.BrickRed
val BrickRedDark = AppColors.BrickRedDark
val ShinyBlue = AppColors.ShinyBlue
val ShinyBlueDark = AppColors.ShinyBlueDark
val WaterBlue = AppColors.WaterBlue
val OrangeNutrient = AppColors.OrangeNutrient
val OrangeNutrientDark = AppColors.OrangeNutrientDark
val CuteSproutGreen = AppColors.CuteSproutGreen
val CuteSproutLight = AppColors.CuteSproutLight

// Shop Concept Colors (Image 2)
val PinkTileBg = AppColors.PinkTileBg
val PinkTileLine = AppColors.PinkTileLine
val ShelfWood = AppColors.ShelfWood
val ShelfWoodDark = AppColors.ShelfWoodDark
val ShopCardBg = AppColors.ShopCardBg
val ShopCardBorder = AppColors.ShopCardBorder
val ShopTabGreen = AppColors.ShopTabGreen
val ShopPriceGold = AppColors.ShopPriceGold
val AwningRed = AppColors.AwningRed
val AwningCream = AppColors.AwningCream

val GardenShapes =
    androidx.compose.material3.Shapes(
        small =
            androidx.compose.foundation.shape
                .RoundedCornerShape(AppDimensions.Dp24),
    )
