"""Package the original bilingual Compose exports into a portable review gallery."""
import json,zipfile
from pathlib import Path
from catalog import ROOT

review=Path('E:/Workspace/mamxanh-redesign/localization-review')
target=review.parent/'HabiTree-bilingual-compose-review.zip'
jobs=json.loads((ROOT/'docs/localization/preview-jobs-en.json').read_text(encoding='utf-8'))
with zipfile.ZipFile(target,'w',compression=zipfile.ZIP_STORED) as archive:
    archive.write(review/'index.html','index.html')
    archive.writestr('README.txt','Open index.html to compare original English and Vietnamese Compose previews.\nClick Original image to inspect a full-size PNG.\nSee REPORT.md for validation and limitations.\n')
    archive.write(ROOT/'docs/localization/REPORT.md','REPORT.md')
    archive.write(ROOT/'docs/localization/verification.json','verification.json')
    for language in ['vi','en']:
        for job in jobs:archive.write(review/language/(job['name']+'.png'),language+'/'+job['name']+'.png')
        archive.write(ROOT/f'docs/localization/captures-{language}.json',f'captures-{language}.json')
print(json.dumps({'archive':str(target),'bytes':target.stat().st_size,'original_images':len(jobs)*2}))
