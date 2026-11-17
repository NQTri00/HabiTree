"""Translate legacy disposition labels at display time without rewriting stored events."""
import json
from catalog import CATALOG
from generate import generate

catalog=json.loads(CATALOG.read_text(encoding='utf-8'))
for key,vi,en in [
    ('history_tree_sold','Đã bán cây · {0}','Tree sold · {0}'),
    ('history_tree_removed','Đã dọn cây · {0}','Tree cleared · {0}'),
]:
    catalog[key]={'vi':vi,'en':en,'canonical':False,'source':'legacy tree disposition history'}
CATALOG.write_text(json.dumps(catalog,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
generate()
