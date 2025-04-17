package com.example.ElasticSearch.ClassDocuments;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class CrewMemberDocument {

    @Id
    private String id;
    private String name;
    private String surName;
    private int salaryPerHour;
    private String position;
    private String expertise;
    private String equipmentList;
    private String gender;
    private String biography;
    private String skills;
    private String languages;
    private String experience;
    private String gmail;
    private Integer workingHoursPerWeek;
}
