package com.example.oopkursova.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "Finance")
public class Finance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "budget", precision = 10, scale = 2)
    private BigDecimal budget;


    @Column(name = "actors_salary", precision = 10, scale = 2)
    private BigDecimal actorsSalary; // Сумма выделенная на зарплату актерам

    @Column(name = "crew_salary", precision = 10, scale = 2)
    private BigDecimal crewSalary; // Сумма выделенная на зарплату членам съемочной группы

    @Column(name = "advertising_cost", precision = 10, scale = 2)
    private BigDecimal advertisingCost; // Затраты на рекламу

    @Column(name = "editing_cost", precision = 10, scale = 2)
    private BigDecimal editingCost; // Затраты на монтаж

    @Column(name = "equipment_cost", precision = 10, scale = 2)
    private BigDecimal equipmentCost; // Затраты на оборудование


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movies movie;
}
