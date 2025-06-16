package com.example.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "ScriptMovie")
public class Script {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movies movie;

}
