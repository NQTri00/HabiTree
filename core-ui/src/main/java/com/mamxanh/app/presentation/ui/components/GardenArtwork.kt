package com.mamxanh.app.presentation.ui.components

import com.mamxanh.app.presentation.ui.design.AppColors
import com.mamxanh.app.presentation.ui.design.AppDimensions
import com.mamxanh.app.presentation.ui.design.AppOpacity
import com.mamxanh.app.presentation.ui.design.AppTypography

import com.mamxanh.shared.localization.TextKey

import com.mamxanh.shared.localization.AppText

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import com.mamxanh.app.presentation.ui.Green
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamxanh.app.presentation.ui.Deep
import com.mamxanh.core.ui.R

enum class GardenArt {
    WATERING_CAN, FERTILIZER, PUMP, BOOK, GLASS, BOOTS, SPROUT, CHECKLIST, JOURNAL,
    SHOP, TREE, NATURAL, FLOWERS, LANTERN, RIBBON, COIN, CALENDAR, CATEGORY, PENCIL, SEED, DROP, NUT, CHECKED_TASK, BROOM
}

@Composable
fun GardenArtwork(art: GardenArt, modifier: Modifier = Modifier) {
    val vector=when(art){
        GardenArt.WATERING_CAN,GardenArt.DROP,GardenArt.GLASS->Icons.Outlined.WaterDrop
        GardenArt.FERTILIZER,GardenArt.SEED,GardenArt.NUT->Icons.Outlined.Yard
        GardenArt.BOOK,GardenArt.JOURNAL->Icons.Outlined.MenuBook
        GardenArt.CHECKLIST,GardenArt.CHECKED_TASK->Icons.Outlined.FactCheck
        GardenArt.SHOP->Icons.Outlined.Storefront
        GardenArt.TREE->Icons.Outlined.Park
        GardenArt.COIN->Icons.Outlined.MonetizationOn
        GardenArt.CALENDAR->Icons.Outlined.CalendarToday
        GardenArt.PENCIL->Icons.Outlined.Edit
        GardenArt.CATEGORY->Icons.Outlined.GridView
        GardenArt.LANTERN->Icons.Outlined.LightMode
        GardenArt.RIBBON->Icons.Outlined.CardGiftcard
        GardenArt.FLOWERS->Icons.Outlined.FilterVintage
        GardenArt.BOOTS->Icons.Outlined.DirectionsWalk
        GardenArt.BROOM->Icons.Outlined.CleaningServices
        GardenArt.PUMP->Icons.Outlined.Air
        GardenArt.SPROUT,GardenArt.NATURAL->Icons.Outlined.Spa
    }
    Box(modifier,contentAlignment=Alignment.Center){Icon(vector,null,Modifier.fillMaxSize().padding(AppDimensions.Dp2),tint=Green)}
}

@Composable
fun GardenCareAction(art: GardenArt, label: String, onClick: () -> Unit, modifier: Modifier = Modifier,
    enabled: Boolean = true, completed: Boolean = false) {
    Column(modifier.alpha(if (enabled || completed) AppOpacity.Value1 else AppOpacity.Value_5)
        .clickable(enabled = enabled && !completed, role = Role.Button, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally) {
        GardenArtwork(art, Modifier.fillMaxWidth().height(AppDimensions.Dp58))
        if (completed) {
            val shape = RoundedCornerShape(AppDimensions.Dp14)
            Box(Modifier.fillMaxWidth().heightIn(min = AppDimensions.Dp30)
                .background(Brush.verticalGradient(listOf(AppColors.SwatchFF739747, AppColors.SwatchFF3E6B2F)), shape)
                .border(AppDimensions.Dp1_5, AppColors.SwatchFF8DAB59, shape)
                .padding(horizontal = AppDimensions.Dp6, vertical = AppDimensions.Dp5), contentAlignment = Alignment.Center) {
                GardenRichText("✓ $label", color = AppColors.SwatchFFFFF5DB, fontSize = AppTypography.Sp15, lineHeight = AppTypography.Sp17,
                    fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
        } else GardenBotanicalFrame(Modifier.fillMaxWidth().heightIn(min = AppDimensions.Dp30),
            contentPadding = PaddingValues(horizontal = AppDimensions.Dp6, vertical = AppDimensions.Dp5), cornerSize = AppDimensions.Dp14) {
            GardenRichText(label, color = Deep, fontSize = AppTypography.Sp15, lineHeight = AppTypography.Sp17, fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun GardenWoodenTray(modifier: Modifier = Modifier, decoration: Boolean = false, content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(if (decoration) AppDimensions.Dp24 else AppDimensions.Dp28)
    Box(modifier.background(Brush.verticalGradient(listOf(AppColors.SwatchFFC78F50,AppColors.SwatchFFB87A3F,AppColors.SwatchFFD6A365)),shape)
        .border(AppDimensions.Dp3,AppColors.SwatchFFDDB17A,shape)) {
        Column(Modifier.fillMaxWidth().padding(horizontal = AppDimensions.Dp12, vertical = AppDimensions.Dp10), content = content)
    }
}

@Preview(name = "Simple care shelf", widthDp = 340)
@Composable
fun GardenCareActionPreview() { GardenWoodenTray { Row(horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp6)) {
    GardenCareAction(GardenArt.WATERING_CAN, AppText.text(TextKey.CopyTuoi617613), {}, Modifier.weight(1f))
    GardenCareAction(GardenArt.FERTILIZER, AppText.text(TextKey.CopyBon4fd741), {}, Modifier.weight(1f))
    GardenCareAction(GardenArt.PUMP, AppText.text(TextKey.CopyBomCfb1a6), {}, Modifier.weight(1f))
} } }

@Preview(name = "Simple navigation icons", widthDp = 360, heightDp = 80)
@Composable
fun GardenArtworkPreview() { Row { listOf(GardenArt.SPROUT,GardenArt.CHECKLIST,GardenArt.TREE,GardenArt.SHOP,GardenArt.JOURNAL).forEach {
    GardenArtwork(it,Modifier.weight(1f).height(AppDimensions.Dp70))
} } }
