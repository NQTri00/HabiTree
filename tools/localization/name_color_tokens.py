"""Give illustration colors stable names and avoid case-only JVM getter clashes."""
import re
from catalog import ROOT
names={name:'Illustration'+name.title() for name in ['bark','bronze','dark','fog','green','light','outline']}
for file in ROOT.glob('*/src/main/**/*.kt'):
    text=file.read_text(encoding='utf-8-sig');before=text
    for old,new in names.items():
        text=re.sub(r'\bAppColors\.'+old+r'\b','AppColors.'+new,text)
        if file.name=='AppColors.kt':text=re.sub(r'\bval '+old+r'\b','val '+new,text)
    if text!=before:file.write_text(text,encoding='utf-8')
