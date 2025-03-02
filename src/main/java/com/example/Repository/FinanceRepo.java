package com.example.Repository;


import com.example.Entity.Finance;
import com.example.loger.Loggable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FinanceRepo extends JpaRepository<Finance, Long> {
    @Loggable
    List<Finance> findByMovieId(Long id);
    @Loggable
     Optional<Finance> findById(Long id);

    Boolean existsByMovieId(Long id);

    Boolean existsFinanceById(Long id);
}
