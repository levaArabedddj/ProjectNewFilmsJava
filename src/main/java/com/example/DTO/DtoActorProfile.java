package com.example.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoActorProfile {
    private String name;
    private String surName;
    private int salaryPerHour;
    private String biography;
    private String skills;
    private String experience;
    private String profile_photo_url;
    private String gmail;
    private String numberPhone;
}
