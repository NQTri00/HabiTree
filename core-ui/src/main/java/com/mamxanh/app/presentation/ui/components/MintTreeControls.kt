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
fun mintTreeHeroHeight(speciesId: String = "", availableWidth: Dp? = null): Dp {
    val density = LocalDensity.current
    val windowHeight = with(density) { LocalWindowInfo.current.containerSize.height.toDp() }
    val bars = WindowInsets.systemBars.asPaddingValues()
    val halfWindow = ((windowHeight - bars.calculateTopPadding() - bars.calculateBottomPadding()) * .5f)
        .coerceAtLeast(240.dp)
    // The temporary succulent sprite is wide. Trim empty scene space, retaining
    // its full visible width and aspect ratio without another bitmap decode.
    return if(speciesId == "succulent" && availableWidth != null)
        minOf(halfWindow, (availableWidth - 92.dp).coerceAtLeast(48.dp) * .62f)
    else halfWindow
}

@Composable
fun MintCompactHeader(coins: Int, title: String = "Mầm Xanh", titleSize: androidx.compose.ui.unit.TextUnit = 14.sp,
    actions: @Composable RowScope.()->Unit = {}) {
    Row(Modifier.fillMaxWidth().mintDepth(androidx.compose.foundation.shape.RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp), MintDepth.bar, 3.dp)
        .heightIn(min = 36.dp).padding(horizontal = 10.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        GardenArtwork(GardenArt.SPROUT, Modifier.size(16.dp))
        Text(title, Modifier.weight(1f), fontSize = titleSize, lineHeight = titleSize * 1.2f, fontWeight = FontWeight.Bold, color = Deep)
        actions()
        GardenBotanicalFrame(contentPadding = PaddingValues(horizontal = 6.dp, vertical = 3.dp), cornerSize = 24.dp) {
            GardenRichText("🪙 $coins xu", color = Deep, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun MintCareIcon(
    art: GardenArt, label: String, enabled: Boolean, onClick: () -> Unit,
    checked: Boolean = false, caption: String? = null
) {
    Column(Modifier.width(48.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(48.dp)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .semantics(mergeDescendants = true) { contentDescription = label },
            contentAlignment = Alignment.Center) {
            androidx.compose.material3.Surface(
                Modifier.size(32.dp).mintDepth(androidx.compose.foundation.shape.CircleShape,
                    if (checked) MintDepth.hint else MintDepth.card, if (enabled || checked) 4.dp else 1.dp),
                shape = androidx.compose.foundation.shape.CircleShape,
                color = androidx.compose.ui.graphics.Color.Transparent,
                border = androidx.compose.foundation.BorderStroke(1.dp, androidx.compose.ui.graphics.Color.White),
                shadowElevation = 0.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    GardenArtwork(art, Modifier.size(20.dp).alpha(if (enabled || checked) 1f else .4f))
                    if (checked) Text("✓", Modifier.align(Alignment.BottomEnd),
                        color = Green, fontSize = 10.sp, lineHeight = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        if (caption != null) Text(caption.replace('\n', ' '), color = Deep, fontSize = 9.sp,
            lineHeight = 11.sp, textAlign = TextAlign.Center)
    }
}

