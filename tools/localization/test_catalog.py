"""Resource invariants that protect interpolation, storage compatibility and coverage."""
import json,re,unittest
import xml.etree.ElementTree as ET
from catalog import CATALOG,ROOT,collect,source_files
from generate import enum_name

class CatalogTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.catalog=json.loads(CATALOG.read_text(encoding='utf-8'))

    def test_complete_languages_and_matching_arguments(self):
        for key,item in self.catalog.items():
            with self.subTest(key=key):
                self.assertTrue(item['vi'])
                self.assertTrue(item['en'])
                arguments=sorted(re.findall(r'\{\d+\}',item['vi']))
                self.assertEqual(arguments,sorted(re.findall(r'\{\d+\}',item['en'])))
                if item.get('en_one'):
                    self.assertEqual(arguments,sorted(re.findall(r'\{\d+\}',item['en_one'])))

    def test_canonical_categories_keep_original_storage_values(self):
        for item in self.catalog.values():
            if item.get('canonical'):
                self.assertEqual(item['vi'],item['storageValue'])

    def test_generated_keys_have_no_collisions(self):
        keys=[enum_name(key) for key in self.catalog]
        self.assertEqual(len(keys),len(set(keys)))

    def test_android_resources_cover_the_same_catalog(self):
        expected=set(self.catalog)
        for qualifier in ['values','values-en','values-vi']:
            xml=ET.parse(ROOT/f'core-ui/src/main/res/{qualifier}/app_copy.xml')
            self.assertEqual(expected,{entry.attrib['name'] for entry in xml.getroot()})
        for file in (ROOT/'app/src/main/res/values').glob('*.xml'):
            english=ROOT/'app/src/main/res/values-en'/file.name
            base={entry.attrib['name'] for entry in ET.parse(file).getroot() if entry.tag in {'string','plurals','string-array'} and entry.attrib.get('translatable')!='false'}
            if not base:continue
            translated={entry.attrib['name'] for entry in ET.parse(english).getroot()}
            self.assertTrue(base.issubset(translated),file.name)

    def test_no_inline_vietnamese_application_copy(self):
        findings=[]
        for file in source_files():
            if 'Preview' in file.name:continue
            for literal in collect(file.read_text(encoding='utf-8-sig')):
                findings.append(f'{file.relative_to(ROOT)}: {literal.template}')
        self.assertEqual([],findings)

if __name__=='__main__':unittest.main()
