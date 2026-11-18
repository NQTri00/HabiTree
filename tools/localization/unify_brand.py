"""One public app name in both languages; preserve package IDs and stored data."""
import json
from catalog import ROOT,CATALOG
from generate import generate

catalog=json.loads(CATALOG.read_text(encoding='utf-8'))
for key,item in catalog.items():
    if item['vi'].casefold()=='mầm xanh':
        item['vi']='HabiTree'
        item['en']='HabiTree'
        print('Unified brand resource:',key)
CATALOG.write_text(json.dumps(catalog,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
file=ROOT/'core-ui/src/main/res/values/app_language.xml'
text=file.read_text(encoding='utf-8').replace('>Mầm Xanh</string>','>HabiTree</string>')
file.write_text(text,encoding='utf-8')
generate()
