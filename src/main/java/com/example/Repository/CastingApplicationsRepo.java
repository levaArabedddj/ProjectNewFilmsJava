package com.example.Repository;


import com.example.Entity.CastingApplications;
import com.example.Entity.Castings;
import com.example.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CastingApplicationsRepo extends JpaRepository<CastingApplications, Long> {

    Boolean existsByActorAndCastings(Users actor, Castings casting);
}