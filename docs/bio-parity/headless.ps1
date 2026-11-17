param([string[]]$Names=@(),[switch]$ComponentTree,[switch]$Online,[string]$Manifest="$PSScriptRoot/preview-jobs.json",[string]$Log="$PSScriptRoot/headless-batch.log")
$env:JAVA_HOME='D:/Program Files/AndroidCLI/state/bio-build-runtime/jbr'
$env:GRADLE_USER_HOME='D:/Program Files/AndroidCLI/state/bio-build-runtime/gradle-online-home'
Remove-Item Env:ANDROID_SDK_HOME -ErrorAction SilentlyContinue
$env:JAVA_OPTS='-Duser.home=E:/Android -Djava.io.tmpdir=E:/Workspace/mamxanh-redesign/.tmp/bio-final'
$bioJobs=Get-Content -Raw -LiteralPath $Manifest | ConvertFrom-Json
$bioOutput='E:/Workspace/mamxanh-redesign/native-bio-headless/batch-'+(Get-Date -Format 'yyyyMMdd-HHmmss')
$bioArgs=@('preview','render','--sdk=E:/Android/Sdk','gradle://E:/Workspace/thuc-tap-sinh?:app',"--output=$bioOutput",'--format=json')
if($ComponentTree){$bioArgs+='--component-tree'}
foreach($bioJob in $bioJobs){if($Names.Count -eq 0 -or $bioJob.name -in $Names){$bioArgs+="--fqn=com.mamxanh.app.presentation.bio.$($bioJob.preview)Kt.$($bioJob.preview)"}}
if(!$Online){$bioArgs+=@('--','--offline')}else{$bioArgs+='--'}
$bioArgs+=@('--no-daemon','--max-workers=1','-Pkotlin.compiler.execution.strategy=in-process','-Dorg.gradle.jvmargs=-Xmx1536m -XX:MaxMetaspaceSize=512m -Duser.home=E:/Android -Djava.io.tmpdir=E:/Workspace/mamxanh-redesign/.tmp/bio-final')
$bioFree=(Get-CimInstance Win32_OperatingSystem).FreePhysicalMemory/1024
if($bioFree -lt 2000){throw 'Preview batch paused: less than2GiB RAM available.'}
$env:ANDROID_USER_HOME='D:/Program Files/AndroidCLI/state'
$env:TEMP='D:/Program Files/AndroidCLI/temp'
$env:TMP=$env:TEMP
& 'D:/Program Files/AndroidCLI/state/bin/android-cli.exe' @bioArgs *> $Log
$bioExit=$LASTEXITCODE
Get-Content -LiteralPath $Log -Tail 14
exit $bioExit
