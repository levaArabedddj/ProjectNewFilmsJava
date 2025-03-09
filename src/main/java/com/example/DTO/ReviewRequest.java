package com.example.DTO;

import com.example.Enum.ApplicationStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class ReviewRequest {
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;
    private String feedback;
}

