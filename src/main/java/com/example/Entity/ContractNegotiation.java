package com.example.Entity;

import com.example.Enum.ContractStatus;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "contract_negotiations")
@Data
public class ContractNegotiation { // переговорный процесс по контракту между актером и режиссером

    //Когда контракт одобрен обеими сторонами, создается финальный контракт в таблице Contract.
    //
    //Все переговорные записи (ContractNegotiation) удаляются, потому что они больше не нужны.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users actor;

    @ManyToOne
    @JoinColumn(name = "director_id", nullable = false)
    private Director director;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movies movie;

    private Double proposedSalary;
    private Double penalty;
    private Double bonuses;
    private String paymentSchedule;

    @Enumerated(EnumType.STRING)
    private ContractStatus status;  // WAITING_ACTOR, WAITING_DIRECTOR, ACCEPTED, REJECTED

    @Column(nullable = true)
    private String actorSignature;

    @Column(nullable = true)
    private String directorSignature;

    // Геттеры и сеттеры
}

