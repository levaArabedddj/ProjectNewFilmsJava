package com.example.Repository;


import com.example.Entity.Trial_Shootings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Trial_ShootingsRepo extends JpaRepository<Trial_Shootings, Long> {
}