"""Build the stable source-copy inventory and typed catalog from Kotlin literals."""
from pathlib import Path
import hashlib,json,re,unicodedata
import yaml
from kotlin_literals import literals,has_copy

ROOT=Path(__file__).resolve().parents[2]
CATALOG_DIR = ROOT / 'localization/strings'
FEATURES = {'home', 'tasks', 'garden', 'catalog', 'tree', 'shop', 'inventory', 'stats', 'settings'}


class CatalogLoader(yaml.SafeLoader):
    """Reject duplicate keys before a YAML mapping can overwrite them."""

    def construct_mapping(self, node, deep=False):
        mapping = {}
        for key_node, value_node in node.value:
            key = self.construct_object(key_node, deep=deep)
            if key in mapping:
                raise ValueError(f'Duplicate YAML key {key!r} at {key_node.start_mark}')
            mapping[key] = self.construct_object(value_node, deep=deep)
        return mapping


def load_catalog():
    entries = {}
    for path in sorted(CATALOG_DIR.glob('*.yaml')):
        records = yaml.load(path.read_text(encoding='utf-8'), Loader=CatalogLoader)
        if not isinstance(records, dict):
            raise ValueError(f'Expected a mapping in {path}')
        for key, record in records.items():
            if not re.fullmatch(r'[a-z][a-z0-9_]*', key) or key in entries:
                raise ValueError(f'Invalid or duplicate copy key {key!r} in {path}')
            entry = dict(record)
            order = entry.pop('order')
            if type(order) is not int or order < 0:
                raise ValueError(f'Invalid copy order for {key} in {path}')
            entries[key] = (order, entry)
    if not entries or len({order for order, _ in entries.values()}) != len(entries):
        raise ValueError('Catalog is empty or has duplicate copy order values')
    return {key: entry for key, (_, entry) in sorted(entries.items(), key=lambda pair: pair[1][0])}


def write_catalog(catalog):
    groups = {}
    for order, (key, entry) in enumerate(catalog.items()):
        module = entry.get('source', '').split('/')[0]
        owner = module if module in FEATURES else 'common'
        groups.setdefault(owner, {})[key] = {'order': order, **entry}
    CATALOG_DIR.mkdir(parents=True, exist_ok=True)
    for owner, entries in groups.items():
        path = CATALOG_DIR / f'{owner}.yaml'
        path.write_text(yaml.safe_dump(entries, allow_unicode=True, sort_keys=False, width=110), encoding='utf-8')
CANONICAL={'Học tập','Sức khỏe','Nếp sống','Việc nhà','Cá nhân','Học','Khác'}

def key_for(value):
    slug=unicodedata.normalize('NFD',value.replace('Đ','D').replace('đ','d'))
    slug=''.join(c for c in slug if not unicodedata.combining(c))
    slug=re.sub(r'[^a-z0-9]+','_',slug.lower()).strip('_')[:52].rstrip('_')
    return 'copy_'+(slug or 'message')+'_'+hashlib.sha256(value.encode()).hexdigest()[:6]

def source_files():
    for module in ['app','core-ui','home','tasks','garden','shop','tree','stats','inventory','settings','data','shared']:
        roots = [ROOT / module / 'src']
        if module in FEATURES:
            roots.extend(ROOT / module / layer / 'src' for layer in ('domain', 'data'))
        for file in (path for source_root in roots for path in source_root.rglob('*.kt')):
            path=file.relative_to(ROOT).as_posix()
            if any(part in path for part in ['/build/','/commonTest/','/test/','/androidTest/','/previews/','/localization/']):continue
            yield file

def collect(source):
    for value in literals(source):
        if has_copy(value):yield value
        for argument in value.arguments:yield from collect(argument)

def inventory():
    catalog = load_catalog() if CATALOG_DIR.exists() else {}
    for file in source_files():
        text=file.read_text(encoding='utf-8-sig')
        for literal in collect(text):
            template=literal.template;key=key_for(template)
            if key not in catalog:
                catalog[key]={'vi':template,'en':None,'canonical':template in CANONICAL,'source':file.relative_to(ROOT).as_posix()}
    write_catalog(catalog)
    print(json.dumps({'entries':len(catalog),'untranslated':sum(item['en'] is None for item in catalog.values())}))

if __name__=='__main__':inventory()
