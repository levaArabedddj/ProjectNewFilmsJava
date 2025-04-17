package com.example.ElasticSearch.ClassDocuments;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import lombok.Data;


@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class MovieDocument {
    @Id
    private String id;

    private String title;
    private String description;
    private String genre_film;

}

