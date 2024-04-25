package com.example.oopkursova.Repository;

import com.example.oopkursova.Entity.Finance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinanceRepo extends JpaRepository<Finance, Long> {
}
