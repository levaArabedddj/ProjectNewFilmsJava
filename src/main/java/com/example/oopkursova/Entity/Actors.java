package com.example.oopkursova.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Actors")
public class Actors {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;
    private  int salaryPerHour;
    private int rating;



    @ManyToMany(fetch = FetchType.EAGER,  cascade = CascadeType.MERGE)
    @JoinTable(
            name = "actors_movies",
            joinColumns = {@JoinColumn(name = "actors_id",
                    referencedColumnName ="id",
                    nullable = false, updatable = false,
                    insertable = false)},
            inverseJoinColumns = {@JoinColumn(name = "movie_id",referencedColumnName = "id",
                    nullable = false, updatable = false, insertable = false)}
    )
    private Set<Movies> movies;
}
