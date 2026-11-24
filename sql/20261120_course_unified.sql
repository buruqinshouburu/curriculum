-- Apply before deploying csys-service. No historical flag or binding backfill.
ALTER TABLE t_csys_course ADD COLUMN unified_course TINYINT DEFAULT 0 COMMENT '是否统设课程：0否，1是';
