"""Offline SQL contract checks; pass the directory containing duckdb/sqlglot."""
from pathlib import Path
import sys
if len(sys.argv) > 1:
    sys.path.insert(0, sys.argv[1])
import duckdb
import sqlglot
from sqlglot import exp

root = Path(__file__).parent
db = duckdb.connect()
db.execute('CREATE TABLE t_csys_course(id BIGINT, name VARCHAR, type VARCHAR, sysflag INT, semester_schedule VARCHAR)')
db.execute('CREATE TABLE t_csys_training_scheme(id BIGINT, sysflag INT)')
db.execute('CREATE TABLE t_csys_course_ref_schedule(course_id BIGINT, semester_schedule VARCHAR)')
db.execute('CREATE TABLE t_csys_training_scheme_course_schedule(id BIGINT,scheme_id BIGINT,course_id BIGINT,term INT,teach_hours DOUBLE,practice_hours DOUBLE,theory_hours DOUBLE,checked INT,hours DOUBLE,credits DOUBLE,sysflag INT)')
db.execute('INSERT INTO t_csys_training_scheme VALUES (1,0),(2,0),(3,2)')
for course_id in range(1, 10):
    db.execute('INSERT INTO t_csys_course VALUES (?,?,?,?,?)',
               [course_id, str(course_id), str((course_id - 1) % 4 + 1), 2 if course_id == 6 else 0, '6'])
    if course_id != 5:  # Header only: no reliable detail marker.
        db.execute("INSERT INTO t_csys_course_ref_schedule VALUES (?,'6')", [course_id])
    if course_id == 9:  # No configured schedule: do not create one.
        continue
    for offset, term in [(0, 1), (1, 6)]:
        db.execute('INSERT INTO t_csys_training_scheme_course_schedule VALUES (?,?,?,?,?,?,?,?,?,?,?)',
                   [course_id * 10 + offset, 1, course_id, term, 12, 4, None, offset, 99, 3, 2 if course_id == 7 else 0])
db.execute('INSERT INTO t_csys_training_scheme_course_schedule VALUES (100,2,1,4,7,NULL,NULL,0,99,3,0),(101,3,1,2,8,2,NULL,1,99,3,0),(102,1,1,3,999,999,NULL,1,99,3,2)')
db.execute('UPDATE t_csys_training_scheme_course_schedule SET term=8 WHERE course_id=8')
db.execute('DELETE FROM t_csys_training_scheme_course_schedule WHERE id=81')
before = db.execute('SELECT * FROM t_csys_training_scheme_course_schedule ORDER BY id').fetchall()

def preview():
    for stmt in sqlglot.parse((root / '20261116_preview_four_year_final_term.sql').read_text(encoding='utf-8'), read='mysql'):
        if stmt is None:
            continue
        if isinstance(stmt, exp.Drop):
            stmt.set('temporary', False)
        db.execute(stmt.sql(dialect='duckdb'))

def migrate():
    for stmt in sqlglot.parse((root / '20261116_update_four_year_final_term.sql').read_text(encoding='utf-8'), read='mysql'):
        if isinstance(stmt, exp.Update):
            target = stmt.this.copy()
            join = target.args.pop('joins')[0]
            stmt.set('this', target)
            stmt.set('from_', exp.From(this=join.this.copy()))
            stmt.set('where', exp.Where(this=exp.and_(stmt.args['where'].this, join.args['on'])))
            for assignment in stmt.expressions:
                assignment.this.set('table', None)
            db.execute(stmt.sql(dialect='duckdb'))
        elif isinstance(stmt, exp.Delete):
            target = stmt.this.copy()
            joins = target.args.pop('joins')
            ids = exp.select('s.id').from_(target).where(stmt.args['where'].this.copy())
            ids.set('joins', joins)
            db.execute('DELETE FROM t_csys_training_scheme_course_schedule WHERE id IN (' + ids.sql(dialect='duckdb') + ')')
        elif isinstance(stmt, exp.Select):
            assert db.execute(stmt.sql(dialect='duckdb')).fetchall() == []

preview()
assert db.execute('SELECT COUNT(*) FROM tmp_four_year_plan').fetchone()[0] == 5
db.execute('BEGIN')
migrate()
for course_id in range(1, 5):
    assert db.execute('SELECT term,teach_hours,practice_hours,theory_hours,checked,hours,credits FROM t_csys_training_scheme_course_schedule WHERE scheme_id=1 AND course_id=? AND sysflag=0', [course_id]).fetchall() == [(8,24,8,None,1,99,3)]
assert db.execute('SELECT term,teach_hours FROM t_csys_training_scheme_course_schedule WHERE id=100').fetchone() == (8,7)
untouched = [row for row in before if row[2] in (5,6,7,8) or row[0] in (101,102)]
assert all(db.execute('SELECT * FROM t_csys_training_scheme_course_schedule WHERE id=?', [row[0]]).fetchone() == row for row in untouched)
db.execute('ROLLBACK')
assert db.execute('SELECT * FROM t_csys_training_scheme_course_schedule ORDER BY id').fetchall() == before
migrate()
preview()
assert db.execute('SELECT COUNT(*) FROM tmp_four_year_plan').fetchone()[0] == 0
assert db.execute('SELECT COUNT(*) FROM t_csys_training_scheme_course_schedule WHERE course_id=9').fetchone()[0] == 0
print('PASS: types 1-4, per-scheme isolation, sum once, credits preserved, deleted/header-only/unconfigured exclusion, rollback and idempotence.')
