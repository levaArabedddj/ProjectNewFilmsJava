package com.example.Entity;



import com.example.Enum.UserRole;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@Getter
@Entity
@Table(name = "Users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long user_id;
    @Column(unique = true)
    private String gmail;

    private String password;
    @Column(unique = true)
    private String userName;
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Actors actor;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private FilmCrewMembers filmCrewMembers;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true )
    private List<MovieRental> movieRentals;

    @OneToMany(mappedBy = "actors", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrialParticipants> trialParticipants;


    @Override
    public String toString() {
        return "Users{" +
                "id=" + user_id +
                ", gmail='" + gmail + '\'' +
                ", name='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
