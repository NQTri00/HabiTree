package com.mamxanh.app.presentation.ui.design

import com.mamxanh.core.ui.R
import androidx.compose.ui.res.stringResource



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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.mamxanh.app.presentation.ui.GardenFontFamily

data class BioPalette(val accent:Color,val soft:Color,val bg:Color,val deep:Color,val muted:Color,val line:Color,val hue:Float=AppThemeEffects.DefaultHue) {
    val ink=AppColors.Ink
    val surface=AppColors.Surface
    val control get()=Color(androidx.core.graphics.ColorUtils.HSLToColor(floatArrayOf(hue,AppThemeEffects.ControlSaturation,AppThemeEffects.ControlLightness)))
}
val LocalBioPalette=staticCompositionLocalOf{BioPalette(AppColors.SwatchFFF4C7D8,AppColors.SwatchFFF6E3EB,AppColors.SwatchFFEEE8EB,AppColors.SwatchFF71354F,AppColors.SwatchFF705F68,AppColors.SwatchFFD3C3CB)}
val LocalBioExpanded=staticCompositionLocalOf{false}
val LocalBioButtonSize=staticCompositionLocalOf{AppTypography.Size14}
val bp:BioPalette @Composable get()=LocalBioPalette.current
fun color(hex:String)=Color(android.graphics.Color.parseColor(hex))
@Composable fun Bt(text:String,modifier:Modifier=Modifier,size:Int=AppTypography.Size14,tint:Color=bp.ink,bold:Boolean=false,semi:Boolean=false,mono:Boolean=false,line:Float=size*(if(size>=AppThemeEffects.LargeTextThreshold)AppThemeEffects.LargeLineHeight else AppThemeEffects.BodyLineHeight),spacing:Float=AppTypography.Tracking0,align:TextAlign?=null,maxLines:Int=Int.MAX_VALUE) {
    val monoFont=LocalBioMonoFont.current
    Text(text,modifier,color=tint,fontSize=size.sp,lineHeight=line.sp,fontFamily=if(mono)monoFont.family else GardenFontFamily,fontWeight=if(bold)FontWeight.Bold else if(semi)FontWeight.SemiBold else FontWeight.Normal,letterSpacing=(spacing-if(mono&&!monoFont.exact)size*AppThemeEffects.MonospaceTrackingCorrection else 0f).sp,textAlign=align,maxLines=maxLines,overflow=if(maxLines<Int.MAX_VALUE)TextOverflow.Ellipsis else TextOverflow.Clip,style=LocalTextStyle.current.copy(platformStyle=androidx.compose.ui.text.PlatformTextStyle(includeFontPadding=false)))
}
@Composable fun Bi(name:String,modifier:Modifier=Modifier,tint:Color=bp.deep,size:Int=AppIcons.Size20) {
    val context=LocalContext.current
    val resource=remember(name){context.resources.getIdentifier("bio_icon_$name","drawable",context.packageName)}
    if(resource!=0)Icon(painterResource(resource),null,modifier.size(size.dp),tint=tint)
}
@Composable fun BioPlant(species:String,modifier:Modifier=Modifier,dead:Boolean=false) {
    val context=LocalContext.current
    val resource=remember(species){context.resources.getIdentifier("bio_plant_$species","drawable",context.packageName)}
    if(resource!=0)Image(painterResource(resource),null,modifier.alpha(if(dead)AppOpacity.Value_55 else AppOpacity.Value1),contentScale=ContentScale.Fit,colorFilter=if(dead)ColorFilter.colorMatrix(ColorMatrix().apply{setToSaturation(0f)})else null)
}
fun bioShape(radius:Int=AppRadii.Value22)=RoundedCornerShape(topStart=radius.dp,topEnd=(radius/2).dp,bottomEnd=radius.dp,bottomStart=radius.dp)
fun bioHeroBrush(p:BioPalette)=object:ShaderBrush(){
    override fun createShader(size:androidx.compose.ui.geometry.Size):Shader{
        val dx=.5735764f;val dy=.8191521f
        val half=(size.width*dx+size.height*dy)/2f
        val center=androidx.compose.ui.geometry.Offset(size.width/2f,size.height/2f)
        val delta=androidx.compose.ui.geometry.Offset(dx*half,dy*half)
        return LinearGradientShader(center-delta,center+delta,listOf(AppColors.White.copy(alpha=AppOpacity.Value_6),p.soft,lerp(p.soft,p.accent,AppThemeEffects.HeroBlend)),listOf(0f,.46f,1f))
    }
}
fun Modifier.bioLift(shape:Shape,tint:Color)=drawBehind{
    val outline=shape.createOutline(size,layoutDirection,this)
    val path=android.graphics.Path()
    when(outline){
        is Outline.Rounded->{val r=outline.roundRect;path.addRoundRect(android.graphics.RectF(r.left,r.top,r.right,r.bottom),floatArrayOf(r.topLeftCornerRadius.x,r.topLeftCornerRadius.y,r.topRightCornerRadius.x,r.topRightCornerRadius.y,r.bottomRightCornerRadius.x,r.bottomRightCornerRadius.y,r.bottomLeftCornerRadius.x,r.bottomLeftCornerRadius.y),android.graphics.Path.Direction.CW)}
        is Outline.Rectangle->path.addRect(outline.rect.left,outline.rect.top,outline.rect.right,outline.rect.bottom,android.graphics.Path.Direction.CW)
        is Outline.Generic->path.set(outline.path.asAndroidPath())
    }
    drawIntoCanvas{canvas->
        val paint=android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)
        paint.color=tint.copy(alpha=AppOpacity.Value_035).toArgb()
        canvas.nativeCanvas.save();canvas.nativeCanvas.translate(0f,AppDimensions.Dp3.toPx());canvas.nativeCanvas.drawPath(path,paint);canvas.nativeCanvas.restore()
        paint.color=AppColors.White.toArgb();paint.setShadowLayer(AppDimensions.Dp12.toPx(),0f,AppDimensions.Dp8.toPx(),tint.copy(alpha=AppOpacity.Value_08).toArgb())
        canvas.nativeCanvas.drawPath(path,paint)
    }
}
@Composable fun BioCard(modifier:Modifier=Modifier,tint:Color=bp.surface,padding:Int=AppSpacing.Value18,radius:Int=AppRadii.Value22,onClick:(()->Unit)?=null,borderColor:Color?=null,interactionSource:MutableInteractionSource?=null,content:@Composable ColumnScope.()->Unit) {
    val shape=bioShape(radius)
    Column(modifier.bioLift(shape,bp.deep).background(tint,shape).border(AppDimensions.Dp1,borderColor?:AppColors.White.copy(alpha=AppOpacity.Value_8),shape).then(if(onClick!=null)Modifier.clickable(interactionSource=interactionSource,indication=LocalIndication.current,onClick=onClick)else Modifier).padding((padding+1).dp),content=content)
}
@Composable fun BioButton(label:String,onClick:()->Unit,modifier:Modifier=Modifier,icon:String="",secondary:Boolean=false,enabled:Boolean=true,danger:Boolean=false,labelSize:Int=LocalBioButtonSize.current,interactionSource:MutableInteractionSource?=null) {
    val shape=bioShape(AppRadii.Value14);val fill=if(danger)AppColors.SwatchFFF7DFDF else if(secondary)AppColors.White.copy(alpha=AppOpacity.Value_8) else bp.accent
    Row(modifier.heightIn(min=AppDimensions.Dp48).alpha(if(enabled)AppOpacity.Value1 else AppOpacity.Value_48).shadow(if(enabled)AppDimensions.Dp2 else AppDimensions.Dp0,shape,ambientColor=bp.deep.copy(alpha=AppOpacity.Value_08),spotColor=bp.deep.copy(alpha=AppOpacity.Value_08)).background(fill,shape).border(AppDimensions.Dp1,if(danger)AppColors.SwatchFFE2B8B8 else if(secondary)bp.line else lerp(bp.accent,bp.deep,AppThemeEffects.ButtonOutlineBlend),shape).clickable(interactionSource=interactionSource,indication=LocalIndication.current,enabled=enabled,role=Role.Button,onClick=onClick).padding(horizontal=if(LocalBioButtonSize.current==13)AppDimensions.Dp8 else if(LocalConfiguration.current.screenWidthDp<AppThemeEffects.CompactWidth)AppDimensions.Dp10 else AppDimensions.Dp18,vertical=AppDimensions.Dp10),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.Center) {
        if(icon.isNotBlank()){Bi(icon,tint=if(danger)AppColors.SwatchFF8C363B else bp.ink,size=AppIcons.Size18);Spacer(Modifier.width(AppDimensions.Dp8))}
        Bt(label,size=labelSize,semi=true,tint=if(danger)AppColors.SwatchFF8C363B else bp.ink,align=TextAlign.Center)
    }
}
@Composable fun BioIconButton(icon:String,onClick:()->Unit,modifier:Modifier=Modifier,boxed:Boolean=false,enabled:Boolean=true,iconSize:Int=AppIcons.Size20,tint:Color=bp.ink,label:String=when(icon){"close"->stringResource(R.string.copy_dong_5d54c2);"back"->stringResource(R.string.copy_quay_lai_6d7935);"next"->stringResource(R.string.copy_tiep_theo_1c33a9);"more"->stringResource(R.string.copy_them_chuc_nang_545f7b);"plus"->stringResource(R.string.copy_them_dd9262);"bag"->stringResource(R.string.copy_mo_tui_do_f3f7f9);"settings"->stringResource(R.string.copy_tuy_chon_598c1b);"refresh"->stringResource(R.string.copy_lam_moi_b4c613);else->icon}) {
    Box(modifier.size(AppDimensions.Dp48).alpha(if(enabled)AppOpacity.Value1 else AppOpacity.Value_45).then(if(boxed)Modifier.background(bp.surface,bioShape(AppRadii.Value12)).border(AppDimensions.Dp1,bp.line,bioShape(AppRadii.Value12))else Modifier).semantics{contentDescription=label}.clickable(enabled=enabled,role=Role.Button,onClick=onClick),contentAlignment=Alignment.Center){Bi(icon,size=iconSize,tint=tint)}
}
@Composable fun BioTextAction(label:String,onClick:()->Unit,icon:String="",tint:Color=bp.deep,modifier:Modifier=Modifier,iconFirst:Boolean=false) {
    Row(modifier.heightIn(min=AppDimensions.Dp48).widthIn(min=AppDimensions.Dp48).clickable(onClick=onClick).padding(horizontal=AppDimensions.Dp2,vertical=AppDimensions.Dp8),horizontalArrangement=Arrangement.Center,verticalAlignment=Alignment.CenterVertically){if(iconFirst&&icon.isNotBlank()){Bi(icon,tint=tint,size=AppIcons.Size16);Spacer(Modifier.width(AppDimensions.Dp5))};Bt(label,size=AppTypography.Size12,tint=tint,semi=true);if(!iconFirst&&icon.isNotBlank()){Spacer(Modifier.width(AppDimensions.Dp5));Bi(icon,tint=tint,size=AppIcons.Size16)}}
}
@Composable fun BioTag(text:String,icon:String="",dot:Boolean=false,modifier:Modifier=Modifier) {
    Row(modifier.background(AppColors.White.copy(alpha=AppOpacity.Value_65),RoundedCornerShape(AppDimensions.Dp20)).border(AppDimensions.Dp1,AppColors.White.copy(alpha=AppOpacity.Value_55),RoundedCornerShape(AppDimensions.Dp20)).padding(horizontal=AppDimensions.Dp9,vertical=AppDimensions.Dp6),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp4)){
        if(dot)Box(Modifier.size(AppDimensions.Dp5).background(bp.deep,RoundedCornerShape(50)))
        if(icon.isNotBlank())Bi(icon,size=AppIcons.Size12)
        Bt(text,size=AppTypography.Size11,tint=bp.deep,line=AppTypography.LineHeight15_4)
    }
}
@Composable fun BioProgress(value:Float,modifier:Modifier=Modifier,white:Boolean=false) {
    Box(modifier.fillMaxWidth().height(AppDimensions.Dp4).clip(RoundedCornerShape(AppDimensions.Dp4)).background(if(white)AppColors.White.copy(alpha=AppOpacity.Value_65)else AppColors.SwatchFFD8DFCE)){Box(Modifier.fillMaxHeight().fillMaxWidth(value.coerceIn(0f,1f)).background(bp.deep))}
}
@Composable fun BioHeader(title:String,coins:Int,primary:Boolean,onBack:()->Unit,onMore:()->Unit,showWallet:Boolean=true) {
    Column(Modifier.fillMaxWidth()){
        Row(Modifier.fillMaxWidth().height(AppDimensions.Dp71).padding(start=if(LocalConfiguration.current.screenWidthDp<AppThemeEffects.CompactWidth)AppDimensions.Dp14 else AppDimensions.Dp20,end=if(LocalConfiguration.current.screenWidthDp<AppThemeEffects.CompactWidth)AppDimensions.Dp14 else AppDimensions.Dp20,top=AppDimensions.Dp10,bottom=AppDimensions.Dp8),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp4)) {
            if(primary){Row(Modifier.weight(1f),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp7)){Bi("brand");Bt(stringResource(R.string.copy_mam_xanh_692a0a),size=AppTypography.Size18,bold=true,spacing=AppTypography.TrackingMinus_7);Box(Modifier.padding(bottom=AppDimensions.Dp15).size(AppDimensions.Dp5).background(bp.deep,RoundedCornerShape(50)))}}
            else{BioIconButton("back",onBack);Bt(title,Modifier.weight(1f),size=AppTypography.Size15,semi=true,maxLines=1)}
            if(showWallet)Row(Modifier.background(AppColors.SwatchFFFFFDF5,RoundedCornerShape(AppDimensions.Dp30)).border(AppDimensions.Dp1,AppColors.SwatchFFD8D0BD,RoundedCornerShape(AppDimensions.Dp30)).padding(horizontal=AppDimensions.Dp10,vertical=AppDimensions.Dp7),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp5)){Bi("coin",size=AppIcons.Size15,tint=AppColors.SwatchFF765F27);Bt(coins.toString(),size=AppTypography.Size12,mono=true)}
            if(primary)BioIconButton("more",onMore)
        }
        Box(Modifier.padding(horizontal=AppDimensions.Dp24).fillMaxWidth().height(AppDimensions.Dp1).background(Brush.horizontalGradient(listOf(bp.line,AppColors.Transparent))))
    }
}
@Composable fun BioIntro(title:String,eyebrow:String="",subtitle:String="",action:(@Composable () -> Unit)?=null) {
    Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.Top,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp14)){
        Column(Modifier.weight(1f)){
            if(eyebrow.isNotBlank()){Bt(eyebrow,size=AppTypography.Size10,tint=bp.muted,mono=true,spacing=AppTypography.Tracking1_8);Spacer(Modifier.height(AppDimensions.Dp7))}
            Bt(title,size=AppTypography.Size27,line=AppTypography.LineHeight35_1,bold=true,spacing=AppTypography.TrackingMinus1)
            if(subtitle.isNotBlank()){Spacer(Modifier.height(AppDimensions.Dp6));Bt(subtitle,size=AppTypography.Size12,tint=bp.muted)}
        }
        if(action!=null)Box(Modifier.padding(top=if(eyebrow.isNotBlank())AppDimensions.Dp17 else AppDimensions.Dp0)){action()}
    }
}
@Composable fun BioSection(title:String,action:String="",onClick:()->Unit={}) {
    Row(Modifier.fillMaxWidth().heightIn(min=if(action.isBlank())AppDimensions.Dp26 else AppDimensions.Dp48),verticalAlignment=Alignment.CenterVertically){Bt(title,Modifier.weight(1f),size=AppTypography.Size16,bold=true,spacing=AppTypography.TrackingMinus_35);if(action.isNotBlank())Row(Modifier.heightIn(min=AppDimensions.Dp48).clickable(role=Role.Button,onClick=onClick),verticalAlignment=Alignment.CenterVertically){Bt(action,size=AppTypography.Size12,tint=bp.deep,semi=true);Spacer(Modifier.width(AppDimensions.Dp5));Bi("next",size=AppIcons.Size16)}}
}
@Composable fun BioLink(title:String,icon:String,onClick:()->Unit,subtitle:String="") {
    BioCard(Modifier.fillMaxWidth(),padding=AppSpacing.Value14,radius=AppRadii.Value20,onClick=onClick){Row(Modifier.fillMaxWidth().heightIn(min=AppDimensions.Dp46),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){
        Box(Modifier.size(AppDimensions.Dp38).background(bp.soft,bioShape(AppRadii.Value12)),contentAlignment=Alignment.Center){Bi(icon,size=AppIcons.Size20)}
        Column(Modifier.weight(1f)){Bt(title,size=AppTypography.Size13,semi=true);if(subtitle.isNotBlank())Bt(subtitle,size=AppTypography.Size11,tint=bp.muted)}
        Bi("next",size=AppIcons.Size16,tint=bp.muted)
    }}
}
@Composable fun BioTabs(options:List<Pair<String,String>>,selected:String,onSelect:(String)->Unit) {
    Row(Modifier.fillMaxWidth().background(lerp(bp.bg,bp.accent,.4f),RoundedCornerShape(AppDimensions.Dp15)).border(AppDimensions.Dp1,AppColors.White.copy(alpha=AppOpacity.Value_5),RoundedCornerShape(AppDimensions.Dp15)).padding(horizontal = AppDimensions.Dp5, vertical = AppDimensions.Dp3),horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp3)){
        options.forEach{(id,label)->Box(Modifier.weight(1f).heightIn(min=AppDimensions.Dp48).background(if(id==selected)bp.surface else AppColors.Transparent,RoundedCornerShape(AppDimensions.Dp11)).semantics{this.selected=id==selected}.clickable(role=Role.Tab){onSelect(id)}.padding(horizontal=AppDimensions.Dp6,vertical=AppDimensions.Dp9),contentAlignment=Alignment.Center){Bt(label,size=AppTypography.Size12,tint=if(id==selected)bp.deep else bp.muted,align=TextAlign.Center,maxLines=1)}}
    }
}
@Composable fun BioMetrics(metrics:List<Pair<String,String>>,soft:Boolean=false) {
    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min),horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp8)){
        metrics.forEach{(label,value)->BioCard(Modifier.weight(1f).fillMaxHeight(),tint=if(soft)bp.soft else bp.surface,padding=AppSpacing.Value7,radius=AppRadii.Value18){Column(Modifier.fillMaxWidth().padding(vertical=AppDimensions.Dp5),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp5)){Bt(value,size=AppTypography.Size19,line=AppTypography.LineHeight29_45,mono=true);Bt(label,size=AppTypography.Size12,tint=bp.muted,align=TextAlign.Center)}}}
    }
}
@Composable fun BioGrid(count:Int,columns:Int=2,content:@Composable (Int,Modifier)->Unit) {
    Column(verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){
        (0 until count).chunked(columns).forEach{row->Row(Modifier.fillMaxWidth().height(IntrinsicSize.Max),horizontalArrangement=Arrangement.spacedBy(if(columns==3)AppDimensions.Dp9 else AppDimensions.Dp12)){
            repeat(columns){cell->if(cell<row.size)content(row[cell],Modifier.weight(1f).fillMaxHeight())else Spacer(Modifier.weight(1f))}
        }}
    }
}
@Composable fun BioEmpty(title:String,subtitle:String="",icon:String="garden",action:String="",onClick:()->Unit={}) {
    val outline=bp.control
    Column(Modifier.fillMaxWidth().background(AppColors.White.copy(alpha=AppOpacity.Value_3),bioShape(AppRadii.Value24)).drawBehind{drawRoundRect(outline,cornerRadius=androidx.compose.ui.geometry.CornerRadius(AppDimensions.Dp24.toPx()),style=androidx.compose.ui.graphics.drawscope.Stroke(AppDimensions.Dp1.toPx(),pathEffect=PathEffect.dashPathEffect(floatArrayOf(AppDimensions.Dp3.toPx(),AppDimensions.Dp3.toPx()))))}.padding(horizontal=AppDimensions.Dp21,vertical=AppDimensions.Dp29),horizontalAlignment=Alignment.CenterHorizontally){
        Bi(icon,size=AppIcons.Size44);Spacer(Modifier.height(AppDimensions.Dp15));Bt(title,size=AppTypography.Size18,line=AppTypography.LineHeight27,bold=true,align=TextAlign.Center)
        if(subtitle.isNotBlank()){Spacer(Modifier.height(AppDimensions.Dp7));Bt(subtitle,size=AppTypography.Size12,tint=bp.muted,align=TextAlign.Center)}
        if(action.isNotBlank()){Spacer(Modifier.height(AppDimensions.Dp20));BioButton(action,onClick)}
    }
}
@Composable fun BioDetails(title:String,initial:Boolean=LocalBioExpanded.current,content:@Composable ColumnScope.()->Unit) {
    var open by rememberSaveable(title){mutableStateOf(initial)}
    Column(Modifier.fillMaxWidth()){
        HorizontalDivider(color=bp.line,thickness=AppDimensions.Dp1)
        val disclosureState = if(open)stringResource(R.string.copy_da_mo_rong_c5e118)else stringResource(R.string.copy_da_thu_gon_9b8c90)
        Row(Modifier.fillMaxWidth().heightIn(min=AppDimensions.Dp60).padding(top=AppDimensions.Dp10).semantics{stateDescription=disclosureState}.clickable(role=Role.Button){open=!open},verticalAlignment=Alignment.CenterVertically){Bt(title,Modifier.weight(1f),size=AppTypography.Size13,semi=true);Bi("next",Modifier.rotate(if(open)270f else 90f),size=AppIcons.Size18,tint=bp.muted)}
        if(open)Column(Modifier.padding(top=AppDimensions.Dp10,bottom=AppDimensions.Dp4),verticalArrangement=Arrangement.spacedBy(AppDimensions.Dp12),content=content)
    }
}
@Composable fun BioFacts(values:List<Pair<String,String>>) {
    Column{values.forEachIndexed{i,(label,value)->if(i>0)HorizontalDivider(color=bp.line,thickness=AppDimensions.Dp1);Row(Modifier.fillMaxWidth().heightIn(min=AppDimensions.Dp39).padding(vertical=AppDimensions.Dp10),horizontalArrangement=Arrangement.spacedBy(AppDimensions.Dp12)){Bt(label,Modifier.weight(1f),size=AppTypography.Size12,tint=bp.muted);Bt(bioDisplayDate(value),Modifier.weight(1f),size=AppTypography.Size12,align=TextAlign.End)}}}
}
