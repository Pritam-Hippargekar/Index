package com.elastic.Index.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class ElasticsearchClientConfig {
    @Bean
    public ElasticsearchClient elasticsearchClient(ElasticsearchConfig config, ObjectMapper objectMapper) {
        String[] hosts = config.getUris().split(":");
        // Create the low-level client
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(config.getUsername(), config.getPassword()));

        Header[] defaultHeaders = new Header[]{new BasicHeader("Content-Type", "application/json")};
       // Header[] defaultHeaders = new Header[]{new BasicHeader("Content-Type", "application/x-www-form-urlencoded")};
        RestClient restClient = RestClient.builder(
                        new HttpHost(hosts[0], Integer.parseInt(hosts[1]), config.getScheme()))
                .setHttpClientConfigCallback(httpAsyncClientBuilder ->
                        httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
                .setDefaultHeaders(defaultHeaders)
                .build();

        // Create the transport with a Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper(objectMapper));

        // And create the API client
        return new ElasticsearchClient(transport);
    }
}
