package com.example.oopkursova.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "Users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long user_id;
    @Column(unique = true)
    private String gmail;

    private String password;
    private String surname;
    private String name;
    private Number phone_number;

    @OneToOne(mappedBy = "user")
    private Directors director;

    @OneToOne(mappedBy = "user")
    private Actors actors;

    @OneToOne(mappedBy = "user")
    private FilmCrewMembers filmCrewMembers;
}
