package com.example.Repository;


import com.example.Entity.Users;
import com.example.loger.Loggable;
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
     Optional<Users> findByUserName(String username);

     Boolean existsUsersByGmail(String gmail);
     Boolean existsUsersByUserName(String name);

     Optional<Users> findByActor_Id(Long id);


}
