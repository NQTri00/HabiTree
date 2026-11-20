package com.mamxanh.app.presentation.bio

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mamxanh.app.R as AppR
import com.mamxanh.core.ui.R
import com.mamxanh.app.core.components.design.AppThemeEffects
import com.mamxanh.app.core.components.design.AppColors
import com.mamxanh.app.core.components.design.AppDimensions
import com.mamxanh.app.core.components.design.AppOpacity
import com.mamxanh.app.core.components.design.AppRadii
import com.mamxanh.app.core.components.design.AppTypography



import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.mamxanh.app.core.components.GardenTypography

import com.mamxanh.app.core.components.design.*

@Composable internal fun BioTheme(world:BioWorld,treeId:String,content:@Composable () -> Unit) {
    val species=world.tree(treeId)?.s("species")?:"succulent"
    val p=world.data.o("palettes").o(species)
    val accent=color(p.s("accent",AppColors.HexCEE3C4));val soft=color(p.s("soft",AppColors.HexE7EFDF));val bg=color(p.s("bg",AppColors.HexE8EDE4));val deep=color(p.s("deep",AppColors.Hex3E5C3C));val muted=color(p.s("muted",AppColors.Hex606B5C))
    val hsv=FloatArray(3);android.graphics.Color.colorToHSV(deep.toArgb(),hsv)
    val hue=p.i("hue",AppThemeEffects.FallbackTreeHue).toFloat();val line=Color(androidx.core.graphics.ColorUtils.HSLToColor(floatArrayOf(hue,AppThemeEffects.OutlineSaturation,AppThemeEffects.OutlineLightness)))
    val palette=BioPalette(accent,soft,bg,deep,muted,line,hue)
    val monoFont=rememberBioMonoFont()
    CompositionLocalProvider(LocalBioPalette provides palette,LocalBioMonoFont provides monoFont) {
        MaterialTheme(colorScheme=lightColorScheme(primary=deep,onPrimary=AppColors.White,primaryContainer=accent,onPrimaryContainer=palette.ink,background=bg,surface=palette.surface,onSurface=palette.ink,outline=line),typography=GardenTypography){
            val touchIndication=remember(deep){BioTouchIndication(deep)}
            CompositionLocalProvider(LocalIndication provides touchIndication,content=content)
        }
    }
}
@Composable internal fun BioNav(route:String,onGo:(String)->Unit) {
    val largeText=LocalDensity.current.fontScale>AppThemeEffects.LargeFontScale
    val parent=when(route){"home","tasks","garden","shop","stats"->route;"task","task-new","focus","templates","insights"->"tasks";"tree","inventory","species","encyclopedia"->"garden";"more"->"";else->"stats"}
    Column(Modifier.fillMaxWidth().background(lerp(bp.bg,AppColors.White,.10f))){HorizontalDivider(color=bp.line,thickness=AppDimensions.Dp1)
        Row(Modifier.fillMaxWidth().height(if(largeText)AppDimensions.Dp110 else AppDimensions.Dp79).padding(horizontal=if(LocalConfiguration.current.screenWidthDp<AppThemeEffects.CompactWidth)AppDimensions.Dp6 else AppDimensions.Dp10,vertical=AppDimensions.Dp10),horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp4)){
            listOf(
                Triple(
                    "home",
                    AppR.drawable.bio_icon_home to AppR.drawable.bio_icon_home_active,
                    stringResource(R.string.copy_hom_nay_e6747a),
                ),
                Triple(
                    "tasks",
                    AppR.drawable.bio_icon_tasks to AppR.drawable.bio_icon_tasks_active,
                    stringResource(R.string.copy_viec_818768),
                ),
                Triple(
                    "garden",
                    AppR.drawable.bio_icon_garden to AppR.drawable.bio_icon_garden_active,
                    stringResource(R.string.copy_vuon_9e4c10),
                ),
                Triple(
                    "shop",
                    AppR.drawable.bio_icon_shop to AppR.drawable.bio_icon_shop_active,
                    stringResource(R.string.copy_tiem_f1ee00),
                ),
                Triple(
                    "stats",
                    AppR.drawable.bio_icon_book to AppR.drawable.bio_icon_book_active,
                    stringResource(R.string.copy_nhat_ky_1e6c3c),
                ),
            ).forEach { (r, icons, label) ->
                val active=parent==r
                Column(Modifier.weight(1f).fillMaxHeight().background(if(active)AppColors.White.copy(alpha=AppOpacity.Value_6)else AppColors.Transparent,bioShape(AppRadii.Value15)).semantics{selected=active}.clickable(role=Role.Tab){onGo(r)}.padding(top=AppDimensions.Dp5,bottom=AppDimensions.Dp4),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp4)){
                    Icon(
                        painterResource(if (active) icons.second else icons.first),
                        contentDescription = null,
                        modifier = Modifier.size(AppDimensions.Dp21),
                        tint = if (active) bp.deep else bp.muted,
                    )
                    Bt(label,size=AppTypography.Size11,line=AppTypography.LineHeight15_4,tint=if(active)bp.deep else bp.muted,semi=active,maxLines=if(largeText)2 else 1,align=TextAlign.Center)
                    Box(Modifier.width(AppDimensions.Dp12).height(AppDimensions.Dp2).background(if(active)bp.deep else AppColors.Transparent,RoundedCornerShape(AppDimensions.Dp2)))
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 120, heightDp = 48)
@Composable
private fun BioNavigationIconStrokePreview() {
    Row(
        modifier = Modifier.fillMaxSize().background(bp.surface),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painterResource(AppR.drawable.bio_icon_home),
            contentDescription = null,
            modifier = Modifier.size(AppDimensions.Dp21),
            tint = bp.deep,
        )
        Icon(
            painterResource(AppR.drawable.bio_icon_home_active),
            contentDescription = null,
            modifier = Modifier.size(AppDimensions.Dp21),
            tint = bp.deep,
        )
    }
}

@Preview(name = "Home bottom navigation", widthDp = 375, showBackground = true)
@Composable
private fun BioNavPreview() {
    BioNav(route = "home", onGo = {})
}
