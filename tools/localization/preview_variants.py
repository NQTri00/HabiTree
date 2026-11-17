"""Generate isolated English/Vietnamese Compose wrappers for real bilingual renders."""
import json,re
from catalog import ROOT

jobs=json.loads((ROOT/'docs/bio-parity/preview-jobs.json').read_text(encoding='utf-8'))
target=ROOT/'app/src/main/java/com/mamxanh/app/presentation/bio/previews/localization'
target.mkdir(parents=True,exist_ok=True)
for language in ['vi','en']:
    variants=[]
    for job in jobs:
        source=(ROOT/job['source']).read_text(encoding='utf-8-sig')
        name=job['preview']+language.title()
        source=source.replace(job['preview'],name)
        source=source.replace('@Preview(',f'@Preview(locale="{language}",')
        file=target/(name+'.kt')
        file.write_text(source.rstrip()+'\n',encoding='utf-8')
        variants.append({**job,'preview':name,'source':file.relative_to(ROOT).as_posix()})
    (ROOT/f'docs/localization/preview-jobs-{language}.json').write_text(json.dumps(variants,indent=2)+'\n',encoding='utf-8')
print(f'Generated {len(jobs)} isolated previews per language.')
