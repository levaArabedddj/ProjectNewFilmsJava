package com.example.DTO;



import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NegotiationRequest {

    @NotNull
    private Double proposedSalary;

    @NotNull
    private Double penalty;

    @NotNull
    private Double bonuses;

    @NotNull
    private String paymentSchedule;


}

