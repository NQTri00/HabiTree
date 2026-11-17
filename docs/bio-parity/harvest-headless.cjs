const fs=require('fs'),path=require('path');
const raw=fs.readFileSync(path.join(__dirname,'headless-batch.log'));
const log=raw.toString(raw[0]===255&&raw[1]===254?'utf16le':'utf8');
const marker=log.match(/(?:^|\r?\n)\{\s*"success"/);
if(!marker)throw Error('No final renderer result yet.');
const start=log.indexOf('{',marker.index),result=JSON.parse(log.slice(start,log.lastIndexOf('}')+1));
if(!result.success)throw Error(result.globalError||'Preview render failed.');
const expected=Number(process.argv[2]||0);
if(expected&&result.results.length!==expected)throw Error(`Expected ${expected} previews, received ${result.results.length}.`);
const jobs=JSON.parse(fs.readFileSync(path.join(__dirname,'preview-jobs.json'),'utf8'));
const out='E:/Workspace/mamxanh-redesign/native-bio-review';
fs.mkdirSync(path.join(out,'components'),{recursive:true});
let count=0;
for(const r of result.results){
 const job=jobs.find(j=>r.methodFqn===`com.mamxanh.app.presentation.bio.${j.preview}Kt.${j.preview}`);
 if(!job)throw Error('Unknown preview '+r.methodFqn);
 if(!r.imagePath||!fs.existsSync(r.imagePath))throw Error('Missing image '+job.name);
 const bytes=fs.readFileSync(r.imagePath),width=bytes.readUInt32BE(16),height=bytes.readUInt32BE(20);
 const target=job.width?[job.width,job.height]:job.name.endsWith('-320')?[320,640]:job.name.endsWith('-844')?[414,390]:[375,812];
 if(width!==target[0]||height!==target[1])throw Error(`Wrong image size for ${job.name}: ${width}x${height}`);
 fs.copyFileSync(r.imagePath,path.join(out,job.name+'.png'));
 if(r.componentTreePath)fs.copyFileSync(r.componentTreePath,path.join(out,'components',job.name+'.json'));
 fs.appendFileSync(path.join(out,'render-log.jsonl'),JSON.stringify({name:job.name,preview:job.preview,exit:0,at:new Date().toISOString(),engine:'Android CLI headless Compose Preview',source:r.imagePath})+'\n');
 count++;
}
console.log('Copied '+count+' original Compose captures without image transformations.');
