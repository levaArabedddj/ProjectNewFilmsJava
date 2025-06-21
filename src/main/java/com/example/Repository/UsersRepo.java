package com.example.Repository;


import com.example.DTO.ContractInitData;
import com.example.Entity.Director;
import com.example.Entity.Users;
import com.example.loger.Loggable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    // Director findByDirector_Id(Long id);

     @Query("""
    select new com.example.DTO.ContractInitData(u, d, m)
    from Users   u,
         Director d,
         Movies   m
    where u.user_id     = :actorId
      and d.users.user_id = :directorId
      and m.id         = :movieId
""")
     ContractInitData findInitData(
             @Param("actorId")    Long actorId,
             @Param("directorId") Long directorId,
             @Param("movieId")    Long movieId
     );

}
