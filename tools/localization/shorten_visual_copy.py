"""Keep English task actions on one line at the verified 320dp phone width."""
import json
from catalog import CATALOG
from generate import generate

catalog=json.loads(CATALOG.read_text(encoding='utf-8'))
revisions={'Task templates':'Templates','Choose manageable tasks':'Choose your tasks'}
for key,item in catalog.items():
    if item['en'] in revisions:
        item['en']=revisions[item['en']]
        print(key,'->',item['en'])
CATALOG.write_text(json.dumps(catalog,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
generate()
