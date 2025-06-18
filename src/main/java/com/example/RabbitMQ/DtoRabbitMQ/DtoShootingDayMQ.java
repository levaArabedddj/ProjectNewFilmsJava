package com.example.RabbitMQ.DtoRabbitMQ;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
@Data
@NoArgsConstructor
public class DtoShootingDayMQ implements Serializable {
    private long filmId;
    private LocalDate shootingDate;
    private LocalTime shootingTime;
    private String location;
    private int estimatedDurationHours;
    

}
