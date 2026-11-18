import json
from catalog import ROOT, key_for, inventory, load_catalog, write_catalog

inventory()
catalog=load_catalog()
data=json.loads((ROOT/'app/src/main/assets/bio_reference.json').read_text(encoding='utf-8'))
values=[]
for palette in data['palettes'].values():values.append(palette['name'])
for item in data['catalog']['species']+data['catalog']['cosmetics']:
    values.extend(item.get(field,'') for field in ['name','description'])
for item in data['catalog']['templates']:values.extend(item.get(field,'') for field in ['title','effort'])
for item in data.get('records',[]):values.append(item.get('status',''))
for item in data.get('goals',[]):values.append(item.get('title',''))
for item in data['state'].get('memories',[]):values.append(item.get('title',''))
for value in values:
    if value and key_for(value) not in catalog:catalog[key_for(value)]={'vi':value,'en':None,'canonical':False,'source':'app/src/main/assets/bio_reference.json'}
catalog['calendar_rest_marker']={'vi':'N','en':'R','canonical':False,'source':'calendar status marker'}
for index in [969,971]:catalog[list(catalog)[index]]['canonical']=True
for index,arg,singular in [(182,1,'{0}/{1} task done'),(249,1,'{0}: {1} task completed'),(416,0,'{0} day'),(643,1,'{0}/{1} required task done'),(647,1,'Completed {0} / {1} task'),(811,1,'{0}: {1} task')]:
    item=catalog[list(catalog)[index]];item['pluralArgument']=arg;item['en_one']=singular
write_catalog(catalog)
for index,(key,item) in enumerate(catalog.items()):
    if not item.get('en'):print(f'{index} {item["vi"]}')
