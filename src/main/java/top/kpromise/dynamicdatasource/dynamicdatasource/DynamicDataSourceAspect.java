package top.kpromise.dynamicdatasource.dynamicdatasource;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j
@Order(-10)
public class DynamicDataSourceAspect {

    @Pointcut("@within(DataSource)")
    public void dataSource() {
    }

    private DataSource findDataSource(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DataSource dataSource = method.getAnnotation(DataSource.class);
        if (dataSource == null) {
            try {
                dataSource = AnnotationUtils.findAnnotation(signature.getMethod().getDeclaringClass(), DataSource.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dataSource;
    }

    @Before("dataSource()")
    public void changeDataSource(JoinPoint joinPoint) {
        DataSource dataSource = findDataSource(joinPoint);
        if (dataSource == null) return;
        String databaseName = dataSource.name();
        if (!DynamicDataSourceContextHolder.isContainsDataSource(databaseName)) {
            log.error("{} === dataSource {} not exists，use default now", joinPoint.getSignature(), databaseName);
        } else {
            log.debug("use dataSource：" + databaseName);
            log.info("{} === use dataSource {} ", joinPoint.getSignature(), databaseName);
            DynamicDataSourceContextHolder.setDataSourceType(databaseName);
        }
    }

    @After("dataSource()")
    public void clearDataSource(JoinPoint joinPoint) {
        DataSource dataSource = findDataSource(joinPoint);
        if (dataSource == null) return;
        DynamicDataSourceContextHolder.clearDataSourceType();
    }
}
