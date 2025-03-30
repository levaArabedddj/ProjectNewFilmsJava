package com.example.Entity;


import com.example.Enum.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Entity
@Table(name = "Director_Profiles")
@Data
public class DirectorProfiles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(0) // Минимальный стаж — 0 лет
    @Max(100) // Максимальный стаж — 100 лет (защита от некорректных данных)
    private int experienceYears;

    @NotBlank // Имя не должно быть пустым
    @Size(max = 50) // Ограничение по длине
    private String firstName;

    @NotBlank
    @Size(max = 50)
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Gender gender;


    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Некорректный формат номера телефона") // Валидация номера
    private String phoneNumber;


    private String biography;


    @Size(max = 50)
    private String mainGenre; // Основной жанр

    @Size(max = 255) // Известные работы, можно переделать в List<String> при необходимости
    private String famousWorks;

    @Size(max = 100)
    private String education;

    @Size(max = 255)
    private String awards; // Награды

    @URL
    @Size(max = 255)
    private String linkedinUrl;

    @URL
    @Size(max = 255)
    private String imdbProfileUrl; // Профиль IMDb

    @URL
    @Size(max = 255)
    private String portfolioUrl; // Портфолио или шоу-рил

    private String language;

    @URL
    @Size(max = 500)
    @Column(name = "profile_photo_url", length = 500)
    private String profilePhotoUrl;

    @OneToOne(mappedBy = "directorProfiles")
    private Director director;


}
