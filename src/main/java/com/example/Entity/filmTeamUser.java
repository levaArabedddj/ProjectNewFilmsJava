package com.example.Entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "film_team_user", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "movie_id"}) // Запрещает дубликаты
})
@Data
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

    @Column(name = "role", nullable = false)
    private String role; // Роль человека в фильме
}
