package com.example.ElasticSearch.Service;



import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import com.example.ElasticSearch.ClassDocuments.ActorDocument;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.example.Entity.ActorProfiles;
import com.example.Entity.Actors;
import com.example.Entity.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActorService {

    private final ElasticsearchClient elasticsearchClient;
    private final String  INDEX = "actors";

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

    public ActorDocument mapToElastic(Actors actors, Users users, ActorProfiles actorProfiles) {
        ActorDocument actorDocument = new ActorDocument();
        actorDocument.setId(String.valueOf(actors.getId()));
        actorDocument.setName(actors.getName());
        actorDocument.setSurName(actors.getSurName());
        actorDocument.setSalaryPerHour(actors.getSalaryPerHour());

        actorDocument.setGmail(users.getGmail());
        actorDocument.setGender(String.valueOf(actorProfiles.getGender()));
        actorDocument.setBiography(actorProfiles.getBiography());

        actorDocument.setSkills(actorProfiles.getSkills());
        actorDocument.setLanguages(actorProfiles.getLanguages());
        actorDocument.setExperience(actorProfiles.getExperience());
        actorDocument.setProfilePhotoUrl(actorProfiles.getProfile_photo_url());
        actorDocument.setNumberPhone(actorProfiles.getNumberPhone());
        return actorDocument;
    }


    public List<ActorDocument> getAllActorsByLanguages(String language) throws IOException {
        var response = elasticsearchClient.search(
                s-> s.index(INDEX).
                        query( q-> q.term(
                                t-> t.field("languages_actor.keyword")
                                        .value(
                                                v -> v.stringValue(language))
                        )), ActorDocument.class);

        return response.hits().hits()
                .stream()
                .map( h -> h.source())
                .toList();

    }


    /**
     * Поиск актёров по email (поле gmail)
     */
    public List<ActorDocument> getAllActorsByEmail(String email) throws IOException {
        var response = elasticsearchClient.search(
                s -> s.index(INDEX)
                        .query(q -> q.term(t -> t
                                .field("gmail")
                                .value(v -> v.stringValue(email))
                        )),
                ActorDocument.class
        );

        return response.hits().hits()
                .stream()
                .map(h -> h.source())
                .toList();
    }

    /**
     * Поиск актёров по полу (поле gender)
     */
    public List<ActorDocument> getAllActorsByGender(String gender) throws IOException {
        // Используем match вместо term
        var response = elasticsearchClient.search(
                s -> s.index(INDEX)
                        .query(q -> q
                                .match(m -> m
                                        .field("gender")            // поле без .keyword
                                        .query(gender)              // строка, как пришла
                                )
                        ),
                ActorDocument.class
        );

        return response.hits().hits()
                .stream()
                .map(h -> h.source())
                .toList();
    }

}

