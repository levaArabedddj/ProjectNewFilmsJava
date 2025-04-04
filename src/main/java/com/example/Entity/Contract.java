package com.example.Entity;

import com.example.Enum.ContractStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Data
@Entity
@Table(name = "contracts")
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Уникальный идентификатор контракта

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private Users user; // Ссылка на пользователя (актер, режиссер, член съемочной группы)

    @ManyToOne
    @JoinColumn(name = "movie_id", referencedColumnName = "id")
    private Movies movie; // Ссылка на фильм

    @Column(name = "salary", precision = 10, scale = 2)
    private BigDecimal salary; // Заработная плата

    @Column(nullable = false)
    private BigDecimal totalPaid = BigDecimal.ZERO;

    @Column(name = "start_date")
    private LocalDate startDate; // Дата начала контракта

    @Column(name = "end_date")
    private LocalDate endDate; // Дата окончания контракта

    private int timeWorkInWeek; //строка для определения сколько часов в неделю должен работать минимум актер

    @Column(name = "contract_type")
    private String contractType; // Тип контракта (Actor, Director, Crew)

    @Enumerated(EnumType.STRING)
    private ContractStatus status;

    private Double penalty; // штрафы
    private Double bonuses; // бонусы
    private String paymentSchedule; // график выплат

    @Column(nullable = true)
    private String actorSignature;

    @Column(nullable = true)
    private String directorSignature;

    public void setTotalPaid(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Total paid amount cannot be null");
        }
        if (amount.compareTo(salary) > 0) {
            throw new IllegalArgumentException("Total paid amount cannot exceed the contract salary!");
        }
        this.totalPaid = amount;
    }

    public void setPenalty(Double penalty) {
        if (penalty == null) {
            this.penalty = 0.0; // Если штраф не задан, то 0
            return;
        }

        if (penalty < 0) {
            throw new IllegalArgumentException("Penalty cannot be negative");
        }

        if (this.salary != null && penalty > this.salary.doubleValue() * 0.5) {
            throw new IllegalArgumentException("Penalty cannot exceed 50% of salary");
        }

        if (this.status.equals("Completed") || this.status.equals("Cancelled")) {
            throw new IllegalStateException("Cannot set a penalty for a completed or cancelled contract");
        }

        this.penalty = penalty;
    }

    public void setBonuses(Double bonuses) {
        if (bonuses == null) {
            this.bonuses = 0.0; // Если бонус не задан, то 0
            return;
        }

        if (bonuses < 0) {
            throw new IllegalArgumentException("Bonuses cannot be negative");
        }

        if (this.salary != null && bonuses > this.salary.doubleValue() * 0.3) {
            throw new IllegalArgumentException("Bonuses cannot exceed 30% of salary");
        }

        if (this.status.equals("Cancelled")) {
            throw new IllegalStateException("Cannot set bonuses for a cancelled contract");
        }

        this.bonuses = bonuses;
    }

    // Добавить возможность, отправлять сообщение на почту юзеру, когда к нему пришла выплата
    public void setPaymentSchedule(String paymentSchedule) {
        if (paymentSchedule == null || paymentSchedule.isEmpty()) {
            throw new IllegalArgumentException("Payment schedule cannot be empty");
        }
        List<String> validSchedules = Arrays.asList("Monthly", "Biweekly", "On Completion");
        if (!validSchedules.contains(paymentSchedule)) {
            throw new IllegalArgumentException("Invalid payment schedule. Allowed values: Monthly, Biweekly, On Completion");
        }
        if (this.status.equals("Completed") || this.status.equals("Cancelled")) {
            throw new IllegalStateException("Cannot change payment schedule for a completed or cancelled contract");
        }


        this.paymentSchedule = paymentSchedule;
    }
}