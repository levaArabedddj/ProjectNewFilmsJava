package com.example.ElasticSearch.Service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.function.Consumer;


// Класс созданный как пример для понятия как просто создавать индексы без прямых обращений в еластик серч
@Service
public class IndexService {

    private final ElasticsearchClient client;

    public IndexService(ElasticsearchClient client) {
        this.client = client;
    }


//     // Универсальный метод создания индекса, тут создается пример
//    // созданные основых параметров для индексов
//    public void createIndexIfNotExists(
//            String indexName,
//            Consumer<IndexSettings.Builder> settingsConfig,
//            Consumer<TypeMapping.Builder> mappingConfig
//    ) throws IOException {
//        boolean exists = client.indices()
//                .exists(e -> e.index(indexName))
//                .value();
//        if (exists) {
//            System.out.println("Index exists: " + indexName);
//            return;
//        }
//
//        CreateIndexResponse resp = client.indices().create(c -> {
//            c.index(indexName);
//            if (settingsConfig != null) {
//                c.settings(s -> {
//                    settingsConfig.accept(s);
//                    return s;
//                });
//            }
//            if (mappingConfig != null) {
//                c.mappings(m -> {
//                    mappingConfig.accept(m);
//                    return m;
//                });
//            }
//            return c;
//        });
//
//        System.out.println("Created index " + indexName + ": acknowledged=" + resp.acknowledged());
//    }
//
// // в это методе именно создание индексов с параметрами для каждого индекса
//    @PostConstruct
//    public void initIndices() throws IOException {
//        // --- 1. CrewMemberDocument индекс ---
//        createIndexIfNotExists(
//                "crew_members_delete",
//                s -> s
//                        .numberOfShards("1")
//                        .numberOfReplicas("1")
//                        .analysis(a -> a
//                                .analyzer("default", an -> an.standard(st -> st))
//                        ),
//                m -> m
//                        .properties("name",          p -> p.text(t -> t))
//                        .properties("surName",       p -> p.text(t -> t))
//                        .properties("salaryPerHour", p -> p.integer(i -> i))
//                        .properties("position",      p -> p.keyword(k -> k))
//                        .properties("expertise",     p -> p.text(t -> t))
//                        .properties("equipmentList", p -> p.text(t -> t))
//                        .properties("gender",        p -> p.keyword(k -> k))
//                        .properties("biography",     p -> p.text(t -> t))
//                        .properties("skills",        p -> p.text(t -> t))
//                        .properties("languages",     p -> p.text(t -> t))
//                        .properties("experience",    p -> p.text(t -> t))
//                        .properties("gmail",         p -> p.keyword(k -> k))
//                        .properties("workingHoursPerWeek", p -> p.integer(i -> i))
//        );
//
//        // --- 2. ActorDocument индекс ---
//        createIndexIfNotExists(
//                "actorsdelete",
//                s -> s
//                        .numberOfShards("1")
//                        .numberOfReplicas("1"),
//                m -> m
//                        .properties("name",            p -> p.text(t -> t))
//                        .properties("surName",         p -> p.text(t -> t))
//                        .properties("salaryPerHour",   p -> p.integer(i -> i))
//                        .properties("rating",          p -> p.integer(i -> i))
//                        .properties("gender",          p -> p.keyword(k -> k))
//                        .properties("biography",       p -> p.text(t -> t))
//                        .properties("skills",          p -> p.text(t -> t))
//                        .properties("languages",       p -> p.text(t -> t))
//                        .properties("experience",      p -> p.text(t -> t))
//                        .properties("profilePhotoUrl", p -> p.keyword(k -> k))
//                        .properties("gmail",           p -> p.keyword(k -> k))
//                        .properties("numberPhone",     p -> p.keyword(k -> k))
//        );
//    }
}


