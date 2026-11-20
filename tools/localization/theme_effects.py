from catalog import ROOT
file=ROOT/'app/src/main/java/com/mamxanh/app/presentation/bio/BioDesign.kt'
text=file.read_text(encoding='utf-8')
text=text.replace('import com.mamxanh.app.core.components.design.AppColors','import com.mamxanh.app.core.components.design.AppThemeEffects\nimport com.mamxanh.app.core.components.design.AppColors',1)
for old,new in {
    'val hue:Float=339f':'val hue:Float=AppThemeEffects.DefaultHue',
    'floatArrayOf(hue,.09f,.52f)':'floatArrayOf(hue,AppThemeEffects.ControlSaturation,AppThemeEffects.ControlLightness)',
    'floatArrayOf(hue,.15f,.78f)':'floatArrayOf(hue,AppThemeEffects.OutlineSaturation,AppThemeEffects.OutlineLightness)',
    'p.i("hue",139)':'p.i("hue",AppThemeEffects.FallbackTreeHue)',
    'staticCompositionLocalOf{14}':'staticCompositionLocalOf{AppTypography.Size14}',
    'if(size>=25)1.3f else 1.55f':'if(size>=AppThemeEffects.LargeTextThreshold)AppThemeEffects.LargeLineHeight else AppThemeEffects.BodyLineHeight',
    'size*.05f':'size*AppThemeEffects.MonospaceTrackingCorrection',
    'lerp(p.soft,p.accent,.6f)':'lerp(p.soft,p.accent,AppThemeEffects.HeroBlend)',
    'lerp(bp.accent,bp.deep,.25f)':'lerp(bp.accent,bp.deep,AppThemeEffects.ButtonOutlineBlend)',
    'screenWidthDp<359':'screenWidthDp<AppThemeEffects.CompactWidth',
}.items():text=text.replace(old,new)
file.write_text(text,encoding='utf-8')
