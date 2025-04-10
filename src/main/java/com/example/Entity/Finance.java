package com.example.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "Finance")
public class Finance {
    // добавить логику проверки что бюджет не могут превышать поля затрат
    // или вывести сообщения что бюджет будет исчерпан через время
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "budget", precision = 10, scale = 2)
    private BigDecimal budget;


    @Column(name = "actors_salary", precision = 10, scale = 2)
    private BigDecimal actorsSalary = BigDecimal.ZERO; // прописать при добавлении актера в фильм плюсововать сюда данные

    @Column(name = "crew_salary", precision = 10, scale = 2)
    private BigDecimal crewSalary = BigDecimal.ZERO;

    @Column(name = "advertising_cost", precision = 10, scale = 2)
    private BigDecimal advertisingCost = BigDecimal.ZERO;

    @Column(name = "editing_cost", precision = 10, scale = 2)
    private BigDecimal editingCost = BigDecimal.ZERO;

    @Column(name = "equipment_cost", precision = 10, scale = 2)
    private BigDecimal equipmentCost = BigDecimal.ZERO;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movies movie;


    public void setActorsSalary(BigDecimal actorsSalary) {

        if(actorsSalary == null || actorsSalary.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Actor salary cannot be null");
        }
        this.actorsSalary = actorsSalary;
        validateBudget();
        validSalaryLimit();
    }
    public void setCrewSalary(BigDecimal crewSalary) {

        if(crewSalary == null || crewSalary.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("Crew salary cannot be null and salary cannot be negative");
        }
        this.crewSalary = crewSalary;
        validateBudget();
        validSalaryLimit();
    }
    public void setAdvertisingCost(BigDecimal advertisingCost) {

        if(advertisingCost == null || advertisingCost.compareTo(BigDecimal.ZERO) < 0 ){
            throw new IllegalArgumentException("Advertising cost cannot be null and salary cannot be greater than zero");
        }
        if(advertisingCost.compareTo(budget.multiply(BigDecimal.valueOf(0.2))) > 0 ){
            throw new IllegalArgumentException("Advertising cost cannot exceed 20% of the budget");
        }
        this.advertisingCost = advertisingCost;
        validateBudget();
    }
    public void setEquipmentCost(BigDecimal equipmentCost) {

        if(equipmentCost == null || equipmentCost.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("Equipment cost cannot be null and salary cannot be greater than zero");
        }

        if(equipmentCost.compareTo(budget.multiply(BigDecimal.valueOf(0.3))) > 0 ){

            throw new IllegalArgumentException("Equipment cost cannot exceed 30% of the budget");
        }
        this.equipmentCost = equipmentCost;
        validateBudget();
    }

    public void setEditingCost(BigDecimal editingCost) {
        if(editingCost == null || editingCost.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("Editing cost cannot be null and salary cannot be greater than zero");
        }
        this.editingCost = editingCost;
        validateBudget();
    }

    public void setBudget(BigDecimal budget) {
        if(budget == null || budget.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Budget cannot be null and cannot be negative");
        }

        BigDecimal totalExpenses = actorsSalary.add(crewSalary)
                .add(advertisingCost)
                .add(editingCost)
                .add(equipmentCost);

        // Если сумма расходов больше нового бюджета — выбросить исключение
        if(budget.compareTo(totalExpenses) < 0){
            throw new IllegalArgumentException("Budget is not enough to cover total expenses");
        }
        this.budget = budget;
    }

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

    // 📌 Универсальная проверка бюджета
    private void validateBudget() {
        if (budget == null) {
            throw new IllegalStateException("Budget is not set.");
        }

        BigDecimal totalExpenses = actorsSalary.add(crewSalary)
                .add(advertisingCost)
                .add(editingCost)
                .add(equipmentCost);

        if (totalExpenses.compareTo(budget) > 0) {
            throw new IllegalArgumentException("Total expenses exceed budget!");
        }
    }

    private void validSalaryLimit(){
        BigDecimal totalSalary = actorsSalary.add(crewSalary);
        if(totalSalary.compareTo(budget.divide(BigDecimal.valueOf(2))) > 0){
            throw new IllegalArgumentException("Total salary exceed budget!");
        }
    }
}
