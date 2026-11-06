package com.mamxanh.app.feature.tree.presentation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mamxanh.app.feature.tree.domain.model.Tree
import com.mamxanh.app.feature.tree.domain.model.treeSpeciesCatalog
import com.mamxanh.app.feature.tree.presentation.components.*
import com.mamxanh.app.presentation.ui.Deep
import com.mamxanh.app.presentation.ui.Green

@Composable
fun TreeInteractionScreen(tree: Tree, fertilizerCount: Int, onWater: () -> Unit,
    onFertilize: () -> Unit, onDecorate: (String) -> Unit, modifier: Modifier = Modifier) {
    val species = treeSpeciesCatalog.firstOrNull { it.id == tree.speciesId } ?: treeSpeciesCatalog.first()
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(tree.name, color = Deep, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
            Text(species.name + " · " + tree.growth.coerceAtMost(120) + " / 120 điểm phát triển", color = Green)
        }
        TreePortrait(tree)
        TreeCareActions(tree.wateredToday, fertilizerCount, onWater, onFertilize)
        DecorationPicker(tree.decoration, onDecorate)
        Text("Cảm ơn bạn đã dành chút thời gian chăm sóc cây hôm nay. 🍃", color = Green)
    }
}
