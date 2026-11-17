package com.mamxanh.app.presentation.ui

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
import com.mamxanh.app.presentation.ui.components.MintDepth
import com.mamxanh.app.presentation.ui.components.mintDepth

@Composable
fun GardenTrailBar(selected: AppDestination, onNavigate: (AppDestination) -> Unit, modifier: Modifier = Modifier) {
    val destinations=listOf(AppDestination.TASKS,AppDestination.GARDEN,AppDestination.HOME,AppDestination.SHOP,AppDestination.STATS)
    Column(modifier.fillMaxWidth().background(Cream)) {
        androidx.compose.material3.HorizontalDivider(color=Color(0xFFE4E7DC))
        Row(Modifier.fillMaxWidth().navigationBarsPadding().padding(vertical=8.dp).selectableGroup()) {
            destinations.forEach{destination->
                val active=destination==selected
                val icon=when(destination){AppDestination.HOME->Icons.Outlined.Home;AppDestination.TASKS->Icons.Outlined.FactCheck;AppDestination.GARDEN->Icons.Outlined.Park;AppDestination.SHOP->Icons.Outlined.Storefront;AppDestination.STATS->Icons.Outlined.MenuBook}
                val label=when(destination){AppDestination.HOME->"Vườn";AppDestination.TASKS->"Việc";AppDestination.GARDEN->"Cây";AppDestination.SHOP->"Tiệm";AppDestination.STATS->"Nhật ký"}
                Column(Modifier.weight(1f).heightIn(min=56.dp).selectable(active,role=Role.Tab,onClick={onNavigate(destination)}),
                    horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(4.dp)) {
                    Icon(icon,null,Modifier.size(22.dp),tint=if(active)Green else Muted)
                    Text(label,color=if(active)Green else Muted,fontSize=10.sp,lineHeight=14.sp,fontWeight=if(active)FontWeight.SemiBold else FontWeight.Normal)
                    Box(Modifier.size(4.dp).background(if(active)Green else Color.Transparent,CircleShape))
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
