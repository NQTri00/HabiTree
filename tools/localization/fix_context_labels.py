"""Keep status labels distinct from verbs in both current and legacy presentations."""
from catalog import ROOT
changes={
    'app/src/main/java/com/mamxanh/app/presentation/bio/BioForms.kt':[
        ('if(lifecycle=="PAUSED")AppText.text(TextKey.CopyTamDung676358)','if(lifecycle=="PAUSED")AppText.text(TextKey.TabPaused)'),
        ('if(lifecycle=="ARCHIVED")AppText.text(TextKey.ActionArchive)','if(lifecycle=="ARCHIVED")AppText.text(TextKey.CopyLuuTru7e7c5f)'),
    ],
    'tasks/src/main/java/com/mamxanh/app/feature/tasks/presentation/TasksScreen.kt':[
        ('"PAUSED" to AppText.text(TextKey.CopyTamDung676358)','"PAUSED" to AppText.text(TextKey.TabPaused)'),
    ],
    'tasks/src/main/java/com/mamxanh/app/feature/tasks/presentation/TaskDetails.kt':[
        ('GardenPaintedButton(AppText.text(TextKey.CopyLuuTru7e7c5f)','GardenPaintedButton(AppText.text(TextKey.ActionArchive)'),
    ],
    'tasks/src/main/java/com/mamxanh/app/feature/tasks/presentation/EngagementScreens.kt':[
        ('else->AppText.text(TextKey.CopyTamDung676358)','else->AppText.text(TextKey.TabPaused)'),
    ],
}
for relative,pairs in changes.items():
    file=ROOT/relative;text=file.read_text(encoding='utf-8')
    for old,new in pairs:text=text.replace(old,new)
    file.write_text(text,encoding='utf-8')
