import json,sys,re
from pathlib import Path
from catalog import CATALOG

start=int(sys.argv[1]);file=Path(sys.argv[2]);translations=json.loads(file.read_text(encoding='utf-8'))
catalog=json.loads(CATALOG.read_text(encoding='utf-8'));keys=list(catalog)
entries=enumerate(translations) if isinstance(translations,list) else ((int(index),value) for index,value in translations.items())
for offset,english in entries:
    key=keys[start+offset];item=catalog[key]
    if not english:raise SystemExit(f'Empty translation at {start+offset}')
    if sorted(re.findall(r'\{\d+\}',item['vi']))!=sorted(re.findall(r'\{\d+\}',english)):raise SystemExit(f'Placeholder mismatch at {start+offset}: {key}')
    if item.get('en') not in (None,english):raise SystemExit(f'Refusing to overwrite reviewed translation: {key}')
    item['en']=english
CATALOG.write_text(json.dumps(catalog,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
print(f'Added {len(translations)} translations. Remaining: {sum(not v.get("en") for v in catalog.values())}')
