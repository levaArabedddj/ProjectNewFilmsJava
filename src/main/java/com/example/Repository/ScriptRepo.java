package com.example.Repository;


import com.example.Entity.Script;
import com.example.loger.Loggable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScriptRepo extends JpaRepository<Script, Long> {
    @Loggable
    Optional<Script> findByMovieId(Long id);
}
