package com.doinner.csys.permission;

public class CoursePermissionException extends RuntimeException {
    private final int status;

    public CoursePermissionException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
