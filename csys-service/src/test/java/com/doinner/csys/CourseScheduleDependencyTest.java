package com.doinner.csys;

import com.doinner.csys.service.CommonService;
import com.doinner.csys.service.CurriculumService;
import com.doinner.csys.service.TrainingService;
import com.doinner.csys.service.impl.CurriculumServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class CourseScheduleDependencyTest {
    @Test
    void startsWithoutCircularReferencesAndResolvesScheduleServiceOnUse() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.getDefaultListableBeanFactory().setAllowCircularReferences(false);
            for (Field field : CurriculumServiceImpl.class.getDeclaredFields()) {
                if (field.getType() == TrainingService.class) continue;
                if (field.isAnnotationPresent(Resource.class) || field.isAnnotationPresent(Autowired.class)) {
                    context.getBeanFactory().registerSingleton(field.getName(), mock(field.getType()));
                }
            }
            TrainingService training = mock(TrainingService.class);
            // Reproduce common -> curriculum -> training -> common using the actual curriculum injection.
            context.registerBean("commonService", CommonService.class, () -> {
                context.getBean(CurriculumService.class);
                return mock(CommonService.class);
            });
            context.registerBean("curriculumService", CurriculumServiceImpl.class);
            context.registerBean("trainingService", TrainingService.class, () -> {
                context.getBean(CommonService.class);
                return training;
            });
            assertDoesNotThrow(context::refresh);
            TrainingService dependency = (TrainingService) ReflectionTestUtils.getField(
                    context.getBean(CurriculumService.class), "trainingService");
            dependency.setCourseSchedule(null, Collections.emptyList());
            verify(training).setCourseSchedule(null, Collections.emptyList());
        }
    }
}
