const fs = require('fs');
const path = require('path');
const jobs = JSON.parse(fs.readFileSync(path.join(__dirname, 'preview-jobs-en.json'), 'utf8'));
const output = 'E:/Workspace/mamxanh-redesign/localization-review';
fs.mkdirSync(output, {recursive: true});
const escape = text => text.replace(/[&<>"']/g, char => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[char]));
fs.writeFileSync(path.join(output, 'index.html'), `<!doctype html><meta charset="utf-8"><title>Bilingual Compose review</title>
<style>body{margin:24px;font:15px system-ui;background:#e9eeea;color:#22372c}nav{position:sticky;top:0;background:#fff;padding:12px;z-index:1}input{padding:10px;width:260px}article{margin:32px 0}section{display:flex;gap:20px;align-items:flex-start;flex-wrap:wrap}figure{margin:0}figcaption{padding:8px}img{display:block;max-width:100%;border:1px solid #abb8ae}h2{font-size:17px}a{color:#285d3f}</style>
<h1>English / Vietnamese — actual Compose renders</h1><p>Original PNG exports. User-authored example names and task titles retain their content.</p><nav><input type="search" placeholder="Find screen or popup" aria-label="Find screen or popup"></nav>
${jobs.map(job=>`<article data-name="${escape(job.name)}"><h2>${escape(job.name)}</h2><section>${['vi','en'].map(language=>`<figure><figcaption>${language==='vi'?'Tiếng Việt':'English'} · <a href="${language}/${job.name}.png">Original image</a></figcaption><img loading="lazy" src="${language}/${job.name}.png" width="${job.width||375}" alt="${escape(job.name)} ${language}"></figure>`).join('')}</section></article>`).join('\n')}
<script>document.querySelector('input').oninput=e=>document.querySelectorAll('article').forEach(a=>a.hidden=!a.dataset.name.includes(e.target.value.toLowerCase()))</script>`);
console.log('Gallery: http://127.0.0.1:4173/localization-review/');
