"""Offline MySQL syntax/numbering checks. Pass dependency directory containing duckdb/sqlglot."""
from pathlib import Path
import sys
if len(sys.argv) > 1:
    sys.path.insert(0, sys.argv[1])
import duckdb
import sqlglot
from sqlglot import exp

root = Path(__file__).parent
db = duckdb.connect()
db.execute('CREATE TABLE t_csys_course(id BIGINT, name VARCHAR, type INTEGER, version VARCHAR, education_level VARCHAR, college_id BIGINT, sysflag INTEGER, code VARCHAR)')
db.execute('CREATE TABLE sys_dept(dept_id BIGINT, dept_name VARCHAR)')
db.execute("INSERT INTO sys_dept VALUES (1, '理学院'),(2, '系统工程学院'),(3, '新学院')")
def course(id, code=None, type=1, version='2026', level='1', dept=1, flag=0):
    db.execute('INSERT INTO t_csys_course VALUES (?,?,?,?,?,?,?,?)', [id, str(id), type, version, level, dept, flag, code])
course(1, '26-JG-01-0008')
course(2)
course(3, '', type=2)
course(4, '  ', type=4)
course(5, flag=2)
course(6, level=None)
course(7, version='2')
course(8, dept=99)
course(9, dept=3)
course(10, level='8', dept=2)
course(11, type=3)
course(12, '26-DB-01-9999', level='2')
course(13, level='2')
course(14, '26-SG-01-bad', level='3')
course(15, level='3')
course(16, 'KEEP')
course(17, '26-JG-01-9998', flag=2)  # Backend max lookup excludes deleted rows.
course(18, level='custom')

def preview():
    for stmt in sqlglot.parse((root / '20261116_preview_missing_course_codes.sql').read_text(encoding='utf-8'), read='mysql'):
        if stmt is None or isinstance(stmt, exp.Select):
            continue
        if isinstance(stmt, exp.Drop):
            stmt.set('temporary', False)
        for pad in stmt.find_all(exp.Pad):
            pad.set('expression', exp.Cast(this=pad.expression.copy(), to=exp.DataType.build('INT')))
        query = stmt.sql(dialect='duckdb')
        db.execute(query)

preview()
planned = dict(db.execute('SELECT id,new_code FROM tmp_practice_code_plan').fetchall())
assert planned == {2:'26-JG-01-0009',3:'26-JG-01-0010',4:'26-JG-01-0011',9:'26-JG-00-0001',10:'26-JY-06-0001',11:'26-JG-01-0012',13:'26-DB-01-10000',18:'26-custom-01-0001'}, planned
update_sql = (root / '20261116_update_missing_course_codes.sql').read_text(encoding='utf-8')
parsed = sqlglot.parse(update_sql, read='mysql')
update = next(stmt for stmt in parsed if isinstance(stmt, exp.Update))
# DuckDB uses UPDATE FROM instead of MySQL UPDATE JOIN; preserve SET and WHERE predicates.
target = update.this.copy()
join = target.args.pop('joins')[0]
update.set('this', target)
update.set('from_', exp.From(this=join.this.copy()))
update.set('where', exp.Where(this=exp.and_(update.args['where'].this, join.args['on'])))
for assignment in update.expressions:
    assignment.this.set('table', None)
db.execute('BEGIN')
db.execute(update.sql(dialect='duckdb'))
for id, code in planned.items():
    assert db.execute('SELECT code FROM t_csys_course WHERE id=?',[id]).fetchone()[0] == code
assert db.execute('SELECT code FROM t_csys_course WHERE id=16').fetchone()[0] == 'KEEP'
assert db.execute('SELECT code FROM t_csys_course WHERE id=5').fetchone()[0] is None
db.execute('ROLLBACK')
assert db.execute('SELECT code FROM t_csys_course WHERE id=2').fetchone()[0] is None
db.execute(update.sql(dialect='duckdb'))
preview()
assert db.execute('SELECT COUNT(*) FROM tmp_practice_code_plan').fetchone()[0] == 0
print('PASS: preview/update parsed; 8 mappings; empty/whitespace, types 1-4, deleted exclusion, invalid fields, unknown college/level, suffix 10000, malformed suffix, rollback and rerun checked offline.')
