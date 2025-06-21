package com.example.RabbitMQ.DtoRabbitMQ;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContractNegotiationEvent {
    private Long contractId;

    private Long actorId;
    private String fullNameActor;
    private String actorEmail;

    private Long directorId;
    private String fullNameDirector;
    private String directorEmail;

    private Long movieId;
    private String movieTitle;

    private Double proposedSalary;
    private Double penalty;
    private Double bonuses;
    private String paymentSchedule;

    private String actorSignature;
    private String directorSignature;

    private LocalDate startDate;
    private LocalDate endDate;
    private boolean confidentialityAgreement;

}

