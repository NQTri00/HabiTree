"""Inventory residual presentation literals for human review, excluding resource sources."""
import re
from catalog import source_files,ROOT
from kotlin_literals import literals

for file in source_files():
    if '/presentation/' not in file.as_posix() or 'Preview' in file.name:continue
    source=file.read_text(encoding='utf-8-sig')
    for value in literals(source):
        if not re.search(r'[A-Za-z]',value.template):continue
        prefix=source[max(0,value.start-55):value.start]
        if re.search(r'(?:\bBt|\bText|BioTag|BioButton|contentDescription\s*=|placeholder\s*=|label\s*=)\s*\(?\s*$',prefix):
            print(f'{file.relative_to(ROOT)}:{source.count(chr(10),0,value.start)+1}: {value.template}')
