package com.example.Repository;

import com.example.Entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepo extends JpaRepository<Contract,Long> {
}
