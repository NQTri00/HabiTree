package com.mamxanh.app.presentation.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun MamXanhPreviewTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Green,
            secondary = Color(0xFFFFA01A),
            background = Cream,
            surface = Color.White
        ),
        content = content
    )
}

@Preview(name = "Mam Xanh preview theme", showBackground = true)
@Composable
fun MamXanhPreviewThemePreview() {
    MamXanhPreviewTheme {
        Surface(color = Cream) {
            androidx.compose.material3.Text(
                "Mầm xanh mỗi ngày",
                color = Green,
                modifier = androidx.compose.ui.Modifier.padding(24.dp)
            )
        }
    }
}
