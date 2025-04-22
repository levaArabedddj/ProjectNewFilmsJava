package com.example.Service;

import com.example.Entity.Actors;
import com.example.Enum.ApplicationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static com.example.Enum.ApplicationStatus.Approved;

@Service
public class SenderService {

    private final JavaMailSender mailSender;

    @Autowired
    public SenderService( JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    @Async
    public void sendRegistrationEmail(String to, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Регистрация на сайте кино");
        message.setText("Привет, " + username + "!\n\nВы успешно зарегистрировались на нашем сайте для киноиндустрии.\n\nС уважением, команда сайта.");

        mailSender.send(message);
    }

    @Async
    public void sendAssignApplication(String to , String username, ApplicationStatus status) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Вашу заявку на сьемки в фильме было " + status);
        if(status.equals(Approved)) {
            message.setText("Приветствуем вас вы были приняты на сьемки в фильм и ваша заявка была одобрена ");
        } else {
            message.setText("Приветствуем ваша заявка  на сьемку в фильме была отклонена ");
        }
        mailSender.send(message);
    }

    @Async
    public void sendMsgForActorInTeam(Actors actor , String role) {

        if(actor.getUser() == null|| actor.getUser().getGmail() == null) {
            throw new RuntimeException("No email found actor");
        }

        String email = actor.getUser().getGmail();
        String username = actor.getUser().getUserName();
        String subject = "Вы приняты в фильм";
        String msg = String.format("Поздравляем , Вы были приняти в фильм на роль " + role);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Вас принято в фильм");
        message.setText(msg);
        mailSender.send(message);
    }


}
