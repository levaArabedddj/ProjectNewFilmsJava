package com.example.oopkursova.Service;

import com.example.oopkursova.Entity.Directors;
import com.example.oopkursova.Entity.Users;
import com.example.oopkursova.Repository.DirectorsRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class DirectorsService {
    @Autowired
    private final DirectorsRepo directorsRepo;

    public Directors directorsCreate(Directors directors){
        return  directorsRepo.save(directors);
    }

    public List<Directors> getAllDirectors() {
        return directorsRepo.findAll();
    }
}
