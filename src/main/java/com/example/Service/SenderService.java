package com.example.Service;

import org.checkerframework.checker.units.qual.A;
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


    public void sendRegistrationEmail(String to, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Регистрация на сайте кино");
        message.setText("Привет, " + username + "!\n\nВы успешно зарегистрировались на нашем сайте для киноиндустрии.\n\nС уважением, команда сайта.");

        mailSender.send(message);
    }


}
