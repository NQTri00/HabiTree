import subprocess,sys,xml.etree.ElementTree as ET,re,json,os
from pathlib import Path
sys.stdout.reconfigure(encoding='utf-8')
adb='E:/Android/Sdk/platform-tools/adb.exe'
out=Path(os.environ.get('HABITREE_RUNTIME_OUTPUT',str(Path(__file__).parent/'runtime')));out.mkdir(parents=True,exist_ok=True)
args=sys.argv[1:]
def call(*a):return subprocess.check_output([adb,'-s','emulator-5580',*a],timeout=45)
if not args or args[0]=='dump':
 call('shell','rm','-f','/sdcard/habitree-runtime.xml')
 status=call('shell','uiautomator','dump','/sdcard/habitree-runtime.xml').decode('utf-8',errors='replace')
 if 'UI hierchary dumped to:' not in status and 'UI hierarchy dumped to:' not in status:
  raise RuntimeError('Android UI dump failed: '+status.strip())
 raw=call('shell','cat','/sdcard/habitree-runtime.xml').decode('utf-8',errors='replace');start=raw.find('<?xml');end=raw.rfind('</hierarchy>')+12
 if start<0 or end<12:raise RuntimeError('Android did not return a UI hierarchy; inspect the device before continuing.')
 xml=raw[start:end];(out/'latest.xml').write_text(xml,encoding='utf-8')
 root=ET.fromstring(xml)
 for n in root.iter('node'):
  text=n.get('text')or n.get('content-desc')
  if text:print(json.dumps({'text':text,'bounds':n.get('bounds'),'clickable':n.get('clickable'),'enabled':n.get('enabled')},ensure_ascii=False))
elif args[0]=='tap': print(call('shell','input','tap',args[1],args[2]).decode())
elif args[0]=='tap-text':
 root=ET.parse(out/'latest.xml').getroot()
 matches=[n for n in root.iter('node') if args[1] in (n.get('text'),n.get('content-desc')) and n.get('enabled')=='true']
 if len(matches)!=1:raise RuntimeError(f'Expected one enabled node matching {args[1]!r}; found {len(matches)}. Dump the current screen first.')
 x1,y1,x2,y2=map(int,re.findall(r'\d+',matches[0].get('bounds')))
 print(call('shell','input','tap',str((x1+x2)//2),str((y1+y2)//2)).decode())
elif args[0]=='back':print(call('shell','input','keyevent','4').decode())
elif args[0]=='text':print(call('shell','input','text',args[1]).decode())
elif args[0]=='swipe':print(call('shell','input','swipe',*args[1:]).decode())
elif args[0]=='shot':(out/(args[1]+'.png')).write_bytes(call('exec-out','screencap','-p'))
