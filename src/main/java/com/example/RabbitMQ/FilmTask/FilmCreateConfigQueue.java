//package com.example.RabbitMQ.FilmTask;
//
//import org.springframework.amqp.core.Binding;
//import org.springframework.amqp.core.BindingBuilder;
//import org.springframework.amqp.core.Queue;
//import org.springframework.amqp.core.TopicExchange;
//import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
//import org.springframework.amqp.rabbit.connection.ConnectionFactory;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class FilmCreateConfigQueue {
//
//    @Bean
//    public TopicExchange FilmCreateTopicExchange() {
//        return new TopicExchange("filmCreateExchange");
//    }
//
//    @Bean
//    public Queue filmCreateQueue() {
//        return new Queue("filmCreateQueue", true);
//    }
//
//    @Bean
//    public Binding FilmCreateBinding(Queue filmCreateQueue, TopicExchange FilmCreateTopicExchange) {
//        return BindingBuilder
//                .bind(filmCreateQueue)
//                .to(FilmCreateTopicExchange)
//                .with("filmcreate.binding");
//    }
//
//    @Bean
//    public RabbitTemplate rabbitTemplateCreateFilm(ConnectionFactory connectionFactory) {
//        RabbitTemplate template = new RabbitTemplate(connectionFactory);
//        template.setMessageConverter(new Jackson2JsonMessageConverter());
//        return template;
//    }
//
//    @Bean
//    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactoryCreateFilm(
//            ConnectionFactory connectionFactory
//    ) {
//        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
//        factory.setConnectionFactory(connectionFactory);
//        factory.setMessageConverter(new Jackson2JsonMessageConverter());
//        return factory;
//    }
//}
