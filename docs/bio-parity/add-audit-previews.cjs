const fs=require('fs'),path=require('path');
const root=path.resolve(__dirname,'../..'),dir=path.join(root,'app/src/main/java/com/mamxanh/app/presentation/bio/previews');
const entries=[
 ['audit-task-title-error','BioAuditTaskTitleErrorPreview','BioReferenceSurface("task-new",referenceFields=mapOf("task-error" to "title"))'],
 ['audit-task-minutes-error','BioAuditTaskMinutesErrorPreview','BioReferenceSurface("task-new",referenceFields=mapOf("title" to "Đọc sách","verification" to "TIMER","minutes" to "2","task-error" to "minutes"))'],
 ['audit-task-days-error','BioAuditTaskDaysErrorPreview','BioReferenceSurface("task-new",referenceFields=mapOf("title" to "Đọc sách","frequency" to "CUSTOM","task-error" to "days"))'],
 ['audit-task-edit','BioAuditTaskEditPreview','BioReferenceSurface("task-new",param="1")'],
 ['audit-focus-45','BioAuditFocus45Preview','BioReferenceSurface("focus",referenceFields=mapOf("focus:minutes" to "45"))'],
 ['audit-focus-invalid','BioAuditFocusInvalidPreview','BioReferenceSurface("focus",referenceFields=mapOf("focus:minutes" to "2"))'],
 ['audit-focus-task','BioAuditFocusTaskPreview','BioReferenceSurface("focus",param="2")'],
 ['audit-templates-selected','BioAuditTemplatesSelectedPreview','BioReferenceSurface("templates",referenceFields=mapOf("template:study25" to "true","template:water" to "true"))'],
 ['audit-large-text-home','BioAuditLargeTextHomePreview','BioReferenceSurface("home")',375,812,1.5],
 ['audit-large-text-popup','BioAuditLargeTextPopupPreview','BioReferenceSurface("shop",modal="purchase",id="succulent")',375,812,1.5],
 ['audit-large-text-form','BioAuditLargeTextFormPreview','BioReferenceSurface("task-new")',375,812,1.5],
 ['audit-landscape-focus','BioAuditLandscapeFocusPreview','BioReferenceSurface("focus")',844,390],
 ['audit-landscape-home','BioAuditLandscapeHomePreview','BioReferenceSurface("home")',844,390],
 ['audit-large-text-species','BioAuditLargeTextSpeciesPreview','BioReferenceSurface("species",param="cherry")',375,812,1.5],
 ['audit-tree-missing','BioAuditTreeMissingPreview','BioReferenceSurface("tree",param="missing-tree")'],
 ['audit-busy-purchase','BioAuditBusyPurchasePreview','BioReferenceSurface("shop",modal="purchase",id="succulent",busy=true)'],
 ['audit-large-text-species-bottom','BioAuditLargeTextSpeciesBottomPreview','BioReferenceSurface("species",param="cherry",bottom=true)',375,812,1.5],
];
const manifest=path.join(__dirname,'preview-jobs.json'),jobs=JSON.parse(fs.readFileSync(manifest,'utf8'));
for(const [name,preview,body,width=375,height=812,scale=1] of entries){
 const source='app/src/main/java/com/mamxanh/app/presentation/bio/previews/'+preview+'.kt';
 fs.writeFileSync(path.join(root,source),`package com.mamxanh.app.presentation.bio\nimport androidx.compose.runtime.Composable\nimport androidx.compose.ui.tooling.preview.Preview\n\n@Preview(name="${name}",widthDp=${width},heightDp=${height},fontScale=${scale}f,device="spec:width=${width}dp,height=${height}dp,dpi=160")\n@Composable fun ${preview}(){${body}}\n`);
 if(!jobs.some(j=>j.name===name))jobs.push({name,preview,source,nativeOnly:true,web:null,width,height});
}
const tall={'data-expanded-full':1100,'encyclopedia-full':2300,'insights-detail-full':1250,'insights-month-full':1450,'onboard-3-full':2200,'rest-full':1260};
for(const [name,height] of Object.entries(tall)){
 const job=jobs.find(j=>j.name===name),file=path.join(root,job.source);
 let source=fs.readFileSync(file,'utf8').replace(/heightDp=\d+/,`heightDp=${height}`).replace(/height=\d+dp/,`height=${height}dp`);
 fs.writeFileSync(file,source);job.height=height;
 job.auditNote='Taller genuine Compose canvas to show the complete Android content after readability fixes.';
}
fs.writeFileSync(manifest,JSON.stringify(jobs,null,2)+'\n');
console.log(jobs.length+' total preview jobs');
