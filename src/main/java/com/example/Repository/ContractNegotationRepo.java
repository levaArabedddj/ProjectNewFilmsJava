package com.example.Repository;

import com.example.Entity.ContractNegotiation;
import com.example.Entity.Movies;
import com.example.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractNegotationRepo extends JpaRepository<ContractNegotiation, Long> {
    void deleteByActorAndMovie(Users actor, Movies movie);
}
