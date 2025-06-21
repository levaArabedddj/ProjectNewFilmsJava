package com.example.RabbitMQ.ElasticTask;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.example.DTO.MovieCreatedEvent;
import com.example.ElasticSearch.ClassDocuments.MovieDocument;
import com.example.ElasticSearch.Service.MovieElasticService;
import com.example.RabbitMQ.DtoRabbitMQ.DtoShootingDayMQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class ElasticUpdateEventListener {

    private static final Logger log = LoggerFactory.getLogger(ElasticSyncListener.class);

    private final ElasticsearchClient esClient;

    private final MovieElasticService movieElasticService;

    public ElasticUpdateEventListener(ElasticsearchClient esClient, MovieElasticService movieElasticService) {
        this.esClient = esClient;
        this.movieElasticService = movieElasticService;
    }


    @RabbitListener(queues = "filmUpdateEl")
    public void onMovieUpdateCreated(MovieCreatedEvent evt) {
        MovieDocument doc = movieElasticService.mapToElasticForQueue(evt);
        String id = evt.getMovieId().toString();
        try {
            log.info("→ Начинаю индексировать в ES: index=movies, id={}", id);
            esClient.index(i -> i
                    .index("movies")
                    .id(String.valueOf(id))
                    .document(doc)
            );
            log.info("← Успешно проиндексировано: id={}", id);
        } catch (IOException e) {
            log.error("❌ Ошибка при индексации id={} : {}", id, e.getMessage(), e);
        }
    }

    @RabbitListener(queues = "filmDeleteQueue")
    public void onMovieDeleteCreated(long id) {

        try {
            esClient.delete( i -> i
                    .index("movies")
                    .id(String.valueOf(id)
                    ));
            log.info("← Документ успешно удален: id={}", id);
        } catch (IOException e) {
            log.error("❌ Ошибка при индексации id={} : {}", id, e.getMessage(), e);
        }
    }

}
