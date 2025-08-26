package org.example.servicefilm;



import org.example.servicefilm.Entity.Director;
import org.example.servicefilm.Entity.Movies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MoviesRepo extends JpaRepository<Movies, Long> {



    Optional<Movies> findById(long id);

    Optional<Movies> findByTitle(String title);

    boolean existsByTitle(String title);

    List<Movies> findByDirector(Director director);

    @Query("SELECT COUNT(m) > 0 FROM Movies m WHERE m.id = :id AND m.director.users.userName = :name")
    boolean existsByIdAndUsername(@Param("id") Long id, @Param("name") String username);


    @Query("SELECT m FROM Movies m WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(m.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Movies> searchByTitleOrDescription(@Param("keyword") String keyword);


    @Query("""
        SELECT m
        FROM Movies m
          JOIN m.director d
          JOIN d.users     u
        WHERE m.id          = :filmId
          AND u.userName    = :username
    """)
    Optional<Movies> findByIdAndDirectorUserUserName(
            @Param("filmId")   Long filmId,
            @Param("username") String username
    );



    long deleteAllByTitleStartingWith(String title);



}
