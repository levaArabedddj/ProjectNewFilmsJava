package com.example.Entity;


import com.example.Enum.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Entity
@Data
@Table(name = "Crew_Member_Profiles")
public class CrewMemberProfiles {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_member_id", nullable = false, unique = true)
    private FilmCrewMembers crewMembers;

    @Column(columnDefinition = "TEXT")
    private String languages;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(columnDefinition = "TEXT")
    private String equipmentList;
    @Column(columnDefinition = "TEXT")
    private String biography;

    private String position;  // Основная роль в фильме (например, "Оператор", "Режиссер", "Монтажер")
    private String expertise; // Узкая специализация (например, "Работа с дронами", "Сведение звука")

    @Column(name = "working_hours_per_week")
    private Integer workingHoursPerWeek; // Количество рабочих часов в неделю

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

    @Column(name = "portfolio_url", length = 500)
    private String portfolioUrl; // Ссылка на портфолио

    @Column(name = "linkedin_url", length = 500)
    private String linkedinUrl; // LinkedIn



}

