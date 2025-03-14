package com.example.Repository;


import com.example.Entity.Director;
import com.example.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DirectorRepo extends JpaRepository<Director, Long> {

    Optional<Director> findByUsers(Users user);


}