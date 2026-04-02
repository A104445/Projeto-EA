package com.projeto_EA.AirBus.models.DTOs;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter @Setter
public class AdquirirPacoteDTO {
    @Min(1)
    private int numViagens;

    @NotEmpty
    private Set<Long> zonaIDs;
}