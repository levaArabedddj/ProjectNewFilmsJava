package com.example.oopkursova.DTO;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class DtoFinance {
    private BigDecimal budget;
    private BigDecimal actorsSalary;
    private BigDecimal crewSalary;
    private BigDecimal advertisingCost;
    private BigDecimal editingCost;
    private BigDecimal equipmentCost;
}
