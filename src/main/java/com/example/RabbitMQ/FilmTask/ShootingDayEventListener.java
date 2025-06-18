package com.example.RabbitMQ.FilmTask;

import com.example.Entity.Actors;
import com.example.RabbitMQ.DtoRabbitMQ.DtoShootingDayMQ;
import com.example.Repository.ActorRepo;
import com.example.Service.ActorsService;
import com.example.Service.SenderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ShootingDayEventListener {

    @Autowired
    private ActorRepo actorsRepository; // репозиторий для актеров

    @Autowired
    private ActorsService actorService;
    @Autowired
    private SenderService emailService; // сервис отправки email

    @RabbitListener(queues = "shootingDayQueue")
    public void handleShootingDayEvent(DtoShootingDayMQ event) {
        System.out.println("🔔 Получено событие ShootingDay: " + event);
        Long filmId = event.getFilmId();
        List<Actors> actors = actorService.getActorsByMovieId(filmId);

        System.out.println("💥 Актёров на фильме " + filmId + ": " + actors.size());
        try {
        for (Actors actor : actors) {
            String email   = actor.getUser().getGmail();
            System.out.println("✉️ Отправляем письмо на: " + email);
            String subject = "Информация о съёмочном дне";
            String message = String.format(
                    "Здравствуйте, %s!%n" +
                            "Напоминаем, что %s в %s вы участвуете в съёмках фильма.%n" +
                            "Локация: %s%n%n" +
                            "Спасибо!",
                    actor.getName(),
                    event.getShootingDate(),
                    event.getShootingTime(),
                    event.getLocation()
            );


            emailService.sendEmail(email, subject, message);
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


