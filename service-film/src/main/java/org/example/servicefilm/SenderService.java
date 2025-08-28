package org.example.servicefilm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SenderService {


    private final JavaMailSender mailSender;

    @Autowired
    public SenderService( JavaMailSender mailSender) {
        this.mailSender = mailSender;
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

    public void sendWithDirectorResultUpdateMovie(String to, byte result) {
        SimpleMailMessage message = new SimpleMailMessage();

        if (result == 1) {
            String subject = "✅ Фильм успешно обновлён!";
            String text = """
                Отличные новости! 🎬

                Ваш фильм был успешно изменён. Все внесённые вами правки теперь сохранены, и 
                вы можете наслаждаться обновлённой версией вашей картины. Во вкладке «О фильме» 
                вы увидите новые настройки, добавленные сцены, обновлённые описания и любые другие 
                изменения, которые сделали ваш фильм ещё лучше.

                Мы уверены, что эти улучшения помогут вам создать уникальный и захватывающий проект. 
                Продолжайте творить, режиссёр! ✨

                Пусть каждая сцена станет шедевром, а каждая деталь — отражением вашего таланта. 
                Удачи и вдохновения на всех этапах работы над фильмом! 🎥
                """;

            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
        } else {
            String subject = "❌ Ошибка при обновлении фильма";
            String text = """
                К сожалению, произошла ошибка при попытке изменить фильм. ⚠️

                Пожалуйста, проверьте внесённые данные и попробуйте снова. 
                Если проблема повторяется, обратитесь в службу поддержки для получения помощи.

                Не отчаивайтесь — каждая ошибка это шаг к совершенству. 💪
                Мы верим, что вы справитесь и ваш фильм станет ещё лучше! 🎬
                """;

            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
        }

        mailSender.send(message);
    }

}
