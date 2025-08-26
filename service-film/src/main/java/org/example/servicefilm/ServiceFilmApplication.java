package org.example.servicefilm;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class ServiceFilmApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceFilmApplication.class, args);
    }

}
