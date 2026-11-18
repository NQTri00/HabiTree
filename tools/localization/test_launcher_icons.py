import json,unittest,xml.etree.ElementTree as ET
from catalog import ROOT

ANDROID='{http://schemas.android.com/apk/res/android}'

class LauncherIconTests(unittest.TestCase):
    def test_exactly_one_launcher_alias_is_enabled_by_default(self):
        application=ET.parse(ROOT/'app/src/main/AndroidManifest.xml').getroot().find('application')
        aliases=application.findall('activity-alias')
        palettes=json.loads((ROOT/'app/src/main/assets/bio_reference.json').read_text(encoding='utf-8'))['palettes']
        self.assertEqual(set(palettes),{alias.attrib[ANDROID+'name'].split('Icon_')[-1] for alias in aliases})
        self.assertEqual(1,sum(alias.attrib[ANDROID+'enabled']=='true' for alias in aliases))
        self.assertIsNone(application.find('activity').find('intent-filter'))
        entry=next(activity for activity in application.findall('activity') if activity.attrib[ANDROID+'name']=='.LauncherActivity')
        self.assertEqual('false',entry.attrib[ANDROID+'exported'])
        self.assertEqual('true',entry.attrib[ANDROID+'relinquishTaskIdentity'])
        self.assertIsNone(entry.find('intent-filter'))
        for alias in aliases:
            self.assertEqual('.LauncherActivity',alias.attrib[ANDROID+'targetActivity'])

    def test_icon_colors_match_the_actual_tree_theme_palettes(self):
        palettes=json.loads((ROOT/'app/src/main/assets/bio_reference.json').read_text(encoding='utf-8'))['palettes']
        colors={entry.attrib['name']:entry.text for entry in ET.parse(ROOT/'app/src/main/res/values/launcher_colors.xml').getroot()}
        for species,palette in palettes.items():
            self.assertEqual(palette['accent'],colors[f'launcher_{species}_background'])
            self.assertEqual(palette['deep'],colors[f'launcher_{species}_ink'])
            resource=ROOT/f'app/src/main/res/mipmap-anydpi-v26/ic_habitree_{species}.xml'
            icon=ET.parse(resource).getroot()
            self.assertEqual('adaptive-icon',icon.tag)
            self.assertIsNotNone(icon.find('monochrome'))

if __name__=='__main__':unittest.main()
