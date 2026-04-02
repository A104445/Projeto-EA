package com.projeto_EA.AirBus.models.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AdquirirBilheteDTO {
    @NotNull
    private Long carreiraID;
}