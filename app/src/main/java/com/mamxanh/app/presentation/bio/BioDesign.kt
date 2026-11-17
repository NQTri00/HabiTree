package com.mamxanh.app.presentation.bio

import androidx.compose.foundation.*
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

internal data class BioPalette(val accent:Color,val soft:Color,val bg:Color,val deep:Color,val muted:Color,val line:Color,val hue:Float=339f) {
    val ink=Color(0xFF292E2D)
    val surface=Color(0xFFFFFDFD)
    val control get()=Color(androidx.core.graphics.ColorUtils.HSLToColor(floatArrayOf(hue,.09f,.52f)))
}
internal val LocalBioPalette=staticCompositionLocalOf{BioPalette(Color(0xFFF4C7D8),Color(0xFFF6E3EB),Color(0xFFEEE8EB),Color(0xFF71354F),Color(0xFF705F68),Color(0xFFD3C3CB))}
internal val LocalBioExpanded=staticCompositionLocalOf{false}
internal val LocalBioButtonSize=staticCompositionLocalOf{14}
internal val bp:BioPalette @Composable get()=LocalBioPalette.current
internal fun color(hex:String)=Color(android.graphics.Color.parseColor(hex))
@Composable internal fun BioTheme(world:BioWorld,treeId:String,content:@Composable () -> Unit) {
    val species=world.tree(treeId)?.s("species")?:"succulent"
    val p=world.data.o("palettes").o(species)
    val accent=color(p.s("accent","#cee3c4"));val soft=color(p.s("soft","#e7efdf"));val bg=color(p.s("bg","#e8ede4"));val deep=color(p.s("deep","#3e5c3c"));val muted=color(p.s("muted","#606b5c"))
    val hsv=FloatArray(3);android.graphics.Color.colorToHSV(deep.toArgb(),hsv)
    val hue=p.i("hue",139).toFloat();val line=Color(androidx.core.graphics.ColorUtils.HSLToColor(floatArrayOf(hue,.15f,.78f)))
    val palette=BioPalette(accent,soft,bg,deep,muted,line,hue)
    val monoFont=rememberBioMonoFont()
    CompositionLocalProvider(LocalBioPalette provides palette,LocalBioMonoFont provides monoFont) {
        MaterialTheme(colorScheme=lightColorScheme(primary=deep,onPrimary=Color.White,primaryContainer=accent,onPrimaryContainer=palette.ink,background=bg,surface=palette.surface,onSurface=palette.ink,outline=line),typography=GardenTypography,content=content)
    }
}
@Composable internal fun Bt(text:String,modifier:Modifier=Modifier,size:Int=14,tint:Color=bp.ink,bold:Boolean=false,semi:Boolean=false,mono:Boolean=false,line:Float=size*(if(size>=25)1.3f else 1.55f),spacing:Float=0f,align:TextAlign?=null,maxLines:Int=Int.MAX_VALUE) {
    val monoFont=LocalBioMonoFont.current
    Text(text,modifier,color=tint,fontSize=size.sp,lineHeight=line.sp,fontFamily=if(mono)monoFont.family else GardenFontFamily,fontWeight=if(bold)FontWeight.Bold else if(semi)FontWeight.SemiBold else FontWeight.Normal,letterSpacing=(spacing-if(mono&&!monoFont.exact)size*.05f else 0f).sp,textAlign=align,maxLines=maxLines,overflow=if(maxLines<Int.MAX_VALUE)TextOverflow.Ellipsis else TextOverflow.Clip,style=LocalTextStyle.current.copy(platformStyle=androidx.compose.ui.text.PlatformTextStyle(includeFontPadding=false)))
}
@Composable internal fun Bi(name:String,modifier:Modifier=Modifier,tint:Color=bp.deep,size:Int=20) {
    val context=LocalContext.current
    val resource=remember(name){context.resources.getIdentifier("bio_icon_$name","drawable",context.packageName)}
    if(resource!=0)Icon(painterResource(resource),null,modifier.size(size.dp),tint=tint)
}
@Composable internal fun BioPlant(species:String,modifier:Modifier=Modifier,dead:Boolean=false) {
    val context=LocalContext.current
    val resource=remember(species){context.resources.getIdentifier("bio_plant_$species","drawable",context.packageName)}
    if(resource!=0)Image(painterResource(resource),null,modifier.alpha(if(dead).55f else 1f),contentScale=ContentScale.Fit,colorFilter=if(dead)ColorFilter.colorMatrix(ColorMatrix().apply{setToSaturation(0f)})else null)
}
internal fun bioShape(radius:Int=22)=RoundedCornerShape(topStart=radius.dp,topEnd=(radius/2).dp,bottomEnd=radius.dp,bottomStart=radius.dp)
internal fun bioHeroBrush(p:BioPalette)=object:ShaderBrush(){
    override fun createShader(size:androidx.compose.ui.geometry.Size):Shader{
        val dx=.5735764f;val dy=.8191521f
        val half=(size.width*dx+size.height*dy)/2f
        val center=androidx.compose.ui.geometry.Offset(size.width/2f,size.height/2f)
        val delta=androidx.compose.ui.geometry.Offset(dx*half,dy*half)
        return LinearGradientShader(center-delta,center+delta,listOf(Color.White.copy(alpha=.6f),p.soft,lerp(p.soft,p.accent,.6f)),listOf(0f,.46f,1f))
    }
}
internal fun Modifier.bioLift(shape:Shape,tint:Color)=drawBehind{
    val outline=shape.createOutline(size,layoutDirection,this)
    val path=android.graphics.Path()
    when(outline){
        is Outline.Rounded->{val r=outline.roundRect;path.addRoundRect(android.graphics.RectF(r.left,r.top,r.right,r.bottom),floatArrayOf(r.topLeftCornerRadius.x,r.topLeftCornerRadius.y,r.topRightCornerRadius.x,r.topRightCornerRadius.y,r.bottomRightCornerRadius.x,r.bottomRightCornerRadius.y,r.bottomLeftCornerRadius.x,r.bottomLeftCornerRadius.y),android.graphics.Path.Direction.CW)}
        is Outline.Rectangle->path.addRect(outline.rect.left,outline.rect.top,outline.rect.right,outline.rect.bottom,android.graphics.Path.Direction.CW)
        is Outline.Generic->path.set(outline.path.asAndroidPath())
    }
    drawIntoCanvas{canvas->
        val paint=android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)
        paint.color=tint.copy(alpha=.035f).toArgb()
        canvas.nativeCanvas.save();canvas.nativeCanvas.translate(0f,3.dp.toPx());canvas.nativeCanvas.drawPath(path,paint);canvas.nativeCanvas.restore()
        paint.color=Color.White.toArgb();paint.setShadowLayer(12.dp.toPx(),0f,8.dp.toPx(),tint.copy(alpha=.08f).toArgb())
        canvas.nativeCanvas.drawPath(path,paint)
    }
}
@Composable internal fun BioCard(modifier:Modifier=Modifier,tint:Color=bp.surface,padding:Int=18,radius:Int=22,onClick:(()->Unit)?=null,borderColor:Color?=null,content:@Composable ColumnScope.()->Unit) {
    val shape=bioShape(radius)
    Column(modifier.bioLift(shape,bp.deep).background(tint,shape).border(1.dp,borderColor?:Color.White.copy(alpha=.8f),shape).then(if(onClick!=null)Modifier.clickable(onClick=onClick)else Modifier).padding((padding+1).dp),content=content)
}
@Composable internal fun BioButton(label:String,onClick:()->Unit,modifier:Modifier=Modifier,icon:String="",secondary:Boolean=false,enabled:Boolean=true,danger:Boolean=false,labelSize:Int=LocalBioButtonSize.current) {
    val shape=bioShape(14);val fill=if(danger)Color(0xFFF7DFDF)else if(secondary)Color.White.copy(alpha=.8f) else bp.accent
    Row(modifier.heightIn(min=48.dp).alpha(if(enabled)1f else .48f).shadow(if(enabled)2.dp else 0.dp,shape,ambientColor=bp.deep.copy(alpha=.08f),spotColor=bp.deep.copy(alpha=.08f)).background(fill,shape).border(1.dp,if(danger)Color(0xFFE2B8B8)else if(secondary)bp.line else lerp(bp.accent,bp.deep,.25f),shape).clickable(enabled=enabled,role=Role.Button,onClick=onClick).padding(horizontal=if(LocalBioButtonSize.current==13)8.dp else if(LocalConfiguration.current.screenWidthDp<359)10.dp else 18.dp,vertical=10.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.Center) {
        if(icon.isNotBlank()){Bi(icon,tint=if(danger)Color(0xFF8C363B)else bp.ink,size=18);Spacer(Modifier.width(8.dp))}
        Bt(label,size=labelSize,semi=true,tint=if(danger)Color(0xFF8C363B)else bp.ink,align=TextAlign.Center)
    }
}
@Composable internal fun BioIconButton(icon:String,onClick:()->Unit,modifier:Modifier=Modifier,boxed:Boolean=false,enabled:Boolean=true,iconSize:Int=20,tint:Color=bp.ink,label:String=when(icon){"close"->"Đóng";"back"->"Quay lại";"next"->"Tiếp theo";"more"->"Thêm chức năng";"plus"->"Thêm";"bag"->"Mở túi đồ";"settings"->"Tùy chọn";"refresh"->"Làm mới";else->icon}) {
    Box(modifier.size(48.dp).alpha(if(enabled)1f else .45f).then(if(boxed)Modifier.background(bp.surface,bioShape(12)).border(1.dp,bp.line,bioShape(12))else Modifier).semantics{contentDescription=label}.clickable(enabled=enabled,role=Role.Button,onClick=onClick),contentAlignment=Alignment.Center){Bi(icon,size=iconSize,tint=tint)}
}
@Composable internal fun BioTextAction(label:String,onClick:()->Unit,icon:String="",tint:Color=bp.deep,modifier:Modifier=Modifier,iconFirst:Boolean=false) {
    Row(modifier.heightIn(min=48.dp).widthIn(min=48.dp).clickable(onClick=onClick).padding(horizontal=2.dp,vertical=8.dp),horizontalArrangement=Arrangement.Center,verticalAlignment=Alignment.CenterVertically){if(iconFirst&&icon.isNotBlank()){Bi(icon,tint=tint,size=16);Spacer(Modifier.width(5.dp))};Bt(label,size=12,tint=tint,semi=true);if(!iconFirst&&icon.isNotBlank()){Spacer(Modifier.width(5.dp));Bi(icon,tint=tint,size=16)}}
}
@Composable internal fun BioTag(text:String,icon:String="",dot:Boolean=false,modifier:Modifier=Modifier) {
    Row(modifier.background(Color.White.copy(alpha=.65f),RoundedCornerShape(20.dp)).border(1.dp,Color.White.copy(alpha=.55f),RoundedCornerShape(20.dp)).padding(horizontal=9.dp,vertical=6.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(4.dp)){
        if(dot)Box(Modifier.size(5.dp).background(bp.deep,RoundedCornerShape(50)))
        if(icon.isNotBlank())Bi(icon,size=12)
        Bt(text,size=11,tint=bp.deep,line=15.4f)
    }
}
@Composable internal fun BioProgress(value:Float,modifier:Modifier=Modifier,white:Boolean=false) {
    Box(modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(4.dp)).background(if(white)Color.White.copy(alpha=.65f)else Color(0xFFD8DFCE))){Box(Modifier.fillMaxHeight().fillMaxWidth(value.coerceIn(0f,1f)).background(bp.deep))}
}
@Composable internal fun BioHeader(title:String,coins:Int,primary:Boolean,onBack:()->Unit,onMore:()->Unit,showWallet:Boolean=true) {
    Column(Modifier.fillMaxWidth()){
        Row(Modifier.fillMaxWidth().height(71.dp).padding(start=if(LocalConfiguration.current.screenWidthDp<359)14.dp else 20.dp,end=if(LocalConfiguration.current.screenWidthDp<359)14.dp else 20.dp,top=10.dp,bottom=8.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(4.dp)) {
            if(primary){Row(Modifier.weight(1f),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(7.dp)){Bi("leaf");Bt("mầm xanh",size=18,bold=true,spacing=-.7f);Box(Modifier.padding(bottom=15.dp).size(5.dp).background(bp.deep,RoundedCornerShape(50)))}}
            else{BioIconButton("back",onBack);Bt(title,Modifier.weight(1f),size=15,semi=true,maxLines=1)}
            if(showWallet)Row(Modifier.background(Color(0xFFFFFDF5),RoundedCornerShape(30.dp)).border(1.dp,Color(0xFFD8D0BD),RoundedCornerShape(30.dp)).padding(horizontal=10.dp,vertical=7.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(5.dp)){Bi("coin",size=15,tint=Color(0xFF765F27));Bt(coins.toString(),size=12,mono=true)}
            if(primary)BioIconButton("more",onMore)
        }
        Box(Modifier.padding(horizontal=24.dp).fillMaxWidth().height(1.dp).background(Brush.horizontalGradient(listOf(bp.line,Color.Transparent))))
    }
}
@Composable internal fun BioNav(route:String,onGo:(String)->Unit) {
    val largeText=LocalDensity.current.fontScale>1.2f
    val parent=when(route){"home","tasks","garden","shop","stats"->route;"task","task-new","focus","templates","insights"->"tasks";"tree","inventory","species","encyclopedia"->"garden";"more"->"";else->"stats"}
    Column(Modifier.fillMaxWidth().background(lerp(bp.bg,Color.White,.10f))){HorizontalDivider(color=bp.line,thickness=1.dp)
        Row(Modifier.fillMaxWidth().height(if(largeText)110.dp else 79.dp).padding(horizontal=if(LocalConfiguration.current.screenWidthDp<359)6.dp else 10.dp,vertical=10.dp),horizontalArrangement=Arrangement.spacedBy(4.dp)){
            listOf(Triple("home","home","Hôm nay"),Triple("tasks","tasks","Việc"),Triple("garden","garden","Vườn"),Triple("shop","shop","Tiệm"),Triple("stats","book","Nhật ký")).forEach{(r,icon,label)->
                val active=parent==r
                Column(Modifier.weight(1f).fillMaxHeight().background(if(active)Color.White.copy(alpha=.6f)else Color.Transparent,bioShape(15)).semantics{selected=active}.clickable(role=Role.Tab){onGo(r)}.padding(top=5.dp,bottom=4.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(4.dp)){
                    Bi(icon,size=21,tint=if(active)bp.deep else bp.muted);Bt(label,size=11,line=15.4f,tint=if(active)bp.deep else bp.muted,semi=active,maxLines=if(largeText)2 else 1,align=TextAlign.Center)
                    Box(Modifier.width(12.dp).height(2.dp).background(if(active)bp.deep else Color.Transparent,RoundedCornerShape(2.dp)))
                }
            }
        }
    }
}
@Composable internal fun BioIntro(title:String,eyebrow:String="",subtitle:String="",action:(@Composable () -> Unit)?=null) {
    Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.Top,horizontalArrangement=Arrangement.spacedBy(14.dp)){
        Column(Modifier.weight(1f)){
            if(eyebrow.isNotBlank()){Bt(eyebrow,size=10,tint=bp.muted,mono=true,spacing=1.8f);Spacer(Modifier.height(7.dp))}
            Bt(title,size=27,line=35.1f,bold=true,spacing=-1f)
            if(subtitle.isNotBlank()){Spacer(Modifier.height(6.dp));Bt(subtitle,size=12,tint=bp.muted)}
        }
        if(action!=null)Box(Modifier.padding(top=if(eyebrow.isNotBlank())17.dp else 0.dp)){action()}
    }
}
@Composable internal fun BioSection(title:String,action:String="",onClick:()->Unit={}) {
    Row(Modifier.fillMaxWidth().heightIn(min=if(action.isBlank())26.dp else 48.dp),verticalAlignment=Alignment.CenterVertically){Bt(title,Modifier.weight(1f),size=16,bold=true,spacing=-.35f);if(action.isNotBlank())Row(Modifier.heightIn(min=48.dp).clickable(role=Role.Button,onClick=onClick),verticalAlignment=Alignment.CenterVertically){Bt(action,size=12,tint=bp.deep,semi=true);Spacer(Modifier.width(5.dp));Bi("next",size=16)}}
}
@Composable internal fun BioLink(title:String,icon:String,onClick:()->Unit,subtitle:String="") {
    BioCard(Modifier.fillMaxWidth(),padding=14,radius=20,onClick=onClick){Row(Modifier.fillMaxWidth().heightIn(min=46.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)){
        Box(Modifier.size(38.dp).background(bp.soft,bioShape(12)),contentAlignment=Alignment.Center){Bi(icon,size=20)}
        Column(Modifier.weight(1f)){Bt(title,size=13,semi=true);if(subtitle.isNotBlank())Bt(subtitle,size=11,tint=bp.muted)}
        Bi("next",size=16,tint=bp.muted)
    }}
}
@Composable internal fun BioTabs(options:List<Pair<String,String>>,selected:String,onSelect:(String)->Unit) {
    Row(Modifier.fillMaxWidth().background(lerp(bp.bg,bp.accent,.4f),RoundedCornerShape(15.dp)).border(1.dp,Color.White.copy(alpha=.5f),RoundedCornerShape(15.dp)).padding(5.dp),horizontalArrangement=Arrangement.spacedBy(3.dp)){
        options.forEach{(id,label)->Box(Modifier.weight(1f).heightIn(min=48.dp).background(if(id==selected)bp.surface else Color.Transparent,RoundedCornerShape(11.dp)).semantics{this.selected=id==selected}.clickable(role=Role.Tab){onSelect(id)}.padding(horizontal=6.dp,vertical=9.dp),contentAlignment=Alignment.Center){Bt(label,size=12,tint=if(id==selected)bp.deep else bp.muted,align=TextAlign.Center,maxLines=1)}}
    }
}
@Composable internal fun BioChips(options:List<Pair<String,String>>,selected:String,onSelect:(String)->Unit) {
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(top=2.dp,bottom=4.dp),horizontalArrangement=Arrangement.spacedBy(8.dp)){
        options.forEach{(id,label)->Box(Modifier.heightIn(min=48.dp).background(if(id==selected)bp.accent else Color.White.copy(alpha=.55f),bioShape(13)).border(1.dp,if(id==selected)bp.control else bp.line,bioShape(13)).semantics{this.selected=id==selected}.clickable(role=Role.RadioButton){onSelect(id)}.padding(horizontal=12.dp,vertical=9.dp),contentAlignment=Alignment.Center){Bt(label,size=12,maxLines=1)}}
    }
}
@Composable internal fun BioMetrics(metrics:List<Pair<String,String>>,soft:Boolean=false) {
    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min),horizontalArrangement=Arrangement.spacedBy(8.dp)){
        metrics.forEach{(label,value)->BioCard(Modifier.weight(1f).fillMaxHeight(),tint=if(soft)bp.soft else bp.surface,padding=7,radius=18){Column(Modifier.fillMaxWidth().padding(vertical=5.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(5.dp)){Bt(value,size=19,line=29.45f,mono=true);Bt(label,size=12,tint=bp.muted,align=TextAlign.Center)}}}
    }
}
@Composable internal fun BioGrid(count:Int,columns:Int=2,content:@Composable (Int,Modifier)->Unit) {
    Column(verticalArrangement=Arrangement.spacedBy(12.dp)){
        (0 until count).chunked(columns).forEach{row->Row(Modifier.fillMaxWidth().height(IntrinsicSize.Max),horizontalArrangement=Arrangement.spacedBy(if(columns==3)9.dp else 12.dp)){
            repeat(columns){cell->if(cell<row.size)content(row[cell],Modifier.weight(1f).fillMaxHeight())else Spacer(Modifier.weight(1f))}
        }}
    }
}
@Composable internal fun BioEmpty(title:String,subtitle:String="",icon:String="garden",action:String="",onClick:()->Unit={}) {
    val outline=bp.control
    Column(Modifier.fillMaxWidth().background(Color.White.copy(alpha=.3f),bioShape(24)).drawBehind{drawRoundRect(outline,cornerRadius=androidx.compose.ui.geometry.CornerRadius(24.dp.toPx()),style=androidx.compose.ui.graphics.drawscope.Stroke(1.dp.toPx(),pathEffect=PathEffect.dashPathEffect(floatArrayOf(3.dp.toPx(),3.dp.toPx()))))}.padding(horizontal=21.dp,vertical=29.dp),horizontalAlignment=Alignment.CenterHorizontally){
        Bi(icon,size=44);Spacer(Modifier.height(15.dp));Bt(title,size=18,line=27f,bold=true,align=TextAlign.Center)
        if(subtitle.isNotBlank()){Spacer(Modifier.height(7.dp));Bt(subtitle,size=12,tint=bp.muted,align=TextAlign.Center)}
        if(action.isNotBlank()){Spacer(Modifier.height(20.dp));BioButton(action,onClick)}
    }
}
@Composable internal fun BioDetails(title:String,initial:Boolean=LocalBioExpanded.current,content:@Composable ColumnScope.()->Unit) {
    var open by rememberSaveable(title){mutableStateOf(initial)}
    Column(Modifier.fillMaxWidth()){
        HorizontalDivider(color=bp.line,thickness=1.dp)
        Row(Modifier.fillMaxWidth().heightIn(min=60.dp).padding(top=10.dp).semantics{stateDescription=if(open)"Đã mở rộng"else "Đã thu gọn"}.clickable(role=Role.Button){open=!open},verticalAlignment=Alignment.CenterVertically){Bt(title,Modifier.weight(1f),size=13,semi=true);Bi("next",Modifier.rotate(if(open)270f else 90f),size=18,tint=bp.muted)}
        if(open)Column(Modifier.padding(top=10.dp,bottom=4.dp),verticalArrangement=Arrangement.spacedBy(12.dp),content=content)
    }
}
@Composable internal fun BioFacts(values:List<Pair<String,String>>) {
    Column{values.forEachIndexed{i,(label,value)->if(i>0)HorizontalDivider(color=bp.line,thickness=1.dp);Row(Modifier.fillMaxWidth().heightIn(min=39.dp).padding(vertical=10.dp),horizontalArrangement=Arrangement.spacedBy(12.dp)){Bt(label,Modifier.weight(1f),size=12,tint=bp.muted);Bt(bioDisplayDate(value),Modifier.weight(1f),size=12,align=TextAlign.End)}}}
}
