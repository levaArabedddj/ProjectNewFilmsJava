package com.example.RabbitMQ.CastingTask;

import com.example.RabbitMQ.DtoRabbitMQ.CastingApplicationAnswerEvent;
import com.example.Service.SenderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CastingApplicationEventListener {

    @Autowired
    private SenderService emailService;

    @RabbitListener(queues = "castingApplicationQueue")
    public void handleAnswerApplicationEvent(CastingApplicationAnswerEvent event) {

        System.out.println("мы зашли в этот метод ");
        if ("Approved".equalsIgnoreCase(event.getStatus())) {
            System.out.println("Мы в ифе");
            String subject = "🎬 Ваша заявка на кастинг принята!";
            String body = String.format(
                    "Добрый день, %s!\n\n" +
                            "Режиссёр одобрил вашу заявку на участие в кастинге фильма. 🎉\n" +
                            "Вы приглашены на пробные съёмки. В ближайшее время мы вышлем дополнительную информацию " +
                            "с точной датой, временем и местом проведения съёмок.\n\n" +
                            "Пожалуйста, следите за своей почтой.\n\n" +
                            "С уважением,\nКоманда проекта 🎥",
                    event.getActorName()
            );
            emailService.sendEmail(event.getActorGmail(), subject, body);

        } else if ("Rejected".equalsIgnoreCase(event.getStatus())) {
            String subject = "📭 Ваша заявка на кастинг не принята";
            String body = String.format(
                    "Здравствуйте, %s.\n\n" +
                            "Благодарим вас за проявленный интерес к нашему проекту.\n" +
                            "К сожалению, в этот раз режиссёр принял решение отклонить вашу заявку.\n\n" +
                            "Мы обязательно свяжемся с вами при появлении подходящих ролей в будущем!\n" +
                            "Желаем вам удачи и вдохновения! 🌟\n\n" +
                            "С уважением,\nКоманда проекта",
                    event.getActorName()
            );
            emailService.sendEmail(event.getActorGmail(), subject, body);
        }
    }
}

