package com.example.oopkursova.DTO;

import com.example.oopkursova.Entity.Movies;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoActor {
    private long id;
    private String name;
    private String surName;
    private int salaryPerHour;
    private int rating;


}
