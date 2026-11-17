process.env.TEMP = process.env.TMP = 'E:/Workspace/mamxanh-redesign/.tmp';
const fs = require('fs'), path = require('path');
const {chromium} = require('E:/Workspace/mamxanh-redesign/node_modules/playwright');
const output = 'E:/Workspace/mamxanh-redesign/localization-review';
const jobs = JSON.parse(fs.readFileSync(path.join(__dirname, 'preview-jobs-en.json'), 'utf8'));
const selection = process.argv.slice(2);
const selected = jobs.filter(job => !selection.length || selection.includes(job.name));
(async () => {
  const browser = await chromium.launch({executablePath:'C:/Program Files/Google/Chrome/Application/chrome.exe',headless:true,env:{...process.env},args:['--disable-crash-reporter','--no-first-run']});
  try {
    const page = await browser.newPage({viewport:{width:1360,height:1200}});
    for (let index=0; index<selected.length; index+=6) {
      const list = selected.slice(index,index+6);
      const name = `contact-${selection.length?'selection-':''}${index/6}`;
      fs.writeFileSync(path.join(output,name+'.html'), `<!doctype html><meta charset="utf-8"><style>body{margin:10px;background:#ddd;font:13px Arial}.grid{display:grid;grid-template-columns:repeat(3,440px);gap:10px}section{display:flex;gap:6px}figure{margin:0}img{width:214px;display:block}figcaption{height:35px}</style><div class="grid">${list.map(job=>`<section>${['vi','en'].map(language=>`<figure><figcaption>${job.name} · ${language}</figcaption><img src="${language}/${job.name}.png"></figure>`).join('')}</section>`).join('')}</div>`);
      await page.goto('http://127.0.0.1:4173/localization-review/'+name+'.html');
      await page.evaluate(()=>Promise.all([...document.images].map(image=>image.decode())));
      await page.screenshot({path:path.join(output,name+'.png'),fullPage:true});
      console.log(name,list.map(job=>job.name).join(','));
    }
  } finally {await browser.close();}
})();
