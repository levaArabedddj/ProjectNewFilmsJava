package org.example.servicefilm.RabbitMqDeleteMovie;

import org.example.servicefilm.RabbitMqUpdateMovie.MovieDtoUpdateRM;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class FilmDeleteConfigQueue {

    @Bean
    public TopicExchange FilmDeleteConfigExchange() {
        return new TopicExchange("filmDeleteConfigExchange");
    }

    @Bean
    public Queue FilmDeleteQueue() {
        return new Queue("filmDeleteQueue", true);
    }

    @Bean
    public Binding FilmUpdateBinding(Queue FilmDeleteQueue, TopicExchange FilmDeleteConfigExchange) {
        return BindingBuilder
                .bind(FilmDeleteQueue)
                .to(FilmDeleteConfigExchange)
                .with("filmdelete.binding");
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverterDelete() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setTrustedPackages("*");
        classMapper.setIdClassMapping(Map.of(
                "com.example.RabbitMQ.DtoRabbitMQ.MovieDtoUpdateRM", MovieDtoUpdateRM.class
        ));
        converter.setClassMapper(classMapper);
        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplateDeleteFilm(ConnectionFactory connectionFactory,
                                                   @Qualifier("jackson2JsonMessageConverterDelete")Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactoryDeleteFilm(
            ConnectionFactory connectionFactory,
            @Qualifier("jackson2JsonMessageConverterDelete") Jackson2JsonMessageConverter converter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter);
        return factory;
    }
}
