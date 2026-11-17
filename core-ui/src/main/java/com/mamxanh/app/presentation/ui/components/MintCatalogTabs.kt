package com.mamxanh.app.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamxanh.app.presentation.ui.Deep
import com.mamxanh.app.presentation.ui.Green

/** Shared category tabs for the shop catalog and owned inventory. */
@Composable
fun MintCatalogTabs(selectedIndex: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth().height(IntrinsicSize.Min).background(com.mamxanh.app.presentation.ui.Pale,RoundedCornerShape(15.dp)).padding(4.dp).selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(1 to "Hạt giống", 0 to "Chăm sóc")
            .forEach { (index, label) ->
                Box(Modifier.weight(1f).heightIn(min = 48.dp)
                    .selectable(selectedIndex == index, role = Role.Tab, onClick = { onSelect(index) }),
                    contentAlignment = Alignment.Center) {
                    Row(Modifier.fillMaxWidth()
                        .mintDepth(RoundedCornerShape(12.dp), if(selectedIndex == index) MintDepth.card else MintDepth.hint, 3.dp)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically) {

                        Text(label, color = if(selectedIndex == index) com.mamxanh.app.presentation.ui.Green else com.mamxanh.app.presentation.ui.Muted,
                            fontWeight = FontWeight.Bold, fontSize = 13.sp, lineHeight = 18.sp)
                    }
                }
            }
    }
}
