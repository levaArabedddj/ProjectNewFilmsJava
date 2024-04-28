package com.example.oopkursova.Repository;

import com.example.oopkursova.Entity.Actors;
import com.example.oopkursova.Entity.Movies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActorRepo extends JpaRepository<Actors, Long> {
    Actors findById(long id);
    @Query(value = "SELECT a.* FROM public.actors a INNER JOIN public.actors_movies am ON a.id = am.actors_id WHERE am.movie_id = :id", nativeQuery = true)
    List<Actors> findMovieWithActorsById(Long id);

}
