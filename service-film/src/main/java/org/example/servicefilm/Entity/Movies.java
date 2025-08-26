package org.example.servicefilm.Entity;



import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.servicefilm.DevelopmentStage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class Movies {

    //убрать связь с актерами и членами сьемочной группы , и настроить связь с юзерами и командами
    // убрать связь с юзером и настроить связь с режиссером , так как только режиссер может создавать фильмы

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private Genre genre_film;

    @ManyToOne
    @JoinColumn(name = "director_id", nullable = false)
    private Director director;

    private LocalDateTime dateTimeCreated;

    // Поле для отслеживания этапа разработки фильма
    @Enumerated(EnumType.STRING)
    @Column(name = "development_stage", nullable = false)
    private DevelopmentStage developmentStage;

    @PrePersist
    private void init(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
        dateTimeCreated = LocalDateTime.parse(LocalDateTime.now().format(formatter), formatter);
    }

    public LocalDateTime getDateTimeCreated() {
        return dateTimeCreated;
    }

    public void setDateTimeCreated(LocalDateTime dateTimeCreated) {
        this.dateTimeCreated = dateTimeCreated;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DevelopmentStage getDevelopmentStage() {
        return developmentStage;
    }

    public void setDevelopmentStage(DevelopmentStage developmentStage) {
        this.developmentStage = developmentStage;
    }

    public Director getDirector() {
        return director;
    }

    public void setDirector(Director director) {
        this.director = director;
    }

    public Genre getGenre_film() {
        return genre_film;
    }

    public void setGenre_film(Genre genre_film) {
        this.genre_film = genre_film;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
//    @Override
//    public String toString() {
//        return "Movies{" +
//                "id=" + id +
//                ", title='" + title + '\'' +
//                ", description='" + description + '\'' +
//                ", genre='" + genre_film + '\'' +
//                '}';
//    }

}
