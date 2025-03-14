package com.example.DTO;

import com.example.Enum.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CastingApplicationDto {

    private Long id;
    private String message;
    private ApplicationStatus status;
    private String castingRole;
    private DtoActor actor;
}
