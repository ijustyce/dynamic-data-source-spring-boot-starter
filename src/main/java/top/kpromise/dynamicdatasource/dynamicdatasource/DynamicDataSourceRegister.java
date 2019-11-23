package top.kpromise.dynamicdatasource.dynamicdatasource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DynamicDataSourceRegister implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private static final String DEFAULT_DATASOURCE_TYPE = "com.zaxxer.hikari.HikariDataSource";
    private DataSource defaultDataSource;
    private Map<String, DataSource> customDataSources = new HashMap<>();

    @Override
    public void setEnvironment(Environment environment) {
        initDefaultDataSource(environment);
        initCustomDataSources(environment);
    }

    private void initDefaultDataSource(Environment environment) {
        Map<String, Object> map = new HashMap<>();
        map.put("driver-class-name", environment.getProperty("spring.datasource.driver-class-name"));
        map.put("url", environment.getProperty("spring.datasource.url"));
        map.put("username", environment.getProperty("spring.datasource.username"));
        map.put("password", environment.getProperty("spring.datasource.password"));
        map.put("type", environment.getProperty("spring.datasource.type"));
        defaultDataSource = buildDataSource(map);
    }


    private void initCustomDataSources(Environment environment) {
        String dataSourcePrefix = environment.getProperty("dynamic.datasource.name");
        if (dataSourcePrefix == null) return;
        for (String dataSourceName : dataSourcePrefix.split(",")) {
            Map<String, Object> map = new HashMap<>();
            map.put("driver-class-name", environment.getProperty("dynamic.datasource." + dataSourceName + ".driver-class-name"));
            map.put("url", environment.getProperty("dynamic.datasource." + dataSourceName + ".url"));
            map.put("username", environment.getProperty("dynamic.datasource." + dataSourceName + ".username"));
            map.put("password", environment.getProperty("dynamic.datasource." + dataSourceName + ".password"));
            map.put("type", environment.getProperty("dynamic.datasource." + dataSourceName + ".type"));
            DataSource dataSource = buildDataSource(map);
            customDataSources.put(dataSourceName, dataSource);
        }
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("dataSource", this.defaultDataSource);
        DynamicDataSourceContextHolder.dataSourceIds.add("dataSource");

        targetDataSources.putAll(customDataSources);
        DynamicDataSourceContextHolder.dataSourceIds.addAll(customDataSources.keySet());

        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(DynamicDataSource.class);
        beanDefinition.setSynthetic(true);
        MutablePropertyValues mpv = beanDefinition.getPropertyValues();
        mpv.addPropertyValue("defaultTargetDataSource", defaultDataSource);
        mpv.addPropertyValue("targetDataSources", targetDataSources);
        beanDefinitionRegistry.registerBeanDefinition("dataSource", beanDefinition);
    }

    @SuppressWarnings("unchecked")
    private DataSource buildDataSource(Map<String, Object> map) {
        try {
            Object type = map.get("type");
            if (type == null) {
                type = DEFAULT_DATASOURCE_TYPE;// 默认DataSource
            }
            Class<? extends DataSource> dataSourceType = (Class<? extends DataSource>) Class.forName((String) type);
            String driverClassName = map.get("driver-class-name").toString();
            String url = map.get("url").toString();
            String username = map.get("username").toString();
            String password = map.get("password").toString();

            DataSourceBuilder factory = DataSourceBuilder.create().driverClassName(driverClassName).url(url)
                    .username(username).password(password).type(dataSourceType);
            return factory.build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
