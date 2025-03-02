package com.example.Repository;


import com.example.Entity.Castings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CastingsRepo extends JpaRepository<Castings, Integer> {
}