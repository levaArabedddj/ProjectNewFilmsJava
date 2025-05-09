package com.example.ElasticSearch.Service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.CompletionSuggestOption;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.Suggestion;
import com.example.ElasticSearch.ClassDocuments.MovieDocument;

import com.example.Entity.Movies;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;


@Service
public class MovieElasticService {

    @Autowired
    private final ElasticsearchClient client;
    private final String INDEX = "movies";

    public MovieElasticService(ElasticsearchClient client) {
        this.client = client;
    }


    public MovieDocument mapToElastic(Movies movie){
        MovieDocument document = new MovieDocument();
        document.setId(String.valueOf(movie.getId()));
        document.setTitle(movie.getTitle());
        document.setDescription(movie.getDescription());
        document.setGenre_film(movie.getGenre_film().name());
        return document;
    }

    public List<MovieDocument> getMovieByTitle(String title, int size) throws IOException {

        SearchResponse<MovieDocument> resp = client.search(s -> s
                        .index(INDEX)
                        .size(size)
                        .query(q -> q
                                .match(m -> m
                                        .field("title")
                                        .query(title)
                                        .fuzziness("AUTO")
                                        .maxExpansions(50)
                                        .prefixLength(1)
                                )
                        ),
                MovieDocument.class
        );

        return resp.hits().hits().stream()
                .map(Hit::source).toList();
    }

    public List<MovieDocument> getGenreBy(String genre) throws IOException {

    var response = client.search(
             s -> s.index(INDEX).
                     query( q -> q.term(
                             t -> t.field("genre_film.keyword")
                                     .value( v -> v.stringValue(genre))
                     )
                     ),
            MovieDocument.class);

    return response.hits().hits()
            .stream()
            .map(h -> h.source()).toList();
    }

    public List<MovieDocument> complexSearch(String title, String genre, String description, int from, int size ) throws IOException {

        var response = client.search(s -> s.index(INDEX)
                        .from(from)
                        .size(size)
                        .query(q ->
                                q.bool(b ->
                                        b.must(m1 -> m1. match
                                                        (m -> m. field("title").
                                                                query(title).
                                                                fuzziness("AUTO").
                                                                maxExpansions(50).
                                                                prefixLength(1)))
                                                .filter(f -> f. term(t -> t. field("genre_film.keyword").
                                                        value(v -> v.stringValue(genre))))
                                                .must(m2 -> m2. match(m -> m. field("description").
                                                        query(description).
                                                        fuzziness("AUTO").
                                                        maxExpansions(50).
                                                        prefixLength(1)))
                                )).
                        sort(
                                st -> st. field(f -> f. field("title.keyword"). order(SortOrder.Asc))
                        ),


                MovieDocument.class);

        return response.hits().hits().stream().map( h -> h.source()).toList();
    }






}
