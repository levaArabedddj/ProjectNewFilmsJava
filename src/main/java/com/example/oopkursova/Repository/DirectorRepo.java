package com.example.oopkursova.Repository;

import com.example.oopkursova.Entity.Director;
import com.example.oopkursova.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DirectorRepo extends JpaRepository<Director, Long> {

    Optional<Director> findByUsers(Users user);
}