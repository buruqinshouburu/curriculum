package com.doinner.csys.permission;

import com.doinner.common.core.domain.Message;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CoursePermissionExceptionHandler {
    @ExceptionHandler(CoursePermissionException.class)
    public ResponseEntity<Message> handle(CoursePermissionException exception) {
        return ResponseEntity.status(exception.getStatus())
                .body(Message.of(exception.getStatus(), exception.getMessage()));
    }
}
