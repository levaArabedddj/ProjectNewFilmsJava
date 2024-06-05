package com.example.oopkursova.config;


import com.example.oopkursova.Entity.Movies;
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
    @Column(unique = true)
    private String name;
    private String role;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY) // добавлено для связи с фильмами
    private List<Movies> moviesList;


    @Override
    public String toString() {
        return "Users{" +
                "id=" + user_id +
                ", gmail='" + gmail + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
