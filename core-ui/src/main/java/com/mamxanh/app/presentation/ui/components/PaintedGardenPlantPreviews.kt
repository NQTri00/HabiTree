package com.mamxanh.app.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview(name = "All painted nursery species", widthDp = 360, heightDp = 440)
@Composable
fun PaintedGardenPlantPreview() {
    Column {
        listOf("cherry", "pine", "succulent", "monstera", "ficus", "snake", "cactus", "aloe", "lavender", "pilea").chunked(3).forEach { row ->
            Row(Modifier.fillMaxWidth()) {
                row.forEach { species ->
                    Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        PaintedGardenPlant(species, Modifier.fillMaxWidth().height(90.dp))
                        Text(species, fontSize = 11.sp)
                    }
                }
                repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}
