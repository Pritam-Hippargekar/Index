package com.elastic.Index.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.elasticsearch.rest")
public class ElasticsearchConfig {
    private String uris;
    private String hostname;
    private Integer port;
    private String username;
    private String password;
    private String scheme;
}
