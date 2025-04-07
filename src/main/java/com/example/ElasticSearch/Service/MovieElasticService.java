package com.example.ElasticSearch.Service;

import com.example.ElasticSearch.ClassDocuments.MovieDocument;

import com.example.Entity.Movies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;

@Service
public class MovieElasticService {


    public MovieDocument mapToElastic(Movies movie){
        MovieDocument document = new MovieDocument();
        document.setId(String.valueOf(movie.getId()));
        document.setTitle(movie.getTitle());
        document.setDescription(movie.getDescription());
        document.setGenre_film(movie.getGenre_film().name());
        return document;
    }
}
