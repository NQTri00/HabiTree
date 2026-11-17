"""Record artifact/evidence hashes and actual host-test totals for the checkpoint."""
import hashlib,json,subprocess,unittest,xml.etree.ElementTree as ET
from datetime import datetime,timezone
from catalog import ROOT,CATALOG

apk=ROOT/'app/build/outputs/apk/debug/app-debug.apk'
checks=unittest.TextTestRunner().run(unittest.defaultTestLoader.discover(str(ROOT/'tools/localization'),pattern='test_*.py'))
assert checks.wasSuccessful()
signature=subprocess.run(['D:/Program Files/AndroidCLI/state/bio-build-runtime/jbr/bin/java.exe','--enable-native-access=ALL-UNNAMED','-Djava.io.tmpdir=E:/Workspace/mamxanh-redesign/.tmp','-jar','E:/Android/Sdk/build-tools/36.0.0/lib/apksigner.jar','verify','--verbose',str(apk)],text=True,capture_output=True,check=True)
v2_verified='Verified using v2 scheme (APK Signature Scheme v2): true' in signature.stdout
assert v2_verified
with apk.open('rb') as stream:apk_hash=hashlib.file_digest(stream,'sha256').hexdigest()
tests=[]
for file in (ROOT/'shared/build/test-results/testAndroidHostTest').glob('TEST-*.xml'):
    suite=ET.parse(file).getroot()
    tests.append({key:suite.attrib[key] for key in ['name','tests','failures','errors','skipped']})
assert sum(int(suite['failures'])+int(suite['errors']) for suite in tests)==0
captures={}
for language in ['vi','en']:
    info=json.loads((ROOT/f'docs/localization/captures-{language}.json').read_text(encoding='utf-8'))
    assert len(info['captures'])==144
    captures[language]=[]
    for item in info['captures']:
        file=__import__('pathlib').Path('E:/Workspace/mamxanh-redesign/localization-review')/language/(item['name']+'.png')
        captures[language].append({'name':item['name'],'sha256':hashlib.sha256(file.read_bytes()).hexdigest()})
comparison=json.loads((ROOT/'docs/localization/baseline-comparison.json').read_text(encoding='utf-8'))
result={'verifiedAt':datetime.now(timezone.utc).isoformat(),'copyEntries':len(json.loads(CATALOG.read_text(encoding='utf-8'))),'resourceAndTokenChecks':checks.testsRun,'hostTests':tests,'apk':{'path':apk.relative_to(ROOT).as_posix(),'bytes':apk.stat().st_size,'sha256':apk_hash,'signatureSchemeV2Verified':v2_verified},'previewStatesPerLanguage':144,'originalPngCount':288,'vietnameseBaselineIdentical':sum(item.get('changedPixels')==0 for item in comparison),'vietnameseBaselineChanged':sum(item.get('changedPixels',0)>0 for item in comparison),'captures':captures}
(ROOT/'docs/localization/verification.json').write_text(json.dumps(result,indent=2)+'\n',encoding='utf-8')
print(json.dumps({key:value for key,value in result.items() if key!='captures'},indent=2))
