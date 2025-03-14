package com.example.Repository;


import com.example.Entity.Castings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CastingsRepo extends JpaRepository<Castings, Integer> {


    List<Castings> findByMovieId(long movieId);
}