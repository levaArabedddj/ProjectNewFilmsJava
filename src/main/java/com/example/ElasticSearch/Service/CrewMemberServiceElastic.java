package com.example.ElasticSearch.Service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.example.ElasticSearch.ClassDocuments.CrewMemberDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CrewMemberServiceElastic {

    private final ElasticsearchClient elasticConfiguration;

    @Autowired
    public CrewMemberServiceElastic(ElasticsearchClient elasticConfiguration) {
        this.elasticConfiguration = elasticConfiguration;
    }


    public void indexCrewMember(CrewMemberDocument crewMember){

        try {
            IndexRequest<CrewMemberDocument> request = IndexRequest.of(i -> i.
                    index("crew_members").
                    id(crewMember.getId()).
                    document(crewMember));
            IndexResponse response = elasticConfiguration.index(request);
            System.out.println("Документ создан , результат " + response);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public List<CrewMemberDocument> getCrewMember(){

        try {
            SearchRequest search = SearchRequest.of( s -> s.
                    index("crew_members").
                    query(Query.of( q -> q.matchAll( m -> m ))));
            SearchResponse<CrewMemberDocument> response =
                    elasticConfiguration.
                            search(search,CrewMemberDocument.class);

            return response.hits().hits().stream()
                    .map( hit -> hit.source())
                    .collect(Collectors.toList());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
