package com.example.RabbitMQ.CastingTask;

import com.example.RabbitMQ.DtoRabbitMQ.CastingApplicationAnswerEvent;
import com.example.RabbitMQ.DtoRabbitMQ.CastingApplicationEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class CastingApplicationEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public CastingApplicationEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishCastingEvent(CastingApplicationAnswerEvent event) {
        System.out.println("сообщение в очереди");
        rabbitTemplate.convertAndSend("castingApplicationExchange", "castingAnswer.application", event);
    }
}
