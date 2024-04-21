package com.example.oopkursova.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Getter
@Entity
@Table(name = "Users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long user_id;
    @Column(unique = true)
    private String gmail;

    private String password;
    private String surname;
    private String name;
    private Number phone_number;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY) // добавлено для связи с фильмами
    private List<Movies> moviesList;
}
