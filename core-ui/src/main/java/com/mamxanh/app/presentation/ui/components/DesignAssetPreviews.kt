package com.mamxanh.app.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamxanh.core.ui.R

class DesignAssetPages : PreviewParameterProvider<Int> { override val values = (0..3).asSequence() }

@Preview(name = "Source-derived artwork audit", widthDp = 360, heightDp = 680)
@Composable
fun DesignAssetPreview(@PreviewParameter(DesignAssetPages::class) page: Int) {
    val assets = listOf(
        "cherry" to R.drawable.design_cherry,
        "pine" to R.drawable.design_pine,
        "succulent" to R.drawable.design_succulent,
        "mandarin" to R.drawable.design_mandarin,
        "panel" to R.drawable.design_panel,
        "journal" to R.drawable.design_journal,
        "nav_tile" to R.drawable.design_nav_tile,
        "nav_selected" to R.drawable.design_nav_selected,
        "nav_sprout" to R.drawable.design_nav_sprout,
        "nav_tasks" to R.drawable.design_nav_tasks,
        "nav_tree" to R.drawable.design_nav_tree,
        "nav_shop" to R.drawable.design_nav_shop,
        "nav_journal" to R.drawable.design_nav_journal,
        "water_can" to R.drawable.design_water_can,
        "fertilizer" to R.drawable.design_fertilizer,
        "pump" to R.drawable.design_pump,
        "primary" to R.drawable.design_primary,
        "secondary" to R.drawable.design_secondary,
        "wood_title" to R.drawable.design_wood_title,
        "name_plate" to R.drawable.design_name_plate,
        "care_tray" to R.drawable.design_care_tray,
        "basket" to R.drawable.design_basket,
        "book" to R.drawable.design_book,
        "glass" to R.drawable.design_glass,
        "boots" to R.drawable.design_boots,
        "lantern" to R.drawable.design_lantern,
        "ribbon" to R.drawable.design_ribbon,
        "sprouted_seed" to R.drawable.design_sprouted_seed,
        "home_scene" to R.drawable.design_home_scene,
        "garden_scene" to R.drawable.design_garden_scene,
        "tree_scene" to R.drawable.design_tree_scene,
        "shop_scene" to R.drawable.design_shop_scene,
        "shop_shelf" to R.drawable.design_shop_shelf,
        "task_panel" to R.drawable.design_task_panel,
        "plant_panel" to R.drawable.design_plant_panel,
        "progress_plate" to R.drawable.design_progress_plate,
        "price_badge" to R.drawable.design_price_badge,
        "check_empty" to R.drawable.design_check_empty,
        "check_done" to R.drawable.design_check_done,
        "vine_stem" to R.drawable.design_vine_stem,
        "progress_sprout" to R.drawable.design_progress_sprout,
        "progress_outline" to R.drawable.design_progress_outline,
        "reward" to R.drawable.design_reward,
        "category_icon" to R.drawable.design_category_icon,
        "calendar_icon" to R.drawable.design_calendar_icon,
        "coin_icon" to R.drawable.design_coin_icon,
        "pencil_icon" to R.drawable.design_pencil_icon,
        "task_house" to R.drawable.design_task_house,
        "seed_stall" to R.drawable.design_seed_stall,
        "input" to R.drawable.design_input,
        "counter_badge" to R.drawable.design_counter_badge,
        "decor_tray" to R.drawable.design_decor_tray,
        "hint" to R.drawable.design_hint,
        "seed_packet" to R.drawable.design_seed_packet,
        "empty_basket" to R.drawable.design_empty_basket
    )
    Column(Modifier.fillMaxSize().background(Color(0xFFE9E4DA))) {
        assets.drop(page * 15).take(15).chunked(3).forEach { row ->
            Row(Modifier.fillMaxWidth()) {
                row.forEach { (name, resource) ->
                    Column(Modifier.weight(1f).padding(3.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(painterResource(resource), null, Modifier.fillMaxWidth().height(108.dp), contentScale = ContentScale.Fit)
                        Text(name, fontSize = 9.sp, lineHeight = 11.sp, maxLines = 2)
                    }
                }
                repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}
