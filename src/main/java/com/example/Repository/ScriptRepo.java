package com.example.Repository;


import com.example.Entity.Script;
import com.example.loger.Loggable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScriptRepo extends JpaRepository<Script, Long> {
    @Loggable
    List<Script> findByMovieId(Long id);
}
