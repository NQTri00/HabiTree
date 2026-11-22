package com.mamxanh.app.core.components.design

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.mamxanh.app.core.components.GardenFontFamily

@Composable fun Bt(
    text: String,
    modifier: Modifier = Modifier,
    size: Int = AppTypography.SIZE_14,
    tint: Color = bp.ink,
    bold: Boolean = false,
    semi: Boolean = false,
    mono: Boolean = false,
    line: Float =
        size *
            (
                if (size >=
                    AppThemeEffects.LARGE_TEXT_THRESHOLD
                ) {
                    AppThemeEffects.LARGE_LINE_HEIGHT
                } else {
                    AppThemeEffects.BODY_LINE_HEIGHT
                }
            ),
    spacing: Float = AppTypography.TRACKING_0,
    align: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
) {
    val monoFont = LocalBioMonoFont.current
    Text(
        text,
        modifier,
        color = tint,
        fontSize = size.sp,
        lineHeight = line.sp,
        fontFamily = if (mono)monoFont.family else GardenFontFamily,
        fontWeight =
            if (bold) {
                FontWeight.Bold
            } else if (semi) {
                FontWeight.SemiBold
            } else {
                FontWeight.Normal
            },
        letterSpacing =
            (
                spacing -
                    if (mono &&
                        !monoFont.exact
                    ) {
                        size * AppThemeEffects.MONOSPACE_TRACKING_CORRECTION
                    } else {
                        0f
                    }
            ).sp,
        textAlign = align,
        maxLines = maxLines,
        overflow =
            if (maxLines <
                Int.MAX_VALUE
            ) {
                TextOverflow.Ellipsis
            } else {
                TextOverflow.Clip
            },
        style =
            LocalTextStyle.current.copy(
                platformStyle =
                    androidx.compose.ui.text
                        .PlatformTextStyle(includeFontPadding = false),
            ),
    )
}

@Composable fun Bi(
    name: String,
    modifier: Modifier = Modifier,
    tint: Color = bp.deep,
    size: Int = AppIcons.SIZE_20,
) {
    val context = LocalContext.current
    val resource = remember(name) { context.resources.getIdentifier("bio_icon_$name", "drawable", context.packageName) }
    if (resource != 0)Icon(painterResource(resource), null, modifier.size(size.dp), tint = tint)
}

@Composable fun BioPlant(
    species: String,
    modifier: Modifier = Modifier,
    dead: Boolean = false,
    tint: Color? = null,
) {
    val context = LocalContext.current
    val resource = remember(species) { context.resources.getIdentifier("bio_plant_$species", "drawable", context.packageName) }
    if (resource != 0) {
        val filter =
            when {
                tint != null -> ColorFilter.tint(tint)
                dead -> ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
                else -> null
            }
        Image(
            painterResource(resource),
            contentDescription = null,
            modifier = modifier.alpha(if (dead) AppOpacity.VALUE_55 else AppOpacity.VALUE_1),
            contentScale = ContentScale.Fit,
            colorFilter = filter,
        )
    }
}
