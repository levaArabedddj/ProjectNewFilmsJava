package com.example.Entity;


import com.example.Enum.TrialResult;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "Trial_Participants")
public class TrialParticipants {

    // тут исправить связь с актера на юзера


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trial_id", nullable = false)
    private Trial_Shootings shootings;

    @ManyToOne
    @JoinColumn(name = "casting_application_id", nullable = false)
    private CastingApplications castingApplications;

    @ManyToOne
    @JoinColumn(name = "casting_id", nullable = false)
    private Castings castings;

    @ManyToOne
    @JoinColumn(name = "actor_id", nullable = false)
    private Users actors;

    private String roleName;

    @Enumerated(EnumType.STRING)
    private TrialResult result;
    private String feedback;
}
