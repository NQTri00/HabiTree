"""Translate legacy disposition labels at display time without rewriting stored events."""
import json
from catalog import load_catalog, write_catalog
from generate import generate

catalog=load_catalog()
for key,vi,en in [
    ('history_tree_sold','Đã bán cây · {0}','Tree sold · {0}'),
    ('history_tree_removed','Đã dọn cây · {0}','Tree cleared · {0}'),
]:
    catalog[key]={'vi':vi,'en':en,'canonical':False,'source':'legacy tree disposition history'}
write_catalog(catalog)
generate()
