package com.example.oopkursova.DTO;

import com.example.oopkursova.Entity.Movies;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
public class DtoScript {
    private Long id;
    private String content;
}
