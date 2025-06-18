package com.example.RabbitMQ.CastingTask;

import com.example.RabbitMQ.DtoRabbitMQ.CastingApplicationEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CastingEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public CastingEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishCastingEvent(CastingApplicationEvent event) {
        rabbitTemplate.convertAndSend("castingExchange", "casting.application", event);
    }
}

