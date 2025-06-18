package com.example.RabbitMQ.CastingTask;

import com.example.RabbitMQ.DtoRabbitMQ.CastingApplicationEvent;
import com.example.Service.SenderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CastingEventListener {


    @Autowired
    private SenderService emailService; // сервис отправки email

    @RabbitListener(queues = "castingQueue")
    public void handleCastingApplicationEvent(CastingApplicationEvent event) {
        if ("Pending".equals(event.getStatus())) {
            String subject = "🎬 Новая заявка на кастинг";
            String body = String.format(
                    "Актёр %s подал заявку на кастинг для фильма \"%s\" (ID: %d).Осмотрите заявку и дайте ваш ответ.",
                    event.getActorName(),
                    event.getMovieTitle(),
                    event.getCastingId()
            );
            emailService.sendEmail(event.getDirectorEmail(), subject, body);
        }
    }
}


