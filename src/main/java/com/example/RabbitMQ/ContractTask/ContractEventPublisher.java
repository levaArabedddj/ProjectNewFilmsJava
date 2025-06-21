package com.example.RabbitMQ.ContractTask;

import com.example.RabbitMQ.DtoRabbitMQ.CastingApplicationAnswerEvent;
import com.example.RabbitMQ.DtoRabbitMQ.ContractNegotiationEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContractEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public ContractEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishContract(ContractNegotiationEvent event) {
        System.out.println("сообщение в очереди");
        rabbitTemplate.convertAndSend("contract-exchange", "contract.binding", event);
    }
}
