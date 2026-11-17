import unittest
from kotlin_literals import literals,has_copy

class KotlinLiteralsTest(unittest.TestCase):
    def test_comments_and_chars_are_not_copy(self):
        text='// "not copy"\nval quote=\'"\'; /* nested /* "also not" */ fine */ val s="Hello"'
        self.assertEqual([v.template for v in literals(text)],['Hello'])
    def test_nested_templates_keep_expressions(self):
        value=literals('"Còn ${world.count(item.s("id"))} hạt · $name"')[0]
        self.assertEqual(value.template,'Còn {0} hạt · {1}')
        self.assertEqual(value.arguments,['world.count(item.s("id"))','name'])
    def test_nested_expression_braces_and_comments(self):
        value=literals('"Việc ${if (ready) { "Đã xong" } else { /* } */ "Chờ" }}"')[0]
        self.assertEqual(value.template,'Việc {0}')
        self.assertIn('/* } */',value.arguments[0])
    def test_escaped_and_raw_strings(self):
        self.assertEqual(literals(r'"Tên\n\"mầm\" \$giá"')[0].template,'Tên\n"mầm" $giá')
        self.assertEqual(literals('"""Mầm ${count}"""')[0].template,'Mầm {0}')
    def test_sql_and_symbols_are_not_translated(self):
        self.assertFalse(has_copy(literals('"SELECT COUNT(*) FROM occurrences WHERE category=\'Học tập\'"')[0]))
        self.assertFalse(has_copy(literals('"×"')[0]))
        self.assertTrue(has_copy(literals('"Nhập tên cây"')[0]))

    def test_vietnamese_copy_without_diacritics_is_not_missed(self):
        for text in ['Mua','Mua & gieo','Mua & gieo 1']:
            self.assertTrue(has_copy(literals('"'+text+'"')[0]))

if __name__=='__main__':unittest.main()
