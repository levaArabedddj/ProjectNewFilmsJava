package com.example.ElasticSearch.ClassDocuments;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class ActorDocument {
    @Id
    private String id;

    // Данные из сущности Actors
    private String name;
    private String surName;
    private int salaryPerHour;
    private int rating;

    // Данные из ActorProfiles
    private String gender;
    private String biography;
    private String skills;
    private String languages;
    private String experience;
    private String profilePhotoUrl;
    private String gmail;
    private String numberPhone;
}
