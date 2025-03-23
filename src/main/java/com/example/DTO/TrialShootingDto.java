package com.example.DTO;

import autovalue.shaded.org.jetbrains.annotations.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TrialShootingDto {

    @NotNull()
    private LocalDate date;

    @NotNull()
    private LocalDateTime startTime;

    @NotBlank(message = "Локация обязательна")
    private String location;

    private String description;
}
