package com.projeto_EA.AirBus.models.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CheckInRequestDTO {

    @NotNull
    private Long paragemID;

    @NotNull
    private Long carreiraID;

    @NotNull
    private Long tituloID;
}