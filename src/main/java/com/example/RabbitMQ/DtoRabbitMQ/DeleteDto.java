package com.example.RabbitMQ.DtoRabbitMQ;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteDto {
    private long film_id;
    private String username;
    private String gmail;
}
