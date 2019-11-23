package top.kpromise.dynamicdatasource.dynamicdatasource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解，用于类和方法，指定使用的数据库，优先使用 方法上的注解，没有时从类上获取
 * name 对应 application.properties 里 设置 的 dynamic.datasoure.name
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)

public @interface DataSource {
    String name();
}
