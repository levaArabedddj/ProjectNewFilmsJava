package com.example.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Director")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Director {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String surName;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private Users users;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", unique = true, nullable = false)
    private DirectorProfiles directorProfiles;
}
