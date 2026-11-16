"""Exercise the mapper's generated-status predicates against an in-memory SQL fixture."""
import sqlite3
import unittest
import xml.etree.ElementTree as ET
from pathlib import Path

ROOT = Path(__file__).resolve().parents[3]
MAPPER = ROOT / 'csys-service/src/main/resources/mapper/csys/TeachingPlanMapper.xml'


class GeneratedStatusTest(unittest.TestCase):
    def setUp(self):
        self.query = ET.parse(MAPPER).getroot().find("select[@id='selectTeachingPlanPage']")
        self.branch = self.query.find("where/choose/when[@test='generatedStatus != null and generatedStatus == 1']")
        self.assertIsNotNone(self.branch, 'list SQL must apply generatedStatus before pagination')
        self.missing = self.query.find("where/choose/when[@test='generatedStatus != null and generatedStatus == 0']")
        self.assertIsNotNone(self.missing)
        self.db = sqlite3.connect(':memory:')
        self.db.executescript('''
            CREATE TABLE course(id INTEGER, type INTEGER);
            CREATE TABLE plan(source_course_id INTEGER, file_id TEXT, file_name TEXT, sysflag INTEGER, current_flag INTEGER);
        ''')
        for kind in (1, 2, 3, 4):
            base = kind * 100
            self.db.executemany('INSERT INTO course VALUES (?, ?)', [(base + n, kind) for n in range(1, 8)])
            self.db.executemany('INSERT INTO plan VALUES (?, ?, ?, ?, ?)', [
                (base + 2, None, None, 0, 1),
                (base + 3, '', '', 0, 1),
                (base + 4, 'file-1', '', 0, 1),
                (base + 5, None, 'plan.docx', 0, 1),
                (base + 6, 'deleted', 'old.docx', 2, 1),
                (base + 7, 'old-version', 'old.docx', 0, 0),
            ])

    def tearDown(self):
        if hasattr(self, 'db'):
            self.db.close()

    def rows(self, kind, status, limit=''):
        branch = self.branch if status == 1 else self.missing if status == 0 else None
        predicate = ''.join(branch.itertext()).strip() if branch is not None else ''
        sql = '''SELECT course.id FROM course LEFT JOIN plan
                 ON course.id = plan.source_course_id AND plan.sysflag = 0 AND plan.current_flag = 1
                 WHERE course.type = ? ''' + predicate + ' ORDER BY course.id ' + limit
        return [row[0] for row in self.db.execute(sql, (kind,))]

    def test_generated_missing_and_clear_all_types(self):
        for kind in (1, 2, 3, 4):
            with self.subTest(kind=kind):
                base = kind * 100
                self.assertEqual(self.rows(kind, 1), [base + 4, base + 5])
                self.assertEqual(self.rows(kind, 0), [base + n for n in (1, 2, 3, 6, 7)])
                self.assertEqual(len(self.rows(kind, None)), 7)

    def test_filter_before_paging(self):
        self.assertEqual(self.rows(2, 1, 'LIMIT 1 OFFSET 1'), [205])
        self.assertEqual(len(self.rows(2, 1)), 2)

    def test_query_bean_accepts_parameter(self):
        source = (ROOT / 'csys-service-api/src/main/java/com/doinner/csys/domain/vo/TeachingPlanQueryVo.java').read_text(encoding='utf-8')
        for value in ('private Integer generatedStatus;', 'getGeneratedStatus()', 'setGeneratedStatus(Integer generatedStatus)'):
            self.assertIn(value, source)


if __name__ == '__main__':
    unittest.main()
