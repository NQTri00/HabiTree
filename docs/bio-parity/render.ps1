param([string[]]$Names=@(),[int]$StudioPid=3404)
$ErrorActionPreference='Stop'
$bioRoot='E:/Workspace/thuc-tap-sinh'
$bioOut='E:/Workspace/mamxanh-redesign/native-bio-review'
New-Item -ItemType Directory -Force $bioOut | Out-Null
$env:TEMP='E:/Workspace/mamxanh-redesign/.tmp'
$env:TMP=$env:TEMP
$bioSources=Get-ChildItem -LiteralPath "$bioRoot/app/src/main/java/com/mamxanh/app/presentation/bio" -Filter *.kt -Recurse
$bioNewest=($bioSources | Sort-Object LastWriteTime -Descending | Select-Object -First 1).LastWriteTime
$bioClass=Get-ChildItem -LiteralPath "$bioRoot/app/build/tmp/kotlin-classes/debug/com/mamxanh/app/presentation/bio" -Filter *.class | Sort-Object LastWriteTime -Descending | Select-Object -First 1
if($bioClass.LastWriteTime -lt $bioNewest){throw 'Compile current native sources before rendering: Studio CLI can reuse old bytecode.'}
$bioJobs=Get-Content -Raw -LiteralPath "$bioRoot/docs/bio-parity/preview-jobs.json" | ConvertFrom-Json
foreach($bioJob in $bioJobs){
 if($Names.Count -gt 0 -and $bioJob.name -notin $Names){continue}
 $bioFree=(Get-CimInstance Win32_OperatingSystem).FreePhysicalMemory/1024
 if($bioFree -lt 1600){throw "Preview batch paused: only $([math]::Round($bioFree)) MiB available. Release task-owned resources before resuming."}
 $bioStarted=Get-Date
 & 'D:/Program Files/AndroidCLI/android.cmd' studio render-compose-preview --pid=$StudioPid --project=$bioRoot "--output-image-file=$bioOut/$($bioJob.name).png" "$bioRoot/$($bioJob.source)" $bioJob.preview
 $bioCode=$LASTEXITCODE
 [pscustomobject]@{name=$bioJob.name;preview=$bioJob.preview;exit=$bioCode;at=$bioStarted.ToString('o');freeMB=[math]::Round($bioFree)}|ConvertTo-Json -Compress|Add-Content -LiteralPath "$bioOut/render-log.jsonl" -Encoding UTF8
 if($bioCode -ne 0){throw "Preview failed: $($bioJob.name)"}
}
