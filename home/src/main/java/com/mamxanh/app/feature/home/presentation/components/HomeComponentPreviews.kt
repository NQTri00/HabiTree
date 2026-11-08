package com.mamxanh.app.feature.home.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mamxanh.app.presentation.ui.MamXanhPreviewTheme

@Preview(name="Farm landscape",widthDp=360,heightDp=800,showBackground=true)
@Composable
fun FarmPastoralBackdropPreview() { FarmPastoralBackdrop(Modifier.fillMaxSize()) }

@Preview(name="Home resources",widthDp=360,showBackground=true)
@Composable
fun HomeTopBarPreview() { MamXanhPreviewTheme { HomeTopBar(68,2,4,120,{}) } }

@Preview(name="Nutrient badge",showBackground=true)
@Composable
fun NutrientCircleBadgePreview() { MamXanhPreviewTheme { NutrientCircleBadge(68) } }

@Preview(name="Care actions",widthDp=360,showBackground=true)
@Composable
fun HomeBottomActionBarPreview() { MamXanhPreviewTheme { HomeBottomActionBar(false,true,2,{},{},{}) } }

@Preview(name="Care action · enabled",showBackground=true)
@Composable
fun CircularActionButtonPreview() { MamXanhPreviewTheme { CircularActionButton("💧","Nước","2",onClick={}) } }

@Preview(name="Water feedback",widthDp=360,showBackground=true)
@Composable
fun WaterSplashFeedbackPreview() { MamXanhPreviewTheme { WaterSplashFeedback(true,{}) } }

@Preview(name="Companion conversation",widthDp=360,heightDp=640,showBackground=true)
@Composable
fun MascotCompanionDialogPreview() { MamXanhPreviewTheme {
    Box(Modifier.fillMaxSize().padding(20.dp), contentAlignment=androidx.compose.ui.Alignment.Center) {
        MascotCompanionDialog({})
    }
} }

@Preview(name="Daily task drawer",widthDp=360,heightDp=640,showBackground=true)
@Composable
fun HomeTasksBottomSheetPreview() { MamXanhPreviewTheme {
    Box(Modifier.fillMaxSize(), contentAlignment=androidx.compose.ui.Alignment.BottomCenter) {
        HomeTasksBottomSheet(2,4,3,{},{},{})
    }
} }

@Preview(name="Pump station · refill",showBackground=true)
@Composable
fun HomePumpStationPreview() { MamXanhPreviewTheme { HomePumpStation(500,{}) } }

@Preview(name="Water controls · ready",showBackground=true)
@Composable
fun HomeWaterCareControlsPreview() { MamXanhPreviewTheme { HomeWaterCareControls(500,true,"Chạm bình · 50 ml",{}) } }

@Preview(name="Water pump art",widthDp=120,heightDp=160,showBackground=true)
@Composable
fun WaterPumpIllustrationPreview() { WaterPumpIllustration(Modifier.fillMaxSize()) }

@Preview(name="Watering can art",widthDp=120,heightDp=120,showBackground=true)
@Composable
fun WateringCanIllustrationPreview() { WateringCanIllustration(Modifier.fillMaxSize()) }

@Preview(name="Companion card",widthDp=320,showBackground=true)
@Composable
fun MascotCompanionContentPreview() { MamXanhPreviewTheme { MascotCompanionContent({}) } }

@Preview(name="Task drawer content",widthDp=360,showBackground=true)
@Composable
fun HomeTasksSheetContentPreview() { MamXanhPreviewTheme { HomeTasksSheetContent(2,4,3,{},{},{}) } }
