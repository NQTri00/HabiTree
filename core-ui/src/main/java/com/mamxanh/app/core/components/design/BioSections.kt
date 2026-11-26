package com.mamxanh.app.core.components.design

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.mamxanh.core.ui.R

@Composable fun BioIntro(
    title: String,
    eyebrow: String = "",
    subtitle: String = "",
    action: (@Composable () -> Unit)? = null,
) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp14)) {
        Column(Modifier.weight(1f)) {
            if (eyebrow.isNotBlank()) {
                Bt(eyebrow, size = AppTypography.SIZE_10, tint = bp.muted, mono = true, spacing = AppTypography.TRACKING_1_8)
                Spacer(Modifier.height(AppDimensions.Dp7))
            }
            Bt(
                title,
                size = AppTypography.SIZE_27,
                line = AppTypography.LINE_HEIGHT_35_1,
                bold = true,
                spacing = AppTypography.TRACKING_MINUS_1,
            )
            if (subtitle.isNotBlank()) {
                Spacer(Modifier.height(AppDimensions.Dp6))
                Bt(subtitle, size = AppTypography.SIZE_12, tint = bp.muted)
            }
        }
        if (action != null)Box(Modifier.padding(top = if (eyebrow.isNotBlank())AppDimensions.Dp17 else AppDimensions.Dp0)) { action() }
    }
}

@Composable fun BioSection(
    title: String,
    action: String = "",
    onClick: () -> Unit = {},
) {
    Row(
        Modifier.fillMaxWidth().heightIn(min = if (action.isBlank())AppDimensions.Dp26 else AppDimensions.Dp48),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Bt(title, Modifier.weight(1f), size = AppTypography.SIZE_16, bold = true, spacing = AppTypography.TRACKING_MINUS_35)
        if (action.isNotBlank()) {
            Row(
                Modifier.heightIn(min = AppDimensions.Dp48).clickable(role = Role.Button, onClick = onClick),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Bt(action, size = AppTypography.SIZE_12, tint = bp.deep, semi = true)
                Spacer(Modifier.width(AppDimensions.Dp5))
                Bi("next", size = AppIcons.SIZE_16)
            }
        }
    }
}

@Composable fun BioLink(
    title: String,
    icon: String,
    onClick: () -> Unit,
    subtitle: String = "",
) {
    BioCard(Modifier.fillMaxWidth(), padding = AppSpacing.VALUE_14, radius = AppRadii.VALUE_20, onClick = onClick) {
        Row(
            Modifier.fillMaxWidth().heightIn(min = AppDimensions.Dp46),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp12),
        ) {
            Box(Modifier.size(AppDimensions.Dp38).background(bp.soft, bioShape(AppRadii.VALUE_12)), contentAlignment = Alignment.Center) {
                Bi(icon, size = AppIcons.SIZE_20)
            }
            Column(Modifier.weight(1f)) {
                Bt(title, size = AppTypography.SIZE_13, semi = true)
                if (subtitle.isNotBlank())Bt(subtitle, size = AppTypography.SIZE_11, tint = bp.muted)
            }
            Bi("next", size = AppIcons.SIZE_16, tint = bp.muted)
        }
    }
}

