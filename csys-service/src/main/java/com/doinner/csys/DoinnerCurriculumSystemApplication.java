package com.doinner.csys;


import com.doinner.common.security.config.ApplicationConfig;
import com.doinner.common.security.feign.FeignAutoConfiguration;
import com.doinner.common.swagger.annotation.EnableCustomSwagger2;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动类
 *
 * @author liuwei
 * @date 2021-10-18
 */

@EnableScheduling
// 表示通过aop框架暴露该代理对象,AopContext能够访问
@EnableAspectJAutoProxy(exposeProxy = true)
// 开启线程异步执行
@EnableAsync
// 自动加载类
@Import({ApplicationConfig.class, FeignAutoConfiguration.class})
@EnableCustomSwagger2
@EnableFeignClients(basePackages = {"com.doinner", "com.doinner"})
@SpringBootApplication(scanBasePackages = {"com.doinner", "com.doinner", "com.agileai.dataparser"},  exclude = {DataSourceAutoConfiguration.class})
@MapperScan(basePackages = {"com.doinner.csys.dao","com.agileai.dataparser.mapper"})
public class DoinnerCurriculumSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(DoinnerCurriculumSystemApplication.class, args);
        System.out.println(" 培养方案模块启动成功！");
    }

}
