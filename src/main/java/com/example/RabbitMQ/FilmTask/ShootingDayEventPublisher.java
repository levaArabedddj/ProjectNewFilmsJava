package com.example.RabbitMQ.FilmTask;

import com.example.RabbitMQ.DtoRabbitMQ.DtoShootingDayMQ;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class ShootingDayEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public ShootingDayEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(DtoShootingDayMQ event, LocalTime shootingTime, LocalDate shootingDate, String shootingLocation) {
        rabbitTemplate.convertAndSend("shootingDayExchange", "shooting.created", event);
    }
}

