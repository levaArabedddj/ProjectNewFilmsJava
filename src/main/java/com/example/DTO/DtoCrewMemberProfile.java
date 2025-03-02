package com.example.DTO;


import com.example.Enum.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtoCrewMemberProfile {
    private String name;
    private String surName;
    private Gender gender;
    private String languages;
    private String equipmentList;
    private String biography;
    private String position;
    private String expertise;
    private Integer workingHoursPerWeek;
    private String experience;
    private String profile_photo_url;
    private String gmail;
    private String numberPhone;
    private String portfolioUrl;
    private String linkedinUrl;
}
