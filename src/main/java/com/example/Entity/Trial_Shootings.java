package com.example.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Trial_Shootings")
@Data
public class Trial_Shootings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movies movies;

    private LocalDate date;
    private LocalDateTime startTime;
    private String location;
    private String description;

    @OneToMany(mappedBy = "shootings", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrialParticipants> participants;
}
