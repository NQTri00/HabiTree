"""Normalize EOF whitespace in newly added text files before the checkpoint."""
import subprocess
from catalog import ROOT
paths=subprocess.check_output(['git','diff','--cached','--name-only','--diff-filter=A'],cwd=ROOT,text=True).splitlines()
for relative in paths:
    file=ROOT/relative
    if file.suffix not in {'.kt','.py','.json','.xml','.md','.cjs','.ps1'}:continue
    text=file.read_text(encoding='utf-8-sig')
    normalized=text.rstrip()+'\n'
    if normalized!=text:file.write_text(normalized,encoding='utf-8')
