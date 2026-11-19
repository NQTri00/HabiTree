"""Resource invariants that protect interpolation, storage compatibility and coverage."""
import json,re,unittest
import xml.etree.ElementTree as ET
from catalog import ROOT, collect, source_files, load_catalog
from generate import enum_name, normalize_placeholders

class CatalogTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.catalog=load_catalog()

    def test_complete_languages_and_matching_arguments(self):
        for key,item in self.catalog.items():
            with self.subTest(key=key):
                item, _ = normalize_placeholders(key, item)
                self.assertTrue(item['vi'])
                self.assertTrue(item['en'])
                arguments=sorted(re.findall(r'\{\d+\}',item['vi']))
                self.assertEqual(arguments,sorted(re.findall(r'\{\d+\}',item['en'])))
                if item.get('en_one'):
                    self.assertEqual(arguments,sorted(re.findall(r'\{\d+\}',item['en_one'])))

    def test_named_arguments_keep_their_declared_order_across_languages(self):
        source = {'vi': '{name}: {count}', 'en': '{count} for {name}',
                  'placeholders': {'name': 'string', 'count': 'integer'}}
        normalized, formats = normalize_placeholders('sample', source)
        self.assertEqual('{0}: {1}', normalized['vi'])
        self.assertEqual('{1} for {0}', normalized['en'])
        self.assertEqual({'{0}': '%1$s', '{1}': '%2$d'}, formats)
        with self.assertRaisesRegex(ValueError, 'Named placeholder mismatch'):
            normalize_placeholders('sample', {**source, 'en': '{count}'})

    def test_backup_size_exports_as_a_formatted_android_string(self):
        key = 'copy_luu_tren_thiet_bi_0_kb_c7f8bb'
        for qualifier in ('values', 'values-en', 'values-vi'):
            resource = ET.parse(ROOT / f'core-ui/src/main/res/{qualifier}/app_copy.xml')
            entry = resource.find(f"./string[@name='{key}']")
            with self.subTest(qualifier=qualifier):
                self.assertIn('%1$d KB', entry.text)
                self.assertNotEqual('false', entry.get('formatted'))

    def test_task_progress_exports_platform_plural_forms(self):
        key = 'copy_0_1_viec_da_xong_cf7a5d'
        expected_forms = {'values': {'other'}, 'values-vi': {'other'}, 'values-en': {'one', 'other'}}
        for qualifier, expected in expected_forms.items():
            resource = ET.parse(ROOT / f'core-ui/src/main/res/{qualifier}/app_copy.xml')
            entries = {
                item.get('quantity'): item.text for item in resource.findall(f"./plurals[@name='{key}']/item")
            }
            with self.subTest(qualifier=qualifier):
                self.assertEqual(expected, entries.keys())
                for text in entries.values():
                    self.assertIn('%1$d/%2$d', text)
                if qualifier == 'values-en':
                    self.assertEqual('%1$d/%2$d task done', entries['one'])
                    self.assertEqual('%1$d/%2$d tasks done', entries['other'])

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
