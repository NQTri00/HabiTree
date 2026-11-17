param([Parameter(Mandatory=$true)][string]$FontDirectory)
$ErrorActionPreference='Stop'
$bioFontRoot=(Resolve-Path -LiteralPath $FontDirectory).Path
$bioProject=(Resolve-Path -LiteralPath "$PSScriptRoot/../..").Path
if(!(Test-Path -LiteralPath (Join-Path $bioProject 'app/build.gradle.kts'))){throw 'Run the importer from the thuc-tap-sinh project.'}
$bioDestination=Join-Path $bioProject 'app/src/main/res/font'
$bioCandidates=@(
 @{Names=@('consola.ttf','Consolas.ttf','Consolas-Regular.ttf','consolas_regular.ttf');Output='consolas_regular.ttf'},
 @{Names=@('consolab.ttf','Consolas-Bold.ttf','ConsolasBold.ttf','Consolas Bold.ttf','consolas_bold.ttf');Output='consolas_bold.ttf'}
)
Add-Type -AssemblyName System.Drawing
$bioFonts=@()
foreach($bioCandidate in $bioCandidates){
 $bioSource=$null
 foreach($bioName in $bioCandidate.Names){
  $bioPath=Join-Path $bioFontRoot $bioName
  if(Test-Path -LiteralPath $bioPath -PathType Leaf){$bioSource=$bioPath;break}
 }
 if(!$bioSource){throw "Missing $($bioCandidate.Output) in the supplied app-licensed font package."}
 $bioCollection=New-Object System.Drawing.Text.PrivateFontCollection
 try{
  $bioCollection.AddFontFile($bioSource)
  if('Consolas' -notin @($bioCollection.Families | ForEach-Object {$_.Name})){throw "Expected the Consolas font family: $bioSource"}
 }finally{$bioCollection.Dispose()}
 $bioFonts+=@{Source=$bioSource;Output=$bioCandidate.Output}
}
New-Item -ItemType Directory -Force -Path $bioDestination | Out-Null
foreach($bioFont in $bioFonts){
 $bioTarget=Join-Path $bioDestination $bioFont.Output
 if([IO.Path]::GetFullPath($bioFont.Source) -ne [IO.Path]::GetFullPath($bioTarget)){
  Copy-Item -LiteralPath $bioFont.Source -Destination $bioTarget -Force
 }
 if((Get-FileHash -LiteralPath $bioFont.Source).Hash -ne (Get-FileHash -LiteralPath $bioTarget).Hash){throw 'Font copy hash mismatch.'}
}
$bioKeep=Join-Path $bioProject 'app/src/main/res/raw/bio_consolas_keep.xml'
'<resources xmlns:tools="http://schemas.android.com/tools" tools:keep="@font/consolas_regular,@font/consolas_bold" />' | Set-Content -LiteralPath $bioKeep -Encoding UTF8
Write-Output 'Consolas regular and bold are packaged as Android resources. Rebuild and rerender the previews.'
