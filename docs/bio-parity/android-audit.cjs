const fs=require('fs'),path=require('path');
process.env.TEMP=process.env.TMP='E:/Workspace/mamxanh-redesign/.tmp';
const {chromium}=require('E:/Workspace/mamxanh-redesign/node_modules/playwright');
const root='E:/Workspace/mamxanh-redesign/native-bio-review';
const jobs=JSON.parse(fs.readFileSync(path.join(__dirname,'preview-jobs.json'),'utf8'));
(async()=>{
 const browser=await chromium.launch({executablePath:'C:/Program Files/Google/Chrome/Application/chrome.exe',headless:true,env:{...process.env,TEMP:'E:/Workspace/mamxanh-redesign/.tmp',TMP:'E:/Workspace/mamxanh-redesign/.tmp'},args:['--disable-crash-reporter','--no-first-run']});
 try{
  const page=await browser.newPage({viewport:{width:1170,height:1000}});
  for(let i=0;i<jobs.length;i+=6){
   const group=jobs.slice(i,i+6),name='audit-'+i/6;
   fs.writeFileSync(path.join(root,name+'.html'),`<!doctype html><meta charset="utf-8"><style>body{margin:8px;background:#ddd;font:14px Arial}.grid{display:grid;grid-template-columns:repeat(3,375px);gap:12px}figure{margin:0}img{width:375px;display:block}figcaption{height:25px}</style><div class="grid">${group.map(j=>`<figure><figcaption>${j.name}</figcaption><img src="${j.name}.png"></figure>`).join('')}</div>`);
   await page.goto('http://127.0.0.1:4173/native-bio-review/'+name+'.html');
   await page.evaluate(()=>Promise.all([...document.images].map(i=>i.decode())));
   await page.screenshot({path:path.join(root,name+'.png'),fullPage:true});
   console.log(name,group.map(j=>j.name).join(','));
  }
 }finally{await browser.close()}
})();
