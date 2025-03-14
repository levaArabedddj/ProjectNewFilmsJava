package com.example.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Actors")
public class Actors {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String surName;

    private int salaryPerHour;
    private int rating;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private Users user;


    @OneToOne(mappedBy = "actors", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ActorProfiles actorProfile;  // 🔹 Добавили связь с профилем актёра

}
