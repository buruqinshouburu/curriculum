package com.doinner.csys.permission;

/** Resource names are a code-owned SQL allowlist, never supplied by clients. */
public enum CourseResource {
    COURSE("t_csys_course", "id"),
    PLAN("t_csys_teaching_plan", "id"),
    TEACHER("t_csys_teaching_plan_teacher", "id"),
    SECTION("t_csys_teaching_plan_section", "id"),
    OBJECTIVE("t_csys_teaching_plan_objective", "id"),
    OBJECTIVE_REF("t_csys_teaching_plan_objective_ref", "id"),
    TASK_BACKGROUND("t_csys_teaching_plan_task_background", "id"),
    TRAINING_PURPOSE("t_csys_teaching_plan_training_purpose", "id"),
    CONTENT("t_csys_teaching_plan_content", "id"),
    TARGET_DESIGN("t_csys_teaching_plan_target_design", "id"),
    PRACTICE_ITEM("t_csys_teaching_plan_practice_item", "id"),
    PRACTICE_DETAIL("t_csys_teaching_plan_practice_item_detail", "id"),
    ASSESSMENT("t_csys_teaching_plan_assessment", "id"),
    TEXTBOOK("t_csys_teaching_plan_textbook", "id"),
    CONDITION("t_csys_teaching_plan_condition", "id"),
    PROCESS_STEP("t_csys_teaching_plan_process_step", "id"),
    REF("t_csys_teaching_plan_ref", "id"),
    COURSE_TARGET("t_csys_course_target", "course_target_id"),
    COURSE_CHAPTER("t_csys_course_chapter", "id"),
    COURSE_TEXTBOOK("t_csys_course_textbook", "id"),
    THEORY_PLAN("t_csys_course_teaching_theory_plan", "id"),
    PRACTICE_PLAN("t_csys_course_teaching_practice_plan", "id"),
    REVIEW("t_csys_course_review", "id"),
    KNOWLEDGE_DOMAIN("t_csys_knowledge_domain", "id"),
    KNOWLEDGE_UNIT("t_csys_knowledge_unit", "id"),
    KNOWLEDGE_POINT("t_csys_knowledge_point", "id");

    final String table;
    final String key;

    CourseResource(String table, String key) {
        this.table = table;
        this.key = key;
    }
}
