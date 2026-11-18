"""One public app name in both languages; preserve package IDs and stored data."""
import json
from catalog import ROOT, load_catalog, write_catalog
from generate import generate

catalog=load_catalog()
for key,item in catalog.items():
    if item['vi'].casefold()=='mầm xanh':
        item['vi']='HabiTree'
        item['en']='HabiTree'
        print('Unified brand resource:',key)
write_catalog(catalog)
file=ROOT/'core-ui/src/main/res/values/app_language.xml'
text=file.read_text(encoding='utf-8').replace('>Mầm Xanh</string>','>HabiTree</string>')
file.write_text(text,encoding='utf-8')
generate()
