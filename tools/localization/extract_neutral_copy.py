"""Move language-neutral attribution and date hints into the same copy resource source."""
import json
from catalog import ROOT,CATALOG
from kotlin_literals import literals
from generate import generate

catalog=json.loads(CATALOG.read_text(encoding='utf-8'))
targets={
    'app/src/main/java/com/mamxanh/app/presentation/ui/DataSettingsScreen.kt':('legal_technology_credits',lambda value:value.startswith('Kotlin · AndroidX')),
    'tasks/src/main/java/com/mamxanh/app/feature/tasks/presentation/EngagementScreens.kt':('date_iso_placeholder',lambda value:value=='YYYY-MM-DD'),
}
for relative,(key,predicate) in targets.items():
    file=ROOT/relative;text=file.read_text(encoding='utf-8-sig');edits=[]
    for literal in literals(text):
        if predicate(literal.template):
            catalog[key]={'vi':literal.template,'en':literal.template,'canonical':False,'source':relative}
            name=''.join(part.title() for part in key.split('_'))
            edits.append((literal.start,literal.end,'AppText.text(TextKey.'+name+')'))
    for start,end,value in reversed(edits):text=text[:start]+value+text[end:]
    if edits:file.write_text(text,encoding='utf-8')
CATALOG.write_text(json.dumps(catalog,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
generate()
