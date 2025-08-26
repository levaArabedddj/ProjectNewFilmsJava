//package com.example.RabbitMQ.FilmTask;
//
//import com.example.RabbitMQ.DtoRabbitMQ.MovieDtoRM;
//import com.example.Repository.DirectorRepo;
//import com.example.Repository.UsersRepo;
//import jakarta.persistence.EntityManager;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class FilmEventPublisher {
//
//
//    private final RabbitTemplate rabbitTemplate;
//
//    @Autowired
//    public FilmEventPublisher(RabbitTemplate rabbitTemplate) {
//        this.rabbitTemplate = rabbitTemplate;
//    }
//
//    public void publish(MovieDtoRM dtoRM){
//        rabbitTemplate.convertAndSend("filmCreateExchange","filmcreate.binding", dtoRM);
//    }
//}
