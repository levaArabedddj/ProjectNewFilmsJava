package com.example.RabbitMQ.ElasticTask;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UpdateFilmConfigQueue {


    public static final String EXCHANGE_NAME = "filmExchange";
    public static final String QUEUE_NAME = "filmUpdateEl";

    public static final String EXCHANGE_NAME_DELETE  = "filmDeleteExchange";
    public static final String QUEUE_NAME_DELETE = "filmDeleteQueue";
    @Bean
    public Queue filmUpdateQueue() {
        return new Queue(QUEUE_NAME);
    }

    @Bean
    public TopicExchange filmExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding filmUpdateBinding(Queue filmUpdateQueue, TopicExchange filmExchange ) {
        return BindingBuilder
                .bind(filmUpdateQueue)
                .to(filmExchange)
                .with("filmupdate.elastic");
    }

    @Bean
    public Queue filmDeleteQueue() {
        return new Queue(QUEUE_NAME_DELETE);
    }

    @Bean
    public TopicExchange filmUpdateEl() {
        return new TopicExchange(EXCHANGE_NAME_DELETE);
    }

    @Bean
    public Binding filmDeleteBinding(Queue filmDeleteQueue, TopicExchange filmUpdateEl ) {
        return BindingBuilder
                .bind(filmDeleteQueue)
                .to(filmUpdateEl)
                .with("filmdelete.elastic");
    }
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        return factory;
    }
}
