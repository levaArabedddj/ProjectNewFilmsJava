package com.example.ElasticSearch;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.*;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

//    @Bean
//    public RestClient lowLevelRestClient() {
//        String auth = "Basic " + Base64.getEncoder()
//                .encodeToString((elasticUser + ":" + elasticPass)
//                        .getBytes(StandardCharsets.UTF_8));
//
//        Header[] headers = new Header[]{
//                new BasicHeader(HttpHeaders.AUTHORIZATION, auth),
//                new BasicHeader("X-Elastic-Product", "Elasticsearch")
//                // НЕ указываем Content-Type
//        };
//
//        return RestClient.builder(HttpHost.create(elasticUri))
//                .setDefaultHeaders(headers)
//                .build();
//    }

// для бонсая попытка передавать залоговки нудные
    @Bean
    public RestClient lowLevelRestClient() throws URISyntaxException {
        String auth = "Basic " + Base64.getEncoder()
                .encodeToString((elasticUser + ":" + elasticPass)
                        .getBytes(StandardCharsets.UTF_8));

        Header[] headers = new Header[]{
                new BasicHeader(HttpHeaders.AUTHORIZATION, auth)
        };

        RestClientBuilder builder = RestClient.builder(HttpHost.create(elasticUri))
                .setDefaultHeaders(headers)
                .setHttpClientConfigCallback(httpClientBuilder ->
                        httpClientBuilder.addInterceptorLast((HttpRequest request, HttpContext context) -> {
                            // Удаляем старый Content-Type
                            request.removeHeaders(HttpHeaders.CONTENT_TYPE);
                            // Ставим обычный
                            request.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
                        })
                );

        return builder.build();
    }




    @Bean
    public ElasticsearchClient elasticsearchClient(RestClient lowLevelRestClient) {
        ElasticsearchTransport transport =
                new RestClientTransport(lowLevelRestClient, new JacksonJsonpMapper());
        return new ElasticsearchClient(transport);
    }
}

