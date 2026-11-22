package com.mamxanh.app.core.components.components

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.core.components.Deep
import com.mamxanh.app.core.components.Green
import com.mamxanh.app.core.components.MamXanhPreviewTheme
import com.mamxanh.app.core.components.Pale
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppOpacity
import com.mamxanh.app.core.components.design.AppTypography
import com.mamxanh.shared.localization.AppText
import com.mamxanh.shared.localization.TextKey

@Composable
fun GardenPageHeader(
    emoji: String,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Pale),
        shape = RoundedCornerShape(AppDimensions.Dp24),
        border = BorderStroke(AppDimensions.Dp1, Green.copy(alpha = AppOpacity.VALUE_0_14)),
        elevation = CardDefaults.cardElevation(defaultElevation = AppDimensions.Dp1),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(AppDimensions.Dp16),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp14),
        ) {
            Surface(
                color =
                    androidx.compose.ui.graphics.Color.White
                        .copy(alpha = AppOpacity.VALUE_0_88),
                shape = RoundedCornerShape(AppDimensions.Dp18),
                modifier = Modifier.size(AppDimensions.Dp54),
            ) {
                androidx.compose.foundation.layout.Box(contentAlignment = Alignment.Center) {
                    Text(emoji, fontSize = AppTypography.Sp27)
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp3)) {
                Text(title, color = Deep, fontSize = AppTypography.Sp20, fontWeight = FontWeight.ExtraBold)
                Text(subtitle, color = Green, fontSize = AppTypography.Sp13)
            }
            Spacer(Modifier.weight(1f))
        }
    }
}

@Preview(name = "Garden page header", showBackground = true)
@Composable
fun GardenPageHeaderPreview() {
    MamXanhPreviewTheme {
        GardenPageHeader("🌱", AppText.text(TextKey.CopyKhuVuonCuaBanC08654), AppText.text(TextKey.CopyMamXanhDangLonLenTungNgay6feecd))
    }
}
