"""Apply locale-aware display formatting without altering persisted ISO keys."""
from catalog import ROOT

directory=ROOT/'app/src/main/java/com/mamxanh/app/presentation/bio'
file=directory/'BioForms.kt'
text=file.read_text(encoding='utf-8')
start=text.index('    BioCard(Modifier.fillMaxWidth()){\n        BioSelect(languageTitle')
end=text.index('\n    fun toggle',start)
card=text[start:end]
text=text[:start]+text[end:]
anchor='Bt(AppText.text(TextKey.CopyNhacViecTuyChonCoTheTreDoTietKiemPinKhongCf13dd),size=AppTypography.Size12,tint=bp.muted)'
text=text.replace(anchor,anchor+'\n'+card)
text=text.replace('listOf("CN","T2","T3","T4","T5","T6","T7")[i]','bioWeekday(i+1)')
file.write_text(text,encoding='utf-8')
for filename in ['BioMainScreens.kt','BioSecondaryScreens.kt']:
    file=directory/filename;text=file.read_text(encoding='utf-8')
    text=text.replace('if(day==1)"CN"else "T$day"','bioWeekday(day)')
    text=text.replace('"REST"->"N"','"REST"->AppText.text(TextKey.CalendarRestMarker)')
    file.write_text(text,encoding='utf-8')
file=directory/'BioDateInputs.kt';text=file.read_text(encoding='utf-8')
text=text.replace('val display=remember{SimpleDateFormat("dd/MM/yyyy",Locale("vi","VN"))}', 'val display=remember(AppText.language){java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT,bioLocale())}')
file.write_text(text,encoding='utf-8')
file=directory/'BioNativeHost.kt';text=file.read_text(encoding='utf-8')
text=text.replace('SimpleDateFormat("dd/MM/yyyy HH:mm",Locale("vi","VN"))','java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.SHORT,java.text.DateFormat.SHORT,bioLocale())')
text=text.replace('SimpleDateFormat("dd/MM · HH:mm",Locale.US)','java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.SHORT,java.text.DateFormat.SHORT,bioLocale())')
file.write_text(text,encoding='utf-8')
