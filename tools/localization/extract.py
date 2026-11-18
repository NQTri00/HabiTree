"""Migrate literal copy while preserving Kotlin expressions and category storage keys."""
import json,re
from catalog import ROOT, key_for, source_files, load_catalog
from generate import enum_name
from kotlin_literals import literals,has_copy

catalog=load_catalog()

def rewrite(source):
    edits=[]
    for literal in literals(source):
        args=[rewrite(argument) for argument in literal.arguments]
        raw=source[literal.start:literal.end]
        if re.search(r'@Preview\([^)]*$',source[max(0,literal.start-300):literal.start]):continue
        if has_copy(literal):
            key=key_for(literal.template)
            if key not in catalog:raise ValueError(f'Copy missing from catalog: {literal.template}')
            accessor='canonical' if catalog[key].get('canonical') else 'text'
            replacement='AppText.'+accessor+'(TextKey.'+enum_name(key)
            if args:replacement+=','+','.join(args)
            replacement+=')'
            edits.append((literal.start,literal.end,replacement))
        elif args!=literal.arguments:
            replacement=raw
            for old,new in zip(literal.arguments,args):
                if old!=new:replacement=replacement.replace('${'+old+'}','${'+new+'}',1)
            edits.append((literal.start,literal.end,replacement))
    for start,end,replacement in reversed(edits):source=source[:start]+replacement+source[end:]
    return source

changed=[]
for file in source_files():
    if 'Preview' in file.name:continue
    before=file.read_text(encoding='utf-8-sig');after=rewrite(before)
    if after==before:continue
    for imported in ['AppText','TextKey']:
        line='import com.mamxanh.shared.localization.'+imported
        if line not in after:after=re.sub(r'(package [^\n]+\n)',r'\1\n'+line+'\n',after,count=1)
    file.write_text(after,encoding='utf-8');changed.append(file.relative_to(ROOT).as_posix())
print(json.dumps({'changed_files':len(changed),'files':changed},indent=2))
