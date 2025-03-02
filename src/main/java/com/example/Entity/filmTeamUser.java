package com.example.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "filmTeamUser")
public class filmTeamUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movies movies;

}
