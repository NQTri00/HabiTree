const fs=require('fs'),path=require('path');
const root='E:/Workspace/mamxanh-redesign/native-bio-review';
const jobs=JSON.parse(fs.readFileSync(path.join(__dirname,'preview-jobs.json'),'utf8'));
const esc=s=>s.replaceAll('&','&amp;').replaceAll('<','&lt;');
const changed=[],unchanged=[],added=[];
const sections=jobs.map(j=>{
 const current=path.join(root,j.name+'.png'),before=path.join(root,'audit-before',j.name+'.png');
 if(!fs.existsSync(current))return '';
 const had=fs.existsSync(before),same=had&&fs.readFileSync(before).equals(fs.readFileSync(current));
 (had?(same?unchanged:changed):added).push(j.name);
 return `<article id="${j.name}"><h2>${esc(j.name)} <small>${had?(same?'unchanged':'updated'):'new audit state'}</small></h2>${j.auditNote?`<p>${esc(j.auditNote)}</p>`:''}<div class="pair">${had?`<figure><figcaption>Android before audit</figcaption><a href="audit-before/${j.name}.png"><img loading="lazy" src="audit-before/${j.name}.png"></a></figure>`:''}<figure><figcaption>Android after audit · ${fs.statSync(current).mtime.toLocaleString()}</figcaption><a href="${j.name}.png"><img loading="lazy" src="${j.name}.png"></a></figure></div></article>`;
});
fs.writeFileSync(path.join(root,'android-audit.html'),`<!doctype html><html lang="en"><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Android UI audit · before and after</title><style>body{font:16px system-ui;margin:24px;background:#ece9eb;color:#292e2d}h1{font-size:26px}h2{font-size:18px}small{font-size:12px;font-weight:400}nav{display:flex;flex-wrap:wrap;gap:8px}a{color:#71354f}.pair{display:flex;gap:20px;align-items:flex-start;overflow:auto}figure{margin:0}figcaption{margin:8px 0}img{width:375px;max-width:none;display:block}article{padding:20px 0;border-top:1px solid #c9bdc4}</style><h1>Android UI audit</h1><p>Real Compose previews. Android before/after only. ${changed.length} original states updated; ${unchanged.length} unchanged; ${added.length} additional audit states. Images are available at original size by clicking them.</p><nav>${jobs.map(j=>`<a href="#${j.name}">${j.name}</a>`).join('')}</nav>${sections.join('')}</html>`);
fs.writeFileSync(path.join(__dirname,'android-audit-regression.json'),JSON.stringify({changed,unchanged,added},null,2));
console.log({changed:changed.length,unchanged:unchanged.length,added:added.length});
