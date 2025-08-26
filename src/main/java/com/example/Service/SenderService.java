package com.example.Service;

import com.example.Entity.Actors;
import com.example.Enum.ApplicationStatus;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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


    public void sendEmail(String email, String subject, String message) {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email);
        msg.setSubject(subject);
        msg.setText(message);
        mailSender.send(msg);
    }

    public void sendWithAttachment(String to, String subject, String text, byte[] pdfBytes) {
        try {
            MimeMessage message = mailSender.createMimeMessage();

            // true — означает, что письмо может содержать вложения
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            // Добавление PDF вложения
            helper.addAttachment("contract.pdf", new ByteArrayResource(pdfBytes));

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Ошибка при отправке письма с вложением", e);
        }
    }

    public void sendWithDirectorResultCreateMovie(String to,byte result) {
        SimpleMailMessage message = new SimpleMailMessage();

        if(result==1) {
            String subject = "✅ Фильм успешно создан!";
            String text = """
                    Поздравляем! 🎬
                    
                    Ваш фильм был успешно создан.
                    Во вкладке «О фильме» вы сможете начать его настраивать —
                    добавлять описание, сцену, съёмочные дни и других участников.
                    
                    Удачной работы, режиссёр! ✨
                    """;

            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
        } else {
            String subject = "❌ Ошибка при создании фильма";
            String text = """
                К сожалению, произошла ошибка при создании фильма. ⚠️
                
                Пожалуйста, проверьте введённые данные и попробуйте снова.
                Если проблема сохраняется — обратитесь в поддержку.
                
                Мы верим, что у вас всё получится! 💪
                """;

            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
        }

        mailSender.send(message);


    }
}

