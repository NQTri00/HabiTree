import re
from catalog import ROOT

file=ROOT/'app/src/main/AndroidManifest.xml'
text=file.read_text(encoding='utf-8')
text=re.sub(r'\s+android:roundIcon="[^"]+"','',text)
file.write_text(text,encoding='utf-8')
file=ROOT/'app/src/main/java/com/mamxanh/app/presentation/bio/BioDesign.kt'
text=file.read_text(encoding='utf-8')
text=text.replace('Bi("leaf");Bt(AppText.text(TextKey.CopyMamXanh692a0a)','Bi("brand");Bt(AppText.text(TextKey.CopyMamXanh692a0a)')
file.write_text(text,encoding='utf-8')
file=ROOT/'app/src/main/java/com/mamxanh/app/presentation/bio/BioNativeHost.kt'
text=file.read_text(encoding='utf-8')
anchor='    var pending by remember{mutableStateOf<GameBackup?>(null)}'
effect='''
    val homeSpecies=world.tree(ui.homeTree)?.s("species").orEmpty()
    LaunchedEffect(homeSpecies,world.loading){
        if(!world.loading)withContext(Dispatchers.IO){HabiTreeLauncherIcon.update(context.applicationContext,homeSpecies)}
    }'''
if 'HabiTreeLauncherIcon.update' not in text:text=text.replace(anchor,anchor+effect)
file.write_text(text,encoding='utf-8')
