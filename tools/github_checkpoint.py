"""Publish verified Android checkpoints with Git's existing credential-manager login.

Credentials remain in memory and are never printed or saved to a file.
"""
import argparse,hashlib,json,os,subprocess,urllib.request,urllib.error
from pathlib import Path

ROOT=Path(__file__).resolve().parents[1]
REPO='NQTri00/HabiTree'

def credentials():
    result=subprocess.run(['git','credential','fill'],input='protocol=https\nhost=github.com\n\n',text=True,capture_output=True,cwd=ROOT,env={**os.environ,'GIT_TERMINAL_PROMPT':'0','GCM_INTERACTIVE':'Never'})
    values=dict(line.split('=',1) for line in result.stdout.splitlines() if '=' in line)
    if result.returncode or not values.get('password'):
        raise SystemExit('No cached GitHub credentials are available for release upload.')
    return values['password']

def main():
    parser=argparse.ArgumentParser()
    parser.add_argument('--inspect',action='store_true')
    parser.add_argument('--apk',type=Path)
    parser.add_argument('--evidence',type=Path)
    parser.add_argument('--tag-prefix',default='android-bilingual')
    parser.add_argument('--title',default='Android checkpoint — English / Vietnamese')
    parser.add_argument('--notes-file',type=Path)
    args=parser.parse_args()
    token=credentials()
    headers={'Authorization':'Bearer '+token,'Accept':'application/vnd.github+json','X-GitHub-Api-Version':'2022-11-28','User-Agent':'HabiTree-checkpoint'}
    def request(url,data=None,extra=None):
        try:
            with urllib.request.urlopen(urllib.request.Request(url,data=data,headers={**headers,**(extra or {})}),timeout=600) as response:return json.load(response)
        except urllib.error.HTTPError as error:
            message=json.loads(error.read()).get('message',str(error.code))
            raise SystemExit(f'GitHub API {error.code}: {message}') from None
    repo=request('https://api.github.com/repos/'+REPO)
    if args.inspect:
        print(json.dumps({'repository':repo['full_name'],'can_push':repo.get('permissions',{}).get('push',False)}))
        return
    if not args.apk or not args.apk.is_file():raise SystemExit('Provide the verified APK path.')
    commit=subprocess.check_output(['git','rev-parse','HEAD'],cwd=ROOT,text=True).strip()
    dirty=subprocess.check_output(['git','status','--porcelain'],cwd=ROOT,text=True).strip()
    if dirty:raise SystemExit('Commit the reviewed checkpoint before publishing its APK.')
    branch=subprocess.check_output(['git','branch','--show-current'],cwd=ROOT,text=True).strip()
    if not branch:raise SystemExit('Check out the authorized branch before publishing.')
    remote=subprocess.check_output(['git','ls-remote','origin','refs/heads/'+branch],cwd=ROOT,text=True).split()[0]
    if remote!=commit:raise SystemExit('Push this checkpoint before uploading its APK.')
    tag=args.tag_prefix+'-'+commit[:8]
    releases=request('https://api.github.com/repos/'+REPO+'/releases?per_page=100')
    release=next((item for item in releases if item['tag_name']==tag),None)
    if release is None:
        notes=args.notes_file.read_text(encoding='utf-8') if args.notes_file else 'Saved English/Vietnamese language selection, shared copy resources and design tokens.\n\nDebug APK for testing. See docs/localization/REPORT.md at this commit for validation and limitations.'
        body={'tag_name':tag,'target_commitish':commit,'name':args.title,'body':notes,'prerelease':True,'draft':False}
        release=request('https://api.github.com/repos/'+REPO+'/releases',json.dumps(body).encode(),{'Content-Type':'application/json'})
    uploaded=[]
    verified={}
    def verify_asset(asset,file):
        if asset['size']!=file.stat().st_size:raise SystemExit('Uploaded asset size does not match.')
        if asset.get('digest'):
            with file.open('rb') as stream:local_digest='sha256:'+hashlib.file_digest(stream,'sha256').hexdigest()
            if asset['digest']!=local_digest:raise SystemExit('Uploaded asset digest does not match.')
        verified[file.name]={'bytes':asset['size'],'digest':asset.get('digest')}
    for file in [args.apk,args.evidence]:
        if file is None:continue
        existing=next((asset for asset in release.get('assets',[]) if asset['name']==file.name),None)
        if existing:
            verify_asset(existing,file)
            uploaded.append(existing['browser_download_url']);continue
        from urllib.parse import quote
        url=release['upload_url'].split('{')[0]+'?name='+quote(file.name)
        with file.open('rb') as stream:
            asset=request(url,stream,{'Content-Type':'application/octet-stream','Content-Length':str(file.stat().st_size)})
        verify_asset(asset,file)
        uploaded.append(asset['browser_download_url'])
    print(json.dumps({'commit':commit,'release':release['html_url'],'assets':uploaded,'verification':verified},indent=2))

if __name__=='__main__':main()
