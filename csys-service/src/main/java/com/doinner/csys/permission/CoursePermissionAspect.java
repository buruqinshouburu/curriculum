package com.doinner.csys.permission;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.lang.reflect.Method;

@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class CoursePermissionAspect {
    private final CoursePermissionService permissions;
    private final TransactionTemplate transaction;
    private final ExpressionParser parser = new SpelExpressionParser();

    public CoursePermissionAspect(CoursePermissionService permissions, PlatformTransactionManager manager) {
        this.permissions = permissions;
        this.transaction = new TransactionTemplate(manager);
    }

    @Around("@annotation(com.doinner.csys.permission.CoursePermission)")
    public Object check(ProceedingJoinPoint point) throws Throwable {
        Method method = AopUtils.getMostSpecificMethod(((MethodSignature) point.getSignature()).getMethod(),
                point.getTarget().getClass());
        CoursePermission annotation = method.getAnnotation(CoursePermission.class);
        MethodBasedEvaluationContext context = new MethodBasedEvaluationContext(point.getTarget(), method,
                point.getArgs(), new DefaultParameterNameDiscoverer());
        Object ids = expression(annotation.value(), context);
        Object request = annotation.request().isEmpty() ? null : expression(annotation.request(), context);
        CourseResource resource = annotation.resource();
        if (annotation.upsert() && request != null && new BeanWrapperImpl(request).getPropertyValue("id") == null) {
            resource = resource == CourseResource.PLAN ? CourseResource.COURSE : CourseResource.PLAN;
            request = null;
        }
        if (annotation.sourceCoursesOnly()) {
            if (resource != CourseResource.COURSE) throw new IllegalStateException("sourceCoursesOnly只用于课程主表");
            ids = permissions.sourceCourseIds(ids);
            if (((java.util.List<?>) ids).isEmpty()) return point.proceed();
        }
        final CourseResource checkedResource = resource;
        final Object checkedRequest = request;
        final Object checkedIds = ids;
        final Object input = point.getArgs().length == 0 ? null : point.getArgs()[0];
        if (!annotation.transactional()) {
            permissions.check(checkedResource, checkedIds, checkedRequest, false);
            return point.proceed();
        }
        // Start the transaction BEFORE permission reads/locks, including self-invoking batch writers.
        try {
            return transaction.execute(status -> {
                permissions.check(checkedResource, checkedIds, checkedRequest, true);
                if (checkedResource != CourseResource.COURSE)
                    permissions.validatePlanScope(checkedResource, checkedIds, input);
                try {
                    return point.proceed();
                } catch (RuntimeException | Error exception) {
                    throw exception;
                } catch (Throwable exception) {
                    throw new InvocationFailure(exception);
                }
            });
        } catch (InvocationFailure exception) {
            throw exception.getCause();
        }
    }

    private Object expression(String expression, MethodBasedEvaluationContext context) {
        try {
            return parser.parseExpression(expression).getValue(context);
        } catch (RuntimeException exception) {
            throw new CoursePermissionException(400, "课程权限参数缺失或格式错误");
        }
    }

    private static class InvocationFailure extends RuntimeException {
        InvocationFailure(Throwable cause) { super(cause); }
    }
}
