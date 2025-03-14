package com.example.DTO;


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
    private ActorProfileDto actorProfileDto;

    public DtoActor(Long id, String name, String surName, int rating, ActorProfileDto actorProfileDto) {
        this.id = id;
        this.name = name;
        this.surName = surName;
        this.rating = rating;
        this.actorProfileDto = actorProfileDto;
    }
}
