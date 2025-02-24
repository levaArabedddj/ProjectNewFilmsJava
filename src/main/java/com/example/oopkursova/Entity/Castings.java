package com.example.oopkursova.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "Castings")
@Data
public class Castings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movies movie;

    @Column(name = "role_name", length = 100)
    private String RoleName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String requirements;

    @OneToMany(mappedBy ="castings", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrialParticipants> trialParticipants;

}
