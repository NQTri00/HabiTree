from pathlib import Path
from collections import Counter
import json,sys
from kotlin_literals import literals,has_copy

ROOT=Path(__file__).resolve().parents[2]
files=[];entries=[]
for module in ['app','core-ui','home','tasks','garden','shop','tree','stats','inventory','data','shared']:
    for file in (ROOT/module/'src').rglob('*.kt'):
        relative=file.relative_to(ROOT).as_posix()
        if any(part in relative for part in ['/build/','/commonTest/','/test/','/androidTest/','/previews/']):continue
        text=file.read_text(encoding='utf-8-sig')
        found=[value for value in literals(text) if has_copy(value)]
        if found:files.append((relative,len(found)))
        for value in found:
            entries.append({'file':relative,'line':text.count('\n',0,value.start)+1,'start':value.start,'end':value.end,'text':value.template,'arguments':value.arguments,'raw':value.raw})
out=ROOT/'docs/localization';out.mkdir(parents=True,exist_ok=True)
(out/'inventory.json').write_text(json.dumps(entries,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
print(json.dumps({'files':len(files),'occurrences':len(entries),'unique':len(set(e['text'] for e in entries)),'by_module':dict(Counter(e['file'].split('/')[0] for e in entries))},indent=2))
