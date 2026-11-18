"""Native vector/adaptive HabiTree marks from the same tree palettes as the UI."""
import json
from pathlib import Path
from catalog import ROOT

data=json.loads((ROOT/'app/src/main/assets/bio_reference.json').read_text(encoding='utf-8'))
palettes=data['palettes']
res=ROOT/'app/src/main/res'
for directory in ['drawable','mipmap-anydpi','mipmap-anydpi-v26','values']:(res/directory).mkdir(parents=True,exist_ok=True)
paths='''
    <path android:fillColor="INK" android:pathData="M54,26 C67,31 67,40 54,49 C41,40 41,31 54,26 Z" />
    <path android:fillColor="INK" android:pathData="M29,41 C44,37 53,44 53,58 C38,60 30,53 29,41 Z" />
    <path android:fillColor="INK" android:pathData="M79,41 C64,37 55,44 55,58 C70,60 78,53 79,41 Z" />
    <path android:fillColor="@android:color/transparent" android:strokeColor="INK" android:strokeWidth="4" android:strokeLineCap="round" android:strokeLineJoin="round" android:pathData="M54,48 V73 M54,62 L40,50 M54,62 L68,50 M39,80 V73 H69 V80" />
    <path android:fillColor="@android:color/transparent" android:strokeColor="INK" android:strokeWidth="2.5" android:pathData="M39,77 A3,3 0,1 0,39,83 A3,3 0,1 0,39,77 M69,77 A3,3 0,1 0,69,83 A3,3 0,1 0,69,77" />
'''
def vector(body,viewport=108):return f'''<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="108dp" android:height="108dp" android:viewportWidth="{viewport}" android:viewportHeight="{viewport}">{body}\n</vector>\n'''
colors=[]
for species,palette in palettes.items():
    colors.extend([f'    <color name="launcher_{species}_background">{palette["accent"]}</color>',f'    <color name="launcher_{species}_ink">{palette["deep"]}</color>'])
    body=paths.replace('INK',f'@color/launcher_{species}_ink')
    (res/'drawable'/f'ic_habitree_foreground_{species}.xml').write_text(vector(body),encoding='utf-8')
    background=f'<path android:fillColor="@color/launcher_{species}_background" android:pathData="M24,4 H84 Q104,4 104,24 V84 Q104,104 84,104 H24 Q4,104 4,84 V24 Q4,4 24,4 Z" />'
    (res/'mipmap-anydpi'/f'ic_habitree_{species}.xml').write_text(vector(background+body),encoding='utf-8')
    (res/'mipmap-anydpi-v26'/f'ic_habitree_{species}.xml').write_text(f'''<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/launcher_{species}_background" />
    <foreground android:drawable="@drawable/ic_habitree_foreground_{species}" />
    <monochrome android:drawable="@drawable/ic_habitree_monochrome" />
</adaptive-icon>
''',encoding='utf-8')
(res/'values/launcher_colors.xml').write_text('<resources>\n'+'\n'.join(colors)+'\n</resources>\n',encoding='utf-8')
(res/'drawable/ic_habitree_monochrome.xml').write_text(vector(paths.replace('INK','@android:color/black')),encoding='utf-8')
(res/'drawable/bio_icon_brand.xml').write_text(vector('<group android:translateX="-18" android:translateY="-18">'+paths.replace('INK','@android:color/black')+'</group>',72),encoding='utf-8')

manifest=ROOT/'app/src/main/AndroidManifest.xml'
text=manifest.read_text(encoding='utf-8')
if 'android:icon=' not in text:text=text.replace('android:label="@string/app_name"','android:label="@string/app_name"\n        android:icon="@mipmap/ic_habitree_succulent"',1)
start=text.index('            <intent-filter>');end=text.index('            </intent-filter>',start)+len('            </intent-filter>')
if '.launcher.Icon_' not in text:
    text=text[:start]+text[end:]
    aliases=[]
    for species in palettes:
        aliases.append(f'''        <activity-alias android:name=".launcher.Icon_{species}"
            android:targetActivity=".MainActivity" android:exported="true"
            android:enabled="{'true' if species=='succulent' else 'false'}"
            android:label="@string/app_name" android:icon="@mipmap/ic_habitree_{species}">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity-alias>''')
    text=text.replace('    </application>','\n'.join(aliases)+'\n    </application>')
manifest.write_text(text,encoding='utf-8')
print(f'Generated {len(palettes)} tree-colored adaptive/legacy launcher icons and aliases.')
