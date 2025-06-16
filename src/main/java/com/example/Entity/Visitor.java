package com.example.Entity;

import com.example.Enum.SubscriptionLevelVisitor;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class Visitor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = true)
    private String sessionId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = true)
    private Users user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionLevelVisitor subscriptionLevel;

    private BigDecimal balance;

}
