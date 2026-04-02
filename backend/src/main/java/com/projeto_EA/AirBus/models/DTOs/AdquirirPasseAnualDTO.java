package com.projeto_EA.AirBus.models.DTOs;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter @Setter
public class AdquirirPasseAnualDTO {
    @NotEmpty
    private Set<Long> zonaIDs;
}