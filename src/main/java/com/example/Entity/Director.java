package com.example.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Director")
@Data
public class Director {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String surName;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private Users users;

    @OneToOne
    @JoinColumn(name = "profile_id", unique = true, nullable = false)
    private DirectorProfiles directorProfiles;
}
