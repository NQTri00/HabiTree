"""Explicitly localize labels while retaining persisted categories and user content."""
import json,re
from catalog import ROOT,CATALOG,source_files
from generate import enum_name

catalog=json.loads(CATALOG.read_text(encoding='utf-8'))
for item in catalog.values():
    if item.get('canonical'):item.setdefault('storageValue',item['vi'])
CATALOG.write_text(json.dumps(catalog,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')

for file in source_files():
    text=file.read_text(encoding='utf-8-sig');before=text
    for key,item in catalog.items():
        if item.get('canonical'):text=text.replace('AppText.text(TextKey.'+enum_name(key)+')','AppText.canonical(TextKey.'+enum_name(key)+')')
    text=re.sub(r'\.contains\(AppText\.text\((TextKey\.\w+)\)',r'.contains(AppText.canonical(\1)',text)
    if '/presentation/' in file.as_posix():
        text=re.sub(r'\bBt\((\w+\.s\("category"\))',r'Bt(AppText.category(\1)',text)
        text=re.sub(r'\bText\((\w+\.category)\b',r'Text(AppText.category(\1)',text)
        text=text.replace('task.s("category")+(if(timer)','AppText.category(task.s("category"))+(if(timer)')
        if file.name in ['BioForms.kt','AddTaskDialog.kt']:text=text.replace('.map{it to it}', '.map{it to AppText.category(it)}')
        if file.name=='BioOnboarding.kt':
            text=text.replace('world.species(id).s("name")else id','world.species(id).s("name")else AppText.category(id)')
            text=text.replace('ui.selectedTemplates.size,ui.selectedCategory)', 'ui.selectedTemplates.size,AppText.category(ui.selectedCategory))')
        if file.name=='BioSecondaryScreens.kt':
            for name in ['consistent','improve']:
                pattern=r'review\.s\("'+name+r'",AppText\.text\((TextKey\.\w+)\)\)'
                text=re.sub(pattern,lambda match:'AppText.category('+match.group()+')',text)
        if file.name=='BioNativeHost.kt':text=text.replace('"title" to it.title,"type" to it.type','"title" to AppText.history(it.title),"type" to it.type')
    if text!=before:file.write_text(text,encoding='utf-8')
print('Updated category/history/catalog presentation boundaries without rewriting saved content.')
