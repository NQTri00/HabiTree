"""Targeted real Compose states for the reported press/name/watering/icon fixes."""
import json
from catalog import ROOT

directory=ROOT/'docs/localization'
names=['home','home-320','home-lavender','home-lavender-full','tree','popup-seed-buy','audit-large-text-home']
for language in ['en','vi']:
    jobs=json.loads((directory/f'preview-jobs-{language}.json').read_text(encoding='utf-8'))
    selected=[job for job in jobs if job['name'] in names]
    preview=f'BioWateredHome{language.title()}Preview'
    selected.append({'name':'home-watered','preview':preview,'source':f'app/src/main/java/com/mamxanh/app/presentation/bio/previews/{preview}.kt'})
    if language=='en':
        for name,preview in [('touch-feedback','BioTouchFeedbackPreview'),('launcher-icons-circle','BioLauncherIconsCirclePreview'),('launcher-icons-square','BioLauncherIconsSquarePreview')]:
            selected.append({'name':name,'preview':preview,'source':f'app/src/main/java/com/mamxanh/app/presentation/bio/previews/{preview}.kt'})
    (directory/f'polish-jobs-{language}.json').write_text(json.dumps(selected,indent=2)+'\n',encoding='utf-8')
print('Prepared 11 English and 8 Vietnamese preview states.')
