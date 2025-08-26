package org.example.servicefilm.RabbitMqService;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class FilmCreateConfigQueue {

    @Bean
    public TopicExchange FilmCreateTopicExchange() {
        return new TopicExchange("filmCreateExchange");
    }

    @Bean
    public Queue filmCreateQueue() {
        return new Queue("filmCreateQueue", true);
    }

    @Bean
    public Binding FilmCreateBinding(Queue filmCreateQueue, TopicExchange FilmCreateTopicExchange) {
        return BindingBuilder
                .bind(filmCreateQueue)
                .to(FilmCreateTopicExchange)
                .with("filmcreate.binding");
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setTrustedPackages("*");
        classMapper.setIdClassMapping(Map.of(
                "com.example.RabbitMQ.DtoRabbitMQ.MovieDtoRM", MovieDtoRM.class
        ));
        converter.setClassMapper(classMapper);
        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplateCreateFilm(ConnectionFactory connectionFactory,
                                                   Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactoryCreateFilm(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter);
        return factory;
    }



}
