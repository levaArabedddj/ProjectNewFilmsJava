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
    private BigDecimal actorsSalary;

    @Column(name = "crew_salary", precision = 10, scale = 2)
    private BigDecimal crewSalary;

    @Column(name = "advertising_cost", precision = 10, scale = 2)
    private BigDecimal advertisingCost;

    @Column(name = "editing_cost", precision = 10, scale = 2)
    private BigDecimal editingCost;

    @Column(name = "equipment_cost", precision = 10, scale = 2)
    private BigDecimal equipmentCost;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movies movie;


    @Override
    public String toString() {
        return "Finance{" +
                "id=" + id +
                ", budget=" + budget +
                ", actorsSalary=" + actorsSalary +
                ", crewSalary=" + crewSalary +
                ", advertisingCost=" + advertisingCost +
                ", editingCost=" + editingCost +
                ", equipmentCost=" + equipmentCost +
                '}';
    }
}
