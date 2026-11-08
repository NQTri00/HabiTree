package com.mamxanh.app.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.presentation.ui.MamXanhPreviewTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamxanh.app.presentation.ui.Deep
import com.mamxanh.app.presentation.ui.Green
import com.mamxanh.app.presentation.ui.Pale

@Composable
fun GardenPageHeader(
    emoji: String,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Pale),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Green.copy(alpha = 0.14f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.88f),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.size(54.dp)
            ) {
                androidx.compose.foundation.layout.Box(contentAlignment = Alignment.Center) {
                    Text(emoji, fontSize = 27.sp)
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(title, color = Deep, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                Text(subtitle, color = Green, fontSize = 13.sp)
            }
            Spacer(Modifier.weight(1f))
        }
    }
}

@Preview(name = "Garden page header", showBackground = true)
@Composable
fun GardenPageHeaderPreview() {
    MamXanhPreviewTheme {
        GardenPageHeader("🌱", "Khu vườn của bạn", "Mầm xanh đang lớn lên từng ngày.")
    }
}
