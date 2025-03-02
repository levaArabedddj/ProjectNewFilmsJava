package com.example.Repository;


import com.example.Entity.TrialParticipants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrialParticipantsRepo extends JpaRepository<TrialParticipants, Long> {
}