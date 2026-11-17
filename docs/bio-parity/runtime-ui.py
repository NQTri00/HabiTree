import subprocess,sys,xml.etree.ElementTree as ET,re,json
from pathlib import Path
sys.stdout.reconfigure(encoding='utf-8')
adb='E:/Android/Sdk/platform-tools/adb.exe'
out=Path(__file__).parent/'runtime';out.mkdir(exist_ok=True)
args=sys.argv[1:]
def call(*a):return subprocess.check_output([adb,'-s','emulator-5580',*a])
if not args or args[0]=='dump':
 raw=call('shell','uiautomator','dump','/dev/tty').decode('utf-8',errors='replace');start=raw.find('<?xml');end=raw.rfind('</hierarchy>')+12
 xml=raw[start:end];(out/'latest.xml').write_text(xml,encoding='utf-8')
 root=ET.fromstring(xml)
 for n in root.iter('node'):
  text=n.get('text')or n.get('content-desc')
  if text:print(json.dumps({'text':text,'bounds':n.get('bounds'),'clickable':n.get('clickable'),'enabled':n.get('enabled')},ensure_ascii=False))
elif args[0]=='tap': print(call('shell','input','tap',args[1],args[2]).decode())
elif args[0]=='back':print(call('shell','input','keyevent','4').decode())
elif args[0]=='text':print(call('shell','input','text',args[1]).decode())
elif args[0]=='swipe':print(call('shell','input','swipe',*args[1:]).decode())
elif args[0]=='shot':(out/(args[1]+'.png')).write_bytes(call('exec-out','screencap','-p'))
