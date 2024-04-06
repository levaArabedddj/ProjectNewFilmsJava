package com.example.oopkursova.Repository;

import com.example.oopkursova.Entity.Actors;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActorRepo extends JpaRepository<Actors, Long> {
}
