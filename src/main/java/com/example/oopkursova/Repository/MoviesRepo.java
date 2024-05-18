package com.example.oopkursova.Repository;

import com.example.oopkursova.Entity.Movies;
import com.example.oopkursova.loger.Loggable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoviesRepo extends JpaRepository<Movies, Long> {
    @Loggable
    Movies findById(long id);
}
