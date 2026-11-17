"""Inspect remaining locale-sensitive presentation and color migration fidelity."""
from pathlib import Path
import subprocess,re
from catalog import ROOT

for file in (ROOT/'app/src/main/java/com/mamxanh/app/presentation/bio').glob('*.kt'):
    for index,line in enumerate(file.read_text(encoding='utf-8').splitlines(),1):
        if any(value in line for value in ['Locale(', 'DateTimeFormatter', 'SimpleDateFormat', '"CN"', '"N"', '"T$']):
            print(f'{file.name}:{index}: {line[:1300]}')

tokens=(ROOT/'core-ui/src/main/java/com/mamxanh/app/presentation/ui/design/AppColors.kt').read_text(encoding='utf-8')
colors=dict(re.findall(r'val (\w+) = Color\(0x([A-Fa-f0-9]+)\)',tokens))
issues=[]
for file in ROOT.glob('*/src/main/**/*.kt'):
    relative=file.relative_to(ROOT).as_posix()
    if '/design/' in relative or 'Preview' in file.name:continue
    result=subprocess.run(['git','show','HEAD:'+relative],cwd=ROOT,capture_output=True)
    if result.returncode:continue
    original=result.stdout.decode('utf-8-sig')
    current=file.read_text(encoding='utf-8-sig')
    old=re.findall(r'\bColor\(\s*0x([A-Fa-f0-9]+)\s*\)',original)
    # Existing named Color constants (White etc.) have no numeric counterpart.
    # Compare the literal sequence, retaining colors deliberately left inside drawing branches.
    new=[]
    for match in re.finditer(r'AppColors\.(\w+)|\bColor\(\s*0x([A-Fa-f0-9]+)\s*\)',current):
        if match.group(2):new.append(match.group(2))
        elif match.group(1) in colors:new.append(colors[match.group(1)])
    if old and [v.upper() for v in old]!=[v.upper() for v in new]:
        issues.append(relative)
        print(relative, '\nold:',old,'\nnew:',new)
print('Color sequence mismatches:',issues)
