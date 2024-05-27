package com.example.oopkursova.Repository;

import com.example.oopkursova.config.Users;
import com.example.oopkursova.loger.Loggable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepo extends JpaRepository<Users, Long> {
     @Loggable
     Users findByGmail(String gmail);

     Optional<Users> findByName(String username);



//     @Query("SELECT u FROM Users u LEFT JOIN FETCH u.moviesList WHERE u.id = :userId")
//     Users findUserWithMovies(Long userId);


}
