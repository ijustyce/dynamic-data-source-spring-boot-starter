package top.kpromise.dynamicdatasource.conf;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "dynamic.datasource")
@Setter
@Getter
public class DbNameConfig {

    private List<String> name;
}
