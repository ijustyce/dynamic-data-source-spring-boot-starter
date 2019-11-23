package top.kpromise.dynamicdatasource.conf;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "dynamic", ignoreInvalidFields = true)
@Setter
@Getter
public class DbConfig {

    private final Map<String, DbInfo> datasource = new HashMap<>();
}
