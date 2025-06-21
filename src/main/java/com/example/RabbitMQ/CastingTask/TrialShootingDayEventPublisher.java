package com.example.RabbitMQ.CastingTask;

import com.example.RabbitMQ.DtoRabbitMQ.CastingApplicationAnswerEvent;
import com.example.RabbitMQ.DtoRabbitMQ.TrialShootingDayEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class TrialShootingDayEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public TrialShootingDayEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishTrialShootingEvent(TrialShootingDayEvent event) {
        rabbitTemplate.convertAndSend("trialTopicExchange", "trial.shooting", event);
    }
}
