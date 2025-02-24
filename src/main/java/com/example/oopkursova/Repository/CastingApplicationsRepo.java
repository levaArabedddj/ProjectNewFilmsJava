package com.example.oopkursova.Repository;

import com.example.oopkursova.Entity.CastingApplications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CastingApplicationsRepo extends JpaRepository<CastingApplications, Long> {
}