import json
from catalog import CATALOG
from generate import enum_name
for key,item in json.loads(CATALOG.read_text(encoding='utf-8')).items():
    if item.get('en_one'):print(enum_name(key),item['en'],item['en_one'],item['pluralArgument'])
