package com.example.RabbitMQ;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.example.DTO.MovieCreatedEvent;
import com.example.ElasticSearch.ClassDocuments.MovieDocument;
import com.example.ElasticSearch.Service.MovieElasticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ElasticSyncListener {

    private static final Logger log = LoggerFactory.getLogger(ElasticSyncListener.class);



    private final ElasticsearchClient esClient;

    private final MovieElasticService movieElasticService;

    @Autowired
    public ElasticSyncListener(ElasticsearchClient esClient, MovieElasticService movieElasticService) {
        this.esClient = esClient;
        this.movieElasticService = movieElasticService;
    }

    @RabbitListener(queues = ElasticConfigQueue.INDEX_QUEUE)
    public void onMovieCreated(MovieCreatedEvent evt) {
        MovieDocument doc = movieElasticService.mapToElasticForQueue(evt);
        String id = evt.getMovieId().toString();
        try {
            log.info("→ Начинаю индексировать в ES: index=movies, id={}", id);
            esClient.index(i -> i.index("movies").id(id).document(doc));
            log.info("← Успешно проиндексировано: id={}", id);
        } catch (IOException e) {
            log.error("❌ Ошибка при индексации id={} : {}", id, e.getMessage(), e);
        }
    }
}

