param([string[]]$Names=@(),[switch]$ComponentTree,[switch]$Online,[string]$Manifest="$PSScriptRoot/preview-jobs.json",[string]$Log="$PSScriptRoot/headless-batch.log",[string]$ProjectDirectory="$PSScriptRoot/../..")
$env:JAVA_HOME='D:/Program Files/AndroidCLI/state/bio-build-runtime/jbr'
$env:GRADLE_USER_HOME='D:/Program Files/AndroidCLI/state/bio-build-runtime/gradle-online-home'
Remove-Item Env:ANDROID_SDK_HOME -ErrorAction SilentlyContinue
$env:JAVA_OPTS='-Duser.home=E:/Android -Djava.io.tmpdir=E:/Workspace/mamxanh-redesign/.tmp/bio-final'
$bioJobs=Get-Content -Raw -LiteralPath $Manifest | ConvertFrom-Json
$bioOutput='E:/Workspace/mamxanh-redesign/native-bio-headless/batch-'+(Get-Date -Format 'yyyyMMdd-HHmmss')
$bioProject=(Resolve-Path -LiteralPath $ProjectDirectory).Path.Replace('\','/')
$bioArgs=@('preview','render','--sdk=E:/Android/Sdk',"gradle://${bioProject}?:app","--output=$bioOutput",'--format=json')
if($ComponentTree){$bioArgs+='--component-tree'}
$bioSelectedJobs = @($bioJobs | Where-Object { $Names.Count -eq 0 -or $_.name -in $Names })
if ($bioSelectedJobs.Count -eq 0) { throw 'No previews matched the manifest selection.' }
$bioPreviewOwners = [System.Collections.Generic.HashSet[string]]::new([System.StringComparer]::Ordinal)
foreach ($bioJob in $bioSelectedJobs) {
    $bioSource = Join-Path $bioProject $bioJob.source
    $bioCode = Get-Content -Raw -LiteralPath $bioSource -ErrorAction Stop
    $bioPackage = [regex]::Match($bioCode, '(?m)^\s*package\s+([\w.]+)')
    $bioMethod = [regex]::Escape($bioJob.preview)
    if (!$bioPackage.Success -or $bioCode -cnotmatch "\bfun\s+$bioMethod\s*\(") {
        throw "Cannot resolve preview $($bioJob.preview) in $($bioJob.source)."
    }
    $bioFacade = [io.path]::GetFileNameWithoutExtension($bioSource) + 'Kt'
    $bioJvmName = [regex]::Match($bioCode, '(?m)^\s*@file:JvmName\("([^"]+)"\)')
    if ($bioJvmName.Success) { $bioFacade = $bioJvmName.Groups[1].Value }
    $bioFqn = "$($bioPackage.Groups[1].Value).$bioFacade.$($bioJob.preview)"
    if (!$bioPreviewOwners.Add($bioFqn)) { throw "Duplicate preview in manifest selection. $bioFqn" }
    $bioArgs += "--fqn=$bioFqn"
}
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
