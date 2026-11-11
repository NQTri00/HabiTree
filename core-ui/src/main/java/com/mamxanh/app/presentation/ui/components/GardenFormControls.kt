package com.mamxanh.app.presentation.ui.components

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
import com.mamxanh.app.presentation.ui.Deep
import com.mamxanh.app.presentation.ui.Green
import com.mamxanh.core.ui.R

@Composable
fun GardenTextField(
    value: String, onValueChange: (String) -> Unit, description: String,
    modifier: Modifier = Modifier, placeholder: String = "", trailingText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    errorMessage: String? = null
) {
    Box(modifier.heightIn(min = 44.dp), contentAlignment = Alignment.Center) {
        Box(Modifier.matchParentSize().background(
            MintDepth.diagonal(if (errorMessage == null) listOf(Color(0xFFE5F2EA), Color.White)
                else listOf(Color(0xFFFFE1DB), Color(0xFFFFF5F2))), RoundedCornerShape(14.dp))
            .border(1.5.dp, if (errorMessage == null) Color(0xFFBED6CB) else Color(0xFFB73D32), RoundedCornerShape(14.dp)))
        BasicTextField(value, onValueChange,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 11.dp).semantics {
                contentDescription = description
                errorMessage?.let { error(it) }
            },
            singleLine = true, keyboardOptions = keyboardOptions,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Deep, fontSize = 16.sp, lineHeight = 20.sp, fontWeight = FontWeight.Bold),
            cursorBrush = SolidColor(Green),
            decorationBox = { inner ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.weight(1f)) {
                        if (value.isEmpty()) GardenRichText(placeholder, color = Deep.copy(alpha = .55f), fontSize = 15.sp)
                        inner()
                    }
                    if (trailingText != null) GardenRichText(trailingText, color = Deep, fontSize = 15.sp)
                }
            })
    }
}

@Composable
fun GardenFieldButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier.heightIn(min = 44.dp).clickable(role = Role.Button, onClick = onClick), contentAlignment = Alignment.Center) {
        Box(Modifier.matchParentSize().mintDepth(RoundedCornerShape(14.dp), MintDepth.secondary, 2.dp))
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            GardenRichText(text, Modifier.weight(1f), color = Deep, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            GardenRichText("▾", color = Deep, fontSize = 16.sp)
        }
    }
}

@Composable
fun GardenChoiceChip(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier.heightIn(min = 44.dp).selectable(selected, role = Role.RadioButton, onClick = onClick), contentAlignment = Alignment.Center) {
        Box(Modifier.matchParentSize().mintDepth(RoundedCornerShape(50),
            if (selected) MintDepth.primary else MintDepth.secondary, if (selected) 4.dp else 1.dp))
        GardenRichText(text, color = if (selected) Color(0xFFFFFFFF) else Deep, fontSize = 13.sp, lineHeight = 16.sp,
            fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp))
    }
}

@Preview(name = "Reference live text field", widthDp = 240)
@Composable
fun GardenTextFieldPreview() { GardenTextField("Đọc 10 trang sách", {}, "Tên việc") }

@Preview(name = "Reference dropdown field", widthDp = 200)
@Composable
fun GardenFieldButtonPreview() { GardenFieldButton("Học tập", {}) }

@Preview(name = "Reference selected choice", widthDp = 120)
@Composable
fun GardenChoiceChipPreview() { GardenChoiceChip("Hằng ngày", true, {}) }