@Composable fun BioTabs(
    options: List<Pair<String, String>>,
    selected: String,
    onSelect: (String) -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(
                lerp(bp.bg, bp.accent, .4f),
                RoundedCornerShape(AppDimensions.Dp15),
            ).border(
                AppDimensions.Dp1,
                AppColors.White.copy(alpha = AppOpacity.VALUE_5),
                RoundedCornerShape(AppDimensions.Dp15),
            ).padding(horizontal = AppDimensions.Dp5, vertical = AppDimensions.Dp3),
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp3),
    ) {
        options.forEach { (id, label) ->
            Box(
                Modifier
                    .weight(1f)
                    .heightIn(min = AppDimensions.Dp48)
                    .background(
                        if (id ==
                            selected
                        ) {
                            bp.surface
                        } else {
                            AppColors.Transparent
                        },
                        RoundedCornerShape(AppDimensions.Dp11),
                    ).semantics {
                        contentDescription = label
                        this.selected = id == selected
                    }.clickable(role = Role.Tab) { onSelect(id) }
                    .padding(horizontal = AppDimensions.Dp6, vertical = AppDimensions.Dp9),
                contentAlignment = Alignment.Center,
            ) {
                Bt(
                    label,
                    size = AppTypography.SIZE_12,
                    tint =
                        if (id ==
                            selected
                        ) {
                            bp.deep
                        } else {
                            bp.muted
                        },
                    align = TextAlign.Center,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable fun BioGrid(
    count: Int,
    columns: Int = 2,
    content: @Composable (Int, Modifier) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp12)) {
        (0 until count).chunked(columns).forEach { row ->
            Row(
                Modifier.fillMaxWidth().height(IntrinsicSize.Max),
                horizontalArrangement =
                    Arrangement.spacedBy(
                        if (columns ==
                            3
                        ) {
                            AppDimensions.Dp9
                        } else {
                            AppDimensions.Dp12
                        },
                    ),
            ) {
                repeat(
                    columns,
                ) { cell -> if (cell < row.size)content(row[cell], Modifier.weight(1f).fillMaxHeight())else Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable fun BioEmpty(
    title: String,
    subtitle: String = "",
    icon: String = "garden",
    action: String = "",
    onClick: () -> Unit = {},
) {
    val outline = bp.control
    Column(
        Modifier
            .fillMaxWidth()
            .background(AppColors.White.copy(alpha = AppOpacity.VALUE_3), bioShape(AppRadii.VALUE_24))
            .drawBehind {
                drawRoundRect(
                    outline,
                    cornerRadius =
                        androidx.compose.ui.geometry
                            .CornerRadius(AppDimensions.Dp24.toPx()),
                    style =
                        androidx.compose.ui.graphics.drawscope.Stroke(
                            AppDimensions.Dp1.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(AppDimensions.Dp3.toPx(), AppDimensions.Dp3.toPx())),
                        ),
                )
            }.padding(horizontal = AppDimensions.Dp21, vertical = AppDimensions.Dp29),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Bi(icon, size = AppIcons.SIZE_44)
        Spacer(Modifier.height(AppDimensions.Dp15))
        Bt(title, size = AppTypography.SIZE_18, line = AppTypography.LINE_HEIGHT_27, bold = true, align = TextAlign.Center)
        if (subtitle.isNotBlank()) {
            Spacer(Modifier.height(AppDimensions.Dp7))
            Bt(subtitle, size = AppTypography.SIZE_12, tint = bp.muted, align = TextAlign.Center)
        }
        if (action.isNotBlank()) {
            Spacer(Modifier.height(AppDimensions.Dp20))
            BioButton(action, onClick)
        }
    }
}

@Composable fun BioDetails(
    title: String,
    initial: Boolean = LocalBioExpanded.current,
    content: @Composable ColumnScope.() -> Unit,
) {
    var open by rememberSaveable(title) { mutableStateOf(initial) }
    Column(Modifier.fillMaxWidth()) {
        HorizontalDivider(color = bp.line, thickness = AppDimensions.Dp1)
        val disclosureState = if (open)stringResource(R.string.copy_da_mo_rong_c5e118)else stringResource(R.string.copy_da_thu_gon_9b8c90)
        Row(
            Modifier
                .fillMaxWidth()
                .heightIn(min = AppDimensions.Dp60)
                .padding(top = AppDimensions.Dp10)
                .semantics {
                    stateDescription =
                        disclosureState
                }.clickable(role = Role.Button) { open = !open },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Bt(title, Modifier.weight(1f), size = AppTypography.SIZE_13, semi = true)
            Bi(if (open) "minus" else "plus", size = AppIcons.SIZE_14, tint = bp.ink)
        }
        if (open) {
            Column(
                Modifier.padding(top = AppDimensions.Dp10, bottom = AppDimensions.Dp4),
                verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp12),
                content = content,
            )
        }
    }
}

@Composable fun BioFacts(values: List<Pair<String, String>>) {
    Column {
        values.forEachIndexed { i, (label, value) ->
            if (i >
                0
            ) {
                HorizontalDivider(color = bp.line, thickness = AppDimensions.Dp1)
            }
            ;Row(
                Modifier.fillMaxWidth().heightIn(min = AppDimensions.Dp39).padding(vertical = AppDimensions.Dp10),
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp12),
            ) {
                Bt(label, Modifier.weight(1f), size = AppTypography.SIZE_12, tint = bp.muted)
                Bt(bioDisplayDate(value), Modifier.weight(1f), size = AppTypography.SIZE_12, align = TextAlign.End)
            }
        }
    }
}

@Preview(name = "Garden sections and empty state", widthDp = 335, showBackground = true)
@Composable
private fun BioSectionsPreview() {
    val garden = stringResource(R.string.copy_vuon_9e4c10)
    val empty = stringResource(R.string.copy_chua_co_o_trong_6abd67)
    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp12)) {
        BioIntro(garden)
        BioSection(garden)
        BioLink(garden, "garden", {})
        BioEmpty(empty)
    }
}
