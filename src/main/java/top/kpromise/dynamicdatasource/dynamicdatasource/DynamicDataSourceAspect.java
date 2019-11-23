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
@Order(-10)     // 确保在 @Transactional 之前执行
@Component
@Slf4j
public class DynamicDataSourceAspect {

    /**
     * 第一个* 代表任意的返回值
     * 包后面.. 表示当前包及其子包
     * 第二个* 表示类名，代表所有类
     * .*(..) 表示任何方法,括号代表参数 .. 表示任意参数
     */
   // @Pointcut("execution(public * top.kpromise..mapper..*.*(..))")
   // @Pointcut("@annotation(DataSource)")  //  @DataSource 标注的方法
    @Pointcut("@within(DataSource)")        //  @DataSource 标注的类和方法
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
            log.error("{} === 数据源 {} 不存在，使用默认的数据源", joinPoint.getSignature(), databaseName);
        } else {
            log.debug("使用数据源：" + databaseName);
            log.info("{} === 使用数据源 {} ", joinPoint.getSignature(), databaseName);
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
