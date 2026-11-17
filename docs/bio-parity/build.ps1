param([string[]]$Tasks=@(':app:compileDebugKotlin'),[string]$Log='compile.log',[switch]$Online)
$env:JAVA_HOME='D:/Program Files/AndroidCLI/state/bio-build-runtime/jbr'
$env:GRADLE_USER_HOME='D:/Program Files/AndroidCLI/state/bio-build-runtime/gradle-online-home'
$env:ANDROID_USER_HOME='E:/Android/.android'
$env:TEMP='E:/Workspace/mamxanh-redesign/.tmp'
$env:TMP=$env:TEMP
Remove-Item Env:JDK_JAVA_OPTIONS -ErrorAction SilentlyContinue
$env:JAVA_OPTS='-Duser.home=E:/Android -Djava.io.tmpdir=E:/Workspace/mamxanh-redesign/.tmp'
$bioArgs=$Tasks+@('--no-daemon','--max-workers=1','-Pkotlin.compiler.execution.strategy=in-process','-Dorg.gradle.jvmargs=-Xmx1536m -XX:MaxMetaspaceSize=512m -Dfile.encoding=UTF-8 -Duser.home=E:/Android -Djava.io.tmpdir=E:/Workspace/mamxanh-redesign/.tmp')
if(!$Online){$bioArgs+='--offline'}
& "$PSScriptRoot/../../gradlew.bat" @bioArgs *> "$PSScriptRoot/$Log"
$bioExit=$LASTEXITCODE
Get-Content -LiteralPath "$PSScriptRoot/$Log" -Tail 12
exit $bioExit
