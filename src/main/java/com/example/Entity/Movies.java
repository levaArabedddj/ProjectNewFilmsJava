package com.example.Entity;


import com.example.Enum.DevelopmentStage;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table
public class Movies {

    //убрать связь с актерами и членами сьемочной группы , и настроить связь с юзерами и командами
    // убрать связь с юзером и настроить связь с режиссером , так как только режиссер может создавать фильмы

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String title;
    private String description ;
    private String genre;

//    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
//    @JoinColumn(name = "user_id") // добавлено для связи с пользователем
//    private Users user;

//    @ManyToMany(mappedBy = "movies",fetch = FetchType.EAGER)
//    private Set<Actors> actors;


//    @ManyToMany(mappedBy = "movies",fetch = FetchType.EAGER)
//    private Set<FilmCrewMembers> filmCrewMembers;

    @ManyToOne
    @JoinColumn(name = "director_id", nullable = false)
    private Director director;

    private LocalDateTime dateTimeCreated;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ShootingDay> shootingDays;

    @OneToOne(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Script script;

    // Поле для отслеживания этапа разработки фильма
    @Enumerated(EnumType.STRING)
    @Column(name = "development_stage", nullable = false)
    private DevelopmentStage developmentStage;

    @OneToOne(mappedBy = "movie", cascade = CascadeType.ALL)
    private Finance filmFinance;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true )
    private List<Castings> castings;
    @OneToMany(mappedBy = "movies", cascade = CascadeType.ALL, orphanRemoval = true )
    private List<Trial_Shootings> trial_Shootings;

    @PrePersist
    private void init(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
        dateTimeCreated = LocalDateTime.parse(LocalDateTime.now().format(formatter), formatter);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((genre == null) ? 0 : genre.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "Movies{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", genre='" + genre + '\'' +
                '}';
    }

}
