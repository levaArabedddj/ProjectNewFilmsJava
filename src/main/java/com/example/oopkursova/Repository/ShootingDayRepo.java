package com.example.oopkursova.Repository;

import com.example.oopkursova.Entity.ShootingDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShootingDayRepo extends JpaRepository<ShootingDay, Long> {
}
