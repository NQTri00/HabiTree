const fs=require('fs'),path=require('path');
const out='E:/Workspace/mamxanh-redesign/native-bio-review';
const allJobs=JSON.parse(fs.readFileSync(path.join(__dirname,'preview-jobs.json'),'utf8'));
const jobs=allJobs.filter(j=>!j.nativeOnly);
const exists=j=>fs.existsSync(path.join(out,j.name+'.png'));
const sourceDir=path.join(__dirname,'../../app/src/main/java/com/mamxanh/app/presentation/bio');
const sharedSourceTime=Math.max(...fs.readdirSync(sourceDir,{recursive:true}).filter(n=>n.endsWith('.kt')&&!n.replaceAll('\\','/').startsWith('previews/')).map(n=>fs.statSync(path.join(sourceDir,n)).mtimeMs));
const stamp=j=>exists(j)?new Date(fs.statSync(path.join(out,j.name+'.png')).mtimeMs).toLocaleString():'';
const fresh=j=>exists(j)&&fs.statSync(path.join(out,j.name+'.png')).mtimeMs>=Math.max(sharedSourceTime,fs.statSync(path.resolve(__dirname,'../..',j.source)).mtimeMs);
const esc=s=>s.replaceAll('&','&amp;').replaceAll('<','&lt;');
const pngWidth=j=>fs.readFileSync(path.join(out,j.name+'.png')).readUInt32BE(16);
const pair=j=>{const w=pngWidth(j),ref='../evidence/visual-review/'+j.web,native=j.name+'.png';return `<section><figure><figcaption>Approved design</figcaption><a href="${ref}"><img loading="lazy" width="${w}" src="${ref}"></a></figure><figure><figcaption>Android Compose · ${stamp(j)}</figcaption><a href="${native}"><img loading="lazy" width="${w}" src="${native}"></a></figure><figure><figcaption>Overlay · slide to inspect alignment</figcaption><div class="overlay" style="width:${w}px"><img loading="lazy" width="${w}" src="${ref}"><img loading="lazy" class="native" width="${w}" src="${native}" style="opacity:.5"></div><input aria-label="Android image opacity" type="range" min="0" max="100" value="50" oninput="this.previousElementSibling.querySelector('.native').style.opacity=this.value/100"></figure></section>`};
const html=`<!doctype html><meta charset="utf-8"><title>Native / approved design comparison</title>
<style>body{margin:24px;background:#ddd;color:#222;font:14px Arial}article{margin:30px 0;break-inside:avoid}section{display:flex;gap:16px}figure{margin:0}figcaption{padding:10px 0}img{display:block}.overlay{position:relative}.overlay .native{position:absolute;top:0;left:0}input{width:100%;margin-top:12px}nav{position:sticky;top:0;background:#ddd;padding:12px;z-index:2;max-height:90px;overflow:auto}nav a{margin-right:15px}</style>
<nav>${jobs.filter(exists).map(j=>`<a href="#${j.name}">${j.name}</a>`).join('')}</nav>
<h1>Actual Compose previews beside approved phone design</h1><p>${jobs.filter(fresh).length}/${jobs.length} captures are newer than current native sources. Earlier captures remain visible and are labelled. These are original rendered images, without alignment or rescaling corrections.</p>
${jobs.map(j=>`<article id="${j.name}"><h2>${esc(j.name)} ${fresh(j)?'':'— fresh capture pending'}</h2>${exists(j)?pair(j):'<p>Native preview pending</p>'}</article>`).join('')}`;
fs.mkdirSync(out,{recursive:true});const extras=allJobs.filter(j=>j.nativeOnly&&exists(j)).map(j=>`<article id="${j.name}"><h2>Android file-import confirmation</h2><p>This native backup-import state has no matching screenshot in the approved web set.</p><img loading="lazy" width="375" src="${j.name}.png"></article>`).join('');fs.writeFileSync(path.join(out,'index.html'),html+extras);
console.log(`${jobs.filter(exists).length}/${jobs.length} comparison pairs available.`);
