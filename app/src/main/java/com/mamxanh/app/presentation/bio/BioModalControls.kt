package com.mamxanh.app.presentation.bio

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.mamxanh.app.core.components.design.*
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppIcons
import com.mamxanh.app.core.components.design.AppOpacity
import com.mamxanh.app.core.components.design.AppRadii
import com.mamxanh.app.core.components.design.AppTypography
import com.mamxanh.core.ui.R

@Composable internal fun BioChoice(
    title: String,
    subtitle: String,
    species: String,
    selected: Boolean,
    onClick: () -> Unit,
    icon: String = "next",
) {
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(
                min = AppDimensions.Dp76,
            ).background(
                if (selected)bp.soft else bp.surface,
                bioShape(AppRadii.VALUE_16),
            ).border(
                AppDimensions.Dp1,
                if (selected)bp.deep else bp.line,
                bioShape(AppRadii.VALUE_16),
            ).clickable(onClick = onClick)
            .padding(AppDimensions.Dp12),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp12),
    ) {
        BioPlant(species, Modifier.size(AppDimensions.Dp50))
        Column(Modifier.weight(1f)) {
            Bt(title, size = AppTypography.SIZE_13, semi = true)
            Spacer(Modifier.height(AppDimensions.Dp3))
            Bt(subtitle, size = AppTypography.SIZE_11, tint = bp.muted)
        }
        Bi(if (selected)"check"else icon, size = AppIcons.SIZE_18)
    }
}

@Composable
internal fun BioPurchaseQuantity(
    quantity: Int,
    busy: Boolean,
    onQuantity: (Int) -> Unit,
) {
    val canDecrease = quantity > 1 && !busy
    val decreaseLabel = stringResource(R.string.shop_decrease_quantity)
    Row(
        Modifier.fillMaxWidth().heightIn(min = AppDimensions.Dp52),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val shape = bioShape(AppRadii.VALUE_12)
        Box(
            Modifier
                .size(AppDimensions.Dp48)
                .alpha(if (canDecrease) AppOpacity.VALUE_1 else AppOpacity.VALUE_45)
                .background(bp.surface, shape)
                .border(AppDimensions.Dp1, bp.line, shape)
                .semantics { contentDescription = decreaseLabel }
                .clickable(enabled = canDecrease, role = Role.Button) { onQuantity(quantity - 1) },
            contentAlignment = Alignment.Center,
        ) {
            Bt("\u2212", size = AppTypography.SIZE_22, tint = bp.deep)
        }
        Spacer(Modifier.width(AppDimensions.Dp25))
        BioField(
            "",
            quantity.toString(),
            { onQuantity(it.toIntOrNull() ?: 1) },
            Modifier.width(AppDimensions.Dp65),
            number = true,
            centered = true,
            accessibilityLabel = stringResource(R.string.shop_purchase_quantity),
        )
        Spacer(Modifier.width(AppDimensions.Dp25))
        BioIconButton(
            "plus",
            { onQuantity(quantity + 1) },
            boxed = true,
            enabled = quantity < 99 && !busy,
            label = stringResource(R.string.shop_increase_quantity),
        )
    }
}

@Preview(name = "Purchase quantity", widthDp = 335, showBackground = true)
@Composable
private fun BioPurchaseQuantityPreview() {
    BioPurchaseQuantity(quantity = 1, busy = false) {}
}
