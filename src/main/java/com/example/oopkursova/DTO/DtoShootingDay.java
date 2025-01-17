package com.example.oopkursova.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoShootingDay {
    private LocalDate shootingDate;
    private LocalTime shootingTime;
    private String shootingLocation;
    private int estimatedDurationHours;
}
