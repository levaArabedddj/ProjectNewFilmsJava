package com.example.Repository;


import com.example.Entity.ShootingDay;
import com.example.loger.Loggable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ShootingDayRepo extends JpaRepository<ShootingDay, Long> {
    @Loggable
    List<ShootingDay> findByMovieId(Long id);
    Boolean existsByShootingDateAndMovieId(LocalDate localDate , Long movieId);
}
