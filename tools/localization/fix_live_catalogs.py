"""Keep catalog labels reactive when language changes without restarting the process."""
from catalog import ROOT
import re

for relative,names in {
 'shared/src/commonMain/kotlin/com/mamxanh/shared/model/Engagement.kt':['habitTemplates'],
 'shared/src/commonMain/kotlin/com/mamxanh/shared/model/GameModels.kt':['catalog'],
 'shared/src/commonMain/kotlin/com/mamxanh/shared/model/LivingGarden.kt':['gardenCosmetics'],
 'shared/src/commonMain/kotlin/com/mamxanh/shared/model/TreeLifecycle.kt':['careProducts'],
 'shared/src/commonMain/kotlin/com/mamxanh/shared/feature/home/domain/model/HomeSpecies.kt':['homeSpeciesCatalog'],
 'app/src/main/java/com/mamxanh/app/presentation/bio/BioModel.kt':['bioRoutes'],
}.items():
    file=ROOT/relative;text=file.read_text(encoding='utf-8')
    for name in names:text=re.sub(r'\bval '+name+r'\s*=',f'val {name} get()=',text)
    file.write_text(text,encoding='utf-8')

file=ROOT/'shared/src/commonMain/kotlin/com/mamxanh/shared/model/TreeLifecycle.kt'
text=file.read_text(encoding='utf-8')
for name in ['CareCue','WateringCanTier','FertilizerTier']:
    start=text.index('enum class '+name+'(');end=text.index('\n}',start)+2
    block=text[start:end]
    block=block.replace('val label: String','private val labelKey: TextKey')
    block=re.sub(r'AppText.text\((TextKey\.\w+)\)',r'\1',block)
    marker=block.find('    companion object')
    if marker>=0:block=block[:marker]+'    val label get()=AppText.text(labelKey)\n'+block[marker:]
    else:block=block[:-2]+';\n    val label get()=AppText.text(labelKey)\n}'
    text=text[:start]+block+text[end:]
file.write_text(text,encoding='utf-8')
print('Catalog getters and enum labels now resolve the current language on access.')
