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
    val shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    val green = Color(0xFF08753D)
    val destinations = listOf(AppDestination.TASKS, AppDestination.GARDEN,
        AppDestination.HOME, AppDestination.SHOP, AppDestination.STATS)
    Box(modifier.fillMaxWidth()) {
        Box(Modifier.matchParentSize().padding(top = 10.dp)
            .mintDepth(shape, MintDepth.bar, 12.dp))
        Row(Modifier.fillMaxWidth().navigationBarsPadding()
            .padding(start = 4.dp, end = 4.dp, bottom = 4.dp).selectableGroup()) {
            destinations.forEach { destination ->
                val active = destination == selected
                val home = destination == AppDestination.HOME
                val icon = when (destination) {
                    AppDestination.HOME -> if (active) Icons.Filled.Home else Icons.Outlined.Home
                    AppDestination.TASKS -> if (active) Icons.Filled.FactCheck else Icons.Outlined.FactCheck
                    AppDestination.GARDEN -> if (active) Icons.Filled.Park else Icons.Outlined.Park
                    AppDestination.SHOP -> if (active) Icons.Filled.Storefront else Icons.Outlined.Storefront
                    AppDestination.STATS -> if (active) Icons.Filled.MenuBook else Icons.Outlined.MenuBook
                }
                val label = when (destination) {
                    AppDestination.HOME -> "Vườn"; AppDestination.TASKS -> "Việc"
                    AppDestination.GARDEN -> "Cây"; AppDestination.SHOP -> "Tiệm"; AppDestination.STATS -> "Nhật ký"
                }
                Column(Modifier.weight(1f).heightIn(min = 48.dp)
                    .selectable(active, role = Role.Tab, onClick = { onNavigate(destination) })
                    .semantics { contentDescription = destination.label },
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(Modifier.fillMaxWidth().height(38.dp), contentAlignment = Alignment.Center) {
                        if (home) Surface(
                            modifier = Modifier.align(Alignment.Center).size(38.dp)
                                .mintDepth(CircleShape, if (active) MintDepth.primary else MintDepth.secondary, 10.dp),
                            shape = CircleShape,
                            color = Color.Transparent,
                            border = androidx.compose.foundation.BorderStroke(3.dp, Color(0xFFF9FDFC)),
                            shadowElevation = 0.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(icon, null, Modifier.size(22.dp), tint = if (active) Color.White else green)
                            }
                        } else Icon(icon, null, Modifier.align(Alignment.BottomCenter).padding(bottom = 5.dp).size(18.dp),
                            tint = if (active) green else Color(0xFF475569))
                    }
                    Text(label, Modifier.fillMaxWidth().padding(top = 2.dp),
                        color = if (active) green else Color(0xFF334155),
                        fontSize = 10.sp, lineHeight = 12.sp, textAlign = TextAlign.Center,
                        fontWeight = if (active || home) FontWeight.Bold else FontWeight.Normal)
                    Box(Modifier.padding(top = 2.dp).width(22.dp).height(2.dp)
                        .background(if (active && !home) green else Color.Transparent, RoundedCornerShape(50)))
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
