package com.example.oopkursova.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Table(name = "Directors")
@Entity
public class Directors {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long admin_id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;
    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY,
            mappedBy = "directors")
    private List<Movies> movies;


}
