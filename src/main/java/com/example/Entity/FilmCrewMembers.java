package com.example.Entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Entity
@Table(name = "filmCrewMember")
@AllArgsConstructor
@NoArgsConstructor
public class FilmCrewMembers {


    // убрать связь с фильмом , и настроить связь с командой фильма

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long crewMember_id;
    private String name;
    private String surName;
    private int salaryPerHours;
//    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
//    @JoinTable(
//            name = "filmCrewMember_movies",
//            joinColumns = {@JoinColumn(name = "filmCrewMember_id",
//                    referencedColumnName ="crewMember_id",
//                    nullable = false, updatable = false, insertable = false)},
//            inverseJoinColumns = {@JoinColumn(name = "movie_id",referencedColumnName = "id",
//                    nullable = false, updatable = false, insertable = false)}
//    )
//    private Set<Movies> movies;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private Users user;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (crewMember_id ^ (crewMember_id >>> 32));
        result = prime * result + salaryPerHours;
        return result;
    }

}
