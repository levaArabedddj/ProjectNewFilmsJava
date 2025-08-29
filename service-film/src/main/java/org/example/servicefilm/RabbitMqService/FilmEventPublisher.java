package org.example.servicefilm.RabbitMqService;


import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class FilmEventPublisher {


    private final RabbitTemplate rabbitTemplateCreateFilm;

    @Autowired
    public FilmEventPublisher(@Qualifier("rabbitTemplateCreateFilm")RabbitTemplate rabbitTemplateCreateFilm) {
        this.rabbitTemplateCreateFilm = rabbitTemplateCreateFilm;
    }

    public void publish(MovieDtoRM dtoRM){
        rabbitTemplateCreateFilm.convertAndSend("filmCreateExchange","filmcreate.binding", dtoRM);
    }
}
