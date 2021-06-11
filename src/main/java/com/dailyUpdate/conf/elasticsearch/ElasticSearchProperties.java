package com.dailyUpdate.conf.elasticsearch;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "es.high-level-client")
public class ElasticSearchProperties {

    private String[] hostAndPorts;

    // 连接超时时间
    private int connectTimeOut = 1000;
    private int sockerTimeOut = 30000;

    private String username;
    private String password;


}
