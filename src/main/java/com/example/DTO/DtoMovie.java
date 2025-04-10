package com.example.DTO;


import com.example.Enum.Genre;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoMovie {
    private long id;
    private String title;
    private String description ;
    private Genre genre_film;
//    private Set<DtoActor> actors;
//    private Set<DtoCrewMember> crewMembers;
    private LocalDateTime dateTimeCreated;
    private Set<DtoShootingDay> shootingDays;
    private DtoScript script;
    private DtoFinance finance;

    public DtoMovie(long id, String title, String description, Genre genre_film) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.genre_film = genre_film;
    }
}
