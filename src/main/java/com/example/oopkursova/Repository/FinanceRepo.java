package com.example.oopkursova.Repository;

import com.example.oopkursova.Entity.Finance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinanceRepo extends JpaRepository<Finance, Long> {
    List<Finance> findByMovieId(Long id);
}
