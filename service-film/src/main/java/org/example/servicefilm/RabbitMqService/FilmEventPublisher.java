package org.example.servicefilm.RabbitMqService;


import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FilmEventPublisher {


    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public FilmEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(MovieDtoRM dtoRM){
        rabbitTemplate.convertAndSend("filmCreateExchange","filmcreate.binding", dtoRM);
    }
}
