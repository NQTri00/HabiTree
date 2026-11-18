"""Apply targeted presentation fixes without changing saved plant names."""
import json
from catalog import ROOT, load_catalog, write_catalog
from generate import generate

file=ROOT/'app/src/main/java/com/mamxanh/app/presentation/bio/BioNativeHost.kt'
text=file.read_text(encoding='utf-8')
text=text.replace('"name" to t.name,"plot"','"name" to AppText.plantName(t.speciesId,t.name),"plot"')
file.write_text(text,encoding='utf-8')
file=ROOT/'app/src/main/java/com/mamxanh/app/presentation/bio/BioDesign.kt'
text=file.read_text(encoding='utf-8')
text=text.replace('.clickable(enabled=enabled,role=Role.Button,onClick=onClick).padding(horizontal=', '.clickable(interactionSource=interactionSource,indication=LocalIndication.current,enabled=enabled,role=Role.Button,onClick=onClick).padding(horizontal=')
file.write_text(text,encoding='utf-8')
catalog=load_catalog()
for item in catalog.values():
    if item['vi']=='Đã tưới':item['en']='Watered'
write_catalog(catalog)
generate()
