"""Offline SQL semantics check: python verify_views.py <sqlglot/duckdb dependency path>."""
from pathlib import Path
import sys

if len(sys.argv) > 1:
    sys.path.insert(0, sys.argv[1])
import duckdb
import sqlglot
from sqlglot import exp

root = Path(__file__).resolve().parent
db = duckdb.connect()
db.execute("CREATE MACRO FIND_IN_SET(value, csv) AS COALESCE(list_position(string_split(csv, ','), value), 0)")
ddl = (root.parents[1] / 'doinner-curriculum-test-3.2.sql').read_text(encoding='utf-8')
tables = ['t_csys_course', 't_csys_training_scheme', 't_csys_training_scheme_ref_course',
          't_csys_scheme_ref_major', 't_csys_std_major', 't_csys_training_scheme_course_schedule',
          't_csys_teaching_programme_instance_extract', 't_csys_teaching_programme_instance_ex_ref_major']
for name in tables:
    block = ddl.split(f'CREATE TABLE `{name}`', 1)[1].split('ENGINE =', 1)[0]
    columns = []
    for line in block.splitlines():
        line = line.strip()
        if line.startswith('`'):
            col = line.split('`')[1]
            kind = line.split('`')[2].strip().lower().split()[0]
            kind = 'DOUBLE' if kind.startswith(('double', 'decimal', 'float')) else 'BIGINT' if kind.startswith(('bigint', 'tinyint', 'int')) else 'VARCHAR'
            columns.append(f'"{col}" {kind}')
    db.execute(f'CREATE TABLE {name} ({", ".join(columns)})')
db.execute('ALTER TABLE t_csys_course ADD invocation_sub_major_ids VARCHAR')
db.execute('CREATE TABLE t_csys_course_ref_schedule(course_id BIGINT, time_Week DOUBLE, unit VARCHAR)')
db.execute('CREATE TABLE sys_dict_data(dict_type VARCHAR, dict_value VARCHAR, dict_label VARCHAR)')
db.execute('CREATE TABLE sys_dept(dept_id BIGINT, dept_name VARCHAR)')

def insert(table, **row):
    db.execute(f'INSERT INTO {table} ({",".join(row)}) VALUES ({",".join("?" for _ in row)})', list(row.values()))

def course(cid, sid=1, **extra):
    row = dict(id=cid, name=f'C{cid}', code=f'C{cid}', type=1, sysflag=0, course_attr=1,
               hours=16, teach_hours=16, credit=1)
    row.update(extra)
    insert('t_csys_course', **row)
    insert('t_csys_training_scheme_ref_course', scheme_id=sid, course_id=cid)

for sid in (1, 2, 3):
    insert('t_csys_training_scheme', id=sid, name=f'S{sid}', major_id=100, instance_id=sid, education_level='1', sysflag=2 if sid == 2 else 0)
for mid in (101, 102):
    insert('t_csys_std_major', id=mid, code=f'D{mid}', name=f'M{mid}', sysflag=0)
    insert('t_csys_scheme_ref_major', scheme_id=1, major_id=mid)
insert('t_csys_scheme_ref_major', scheme_id=1, major_id=101)
course(1)  # Shared course with NULL module.
insert('t_csys_training_scheme_ref_course', scheme_id=1, course_id=1)
course(2, invocation_sub_major_ids='101,102', sub_major_id=999)
course(3, sub_major_id=101)
course(4, type=2, time_Week=3, unit='1', credit=999)
course(5, type=4, time_Week=24, unit='2', credit=999)
course(6, sysflag=2)
course(7, sid=2)
course(8, type=2, major_id=999, time_Week=2, unit='1')
course(9, source_id=90)
course(10, source_id=90, sub_major_id=101, hours=32, credit=2)
course(11, source_id=90, sub_major_id=101, hours=16, credit=1)  # Historical copies remain independent.
course(12, invocation_sub_major_ids='')  # Explicit empty scope belongs to no direction.
course(13, type=2, unit='1', time_Week=9)
insert('t_csys_course_ref_schedule', course_id=13, unit='1', time_Week=1)
insert('t_csys_course_ref_schedule', course_id=13, unit='2', time_Week=8)
course(14, type=2, unit='1', time_Week=24)
insert('t_csys_course_ref_schedule', course_id=14, unit='2', time_Week=24)
for cid, duration in [(15, 20), (16, 28)]:
    course(cid, type=4, unit='2', time_Week=duration)
for flag, term in [(0, 1), (2, 2)]:
    insert('t_csys_training_scheme_course_schedule', scheme_id=1, course_id=1, sysflag=flag, checked=1, term=term, teach_hours=16)
for mid, cid, hours in [(101, 10, 32), (102, 9, 48)]:
    insert('t_csys_training_scheme_course_schedule', scheme_id=1, course_id=cid, sysflag=0, checked=1,
           term=3, teach_hours=hours)
    insert('t_csys_teaching_programme_instance_extract', id=mid, instance_Id=1, sysflag=0, total_Credit=str(mid))
    insert('t_csys_teaching_programme_instance_ex_ref_major', extract_id=mid, major_id=mid)
