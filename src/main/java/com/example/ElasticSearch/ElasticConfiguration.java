package com.example.ElasticSearch;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import jakarta.annotation.PostConstruct;
import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
@Configuration
public class ElasticConfiguration {

    @Value("${spring.elasticsearch.uris}")
    private String elasticUri;      // например "https://localhost:9200"

    @Value("${spring.elasticsearch.username}")
    private String elasticUser;     // "elastic"

    @Value("${spring.elasticsearch.password}")
    private String elasticPass;     // твой пароль

    @Bean
    public RestClient lowLevelRestClient() {
        String auth = "Basic " + Base64.getEncoder()
                .encodeToString((elasticUser + ":" + elasticPass)
                        .getBytes(StandardCharsets.UTF_8));

        Header[] headers = new Header[]{
                new BasicHeader(HttpHeaders.AUTHORIZATION, auth),
                new BasicHeader("X-Elastic-Product", "Elasticsearch")
                // НЕ указываем Content-Type
        };

        return RestClient.builder(HttpHost.create(elasticUri))
                .setDefaultHeaders(headers)
                .build();
    }



    @Bean
    public ElasticsearchClient elasticsearchClient(RestClient lowLevelRestClient) {
        ElasticsearchTransport transport =
                new RestClientTransport(lowLevelRestClient, new JacksonJsonpMapper());
        return new ElasticsearchClient(transport);
    }
}

