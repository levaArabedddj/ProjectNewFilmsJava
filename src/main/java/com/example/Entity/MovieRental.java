package com.example.Entity;



import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "movie_rental",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "movie_id"})
)
@Data
public class MovieRental{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   // Кто арендовал фильм
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    // Какой фильм
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movies movie;

    // Когда была совершена аренда
    @Column(nullable = false)
    private LocalDateTime purchasedAt = LocalDateTime.now();


    @Column(nullable = false)
    private BigDecimal price;


    /** Когда началась аренда */
    @Column(nullable = false)
    private LocalDateTime rentedAt = LocalDateTime.now();

    /** Когда кончается аренда (через месяц) */
    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @PrePersist
    private void prePersist() {
        // аренда ровно на месяц
        this.expiresAt = this.rentedAt.plusMonths(1);
    }
}

