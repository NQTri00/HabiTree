"""Centralize custom shape arguments and theme effects missed by unit extraction."""
import re
from catalog import ROOT

package='com.mamxanh.app.core.components.design'
directory=ROOT/'core-ui/src/main/java/com/mamxanh/app/core/components/design'
radius_file=directory/'AppRadii.kt'
radius=radius_file.read_text(encoding='utf-8');additions={}
for file in ROOT.glob('*/src/main/**/*.kt'):
    if 'Preview' in file.name or '/design/' in file.as_posix():continue
    text=file.read_text(encoding='utf-8-sig');before=text
    def replace(match):
        value=match.group(2);name='Value'+value
        if not re.search(r'\b'+name+r'\b',radius):additions[name]=value
        return match.group(1)+'AppRadii.'+name+')'
    text=re.sub(r'\b(bioShape\()(\d+)\)',replace,text)
    if text!=before:
        if 'import '+package+'.AppRadii' not in text:
            text=re.sub(r'(package [^\n]+\n)',r'\1\nimport '+package+'.AppRadii\n',text,count=1)
        file.write_text(text,encoding='utf-8')
if additions:
    radius=radius.rstrip().removesuffix('}')+'\n'+''.join('    const val '+name+' = '+value+'\n' for name,value in sorted(additions.items()))+'}\n'
    radius_file.write_text(radius,encoding='utf-8')
