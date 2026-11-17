"""Preserve lexical boundaries when replacing a closing parenthesis with an identifier."""
import re
from catalog import ROOT
for file in ROOT.glob('*/src/main/**/*.kt'):
    text=file.read_text(encoding='utf-8-sig')
    updated=re.sub(r'\b(App(?:Colors|Dimensions|Typography|Spacing|Radii|Icons|Opacity)\.\w+?)else\b',r'\1 else',text)
    updated=updated.replace('androidx.compose.ui.graphics.AppColors.','AppColors.')
    if updated!=text:file.write_text(updated,encoding='utf-8')
