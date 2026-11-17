const fs=require('fs'),path=require('path'),crypto=require('crypto');
const root=path.resolve(__dirname,'../..'),images='E:/Workspace/mamxanh-redesign/native-bio-review';
const dir=path.join(root,'app/src/main/java/com/mamxanh/app/presentation/bio');
const sources=fs.readdirSync(dir).filter(n=>n.endsWith('.kt')).map(n=>path.join(dir,n));
sources.push(path.join(root,'app/src/main/res/drawable-nodpi/bio_icon_more.xml'));
const sharedTime=Math.max(...sources.map(f=>fs.statSync(f).mtimeMs));
const jobs=JSON.parse(fs.readFileSync(path.join(__dirname,'preview-jobs.json'),'utf8'));
const stale=[],missing=[],wrongSize=[];
for(const j of jobs){
 const file=path.join(images,j.name+'.png');
 if(!fs.existsSync(file)){missing.push(j.name);continue;}
 if(fs.statSync(file).mtimeMs<Math.max(sharedTime,fs.statSync(path.join(root,j.source)).mtimeMs))stale.push(j.name);
 const data=fs.readFileSync(file),width=data.readUInt32BE(16),height=data.readUInt32BE(20);
 const expected=j.width?[j.width,j.height]:j.name.endsWith('-320')?[320,640]:j.name.endsWith('-844')?[414,390]:[375,812];
 if(width!==expected[0]||height!==expected[1])wrongSize.push({name:j.name,width,height,expected});
}
const sourceHash=crypto.createHash('sha256');for(const f of sources.sort())sourceHash.update(fs.readFileSync(f));
const result={at:new Date().toISOString(),count:jobs.length,stale,missing,wrongSize,sourceSha256:sourceHash.digest('hex')};
fs.writeFileSync(path.join(__dirname,'android-audit-verification.json'),JSON.stringify(result,null,2));
console.log(result);
if(stale.length||missing.length||wrongSize.length)process.exitCode=1;
