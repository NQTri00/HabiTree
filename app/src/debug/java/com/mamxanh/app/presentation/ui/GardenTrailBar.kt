package com.mamxanh.app.presentation.ui
import com.mamxanh.app.core.components.Cream
import com.mamxanh.app.core.components.Green
import com.mamxanh.app.core.components.MamXanhPreviewTheme
import com.mamxanh.app.core.components.Muted

import com.mamxanh.app.core.components.design.AppColors
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppTypography

import com.mamxanh.shared.localization.TextKey

import com.mamxanh.shared.localization.AppText

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Park
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamxanh.app.core.components.components.MintDepth
import com.mamxanh.app.core.components.components.mintDepth

@Composable
fun GardenTrailBar(selected: AppDestination, onNavigate: (AppDestination) -> Unit, modifier: Modifier = Modifier) {
    val destinations=listOf(AppDestination.TASKS,AppDestination.GARDEN,AppDestination.HOME,AppDestination.SHOP,AppDestination.STATS)
    Column(modifier.fillMaxWidth().background(Cream)) {
        androidx.compose.material3.HorizontalDivider(color=AppColors.SwatchFFE4E7DC)
        Row(Modifier.fillMaxWidth().navigationBarsPadding().padding(vertical=AppDimensions.Dp8).selectableGroup()) {
            destinations.forEach{destination->
                val active=destination==selected
                val icon=when(destination){AppDestination.HOME->Icons.Outlined.Home;AppDestination.TASKS->Icons.Outlined.FactCheck;AppDestination.GARDEN->Icons.Outlined.Park;AppDestination.SHOP->Icons.Outlined.Storefront;AppDestination.STATS->Icons.Outlined.MenuBook}
                val label=when(destination){AppDestination.HOME->AppText.text(TextKey.CopyVuon9e4c10);AppDestination.TASKS->AppText.text(TextKey.CopyViec818768);AppDestination.GARDEN->AppText.text(TextKey.CopyCayC854d7);AppDestination.SHOP->AppText.text(TextKey.CopyTiemF1ee00);AppDestination.STATS->AppText.text(TextKey.CopyNhatKy1e6c3c)}
                Column(Modifier.weight(1f).heightIn(min=AppDimensions.Dp56).selectable(active,role=Role.Tab,onClick={onNavigate(destination)}),
                    horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp4)) {
                    Icon(icon,null,Modifier.size(AppDimensions.Dp22),tint=if(active)Green else Muted)
                    Text(label,color=if(active)Green else Muted,fontSize=AppTypography.Sp10,lineHeight=AppTypography.Sp14,fontWeight=if(active)FontWeight.SemiBold else FontWeight.Normal)
                    Box(Modifier.size(AppDimensions.Dp4).background(if(active)Green else AppColors.Transparent,CircleShape))
                }
            }
        }
    }
}

@Preview(widthDp = 360, device = "spec:width=360dp,height=110dp,dpi=160")
@Composable
fun GardenTrailBarPreview() { MamXanhPreviewTheme { GardenTrailBar(AppDestination.HOME, {}) } }
@Preview(widthDp = 320, fontScale = 1.3f, device = "spec:width=320dp,height=130dp,dpi=160")
@Composable
fun GardenTrailBarLargeTextPreview() { MamXanhPreviewTheme { GardenTrailBar(AppDestination.STATS, {}) } }
