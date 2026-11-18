"""Keep English task actions on one line at the verified 320dp phone width."""
import json
from catalog import load_catalog, write_catalog
from generate import generate

catalog=load_catalog()
revisions={'Task templates':'Templates','Choose manageable tasks':'Choose your tasks'}
for key,item in catalog.items():
    if item['en'] in revisions:
        item['en']=revisions[item['en']]
        print(key,'->',item['en'])
write_catalog(catalog)
generate()
