package com.example.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "ShootingDays")
public class ShootingDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    @NotNull
    private Movies movie;

    @Column(name = "shooting_date")
    private LocalDate shootingDate;

    @Column(name = "shooting_time")
    private LocalTime shootingTime;
    private String shootingLocation;
    private int estimatedDurationHours;

    @Override
    public String toString() {
        return "ShootingDay{" +
                "id=" + id +
                ", estimatedDurationHours=" + estimatedDurationHours +
                ", shootingDate=" + shootingDate +
                ", shootingLocation='" + shootingLocation + '\'' +
                ", shootingTime='" + shootingTime + '\'' +
                '}';
    }

}