insert('t_csys_training_scheme', id=4, name='Legacy', sub_major_id=101, sysflag=0, education_level='1')
course(17, sid=4)
course(18, sid=3)  # No direction configured: do not silently drop the course.
insert('t_csys_training_scheme', id=5, name='Empty', sysflag=0, education_level='1')

for filename in ('01-course-time-credit.sql', '02-hours-credits.sql', '03-scheme-import.sql'):
    stmt = sqlglot.parse((root / filename).read_text(encoding='utf-8'), read='mysql')[-1]
    query = stmt.expression.transform(lambda n: n.this.copy() if isinstance(n, exp.Collate) else n)
    query = query.transform(lambda n: exp.Cast(this=n.this.copy(), to=exp.DataType.build('VARCHAR'))
                            if isinstance(n, exp.Cast) and n.args['to'].this == exp.DataType.Type.CHARACTER_SET else n)
    db.execute(f'CREATE VIEW {stmt.this.name} AS {query.sql(dialect="duckdb")}')

def rows(view):
    cur = db.execute(f'SELECT * FROM {view}')
    return [dict(zip([d[0] for d in cur.description], row)) for row in cur.fetchall()]

metrics = {r['course_id']: r for r in rows('v_csys_course_time_credit')}
assert 6 not in metrics
assert metrics[4]['weeks'] == 3 and metrics[4]['hours'] == 0 and metrics[4]['credits'] == 3
assert metrics[5]['hours'] == 24 and metrics[5]['credits'] == 1.5
assert metrics[13]['unit_name'] == '混合（学时/周）' and metrics[13]['credits'] == 1.5
assert metrics[13]['weeks'] == 1 and metrics[13]['hours'] == 8
assert metrics[14]['unit_name'] == '学时' and metrics[14]['credits'] == 1.5
assert metrics[15]['credits'] == 1 and metrics[16]['credits'] == 2
summary = rows('v_csys_scheme_hours_credits')
totals = {r['专业方向ID']: r for r in summary if r['培养方案ID'] == 1 and r['行类型'] == '合计'}
assert set(totals) == {101, 102}
assert totals[101]['小计学时'] == 200, totals
assert totals[102]['小计学时'] == 152, totals
assert totals[101]['小计周数'] == totals[102]['小计周数'] == 4
assert float(totals[101]['小计学分']) == 16.5, totals
assert float(totals[102]['小计学分']) == 13.5, totals
assert all(r['培养方案ID'] != 2 for r in summary)
assert any(r['培养方案ID'] == 3 and r['专业方向ID'] is None for r in summary)
assert any(r['培养方案ID'] == 5 and r['行类型'] == '合计' and r['小计学时'] == 0 for r in summary)
assert all(r['专业方向编号'] == f'D{r["专业方向ID"]}' for r in summary if r['专业方向ID'] is not None)
all_imported = rows('v_csys_training_scheme_import')
keys = [(r['培养方案ID'], r['专业方向ID'], r['课程ID']) for r in all_imported]
assert len(keys) == len(set(keys)), 'Duplicate scheme/direction/course rows'
by_direction = {mid: {r['课程编号']: r for r in all_imported if r['培养方案ID'] == 1 and r['专业方向ID'] == mid} for mid in (101, 102)}
assert set(by_direction[101]) == {'C1', 'C2', 'C3', 'C4', 'C5', 'C10', 'C11', 'C13', 'C14', 'C15', 'C16'}
assert set(by_direction[102]) == {'C1', 'C2', 'C4', 'C5', 'C9', 'C13', 'C14', 'C15', 'C16'}
for mid, items in by_direction.items():
    assert sum(r['实际学时'] or 0 for r in items.values()) == totals[mid]['小计学时']
    assert sum(r['学分'] or 0 for r in items.values()) == float(totals[mid]['小计学分'])
    assert sum(r['周数'] or 0 for r in items.values()) == totals[mid]['小计周数']
    assert all(r['专业名称'] == f'M{mid}' and r['专业号'] == f'D{mid}' and r['专业总学分要求'] == str(mid) for r in items.values())
    assert items['C10' if mid == 101 else 'C9']['学期学时3'] == (32 if mid == 101 else 48)
    assert items['C1']['学期学时3'] is None
assert any(r['培养方案ID'] == 4 and r['专业方向ID'] == 101 for r in all_imported)
assert any(r['培养方案ID'] == 3 and r['专业方向ID'] is None for r in all_imported)
assert not any(r['培养方案ID'] == 2 or r['课程编号'] in ('C6', 'C7', 'C8', 'C12') for r in all_imported)
imported = by_direction[101]
assert imported['C1']['开课学期1'] == 1 and imported['C1']['开课学期2'] is None
assert imported['C4']['总学时'] == 3 and imported['C4']['单位'] == '周'
assert imported['C5']['总学时'] == 24 and imported['C5']['学分'] == 1.5
assert len(imported['C1']) == 44
print('PASS: 3 MySQL view definitions parsed; direction scope, totals, duplicate refs, independent copies, empty schemes, sysflag=2, units, mixed detail and backend credit rounding checked offline.')
