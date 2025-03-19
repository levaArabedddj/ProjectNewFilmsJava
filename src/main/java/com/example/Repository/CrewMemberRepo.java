package com.example.Repository;


import com.example.Entity.FilmCrewMembers;
import com.example.loger.Loggable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CrewMemberRepo extends JpaRepository<FilmCrewMembers, Long> {
//    @Loggable
//    @Query("SELECT fcm FROM FilmCrewMembers fcm JOIN FETCH fcm.movies")
//    List<FilmCrewMembers> findAllWithMovies();

    @Loggable
    @Query(value = "SELECT f.* FROM public.film_crew_member f INNER JOIN public.film_crew_member_movies fm " +
            "ON f.crew_member_id = fm.film_crew_member_id WHERE fm.movie_id = :id", nativeQuery = true)
    List<FilmCrewMembers> findCrewMembersByMovieId(Long id);


    @Query("SELECT fm FROM FilmCrewMembers fm WHERE fm.user.user_id = :userId")
    Optional<FilmCrewMembers> findByUserUserId(@Param("userId") Long userId);


}
