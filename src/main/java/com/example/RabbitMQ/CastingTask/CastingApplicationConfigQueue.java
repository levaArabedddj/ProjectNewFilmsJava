package com.example.RabbitMQ.CastingTask;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@EnableRabbit
@Configuration
public class CastingApplicationConfigQueue {

    public static final String CASTING_EXCHANGE = "castingApplicationExchange";
    public static final String CASTING_QUEUE = "castingApplicationQueue";
    public static final String CASTING_ROUTING_KEY = "castingAnswer.application";

    @Bean
    public TopicExchange castingApplicationExchange() {
        return new TopicExchange(CASTING_EXCHANGE);
    }

    @Bean
    public Queue castingApplicationQueue() {
        return new Queue(CASTING_QUEUE, true);
    }

    @Bean
    public Binding castingApplicationBinding(Queue castingApplicationQueue, TopicExchange castingApplicationExchange) {
        return BindingBuilder
                .bind(castingApplicationQueue)
                .to(castingApplicationExchange)
                .with(CASTING_ROUTING_KEY);
    }

    @Bean
    public RabbitTemplate rabbitTemplateCastingAppl(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactoryCastingApp(
            ConnectionFactory connectionFactory
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        return factory;
    }
}
