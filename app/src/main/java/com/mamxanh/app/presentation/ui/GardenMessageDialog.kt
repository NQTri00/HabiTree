package com.mamxanh.app.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mamxanh.app.presentation.ui.components.*

@Composable
fun GardenMessageDialog(message: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)) {
        Box(Modifier.fillMaxSize().safeDrawingPadding().padding(16.dp), contentAlignment = Alignment.Center) {
            GardenMessageCard(message, onDismiss, Modifier.widthIn(max = 420.dp).fillMaxWidth().heightIn(max = 560.dp))
        }
    }
}

@Composable
private fun GardenMessageCard(message: String, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    val success = message.startsWith("Đã") || message.startsWith("+") || message.contains("nhận được") || message.contains("nhận thêm")
    GardenBotanicalFrame(modifier, contentPadding = PaddingValues(24.dp), skin = GardenPanelSkin.DIALOG) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Column(Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(Modifier.size(76.dp).background(Brush.verticalGradient(
                    if(success) listOf(Color(0xFFA0DA36), Color(0xFF43A62B)) else listOf(Color(0xFFFF9B75), Color(0xFFDE3932))), CircleShape),
                    contentAlignment = Alignment.Center) {
                    Text(if(success) "✓" else "!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 48.sp)
                }
                Text(if(message.startsWith("Đã thêm")) "Đã thêm nhiệm vụ" else if(success) "Hoàn tất!" else "Thông báo",
                    color = Deep, fontWeight = FontWeight.Bold, fontSize = 24.sp, textAlign = TextAlign.Center)
                GardenRichText(message, color = Deep, textAlign = TextAlign.Center)
            }
            GardenPaintedButton(if(message.startsWith("Đã thêm")) "Xem nhiệm vụ" else "OK", onDismiss, Modifier.fillMaxWidth())
        }
    }
}

@Preview(widthDp = 360, heightDp = 440, device = "spec:width=360dp,height=440dp,dpi=160")
@Composable
fun GardenMessageDialogPreview() {
    MamXanhPreviewTheme { GardenMessageCard("Đã thêm nhiệm vụ. Nhiệm vụ được lưu vào danh sách của bạn.", {}, Modifier.padding(16.dp)) }
}
