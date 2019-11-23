package top.kpromise.dynamicdatasource.conf;

import lombok.Data;

@Data
public class DbInfo {

    private String password;
    private String url;
    private String username;
    private String type;
    private String driverClassName;
}
