package com.example.RabbitMQ.CastingTask;


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
public class CastingConfigQueue {

    @Bean
    public TopicExchange castingExchange() {
        return new TopicExchange("castingExchange");
    }

    @Bean
    public Queue castingQueue() {
        return new Queue("castingQueue");
    }

    @Bean
    public Binding castingBinding(Queue castingQueue, TopicExchange castingExchange) {
        return BindingBuilder
                .bind(castingQueue)
                .to(castingExchange)
                .with("casting.application");
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate t = new RabbitTemplate(connectionFactory);
        t.setMessageConverter(new Jackson2JsonMessageConverter());
        return t;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory f = new SimpleRabbitListenerContainerFactory();
        f.setConnectionFactory(connectionFactory);
        f.setMessageConverter(new Jackson2JsonMessageConverter());
        return f;
    }

}
