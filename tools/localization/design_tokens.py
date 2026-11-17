"""Extract UI literals into shared, typed design tokens without changing values."""
from pathlib import Path
import re,json
from catalog import ROOT
from kotlin_literals import literals,_comment_end,_char_end

PACKAGE='com.mamxanh.app.presentation.ui.design'
TARGET=ROOT/'core-ui/src/main/java/com/mamxanh/app/presentation/ui/design'

def mask(source):
    chars=list(source);i=0
    strings={value.start:value.end for value in literals(source)}
    while i<len(source):
        if source.startswith(('//','/*'),i):end=_comment_end(source,i)
        elif source[i]=="'":end=_char_end(source,i)
        elif i in strings:end=strings[i]
        else:i+=1;continue
        for index in range(i,end):
            if chars[index]!='\n':chars[index]=' '
        i=end
    return ''.join(chars)

def suffix(value):
    return value.rstrip('f').replace('-','Minus').replace('.','_')

def files():
    for module in ['app','core-ui','home','tasks','garden','shop','tree','stats','inventory']:
        for file in (ROOT/module/'src/main').rglob('*.kt'):
            path=file.relative_to(ROOT).as_posix()
            if '/design/' in path or '/previews/' in path or 'Preview' in file.name:continue
            yield file

def migrate():
    if (TARGET/'AppColors.kt').exists():
        raise RuntimeError('One-time migration already applied. Edit existing tokens; do not overwrite them.')
    sources={file:file.read_text(encoding='utf-8-sig') for file in files()}
    colors={};hexes={};dimensions={};font_units={};numeric={'AppTypography':{},'AppSpacing':{},'AppRadii':{},'AppIcons':{},'AppOpacity':{},'AppDimensions':{}}
    known={'FF292E2D':'Ink','FFA63936':'Danger','FFFFFDFD':'Surface','FFF3E8C9':'RewardSurface'}
    for source in sources.values():
        for name,value in re.findall(r'val\s+(\w+)\s*=\s*Color\(0x([0-9a-fA-F]{6,8})\)',source):known.setdefault(value.upper(),name)
    changed={};counts={'colors':0,'dimensions':0,'typography':0,'component_values':0,'opacity':0}
    for file,source in sources.items():
        code=mask(source);edits=[];used=set()
        def add(match,replacement,category):
            edits.append((match.start(),match.end(),replacement));used.add(replacement.split('.')[0]);counts[category]+=1
        for match in re.finditer(r'\bColor\(\s*0x([0-9a-fA-F]{6,8})\s*\)',code):
            value=match.group(1).upper();name=known.get(value,'Swatch'+value)
            colors[name]='Color(0x'+value+')';add(match,'AppColors.'+name,'colors')
        for match in re.finditer(r'(?<![\w.])Color\.(White|Black|Transparent)\b',code):
            name=match.group(1);colors[name]='Color.'+name;add(match,'AppColors.'+name,'colors')
        for literal in literals(source):
            if literal.arguments or not re.fullmatch(r'#[0-9A-Fa-f]{6,8}',literal.template):continue
            name='Hex'+literal.template[1:].upper();hexes[name]=literal.template
            edits.append((literal.start,literal.end,'AppColors.'+name));used.add('AppColors');counts['colors']+=1
        unit_pattern=r'(?<![\w.])(?:\((-?\d+(?:\.\d+)?f?)\)|(-?\d+(?:\.\d+)?f?))\.(dp|sp)\b'
        for match in re.finditer(unit_pattern,code):
            value=match.group(1) or match.group(2);unit=match.group(3);name=('Dp' if unit=='dp' else 'Sp')+suffix(value)
            if unit=='dp':dimensions[name]=value;owner='AppDimensions';category='dimensions'
            else:font_units[name]=value;owner='AppTypography';category='typography'
            add(match,owner+'.'+name,category)
        # Discover the enclosing call for custom component parameters, including default values.
        names={};stack=[]
        parameter=re.compile(r'\b(size|iconSize|labelSize|padding|radius|line|spacing)(?:\s*:\s*(?:Int|Float))?\s*=\s*(-?(?:\d+(?:\.\d+)?|\.\d+)f?)(?![\w.])')
        matches=list(parameter.finditer(code));cursor=0
        for match in matches:
            while cursor<match.start():
                if code[cursor]=='(':
                    call=re.search(r'([\w.]+)\s*$',code[:cursor]);stack.append(call.group(1).split('.')[-1] if call else '')
                elif code[cursor]==')' and stack:stack.pop()
                cursor+=1
            arg,value=match.group(1),match.group(2);call=stack[-1] if stack else ''
            if code[match.end():].lstrip().startswith(('.','dp','sp')):continue
            if arg in ['line','spacing']:
                owner='AppTypography';name=('LineHeight' if arg=='line' else 'Tracking')+suffix(value)
                if not value.endswith('f'):value+='f'
            elif arg=='padding':owner='AppSpacing';name='Value'+suffix(value)
            elif arg=='radius':owner='AppRadii';name='Value'+suffix(value)
            elif arg=='iconSize' or call=='Bi':owner='AppIcons';name='Size'+suffix(value)
            elif arg=='labelSize' or call=='Bt':owner='AppTypography';name='Size'+suffix(value)
            else:owner='AppDimensions';name='Value'+suffix(value)
            numeric[owner][name]=value
            start=match.end()-len(match.group(2));edits.append((start,match.end(),owner+'.'+name));used.add(owner);counts['component_values']+=1
        opacity_ranges=[]
        for match in re.finditer(r'\.alpha\(',code):
            cursor=match.end();depth=1
            while cursor<len(code) and depth:
                if code[cursor]=='(':depth+=1
                elif code[cursor]==')':depth-=1
                cursor+=1
            opacity_ranges.append((match.end(),cursor-1))
        for match in re.finditer(r'\balpha\s*=\s*(-?(?:\d+(?:\.\d+)?|\.\d+)f)',code):opacity_ranges.append((match.start(1),match.end(1)))
        seen=set()
        for start,end in opacity_ranges:
            for match in re.finditer(r'(?<![\w.])(?:\d+(?:\.\d+)?|\.\d+)f\b',code[start:end]):
                begin,finish=start+match.start(),start+match.end()
                if (begin,finish) in seen:continue
                seen.add((begin,finish));value=match.group();name='Value'+suffix(value)
                numeric['AppOpacity'][name]=value;edits.append((begin,finish,'AppOpacity.'+name));used.add('AppOpacity');counts['opacity']+=1
        # Reject overlapping edits instead of risking a corrupt expression.
        edits.sort()
        for previous,current in zip(edits,edits[1:]):
            if previous[1]>current[0]:raise ValueError(f'Overlapping token edits in {file}: {previous} / {current}')
        for start,end,value in reversed(edits):source=source[:start]+value+source[end:]
        if edits:
            imports='\n'.join('import '+PACKAGE+'.'+name for name in sorted(used))
            source=re.sub(r'(package [^\n]+\n)',r'\1\n'+imports+'\n',source,count=1)
            changed[file]=source
    TARGET.mkdir(parents=True,exist_ok=True)
    header='// Shared design values. Extracted without changing the approved numeric values.\npackage '+PACKAGE+'\n\n'
    color_code=header+'import androidx.compose.ui.graphics.Color\n\nobject AppColors {\n'
    color_code+='\n'.join('    val '+name+' = '+value for name,value in sorted(colors.items()))+'\n'
    color_code+='\n'.join('    const val '+name+' = '+json.dumps(value) for name,value in sorted(hexes.items()))+'\n}\n'
    (TARGET/'AppColors.kt').write_text(color_code,encoding='utf-8')
    for owner in numeric:
        code=header
        if owner=='AppDimensions':code+='import androidx.compose.ui.unit.dp\n\n'
        if owner=='AppTypography':code+='import androidx.compose.ui.unit.sp\n\n'
        code+='object '+owner+' {\n'
        code+='\n'.join('    const val '+name+' = '+value for name,value in sorted(numeric[owner].items()))+'\n'
        if owner=='AppDimensions':code+='\n'.join('    val '+name+' = '+value+'.dp' for name,value in sorted(dimensions.items()))+'\n'
        if owner=='AppTypography':code+='\n'.join('    val '+name+' = '+value+'.sp' for name,value in sorted(font_units.items()))+'\n'
        code+='}\n';(TARGET/(owner+'.kt')).write_text(code,encoding='utf-8')
    for file,source in changed.items():file.write_text(source,encoding='utf-8')
    print(json.dumps({'files':len(changed),'replacements':counts,'palette_tokens':len(colors),'dimension_tokens':len(dimensions)},indent=2))

if __name__=='__main__':migrate()
