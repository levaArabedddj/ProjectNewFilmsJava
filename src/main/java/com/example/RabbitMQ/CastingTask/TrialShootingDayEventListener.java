package com.example.RabbitMQ.CastingTask;

import com.example.RabbitMQ.DtoRabbitMQ.TrialShootingDayEvent;
import com.example.Service.SenderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class TrialShootingDayEventListener {

    @Autowired
    private SenderService emailService;

    @RabbitListener(queues = "trialShooting")
    public void TrialShootingDayListener(TrialShootingDayEvent event){
        String subject = "🎬 Приглашение на пробные съёмки фильма";
        String body = String.format(
                "Здравствуйте, %s!\n\n" +
                        "Режиссёр пригласил вас на пробные съёмки фильма (Title: %s).\n\n" +
                        "Дата: %s\n" +
                        "Время: %s\n" +
                        "Место: %s\n\n" +
                        "Пожалуйста, подтвердите своё участие и не опаздывайте.\n\n" +
                        "С уважением,\nКоманда проекта 🎥",
                event.getNameActor(),
                event.getTitle(),
                event.getStartDate(),
                event.getStartTime(),
                event.getLocation()
        );
        emailService.sendEmail(event.getActorGmail(), subject, body);
    }

}

