package com.example.RabbitMQ.ElasticTask;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

@Configuration
public class ElasticConfigQueue {

    public static final String INDEX_QUEUE    = "elasticsearch_sync_queue";
    public static final String INDEX_EXCHANGE = "elasticsearch_exchange";


    @Bean
    public Queue indexQueue() {
        return new Queue(INDEX_QUEUE);
    }

    @Bean
    public TopicExchange indexExchange() {
        return new TopicExchange(INDEX_EXCHANGE);
    }

    @Bean
    public Binding bindingIndex(Queue indexQueue, TopicExchange indexExchange) {
        return BindingBuilder.
                bind(indexQueue).
                to(indexExchange).
                with("movies.created");
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
