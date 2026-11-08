package com.mamxanh.app.feature.tree.presentation.components
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mamxanh.app.feature.tree.domain.model.Tree
import com.mamxanh.app.presentation.ui.*
import com.mamxanh.app.presentation.ui.MamXanhPreviewTheme
import com.mamxanh.app.presentation.ui.components.GardenBotanicalFrame

@Composable
fun TreePortrait(
    tree: Tree,
    modifier: Modifier = Modifier,
    sharedTreeModifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth().height(260.dp),
        contentAlignment = Alignment.Center
    ) {
        KawaiiPlantIllustration(
            growth = tree.growth,
            speciesId = tree.speciesId,
            decoration = tree.decoration,
            modifier = sharedTreeModifier.width(290.dp).height(310.dp)
        )
    }
}

@Composable
fun TreeCareActions(
    wateredToday: Boolean,
    fertilizerCount: Int,
    wateringCanMl: Int,
    onWater: () -> Unit,
    onFertilize: () -> Unit
) {
    val canWater = !wateredToday && wateringCanMl >= 50
    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        Text("Chăm sóc hôm nay", color = Deep, style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = onWater, enabled = canWater, modifier = Modifier.weight(1f)) {
                Text(
                    when {
                        wateredToday -> "Đã tưới 💧"
                        wateringCanMl < 50 -> "Cần thêm nước"
                        else -> "Tưới · 50 ml 💧"
                    }
                )
            }
            OutlinedButton(onClick = onFertilize, enabled = fertilizerCount > 0, modifier = Modifier.weight(1f)) {
                Text("Bón phân 🌿")
            }
        }
        Text("Bình tưới: " + wateringCanMl + " / 1000 ml · Phân bón: " + fertilizerCount, color = Muted, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun DecorationPicker(selected: String, onSelect: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        Text("Thêm chút xinh xắn", color = Deep, style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("NONE" to "Tự nhiên", "FLOWERS" to "Hoa 🌼", "LANTERNS" to "Đèn 🏮", "RIBBON" to "Nơ 🎀")
                .forEach { (code, label) ->
                    FilterChip(
                        selected = selected == code,
                        onClick = { onSelect(code) },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Pale,
                            selectedLabelColor = Deep
                        )
                    )
                }
        }
    }
}

@Preview(name = "Tree care actions", showBackground = true)
@Composable
fun TreeCareActionsPreview() {
    MamXanhPreviewTheme {
        GardenBotanicalFrame {
            TreeCareActions(false, 2, 500, {}, {})
        }
    }
}

@Preview(name = "Tree decoration picker", showBackground = true)
@Composable
fun DecorationPickerPreview() {
    MamXanhPreviewTheme {
        GardenBotanicalFrame {
            DecorationPicker("FLOWERS", {})
        }
    }
}
