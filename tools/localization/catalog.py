"""Build the stable source-copy inventory and typed catalog from Kotlin literals."""
from pathlib import Path
import hashlib,json,re,unicodedata
from kotlin_literals import literals,has_copy

ROOT=Path(__file__).resolve().parents[2]
CATALOG=ROOT/'shared/src/commonMain/resources/i18n/messages.json'
CANONICAL={'Học tập','Sức khỏe','Nếp sống','Việc nhà','Cá nhân','Học','Khác'}

def key_for(value):
    slug=unicodedata.normalize('NFD',value.replace('Đ','D').replace('đ','d'))
    slug=''.join(c for c in slug if not unicodedata.combining(c))
    slug=re.sub(r'[^a-z0-9]+','_',slug.lower()).strip('_')[:52].rstrip('_')
    return 'copy_'+(slug or 'message')+'_'+hashlib.sha256(value.encode()).hexdigest()[:6]

def source_files():
    for module in ['app','core-ui','home','tasks','garden','shop','tree','stats','inventory','data','shared']:
        for file in (ROOT/module/'src').rglob('*.kt'):
            path=file.relative_to(ROOT).as_posix()
            if any(part in path for part in ['/build/','/commonTest/','/test/','/androidTest/','/previews/','/localization/']):continue
            yield file

def collect(source):
    for value in literals(source):
        if has_copy(value):yield value
        for argument in value.arguments:yield from collect(argument)

def inventory():
    catalog=json.loads(CATALOG.read_text(encoding='utf-8')) if CATALOG.exists() else {}
    for file in source_files():
        text=file.read_text(encoding='utf-8-sig')
        for literal in collect(text):
            template=literal.template;key=key_for(template)
            if key not in catalog:
                catalog[key]={'vi':template,'en':None,'canonical':template in CANONICAL,'source':file.relative_to(ROOT).as_posix()}
    CATALOG.parent.mkdir(parents=True,exist_ok=True)
    CATALOG.write_text(json.dumps(catalog,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
    print(json.dumps({'entries':len(catalog),'untranslated':sum(item['en'] is None for item in catalog.values())}))

if __name__=='__main__':inventory()
