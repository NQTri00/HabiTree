package com.mamxanh.app.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamxanh.app.presentation.ui.*

/** Half the usable app window, including the space occupied by app navigation. */
@Composable
fun mintTreeHeroHeight(): Dp {
    val density = LocalDensity.current
    val windowHeight = with(density) { LocalWindowInfo.current.containerSize.height.toDp() }
    val bars = WindowInsets.systemBars.asPaddingValues()
    return ((windowHeight - bars.calculateTopPadding() - bars.calculateBottomPadding()) * .5f)
        .coerceAtLeast(240.dp)
}

@Composable
fun MintCompactHeader(coins: Int) {
    Row(Modifier.fillMaxWidth().heightIn(min = 48.dp).padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        GardenArtwork(GardenArt.SPROUT, Modifier.size(24.dp))
        Text("Mầm Xanh", Modifier.weight(1f), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Deep)
        GardenBotanicalFrame(contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp), cornerSize = 24.dp) {
            GardenRichText("🪙 $coins xu", color = Deep, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun MintCareIcon(
    art: GardenArt, label: String, enabled: Boolean, onClick: () -> Unit,
    checked: Boolean = false, caption: String? = null
) {
    GardenBotanicalFrame(Modifier.fillMaxWidth().heightIn(min = 48.dp)
        .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
        .semantics(mergeDescendants = true) { contentDescription = label },
        contentPadding = PaddingValues(4.dp), cornerSize = 18.dp,
        skin = if (checked) GardenPanelSkin.HINT else GardenPanelSkin.BADGE) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(32.dp), contentAlignment = Alignment.Center) {
                GardenArtwork(art, Modifier.size(28.dp).alpha(if (enabled || checked) 1f else .4f))
                if (checked) Text("✓", Modifier.align(Alignment.BottomEnd),
                    color = Green, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            if (caption != null) Text(caption, color = Deep, fontSize = 10.sp,
                lineHeight = 12.sp, textAlign = TextAlign.Center)
        }
    }
}

