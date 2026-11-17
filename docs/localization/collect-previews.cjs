const fs = require('fs');
const path = require('path');
const language = process.argv[2];
if (!['vi', 'en'].includes(language)) throw Error('Choose vi or en.');
const bytes = fs.readFileSync(path.join(__dirname, `render-${language}.log`));
const log = bytes.toString(bytes[0] === 255 && bytes[1] === 254 ? 'utf16le' : 'utf8');
const marker = log.match(/(?:^|\r?\n)\{\s*"success"/);
if (!marker) throw Error('The renderer has not returned a final result.');
const result = JSON.parse(log.slice(log.indexOf('{', marker.index), log.lastIndexOf('}') + 1));
if (!result.success) throw Error(result.globalError || 'Rendering failed.');
const jobs = JSON.parse(fs.readFileSync(path.join(__dirname, `preview-jobs-${language}.json`), 'utf8'));
const output = `E:/Workspace/mamxanh-redesign/localization-review/${language}`;
fs.mkdirSync(output, {recursive: true});
const captures = [];
for (const rendered of result.results) {
  const job = jobs.find(item => rendered.methodFqn === `com.mamxanh.app.presentation.bio.${item.preview}Kt.${item.preview}`);
  if (!job || !rendered.imagePath) throw Error('Unknown or failed preview: ' + rendered.methodFqn);
  const image = fs.readFileSync(rendered.imagePath);
  const width = image.readUInt32BE(16), height = image.readUInt32BE(20);
  const expected = job.width ? [job.width, job.height] : job.name.endsWith('-320') ? [320, 640] : job.name.endsWith('-844') ? [414, 390] : [375, 812];
  if (width !== expected[0] || height !== expected[1]) throw Error(`Wrong size: ${job.name} ${width}x${height}`);
  const target=path.join(output,job.name+'.png');
  const before=path.join(output,'before-copy-fixes',job.name+'.png');
  if(fs.existsSync(target)&&!fs.existsSync(before)){
    fs.mkdirSync(path.dirname(before),{recursive:true});
    fs.copyFileSync(target,before);
  }
  fs.copyFileSync(rendered.imagePath, target);
  if (rendered.componentTreePath) fs.copyFileSync(rendered.componentTreePath, path.join(output, job.name + '.json'));
  captures.push({name: job.name, width, height, source: rendered.imagePath, preview: job.preview});
}
if (captures.length !== jobs.length) throw Error(`Expected ${jobs.length} captures, got ${captures.length}.`);
fs.writeFileSync(path.join(__dirname, `captures-${language}.json`), JSON.stringify({language, collectedAt: new Date().toISOString(), captures}, null, 2));
console.log(`Collected ${captures.length} original ${language} Compose images.`);
