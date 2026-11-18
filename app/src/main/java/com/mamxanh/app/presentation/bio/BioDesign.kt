package com.mamxanh.app.presentation.bio

import com.mamxanh.app.presentation.ui.design.AppThemeEffects
import com.mamxanh.app.presentation.ui.design.AppColors
import com.mamxanh.app.presentation.ui.design.AppDimensions
import com.mamxanh.app.presentation.ui.design.AppIcons
import com.mamxanh.app.presentation.ui.design.AppOpacity
import com.mamxanh.app.presentation.ui.design.AppRadii
import com.mamxanh.app.presentation.ui.design.AppSpacing
import com.mamxanh.app.presentation.ui.design.AppTypography

import com.mamxanh.shared.localization.TextKey

import com.mamxanh.shared.localization.AppText

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.mamxanh.app.presentation.ui.GardenFontFamily
import com.mamxanh.app.presentation.ui.GardenTypography

import com.mamxanh.app.presentation.ui.design.*

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
    val largeText=LocalDensity.current.fontScale>1.2f
    val parent=when(route){"home","tasks","garden","shop","stats"->route;"task","task-new","focus","templates","insights"->"tasks";"tree","inventory","species","encyclopedia"->"garden";"more"->"";else->"stats"}
    Column(Modifier.fillMaxWidth().background(lerp(bp.bg,AppColors.White,.10f))){HorizontalDivider(color=bp.line,thickness=AppDimensions.Dp1)
        Row(Modifier.fillMaxWidth().height(if(largeText)AppDimensions.Dp110 else AppDimensions.Dp79).padding(horizontal=if(LocalConfiguration.current.screenWidthDp<AppThemeEffects.CompactWidth)AppDimensions.Dp6 else AppDimensions.Dp10,vertical=AppDimensions.Dp10),horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp4)){
            listOf(Triple("home","home",AppText.text(TextKey.CopyHomNayE6747a)),Triple("tasks","tasks",AppText.text(TextKey.CopyViec818768)),Triple("garden","garden",AppText.text(TextKey.CopyVuon9e4c10)),Triple("shop","shop",AppText.text(TextKey.CopyTiemF1ee00)),Triple("stats","book",AppText.text(TextKey.CopyNhatKy1e6c3c))).forEach{(r,icon,label)->
                val active=parent==r
                Column(Modifier.weight(1f).fillMaxHeight().background(if(active)AppColors.White.copy(alpha=AppOpacity.Value_6)else AppColors.Transparent,bioShape(AppRadii.Value15)).semantics{selected=active}.clickable(role=Role.Tab){onGo(r)}.padding(top=AppDimensions.Dp5,bottom=AppDimensions.Dp4),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp4)){
                    Bi(icon,size=AppIcons.Size21,tint=if(active)bp.deep else bp.muted);Bt(label,size=AppTypography.Size11,line=AppTypography.LineHeight15_4,tint=if(active)bp.deep else bp.muted,semi=active,maxLines=if(largeText)2 else 1,align=TextAlign.Center)
                    Box(Modifier.width(AppDimensions.Dp12).height(AppDimensions.Dp2).background(if(active)bp.deep else AppColors.Transparent,RoundedCornerShape(AppDimensions.Dp2)))
                }
            }
        }
    }
}
