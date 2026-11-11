package com.mamxanh.app.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamxanh.app.presentation.ui.Deep

/** Brief feedback that never opens a modal or blocks the rest of the screen. */
@Composable
fun MintFeedback(message: String, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    val dismiss by rememberUpdatedState(onDismiss)
    LaunchedEffect(message) {
        kotlinx.coroutines.delay(3500)
        dismiss()
    }
    Box(modifier.fillMaxSize().padding(10.dp), contentAlignment = Alignment.BottomCenter) {
        Surface(Modifier.widthIn(max = 440.dp).fillMaxWidth().semantics { liveRegion = LiveRegionMode.Polite },
            shape = RoundedCornerShape(18.dp), color = Deep, contentColor = Color.White, shadowElevation = 6.dp) {
            Row(Modifier.padding(start = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                val copy = message.substringAfter("\n", message).let {
                    if(message.startsWith("Đã")) it.substringBefore(" · ") else it
                }
                GardenRichText(copy, Modifier.weight(1f).padding(vertical = 8.dp),
                    color = Color.White, fontSize = 12.sp, lineHeight = 16.sp,
                    maxLines = 3, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                TextButton(onClick = onDismiss, contentPadding = PaddingValues(8.dp)) {
                    Text("×", color = Color.White, fontSize = 20.sp)
                }
            }
        }
    }
}
