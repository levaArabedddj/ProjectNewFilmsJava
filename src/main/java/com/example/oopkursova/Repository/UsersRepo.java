package com.example.oopkursova.Repository;

import com.example.oopkursova.Entity.Users;
import com.example.oopkursova.loger.Loggable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepo extends JpaRepository<Users, Long> {
//     @Loggable
//     Users findByGmail(String gmail);
     @Loggable
     Optional<Users> findByGmail(String email);

     @Loggable
     Optional<Users> findByName(String username);

     Boolean existsUsersByGmail(String gmail);
     Boolean existsUsersByName(String name);



}
