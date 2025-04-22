package com.example.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CastingDto {
    private int id;
    @NotBlank
    private String roleName;
    private String description;
    private String requirements;

    public CastingDto(int id, String roleName, String description, String requirements) {
        this.id = id;
        this.roleName = roleName;
        this.description = description;
        this.requirements = requirements;
    }
}

