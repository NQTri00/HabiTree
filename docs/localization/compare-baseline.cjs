process.env.TEMP = process.env.TMP = 'E:/Workspace/mamxanh-redesign/.tmp';
const fs = require('fs'), path = require('path');
const {chromium} = require('E:/Workspace/mamxanh-redesign/node_modules/playwright');
const jobs = JSON.parse(fs.readFileSync(path.join(__dirname,'preview-jobs-vi.json'),'utf8'));
(async()=>{
  const browser=await chromium.launch({executablePath:'C:/Program Files/Google/Chrome/Application/chrome.exe',headless:true,env:{...process.env},args:['--disable-crash-reporter','--no-first-run']});
  const results=[];
  try {
    const page=await browser.newPage();
    await page.goto('http://127.0.0.1:4173/localization-review/');
    for(const job of jobs){
      results.push(await page.evaluate(async name=>{
        const load=async url=>{const image=new Image();image.src=url;await image.decode();const canvas=document.createElement('canvas');canvas.width=image.naturalWidth;canvas.height=image.naturalHeight;const context=canvas.getContext('2d');context.drawImage(image,0,0);return {width:canvas.width,height:canvas.height,data:context.getImageData(0,0,canvas.width,canvas.height).data};};
        const old=await load('/native-bio-review/'+name+'.png'),current=await load('/localization-review/vi/'+name+'.png');
        if(old.width!==current.width||old.height!==current.height)return {name,sizeChanged:true};
        let changed=0,absolute=0;
        for(let i=0;i<old.data.length;i+=4){let different=false;for(let channel=0;channel<3;channel++){const difference=Math.abs(old.data[i+channel]-current.data[i+channel]);absolute+=difference;if(difference)different=true;}if(different)changed++;}
        return {name,changedPixels:changed,totalPixels:old.width*old.height,changedPercent:changed/(old.width*old.height)*100,meanChannelDifference:absolute/(old.width*old.height*3)};
      },job.name));
    }
  } finally {await browser.close();}
  fs.writeFileSync(path.join(__dirname,'baseline-comparison.json'),JSON.stringify(results,null,2));
  console.log({screens:results.length,identical:results.filter(result=>result.changedPixels===0).length,changed:results.filter(result=>result.changedPixels>0).map(result=>({name:result.name,percent:result.changedPercent.toFixed(3)}))});
})();
