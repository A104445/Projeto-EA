package com.projeto_EA.AirBus.models.DTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleLoginRequestDTO {

    @NotBlank
    private String code;
}
