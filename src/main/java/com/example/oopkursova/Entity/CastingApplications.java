package com.example.oopkursova.Entity;

import com.example.oopkursova.Enum.ApplicationStatus;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "Casting_Applications")
public class CastingApplications {

    // исправить связь на юзера , а не актера


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "casting_id", nullable = false)
    private Castings castings;
    @ManyToOne
    @JoinColumn(name = "actor_id", nullable = false)
    private Users actor;
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;
    private String message;
    @OneToMany(mappedBy = "castingApplications", cascade = CascadeType.ALL, orphanRemoval = true )
    private List<TrialParticipants> trialParticipants;
}
