package com.example.oopkursova.Repository;

import com.example.oopkursova.Entity.Movies;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoviesRepo extends JpaRepository<Movies, Long> {
    Movies findById(long id);
}
