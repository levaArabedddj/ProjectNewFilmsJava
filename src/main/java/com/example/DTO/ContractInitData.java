package com.example.DTO;

import com.example.Entity.Director;
import com.example.Entity.Movies;
import com.example.Entity.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ContractInitData {
    private final Users actor;
    private final Director director;
    private final Movies movie;

    public ContractInitData(Users actor, Director director, Movies movie) {
        this.actor = actor;
        this.director = director;
        this.movie = movie;
    }
}
