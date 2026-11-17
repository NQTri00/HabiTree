const fs=require('fs'),path=require('path');
const manifest=path.join(__dirname,'preview-jobs.json');
const jobs=JSON.parse(fs.readFileSync(manifest));
const root=path.resolve(__dirname,'../..');
const refs='E:/Workspace/mamxanh-redesign/evidence/visual-review';
let count=0;
for(const web of fs.readdirSync(refs).filter(n=>n.endsWith('-full.png'))){
 const name=web.slice(0,-4);if(jobs.some(j=>j.name===name))continue;
 const base=jobs.find(j=>j.name===name.slice(0,-5));if(!base)throw Error('No base '+name);
 const bytes=fs.readFileSync(path.join(refs,web)),width=bytes.readUInt32BE(16),height=bytes.readUInt32BE(20);
 const preview=base.preview.replace(/Preview$/,'FullPreview');
 const source=base.source.replace(base.preview,preview);
 let kt=fs.readFileSync(path.join(root,base.source),'utf8');
 kt=kt.replaceAll(base.preview,preview).replace(/@Preview\([^\n]+\)/,`@Preview(name="${name}",widthDp=${width},heightDp=${height},device="spec:width=${width}dp,height=${height}dp,dpi=160")`);
 fs.writeFileSync(path.join(root,source),kt);
 jobs.push({name,preview,web,source,width,height});count++;
}
fs.writeFileSync(manifest,JSON.stringify(jobs,null,2)+'\n');console.log('Added '+count+' full content previews.');
