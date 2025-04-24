package com.example.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Для передачи подписи актёра или режиссёра
 */
@Data
public class SignatureRequest {
    @NotBlank
    private String signature;
}
