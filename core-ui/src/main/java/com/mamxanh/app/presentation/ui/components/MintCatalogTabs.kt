package com.mamxanh.app.presentation.ui.components

import com.mamxanh.app.presentation.ui.design.AppDimensions
import com.mamxanh.app.presentation.ui.design.AppTypography

import com.mamxanh.shared.localization.TextKey

import com.mamxanh.shared.localization.AppText

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
    Row(modifier.fillMaxWidth().height(IntrinsicSize.Min).background(com.mamxanh.app.presentation.ui.Pale,RoundedCornerShape(AppDimensions.Dp15)).padding(AppDimensions.Dp4).selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp8)) {
        listOf(1 to AppText.text(TextKey.CopyHatGiongF02257), 0 to AppText.text(TextKey.CopyChamSoc7b7df6))
            .forEach { (index, label) ->
                Box(Modifier.weight(1f).heightIn(min = AppDimensions.Dp48)
                    .selectable(selectedIndex == index, role = Role.Tab, onClick = { onSelect(index) }),
                    contentAlignment = Alignment.Center) {
                    Row(Modifier.fillMaxWidth()
                        .mintDepth(RoundedCornerShape(AppDimensions.Dp12), if(selectedIndex == index) MintDepth.card else MintDepth.hint, AppDimensions.Dp3)
                        .padding(horizontal = AppDimensions.Dp8, vertical = AppDimensions.Dp6),
                        horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp6, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically) {

                        Text(label, color = if(selectedIndex == index) com.mamxanh.app.presentation.ui.Green else com.mamxanh.app.presentation.ui.Muted,
                            fontWeight = FontWeight.Bold, fontSize = AppTypography.Sp13, lineHeight = AppTypography.Sp18)
                    }
                }
            }
    }
}
