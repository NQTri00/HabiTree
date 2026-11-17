"""Fix copy issues observed in actual bilingual Compose screenshots."""
import json
from catalog import ROOT,CATALOG,key_for
from generate import generate,enum_name
from kotlin_literals import literals

catalog=json.loads(CATALOG.read_text(encoding='utf-8'))
copy={
    'Mua':'Buy',
    'Mua & gieo':'Buy & plant',
    'Mua & gieo 1':'Buy & plant 1',
}
for vi,en in copy.items():
    catalog[key_for(vi)]={'vi':vi,'en':en,'canonical':False,'source':'bilingual visual review: purchase actions'}
for key,vi,en in [
    ('action_archive','Lưu trữ','Archive'),
    ('route_more','Thêm','More'),
    ('tab_paused','Tạm dừng','Paused'),
    ('choose_weekdays','Chọn ngày','Choose days'),
]:catalog[key]={'vi':vi,'en':en,'canonical':False,'source':'bilingual visual review: contextual labels'}
CATALOG.write_text(json.dumps(catalog,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
directory=ROOT/'app/src/main/java/com/mamxanh/app/presentation/bio'
for filename in ['BioModal.kt','BioForms.kt','BioMainScreens.kt','BioModel.kt']:
    file=directory/filename;text=file.read_text(encoding='utf-8');edits=[]
    for literal in literals(text):
        if literal.template in copy:
            edits.append((literal.start,literal.end,'AppText.text(TextKey.'+enum_name(key_for(literal.template))+')'))
    for start,end,value in reversed(edits):text=text[:start]+value+text[end:]
    if filename in ['BioModal.kt','BioForms.kt']:
        text=text.replace('TextKey.CopyLuuTru7e7c5f','TextKey.ActionArchive')
    if filename=='BioMainScreens.kt':
        text=text.replace('"paused" to AppText.text(TextKey.CopyTamDung676358)','"paused" to AppText.text(TextKey.TabPaused)')
    if filename=='BioForms.kt':
        text=text.replace('Bt(AppText.text(TextKey.CopyChonNgayD724ec)','Bt(AppText.text(TextKey.ChooseWeekdays)')
    if filename=='BioModel.kt':
        text=text.replace('"more" to AppText.text(TextKey.CopyThemDd9262)','"more" to AppText.text(TextKey.RouteMore)')
        text=text.replace('owned(data.a("records").objects(),"status")','owned(data.a("records").objects(),"status","firstPlanted","firstMatured")')
    file.write_text(text,encoding='utf-8')
generate()
