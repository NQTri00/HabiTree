package com.mamxanh.app.core.components.components

import com.mamxanh.app.core.components.design.AppColors
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppOpacity
import com.mamxanh.app.core.components.design.AppTypography

import com.mamxanh.shared.localization.TextKey

import com.mamxanh.shared.localization.AppText

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mamxanh.app.core.components.Deep
import com.mamxanh.app.core.components.Green
import com.mamxanh.core.ui.R

@Composable
fun GardenTextField(
    value: String, onValueChange: (String) -> Unit, description: String,
    modifier: Modifier = Modifier, placeholder: String = "", trailingText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    errorMessage: String? = null
) {
    Box(modifier.heightIn(min = AppDimensions.Dp48), contentAlignment = Alignment.Center) {
        Box(Modifier.matchParentSize().background(
            MintDepth.diagonal(if (errorMessage == null) listOf(AppColors.White, AppColors.White)
                else listOf(AppColors.SwatchFFFFE1DB, AppColors.SwatchFFFFF5F2)), RoundedCornerShape(AppDimensions.Dp14))
            .border(AppDimensions.Dp1_5, if (errorMessage == null) AppColors.SwatchFFE4E7DC else AppColors.SwatchFFB73D32, RoundedCornerShape(AppDimensions.Dp14)))
        BasicTextField(value, onValueChange,
            modifier = Modifier.fillMaxWidth().padding(horizontal = AppDimensions.Dp14, vertical = AppDimensions.Dp12).semantics {
                contentDescription = description
                errorMessage?.let { error(it) }
            },
            singleLine = true, keyboardOptions = keyboardOptions,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Deep, fontSize = AppTypography.Sp14, lineHeight = AppTypography.Sp20, fontWeight = FontWeight.Normal),
            cursorBrush = SolidColor(Green),
            decorationBox = { inner ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.weight(1f)) {
                        if (value.isEmpty()) GardenRichText(placeholder, color = Deep.copy(alpha = AppOpacity.Value_55), fontSize = AppTypography.Sp12)
                        inner()
                    }
                    if (trailingText != null) GardenRichText(trailingText, color = Deep, fontSize = AppTypography.Sp12)
                }
            })
    }
}

@Composable
fun GardenFieldButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier.heightIn(min = AppDimensions.Dp48).clickable(role = Role.Button, onClick = onClick), contentAlignment = Alignment.Center) {
        Box(Modifier.matchParentSize().mintDepth(RoundedCornerShape(AppDimensions.Dp14), MintDepth.secondary, AppDimensions.Dp2))
        Row(Modifier.fillMaxWidth().padding(horizontal = AppDimensions.Dp8, vertical = AppDimensions.Dp6), verticalAlignment = Alignment.CenterVertically) {
            GardenRichText(text, Modifier.weight(1f), color = Deep, fontSize = AppTypography.Sp12, fontWeight = FontWeight.Bold)
            GardenRichText("▾", color = Deep, fontSize = AppTypography.Sp16)
        }
    }
}

@Composable
fun GardenChoiceChip(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier.heightIn(min = AppDimensions.Dp48).selectable(selected, role = Role.RadioButton, onClick = onClick), contentAlignment = Alignment.Center) {
        Box(Modifier.matchParentSize().mintDepth(RoundedCornerShape(50),
            if (selected) MintDepth.primary else MintDepth.secondary, if (selected) AppDimensions.Dp4 else AppDimensions.Dp1))
        GardenRichText(text, color = if (selected) AppColors.SwatchFFFFFFFF else Deep, fontSize = AppTypography.Sp11, lineHeight = AppTypography.Sp14,
            fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = AppDimensions.Dp8, vertical = AppDimensions.Dp6))
    }
}

@Preview(name = "Reference live text field", widthDp = 240)
@Composable
fun GardenTextFieldPreview() { GardenTextField(AppText.text(TextKey.CopyDoc10TrangSachA3c873), {}, AppText.text(TextKey.CopyTenViec672c8c)) }

@Preview(name = "Reference dropdown field", widthDp = 200)
@Composable
fun GardenFieldButtonPreview() { GardenFieldButton(AppText.canonical(TextKey.CopyHocTapA51516), {}) }

@Preview(name = "Reference selected choice", widthDp = 120)
@Composable
fun GardenChoiceChipPreview() { GardenChoiceChip(AppText.text(TextKey.CopyHangNgay545b54), true, {}) }
