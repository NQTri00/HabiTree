package com.mamxanh.app.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamxanh.app.presentation.ui.components.*
import com.mamxanh.core.ui.R

@Composable
fun GardenTrailBar(selected: AppDestination, onNavigate: (AppDestination) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(Color(0xFF276452), Color(0xFF164C3F))), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
        .border(1.dp, Color(0xFF638D6B), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
        .navigationBarsPadding().padding(horizontal = 5.dp, vertical = 2.dp).selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(1.dp)) {
        AppDestination.entries.forEach { destination ->
            val active = selected == destination
            Box(Modifier.weight(1f).height(74.dp).selectable(active, role = Role.Tab, onClick = { onNavigate(destination) }),
                contentAlignment = Alignment.Center) {
                if (active) Box(Modifier.align(Alignment.BottomCenter).padding(bottom = 5.dp)
                    .width(48.dp).height(4.dp).background(Color(0xFFFFDE75), RoundedCornerShape(50)))
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(vertical = 6.dp)) {
                    GardenArtwork(when (destination) {
                        AppDestination.HOME -> GardenArt.SPROUT
                        AppDestination.TASKS -> GardenArt.CHECKLIST
                        AppDestination.GARDEN -> GardenArt.TREE
                        AppDestination.SHOP -> GardenArt.SHOP
                        AppDestination.STATS -> GardenArt.JOURNAL
                    }, Modifier.size(30.dp))
                    Text(when (destination) {
                        AppDestination.HOME -> "Vườn"
                        AppDestination.TASKS -> "Việc"
                        AppDestination.GARDEN -> "Cây"
                        AppDestination.SHOP -> "Tiệm"
                        AppDestination.STATS -> "Nhật ký"
                    }, color = Color(0xFFFFEBC0), fontSize = 14.sp, lineHeight = 17.sp, fontWeight = FontWeight.Normal, maxLines = 1)
                }
            }
        }
    }
}

@Preview(name = "Reference navigation", widthDp = 360)
@Composable
fun GardenTrailBarPreview() { MamXanhPreviewTheme { GardenTrailBar(AppDestination.HOME, {}) } }
