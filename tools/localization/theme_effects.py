from catalog import ROOT
file=ROOT/'app/src/main/java/com/mamxanh/app/presentation/bio/BioDesign.kt'
text=file.read_text(encoding='utf-8')
text=text.replace('import com.mamxanh.app.core.components.design.AppColors','import com.mamxanh.app.core.components.design.AppThemeEffects\nimport com.mamxanh.app.core.components.design.AppColors',1)
for old,new in {
    'val hue:Float=339f':'val hue:Float=AppThemeEffects.DEFAULT_HUE',
    'floatArrayOf(hue,.09f,.52f)':'floatArrayOf(hue,AppThemeEffects.CONTROL_SATURATION,AppThemeEffects.CONTROL_LIGHTNESS)',
    'floatArrayOf(hue,.15f,.78f)':'floatArrayOf(hue,AppThemeEffects.OUTLINE_SATURATION,AppThemeEffects.OUTLINE_LIGHTNESS)',
    'p.i("hue",139)':'p.i("hue",AppThemeEffects.FALLBACK_TREE_HUE)',
    'staticCompositionLocalOf{14}':'staticCompositionLocalOf{AppTypography.SIZE_14}',
    'if(size>=25)1.3f else 1.55f':'if(size>=AppThemeEffects.LARGE_TEXT_THRESHOLD)AppThemeEffects.LARGE_LINE_HEIGHT else AppThemeEffects.BODY_LINE_HEIGHT',
    'size*.05f':'size*AppThemeEffects.MONOSPACE_TRACKING_CORRECTION',
    'lerp(p.soft,p.accent,.6f)':'lerp(p.soft,p.accent,AppThemeEffects.HERO_BLEND)',
    'lerp(bp.accent,bp.deep,.25f)':'lerp(bp.accent,bp.deep,AppThemeEffects.BUTTON_OUTLINE_BLEND)',
    'screenWidthDp<359':'screenWidthDp<AppThemeEffects.COMPACT_WIDTH',
}.items():text=text.replace(old,new)
file.write_text(text,encoding='utf-8')
