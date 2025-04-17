package com.example.ElasticSearch.Service;



import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import com.example.ElasticSearch.ClassDocuments.ActorDocument;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActorService {

    private final ElasticsearchClient elasticsearchClient;

    @Autowired
    public ActorService(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    // Метод для индексации (создания) актёра
    public void indexActor(ActorDocument actor) {
        try {
            IndexRequest<ActorDocument> request = IndexRequest.of(i -> i
                    .index("actors")
                    .id(actor.getId())
                    .document(actor)
            );
            IndexResponse response = elasticsearchClient.index(request);
            System.out.println("Документ проиндексирован, результат: " + response.result());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод для получения всех актёров
    public List<ActorDocument> getAllActors() {
        try {
            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index("actors")
                    .query(Query.of(q -> q.matchAll(m -> m)))
            );
            SearchResponse<ActorDocument> searchResponse =
                    elasticsearchClient.search(searchRequest, ActorDocument.class);

            return searchResponse.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

