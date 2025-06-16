package com.example.Entity;

import com.example.Enum.AdminPermission;
import com.example.Enum.AdminRole;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "admin")
@Data
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private Users user;


    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private AdminRole role; // Роль админа (главный, модератор и т. д.)

    @Enumerated(EnumType.STRING)
    @Column(name = "access_level", nullable = false)
    private AdminPermission accessLevel; // Уровень доступа

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movies movie;
}
