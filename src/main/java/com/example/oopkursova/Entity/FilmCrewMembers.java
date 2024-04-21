package com.example.oopkursova.Entity;


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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long crewMember_id;

    private int salaryPerHours;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "filmCrewMember_movies",
            joinColumns = {@JoinColumn(name = "filmCrewMember_id",
                    referencedColumnName ="crewMember_id",
                    nullable = false, updatable = false, insertable = false)},
            inverseJoinColumns = {@JoinColumn(name = "movie_id",referencedColumnName = "id",
                    nullable = false, updatable = false, insertable = false)}
    )
    private Set<Movies> movies;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (crewMember_id ^ (crewMember_id >>> 32));
        result = prime * result + salaryPerHours;
        return result;
    }

}
