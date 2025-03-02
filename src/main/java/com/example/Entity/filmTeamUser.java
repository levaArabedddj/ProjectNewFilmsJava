package com.example.Entity;

import com.example.Enum.FilmRole;
import jakarta.persistence.*;

@Entity
@Table(name = "film_team_user", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "movie_id"}) // Запрещает дубликаты
})
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

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private FilmRole role; // Роль человека в фильме
}
