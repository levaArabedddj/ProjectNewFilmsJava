package com.example.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtoDirectorProfiles {
    private String firstName;
    private String lastName;
    private int experienceYears;
    private String biography;
    private String profilePhotoUrl;
    private String portfolioUrl; // Портфолио или шоу-рил
    private String language;
    private String imdbProfileUrl; // Профиль IMDb
    private String linkedinUrl;
    private String awards; // Награды
    private String education;
    private String famousWorks;
    private String mainGenre; // Основной жанр
}
