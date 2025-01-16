package com.example.oopkursova.Repository;

import com.example.oopkursova.Entity.Movies;
import com.example.oopkursova.Entity.Users;
import com.example.oopkursova.loger.Loggable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MoviesRepo extends JpaRepository<Movies, Long> {
    @Loggable
    Movies findById(long id);

    List<Movies> findByUserName(String currentUsername);

    List<Movies> findAllByUser(Users currentUser);

    List<Movies> findByUser(Users user);

    Optional<Movies> findByTitle(String title);

}
