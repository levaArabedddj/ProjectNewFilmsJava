package com.example.oopkursova.Repository;

import com.example.oopkursova.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepo extends JpaRepository<Users, Long> {
     Users findByGmail(String gmail);
}
