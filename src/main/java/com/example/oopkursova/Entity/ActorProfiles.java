package com.example.oopkursova.Entity;

import com.example.oopkursova.Enum.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Entity
@Data
@Table(name = "Actor_Profiles")
public class ActorProfiles {
    // создать новую сущность
    // для актеров , куда они будут добавлять в свои профили
    // свои возможности и умения , по тибу своего личного кабинета для актера

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id", nullable = false, unique = true)
    private Actors actors;

    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Column(columnDefinition = "TEXT")
    private String biography;
    private String skills;

    @Column(columnDefinition = "TEXT")
    private String languages;

    @Column(columnDefinition = "TEXT")
    private String experience;

    @Column(name = "profile_photo_url", length = 500)
    private String profile_photo_url;

    @Column(unique = true, length = 50)
    @Email(message = "Некоректний формат email")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@gmail\\.com$", message = "Email повинен закінчуватися на @gmail.com")
    private String gmail;

    @Column(unique = true)
    @Pattern(regexp = "^\\+\\d{10,15}$", message = "Некоректний формат телефону. Повинен починатися з + і містити 10-15 цифр")
    private String numberPhone;

}
