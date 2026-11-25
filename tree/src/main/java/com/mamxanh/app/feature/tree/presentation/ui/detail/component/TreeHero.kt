package com.mamxanh.app.feature.tree.presentation.ui.detail.component

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import com.mamxanh.app.core.components.design.*
import com.mamxanh.core.ui.R

private const val GRID_STROKE_PIXELS = 1f
private const val PLANT_SHADOW_SATURATION = 0.22f
private const val PLANT_SHADOW_LIGHTNESS = 0.30f
private const val PLANT_SHADOW_ALPHA = 0.12f
private val PlantShadowOffset = AppDimensions.Dp9
private val PlantShadowBlur = AppDimensions.Dp5

@Composable
fun TreeHero(
    state: TreeHeroState,
    onAction: (TreeHeroAction) -> Unit,
) {
    val home = state.home
    val short = state.compact
    val dead = state.dead
    val p = bp
    val configuration = LocalConfiguration.current
    val narrow = configuration.screenWidthDp < AppThemeEffects.COMPACT_WIDTH
    val landscape = configuration.screenHeightDp < AppThemeEffects.COMPACT_HEIGHT
    val shape = bioShape(AppRadii.VALUE_26)
    val horizontalPadding = if (narrow) AppDimensions.Dp15 else AppDimensions.Dp19
    Column(
        Modifier
            .fillMaxWidth()
            .shadow(
                AppDimensions.Dp5,
                shape,
                ambientColor = p.deep.copy(alpha = AppOpacity.VALUE_35),
                spotColor = p.deep.copy(alpha = AppOpacity.VALUE_30),
            ).background(remember(p) { bioHeroBrush(p) }, shape)
            .border(AppDimensions.Dp1, AppColors.White.copy(alpha = AppOpacity.VALUE_75), shape)
            .clip(shape)
            .drawBehind {
                val step = AppDimensions.Dp24.toPx()
                val grid = AppColors.White.copy(alpha = AppOpacity.VALUE_08)
                var x = 0f
                while (x < size.width) {
                    drawLine(grid, Offset(x, 0f), Offset(x, size.height), GRID_STROKE_PIXELS)
                    x += step
                }
                var y = 0f
                while (y < size.height) {
                    drawLine(grid, Offset(0f, y), Offset(size.width, y), GRID_STROKE_PIXELS)
                    y += step
                }
                val right = size.width - AppDimensions.Dp12.toPx()
                val top = AppDimensions.Dp12.toPx()
                val length = AppDimensions.Dp18.toPx()
                val outline = p.deep.copy(alpha = AppOpacity.VALUE_45)
                drawLine(outline, Offset(right - length, top), Offset(right, top), AppDimensions.Dp1.toPx())
                drawLine(outline, Offset(right, top), Offset(right, top + length), AppDimensions.Dp1.toPx())
            }.padding(start = horizontalPadding, end = horizontalPadding, top = AppDimensions.Dp15, bottom = AppDimensions.Dp19),
    ) {
        Row(
            Modifier.fillMaxWidth().height(AppDimensions.Dp48),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            BioTag(state.stageLabel, dot = true)
            if (home) {
                BioTextAction(stringResource(R.string.copy_doi_cay_fada9c), { onAction(TreeHeroAction.ChooseTree) }, "next")
            } else {
                BioTextAction(
                    if (state.selectedOnHome) {
                        stringResource(
                            R.string.copy_tren_trang_chu_e7c2f5,
                        )
                    } else {
                        stringResource(R.string.copy_dat_o_trang_chu_d8241c)
                    },
                    {
                        onAction(TreeHeroAction.PinTree)
                    },
                )
            }
        }
        Spacer(Modifier.height(AppDimensions.Dp2))
        val artHeight =
            when {
                landscape -> AppDimensions.Dp100
                home && short -> AppDimensions.HomeShortHeroArtworkHeight
                home -> AppDimensions.HomeHeroArtworkHeight
                short -> AppDimensions.TreeShortHeroArtworkHeight
                narrow -> AppDimensions.TreeNarrowHeroArtworkHeight
                else -> AppDimensions.Dp220
            }
        Box(
            Modifier.fillMaxWidth().height(artHeight).then(
                if (home) {
                    Modifier.clickable {
                        onAction(TreeHeroAction.OpenTree)
                    }
                } else {
                    Modifier
                },
            ),
            contentAlignment = Alignment.Center,
        ) {
            val plantHeight = if (home) artHeight else artHeight - AppDimensions.Dp12
            val artwork = Modifier.height(plantHeight).fillMaxWidth()
            if (!dead && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                BioPlant(
                    state.species,
                    artwork.offset(y = PlantShadowOffset).blur(PlantShadowBlur, BlurredEdgeTreatment.Unbounded),
                    tint = Color.hsl(p.hue, PLANT_SHADOW_SATURATION, PLANT_SHADOW_LIGHTNESS, PLANT_SHADOW_ALPHA),
                )
            }
            BioPlant(state.species, artwork, dead)
            val accessory = state.accessoryIcon
            if (accessory != null &&
                !dead
            ) {
                Box(
                    Modifier
                        .align(
                            Alignment.TopEnd,
                        ).padding(
                            top = AppDimensions.Dp55,
                            end = AppDimensions.Dp14,
                        ).size(
                            AppDimensions.Dp32,
                        ).background(
                            AppColors.White.copy(alpha = AppOpacity.VALUE_6),
                            RoundedCornerShape(50),
                        ).border(AppDimensions.Dp1, p.accent, RoundedCornerShape(50)),
                    contentAlignment = Alignment.Center,
                ) {
                    Bi(accessory, size = AppIcons.SIZE_23)
                }
            }
            if (!home &&
                state.canBrowseTrees
            ) {
                BioIconButton(
                    "back",
                    {
                        onAction(TreeHeroAction.PreviousTree)
                    },
                    Modifier
                        .align(
                            Alignment.CenterStart,
                        ).offset(
                            x = AppDimensions.DpMinus6,
                        ),
                    boxed = true,
                    iconSize = AppIcons.SIZE_18,
                    tint = p.deep,
                    label = stringResource(R.string.copy_cay_truoc_6648b1),
                )
                BioIconButton(
                    "next",
                    {
                        onAction(TreeHeroAction.NextTree)
                    },
                    Modifier
                        .align(
                            Alignment.CenterEnd,
                        ).offset(
                            x = AppDimensions.Dp6,
                        ),
                    boxed = true,
                    iconSize = AppIcons.SIZE_18,
                    tint = p.deep,
                    label = stringResource(R.string.copy_cay_tiep_theo_242116),
                )
            }
        }
        Spacer(Modifier.height(AppDimensions.Dp10))
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp8),
        ) {
            Column(Modifier.weight(1f)) {
                Bt(
                    state.name,
                    size = AppTypography.SIZE_21,
                    line = AppTypography.LINE_HEIGHT_28_35,
                    bold = true,
                    spacing = AppTypography.TRACKING_MINUS_7,
                )
                Bt(
                    stringResource(R.string.copy_0_1_giai_doan_869247, state.stage, state.stages),
                    size = AppTypography.SIZE_12,
                    tint = p.muted,
                    line = AppTypography.LINE_HEIGHT_21_7,
                )
            }
            BioTag(
                if (dead) {
                    stringResource(R.string.copy_kho_heo_bf546a)
                } else if (state.watered) {
                    stringResource(R.string.copy_du_nuoc_c17aa8)
                } else {
                    stringResource(R.string.copy_can_tuoi_a0957f)
                },
                "water",
            )
        }
        Spacer(Modifier.height(AppDimensions.Dp12))
        BioProgress(state.progress / 100f, white = true)
        Spacer(Modifier.height(AppDimensions.Dp6))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Bt(state.growthCaption, size = AppTypography.SIZE_10, tint = p.muted, line = AppTypography.LINE_HEIGHT_18_6)
            Bt("${state.progress}%", size = AppTypography.SIZE_11, tint = p.deep, mono = true, line = AppTypography.LINE_HEIGHT_18_6)
        }
        if (home) {
            Spacer(Modifier.height(AppDimensions.Dp13))
            if (LocalDensity.current.fontScale >
                AppThemeEffects.LARGE_FONT_SCALE
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.Dp10)) {
                    BioButton(
                        if (state.watered)stringResource(R.string.copy_da_tuoi_de2042) else stringResource(R.string.copy_tuoi_cay_0990f6),
                        {
                            onAction(TreeHeroAction.Water)
                        },
                        Modifier.fillMaxWidth(),
                        "water",
                        enabled =
                            !state.watered && !dead && !state.busy,
                    )
                    BioButton(
                        stringResource(R.string.copy_trang_tri_d48231),
                        { onAction(TreeHeroAction.Decorate) },
                        Modifier.fillMaxWidth(),
                        "spark",
                        secondary = true,
                        enabled =
                            !dead && !state.busy,
                    )
                }
            } else {
                Row(Modifier.height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(AppDimensions.Dp10)) {
                    BioButton(
                        if (state.watered)stringResource(R.string.copy_da_tuoi_de2042) else stringResource(R.string.copy_tuoi_cay_0990f6),
                        {
                            onAction(TreeHeroAction.Water)
                        },
                        Modifier.weight(1f).fillMaxHeight(),
                        "water",
                        enabled =
                            !state.watered && !dead && !state.busy,
                        labelSize = if (narrow)AppTypography.SIZE_12 else AppTypography.SIZE_14,
                    )
                    BioButton(
                        stringResource(R.string.copy_trang_tri_d48231),
                        {
                            onAction(TreeHeroAction.Decorate)
                        },
                        Modifier.weight(1f).fillMaxHeight(),
                        "spark",
                        secondary = true,
                        enabled =
                            !dead && !state.busy,
                        labelSize = if (narrow)AppTypography.SIZE_12 else AppTypography.SIZE_14,
                    )
                }
            }
        }
    }
}
