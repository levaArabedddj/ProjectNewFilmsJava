package com.example.RabbitMQ.DtoRabbitMQ;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CastingApplicationEvent {
    private Long castingId;
    private Long actorId;
    private String directorEmail;
    private String movieTitle;
    private String status;
    private String actorName;
}
