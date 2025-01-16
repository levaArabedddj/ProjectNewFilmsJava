package com.example.oopkursova.DTO;

import com.example.oopkursova.Entity.Movies;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;
@Data
@AllArgsConstructor
public class DtoCrewMember {

    private long crewMember_id;
    private String name;
    private String surName;
    private int salaryPerHours;
}
