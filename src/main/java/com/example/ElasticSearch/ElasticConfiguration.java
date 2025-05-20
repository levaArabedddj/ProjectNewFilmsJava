package com.example.ElasticSearch;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import jakarta.annotation.PostConstruct;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Base64;

@Configuration
public class ElasticConfiguration {

    @Value("${spring.elasticsearch.uris}")
    private String login;

    @Value("${spring.elasticsearch.username}")
    private String elasticUser;

    @Value("${spring.elasticsearch.password}")
    private String elasticPass;


    @Bean
    public RestClient lowLevelRestClient() {
        // 1) Basic auth
        BasicCredentialsProvider creds = new BasicCredentialsProvider();
        creds.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(elasticUser, elasticPass));

        // 2) Собираем builder по URI
        RestClientBuilder builder = RestClient.builder(HttpHost.create(login))
                .setHttpClientConfigCallback(httpClient ->
                        httpClient.setDefaultCredentialsProvider(creds)
                )
                // 3) Переопределяем заголовки по-умолчанию:
                .setDefaultHeaders(new Header[] {
                        // a) чтобы кластер 7.x принимал body
                        new BasicHeader("Content-Type", "application/json"),
                        // b) чтобы кластер распознавал, что это именно Elasticsearch
                        new BasicHeader("X-Elastic-Product", "Elasticsearch")
                });

        return builder.build();
    }


    @Bean
    public ElasticsearchClient elasticsearchClient(RestClient lowLevelRestClient) {
        ElasticsearchTransport transport =
                new RestClientTransport(lowLevelRestClient, new JacksonJsonpMapper());
        return new ElasticsearchClient(transport);
    }


}
