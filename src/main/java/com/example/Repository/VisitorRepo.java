package com.example.Repository;

import com.example.Entity.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitorRepo extends JpaRepository<Visitor,Long> {
}
