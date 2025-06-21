package com.example.RabbitMQ.DtoRabbitMQ;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrialShootingDayEvent {
    private String title;
    private LocalDate startDate;
    private LocalDateTime startTime;
    private String location;
    private String nameActor;
    private String actorGmail;
}
