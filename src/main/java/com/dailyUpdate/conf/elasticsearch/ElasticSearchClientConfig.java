package com.dailyUpdate.conf.elasticsearch;

import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;

@Configuration
@ConditionalOnClass(RestClient.class)
@EnableConfigurationProperties(ElasticSearchProperties.class)
public class ElasticSearchClientConfig extends AbstractElasticsearchConfiguration {

    @Autowired
    private ElasticSearchProperties properties;

    @Override
    public RestHighLevelClient elasticsearchClient() {

        ClientConfiguration.MaybeSecureClientConfigurationBuilder maybeSecureClientConfigurationBuilder = ClientConfiguration.builder().connectedTo(properties.getHostAndPorts());

        maybeSecureClientConfigurationBuilder.withConnectTimeout(properties.getConnectTimeOut()).withSocketTimeout(properties.getSockerTimeOut());

        String username = properties.getUsername();
        String password = properties.getPassword();
        if(StringUtils.isNotBlank(username)){
            maybeSecureClientConfigurationBuilder.withBasicAuth(username,password);
        }

        return RestClients.create(maybeSecureClientConfigurationBuilder.build()).rest();
    }


}
