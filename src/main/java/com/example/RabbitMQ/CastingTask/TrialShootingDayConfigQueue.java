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
public class TrialShootingDayConfigQueue {

    @Bean
    public Queue trialShooting(){
        return new Queue("trialShooting");
    }

    @Bean
    public TopicExchange trialShootingExchange(){
        return new TopicExchange("trialTopicExchange");
    }

    @Bean
    public Binding trialShootingBinding(Queue trialShooting, TopicExchange trialShootingExchange) {
        return BindingBuilder
                .bind(trialShooting)
                .to(trialShootingExchange)
                .with("trial.shooting");
    }

    @Bean
    public RabbitTemplate rabbitTemplateTrial(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactoryTrial(
            ConnectionFactory connectionFactory
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        return factory;
    }
}
