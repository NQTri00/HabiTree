import json
from catalog import ROOT,CATALOG
from kotlin_literals import VIETNAMESE

data=json.loads((ROOT/'app/src/main/assets/bio_reference.json').read_text(encoding='utf-8'))
known={item['vi'] for item in json.loads(CATALOG.read_text(encoding='utf-8')).values()}
def walk(value,path=''):
    if isinstance(value,dict):
        for key,item in value.items():walk(item,path+'/'+key)
    elif isinstance(value,list):
        for index,item in enumerate(value):walk(item,path+'/'+str(index))
    elif isinstance(value,str) and value not in known and VIETNAMESE.search(value):print(path,repr(value))
walk(data)
