import re,unittest
from catalog import ROOT
from design_tokens import mask

class DesignTokenTests(unittest.TestCase):
    def test_screen_code_does_not_reintroduce_inline_color_or_unit_literals(self):
        findings=[]
        for file in ROOT.glob('*/src/main/**/*.kt'):
            relative=file.relative_to(ROOT).as_posix()
            if '/design/' in relative or '/previews/' in relative or 'Preview' in file.name:continue
            source=mask(file.read_text(encoding='utf-8-sig'))
            for match in re.finditer(r'\bColor\(\s*0x[0-9a-fA-F]+\s*\)|(?<![\w.])\d+(?:\.\d+)?f?\.(?:dp|sp)\b',source):
                findings.append(f'{relative}: {match.group()}')
        self.assertEqual([],findings)

    def test_all_token_references_resolve_and_jvm_getters_are_unique(self):
        directory=ROOT/'core-ui/src/main/java/com/mamxanh/app/presentation/ui/design'
        objects={}
        for file in directory.glob('*.kt'):
            names=re.findall(r'\bval\s+(\w+)\s*=',file.read_text(encoding='utf-8'))
            getters=[name[0].upper()+name[1:] for name in names]
            self.assertEqual(len(getters),len(set(getters)),file.name)
            objects[file.stem]=set(names)
        findings=[]
        for file in ROOT.glob('*/src/main/**/*.kt'):
            source=mask(file.read_text(encoding='utf-8-sig'))
            for owner,name in re.findall(r'\b(AppColors|AppDimensions|AppTypography|AppIcons|AppSpacing|AppRadii|AppOpacity|AppThemeEffects)\.(\w+)',source):
                if name not in objects.get(owner,set()):findings.append(f'{file.relative_to(ROOT)}: {owner}.{name}')
        self.assertEqual([],findings)

if __name__=='__main__':unittest.main()
