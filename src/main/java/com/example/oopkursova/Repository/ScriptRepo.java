package com.example.oopkursova.Repository;

import com.example.oopkursova.Entity.Script;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScriptRepo extends JpaRepository<Script, Long> {
    List<Script> findByMovieId(Long id);
}
