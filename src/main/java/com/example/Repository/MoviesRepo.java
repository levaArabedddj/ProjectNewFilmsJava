package com.example.Repository;


import com.example.Entity.Director;
import com.example.Entity.Movies;
import com.example.loger.Loggable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MoviesRepo extends JpaRepository<Movies, Long> {
    @Loggable
    Movies findById(long id);

    Optional<Movies> findById(Long id);

   // List<Movies> findByUserName(String currentUsername);

//    List<Movies> findAllByUser(Users currentUser);
//
//    List<Movies> findByUser(Users user);

    Optional<Movies> findByTitle(String title);

    List<Movies> findByDirector(Director director);

    @Query("SELECT COUNT(m) > 0 FROM Movies m WHERE m.id = :id AND m.director.users.userName = :name")
    boolean existsByIdAndUsername(@Param("id") Long id, @Param("name") String username);


}
