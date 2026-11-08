package com.mamxanh.app.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun GardenMessageDialog(message: String, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        GardenMessageCard(message, onDismiss, Modifier.fillMaxWidth(0.9f).widthIn(max = 420.dp))
    }
}

@Composable
private fun GardenMessageCard(message: String, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = Cream,
        shape = RoundedCornerShape(30.dp),
        shadowElevation = 18.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                Modifier.fillMaxWidth().height(150.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(color = Green, shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp), modifier = Modifier.fillMaxSize()) {}
                Surface(
                    color = Color.White.copy(alpha = 0.08f),
                    shape = CircleShape,
                    modifier = Modifier.align(Alignment.TopEnd).offset(x = 22.dp, y = (-48).dp).size(150.dp)
                ) {}
                Surface(
                    color = Color.White.copy(alpha = 0.13f),
                    shape = RoundedCornerShape(25.dp),
                    modifier = Modifier.size(92.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) { Text("🌼", style = MaterialTheme.typography.displaySmall) }
                }
                Surface(
                    color = Gold,
                    shape = CircleShape,
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color.White),
                    modifier = Modifier.align(Alignment.BottomEnd).padding(end = 24.dp).offset(y = 18.dp).size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) { Text("✦", color = Deep, fontWeight = FontWeight.Black) }
                }
            }
            Text(
                "Tin nhắn từ khu vườn",
                color = Deep,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 22.dp, end = 22.dp, top = 26.dp)
            )
            Text(
                message,
                color = Muted,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp)
            )
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Green),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp).heightIn(min = 52.dp)
            ) {
                Text("Đã hiểu  🌱", fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(22.dp))
        }
    }
}

@Preview(name = "Garden notification · large text", showBackground = true, widthDp = 360, fontScale = 1.3f)
@Preview(name = "Garden notification", showBackground = true, widthDp = 360)
@Composable
fun GardenMessageDialogPreview() {
    MamXanhPreviewTheme {
        GardenMessageCard(
            "Bạn nhận được 5 xu! Cây đào hồng đang vươn thêm một chiếc lá mới.",
            {},
            Modifier.fillMaxWidth().padding(16.dp)
        )
    }
}
