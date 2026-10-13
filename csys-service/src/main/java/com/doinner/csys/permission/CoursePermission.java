package com.doinner.csys.permission;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CoursePermission {
    CourseResource resource() default CourseResource.COURSE;
    String value();
    /** Entity being updated: compare supplied owner fields with the persisted row. */
    String request() default "";
    /** For save interfaces: new plans resolve a course, new plan children resolve a plan. */
    boolean upsert() default false;
    /** File generation does its final locked check immediately before binding the file. */
    boolean transactional() default true;
}
