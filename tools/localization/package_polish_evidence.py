"""Preserve the follow-up's actual Compose images, tests and matching APK metadata."""
import hashlib,json,subprocess,unittest,zipfile,xml.etree.ElementTree as ET
from datetime import datetime,timezone
from pathlib import Path
from catalog import ROOT

checks=unittest.TextTestRunner().run(unittest.defaultTestLoader.discover(str(ROOT/'tools/localization'),pattern='test_*.py'))
assert checks.wasSuccessful()
apk=ROOT/'app/build/outputs/apk/debug/app-debug.apk'
signature=subprocess.run(['D:/Program Files/AndroidCLI/state/bio-build-runtime/jbr/bin/java.exe','--enable-native-access=ALL-UNNAMED','-Djava.io.tmpdir=E:/Workspace/mamxanh-redesign/.tmp','-jar','E:/Android/Sdk/build-tools/36.0.0/lib/apksigner.jar','verify','--verbose',str(apk)],text=True,capture_output=True,check=True)
assert 'Verified using v2 scheme (APK Signature Scheme v2): true' in signature.stdout
with apk.open('rb') as stream:apk_hash=hashlib.file_digest(stream,'sha256').hexdigest()
tests=[]
for file in (ROOT/'shared/build/test-results/testAndroidHostTest').glob('TEST-*.xml'):
    suite=ET.parse(file).getroot().attrib
    tests.append({key:suite[key] for key in ['name','tests','failures','errors','skipped']})
assert tests and all(int(test['failures'])+int(test['errors'])==0 for test in tests)
review=Path('E:/Workspace/mamxanh-redesign/habitree-polish-review')
captures={}
for language,count in [('en',11),('vi',8)]:
    info=json.loads((ROOT/f'docs/localization/polish-captures-{language}.json').read_text(encoding='utf-8'))
    assert len(info['captures'])==count
    captures[language]=[{'name':item['name'],'width':item['width'],'height':item['height'],'sha256':hashlib.sha256((review/language/(item['name']+'.png')).read_bytes()).hexdigest()} for item in info['captures']]
verification={'verifiedAt':datetime.now(timezone.utc).isoformat(),'resourceAndIconChecks':checks.testsRun,'hostTests':tests,'apk':{'bytes':apk.stat().st_size,'sha256':apk_hash,'signatureSchemeV2Verified':True},'originalComposeCaptures':captures,'deviceInteractionVerified':False,'launcherSwitchingVerifiedOnDevice':False}
live_file=ROOT/'docs/localization/live-verification.json'
live_root=review.parent/'habitree-live-review'
live=None
if live_file.exists():
    live=json.loads(live_file.read_text(encoding='utf-8'))
    assert live['apkSha256']==apk_hash, 'Live checks must use the APK being packaged.'
    for item in live['captures']:
        assert Path(item['file']).name==item['file']
        data=(live_root/item['file']).read_bytes()
        assert data.startswith(b'\x89PNG\r\n\x1a\n')
        assert hashlib.sha256(data).hexdigest()==item['sha256']
        assert [int.from_bytes(data[16:20],'big'),int.from_bytes(data[20:24],'big')]==[item['width'],item['height']]
    def passed(*names):return all(live['checks'].get(name,{}).get('status')=='pass' for name in names)
    verification['deviceInteractionVerified']=passed('languageSwitching','defaultPlantNames','customNicknames','wateringAlignment','heldPress','localeRestart')
    verification['launcherSwitchingVerifiedOnDevice']=passed('mainTreeIconSwitching','launcherRestart')
    verification['liveVerification']=live
file=ROOT/'docs/localization/polish-verification.json'
file.write_text(json.dumps(verification,indent=2)+'\n',encoding='utf-8')
archive=review.parent/'HabiTree-icon-and-touch-review.zip'
with zipfile.ZipFile(archive,'w',compression=zipfile.ZIP_STORED) as zipped:
    zipped.write(review/'index.html','index.html')
    zipped.write(ROOT/'docs/localization/POLISH-REPORT.md','REPORT.md')
    zipped.write(file,'verification.json')
    for language,items in captures.items():
        for item in items:zipped.write(review/language/(item['name']+'.png'),language+'/'+item['name']+'.png')
    if live:
        zipped.write(live_file,'live/verification.json')
        for item in live['captures']:zipped.write(live_root/item['file'],'live/'+item['file'])
print(json.dumps({'apkSha256':apk_hash,'apkBytes':apk.stat().st_size,'archive':str(archive),'archiveBytes':archive.stat().st_size,'composeImages':sum(map(len,captures.values()))},indent=2))
