const fs=require('fs'),path=require('path');
const root='E:/Workspace/mamxanh-redesign/habitree-polish-review';
const languages=['en','vi'];
const jobs=Object.fromEntries(languages.map(language=>[language,JSON.parse(fs.readFileSync(path.join(__dirname,`polish-jobs-${language}.json`),'utf8'))]));
const names=[...new Set(languages.flatMap(language=>jobs[language].map(job=>job.name)))];
fs.mkdirSync(root,{recursive:true});
fs.writeFileSync(path.join(root,'index.html'),`<!doctype html><meta charset="utf-8"><title>HabiTree — touch, names and icons</title><style>body{font:15px system-ui;margin:24px;background:#ece9eb;color:#292e2d}article{padding:20px 0;border-top:1px solid #c9bdc4}section{display:flex;gap:20px;align-items:flex-start;flex-wrap:wrap}figure{margin:0}figcaption{padding:8px}img{display:block;max-width:100%}h2{font-size:18px}</style><h1>HabiTree — native Compose review</h1><p>Real composables. The interaction sheet uses controlled resting, held-press and focused states. Icon sheets display the actual native foreground/background resources under circular and rounded-square masks.</p>${names.map(name=>`<article><h2>${name}</h2><section>${languages.filter(language=>jobs[language].some(job=>job.name===name)).map(language=>`<figure><figcaption>${language} · <a href="${language}/${name}.png">Original image</a></figcaption><img src="${language}/${name}.png" width="${name.endsWith('-320')?320:375}"></figure>`).join('')}</section></article>`).join('')}`);
console.log('Gallery: http://127.0.0.1:4173/habitree-polish-review/');
