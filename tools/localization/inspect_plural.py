import json
from catalog import load_catalog
from generate import enum_name
for key,item in load_catalog().items():
    if item.get('en_one'):print(enum_name(key),item['en'],item['en_one'],item['pluralArgument'])
