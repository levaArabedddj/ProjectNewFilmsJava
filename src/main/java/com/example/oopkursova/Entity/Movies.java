package com.example.oopkursova.Entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@Table
public class Movies {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String title;
    private String description ;
    private double rating;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    private Directors directors;

    @ManyToMany(mappedBy = "movies",fetch = FetchType.EAGER)
    private Set<Actors> actors;


    @ManyToMany(mappedBy = "movies",fetch = FetchType.EAGER)
    private Set<FilmCrewMembers> filmCrewMembers;

    private LocalDateTime dateTimeCreated;

    @PrePersist
    private void init(){
        dateTimeCreated = LocalDateTime.now();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + Double.hashCode(rating);
        return result;
    }

}
