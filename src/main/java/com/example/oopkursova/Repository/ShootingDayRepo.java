package com.example.oopkursova.Repository;

import com.example.oopkursova.Entity.ShootingDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShootingDayRepo extends JpaRepository<ShootingDay, Long> {
    List<ShootingDay> findByMovieId(Long id);
}
