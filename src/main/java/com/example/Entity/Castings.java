package com.example.Entity;

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

    @ElementCollection
    @CollectionTable(name = "casting_roles", joinColumns = @JoinColumn(name = "casting_id"))
    @Column(name = "role_name")
    private List<String> roleName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String requirements;

    @OneToMany(mappedBy ="castings", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrialParticipants> trialParticipants;

}
