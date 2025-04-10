package com.example.Repository;


import com.example.Entity.Director;
import com.example.Entity.DirectorProfiles;
import com.example.Entity.Movies;
import com.example.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DirectorRepo extends JpaRepository<Director, Long> {

    Optional<Director> findByUsers(Users user);


    @Query("SELECT dir from Director dir where dir.users.user_id = :userId")
    Optional<Director> findByUserUserId(@Param("userId") Long directorId);

    @Query("select mov from Movies mov where mov.director.id = :directorId")
    List<Movies> findMoviesByDirectorId(Long directorId);
}